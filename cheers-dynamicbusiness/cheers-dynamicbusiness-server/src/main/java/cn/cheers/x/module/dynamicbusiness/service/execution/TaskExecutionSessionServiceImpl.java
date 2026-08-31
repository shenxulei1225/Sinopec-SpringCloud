package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.FieldFilterReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.inspection.TaskExecutionBootstrapService;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务模块标准执行会话实现。
 *
 * <p><b>负责</b>：一次执行记录创建 + bootstrap；按执行记录 id 回写状态/步骤。</p>
 * <p><b>权威</b>：执行记录与步骤实体；过程字段写入 custom_fields（task_id / pending_execution_id / execution_status）。</p>
 * <p><b>禁止</b>：写域任务表 device* 列；无执行记录时猜任务定义；有 gapCodes 仍开跑。</p>
 */
@Service
public class TaskExecutionSessionServiceImpl implements TaskExecutionSessionService {

    public static final String DEFAULT_RECORD_TYPE = "task_excution_record";
    public static final String STEP_ENTITY_TYPE = "task_execution_step";

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_IN_PROGRESS = "in_progress";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_FAULT = "fault";
    public static final String STATUS_FAILED = "failed";

    @Resource
    private EntityService entityService;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private TaskExecutionBootstrapService taskExecutionBootstrapService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskExecutionStartRespDTO start(TaskExecutionStartReqDTO req) {
        if (req.getGapCodes() != null && !req.getGapCodes().isEmpty()) {
            throw new ServiceException(400, "存在 SOP 解析缺口，禁止开跑：" + req.getGapCodes());
        }
        if (req.getTaskDefinitionId() == null) {
            throw new ServiceException(400, "taskDefinitionId 不能为空");
        }
        String entityTypeCode = StringUtils.hasText(req.getEntityTypeCode())
                ? req.getEntityTypeCode().trim()
                : DEFAULT_RECORD_TYPE;
        Long modelId = resolveModelId(req);

        EntityCreateReqVO createReq = new EntityCreateReqVO();
        Map<String, Object> base = new LinkedHashMap<>();
        base.put("entityTypeCode", entityTypeCode);
        base.put("modelId", modelId);
        base.put("name", StringUtils.hasText(req.getName())
                ? req.getName().trim()
                : "执行-" + req.getTaskDefinitionId());
        base.put("status", 1);
        createReq.setBaseFields(base);

        Map<String, Object> custom = new LinkedHashMap<>();
        custom.put("task_id", req.getTaskDefinitionId());
        if (StringUtils.hasText(req.getPendingRef())) {
            custom.put("pending_execution_id", req.getPendingRef().trim());
        }
        custom.put("execution_status", STATUS_PENDING);
        createReq.setCustomFields(custom);

        Long recordId = entityService.create(createReq);

        TaskExecutionBootstrapReqVO bootstrapReq = new TaskExecutionBootstrapReqVO();
        bootstrapReq.setExecutionRecordId(recordId);
        bootstrapReq.setStandardSnapshot(req.getStandardSnapshot());
        bootstrapReq.setParameterSnapshot(req.getParameterSnapshot());
        bootstrapReq.setGapCodes(List.of());
        bootstrapReq.setSteps(toBootstrapSteps(req.getSteps()));
        TaskExecutionBootstrapRespVO bootstrapResp = taskExecutionBootstrapService.bootstrap(bootstrapReq);

        TaskExecutionStartRespDTO resp = new TaskExecutionStartRespDTO();
        resp.setExecutionRecordId(recordId);
        resp.setStepIds(bootstrapResp.getStepIds());
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void writeback(TaskExecutionWritebackReqDTO req) {
        String entityTypeCode = StringUtils.hasText(req.getEntityTypeCode())
                ? req.getEntityTypeCode().trim()
                : DEFAULT_RECORD_TYPE;
        EntityRespVO record = entityService.get(req.getExecutionRecordId(), entityTypeCode);
        if (record == null || record.getId() == null) {
            throw new ServiceException(404, "执行记录不存在：" + req.getExecutionRecordId());
        }

        if (StringUtils.hasText(req.getExecutionStatus())) {
            EntityUpdateReqVO updateReq = new EntityUpdateReqVO();
            updateReq.setId(record.getId());
            Map<String, Object> base = copyMap(record.getBaseFields());
            base.put("entityTypeCode", entityTypeCode);
            if (record.getModelId() != null) {
                base.put("modelId", record.getModelId());
            }
            if (record.getName() != null) {
                base.put("name", record.getName());
            }
            updateReq.setBaseFields(base);
            Map<String, Object> custom = copyMap(record.getCustomFields());
            custom.put("execution_status", req.getExecutionStatus().trim());
            updateReq.setCustomFields(custom);
            entityService.update(updateReq);
        }

        if (CollectionUtils.isEmpty(req.getStepUpdates())) {
            return;
        }
        for (TaskExecutionWritebackReqDTO.StepUpdate stepUpdate : req.getStepUpdates()) {
            applyStepUpdate(req.getExecutionRecordId(), stepUpdate);
        }
    }

    private void applyStepUpdate(Long executionRecordId, TaskExecutionWritebackReqDTO.StepUpdate stepUpdate) {
        Long stepId = stepUpdate.getStepId();
        if (stepId == null) {
            if (!StringUtils.hasText(stepUpdate.getStepCode())) {
                throw new ServiceException(400, "步骤更新须提供 stepId 或 stepCode");
            }
            stepId = findStepIdByCode(executionRecordId, stepUpdate.getStepCode().trim());
        }
        EntityRespVO step = entityService.get(stepId, STEP_ENTITY_TYPE);
        if (step == null || step.getId() == null) {
            throw new ServiceException(404, "执行步骤不存在：" + stepId);
        }
        EntityUpdateReqVO updateReq = new EntityUpdateReqVO();
        updateReq.setId(step.getId());
        Map<String, Object> base = copyMap(step.getBaseFields());
        base.put("entityTypeCode", STEP_ENTITY_TYPE);
        if (step.getModelId() != null) {
            base.put("modelId", step.getModelId());
        }
        if (step.getName() != null) {
            base.put("name", step.getName());
        }
        base.put("step_status", stepUpdate.getStatus().trim());
        if (stepUpdate.getResultPayload() != null) {
            base.put("result_payload", JSON.toJSONString(stepUpdate.getResultPayload()));
        }
        updateReq.setBaseFields(base);
        updateReq.setCustomFields(copyMap(step.getCustomFields()));
        entityService.update(updateReq);
    }

    private Long findStepIdByCode(Long executionRecordId, String stepCode) {
        EntityPageReqVO pageReq = new EntityPageReqVO();
        pageReq.setEntityTypeCode(STEP_ENTITY_TYPE);
        pageReq.setPageNo(1);
        pageReq.setPageSize(5);
        List<FieldFilterReqVO> filters = new ArrayList<>();
        FieldFilterReqVO byRecord = new FieldFilterReqVO();
        byRecord.setFieldCode("execution_record_id");
        byRecord.setOp("EQ");
        byRecord.setValue(executionRecordId);
        filters.add(byRecord);
        FieldFilterReqVO byCode = new FieldFilterReqVO();
        byCode.setFieldCode("step_code");
        byCode.setOp("EQ");
        byCode.setValue(stepCode);
        filters.add(byCode);
        pageReq.setFilters(filters);
        PageResult<EntityRespVO> page = entityService.pageSearchEntities(pageReq);
        if (page == null || CollectionUtils.isEmpty(page.getList())) {
            throw new ServiceException(404, "执行步骤不存在 stepCode=" + stepCode);
        }
        return page.getList().get(0).getId();
    }

    private Long resolveModelId(TaskExecutionStartReqDTO req) {
        if (req.getModelId() != null) {
            return req.getModelId();
        }
        if (!StringUtils.hasText(req.getModelCode())) {
            throw new ServiceException(400, "须提供 modelId 或 modelCode");
        }
        ModelDO model = modelMapper.selectByCode(req.getModelCode().trim());
        if (model == null || model.getId() == null) {
            throw new ServiceException(500, "缺少执行记录型号：" + req.getModelCode());
        }
        return model.getId();
    }

    private static List<TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO> toBootstrapSteps(
            List<TaskExecutionStartReqDTO.StepDraft> drafts) {
        List<TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO> steps = new ArrayList<>();
        for (TaskExecutionStartReqDTO.StepDraft draft : drafts) {
            TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO step =
                    new TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO();
            step.setName(draft.getName());
            step.setStepCode(draft.getStepCode());
            step.setStepOrder(draft.getStepOrder());
            step.setStepTitle(draft.getStepTitle());
            step.setStepType(draft.getStepType());
            step.setStepRequired(draft.getStepRequired());
            step.setParentStepCode(draft.getParentStepCode());
            step.setSource(draft.getSource());
            steps.add(step);
        }
        return steps;
    }

    private static Map<String, Object> copyMap(Map<String, Object> source) {
        Map<String, Object> copy = new LinkedHashMap<>();
        if (source != null) {
            copy.putAll(source);
        }
        return copy;
    }
}
