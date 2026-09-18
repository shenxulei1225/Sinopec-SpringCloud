package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntitySceneQueryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.FieldFilterReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryScene;
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
 * <p><b>负责</b>：新建这次执行的账；更新这次/某一步的状态；往账里记一条过程。</p>
 * <p><b>权威</b>：执行记录与步骤实体；过程写在 custom_fields.process_entries，其它过程字段同袋
 * （task_id / pending_execution_id / execution_status）。</p>
 * <p><b>禁止</b>：写域任务表 device* 列；无执行记录时猜任务或设备；用日志冒充已记过程。</p>
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

    /** 执行账上「何时收到了什么」的过程列表，写在 custom_fields */
    public static final String PROCESS_ENTRIES_KEY = "process_entries";

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
        String entityTypeCode = resolveRecordType(req.getEntityTypeCode());
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskExecutionAppendProcessRespDTO appendProcess(TaskExecutionAppendProcessReqDTO req) {
        if (req.getExecutionRecordId() == null) {
            throw new ServiceException(400, "往执行账记过程必须带这次执行的账本编号，禁止按设备号猜");
        }
        String entityTypeCode = resolveRecordType(req.getEntityTypeCode());
        EntityRespVO record = entityService.get(req.getExecutionRecordId(), entityTypeCode);
        if (record == null || record.getId() == null) {
            throw new ServiceException(404, "执行记录不存在：" + req.getExecutionRecordId());
        }

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
        List<Map<String, Object>> entries = readProcessEntries(custom.get(PROCESS_ENTRIES_KEY));
        entries.add(toProcessEntry(req));
        custom.put(PROCESS_ENTRIES_KEY, entries);
        updateReq.setCustomFields(custom);
        entityService.update(updateReq);

        TaskExecutionAppendProcessRespDTO resp = new TaskExecutionAppendProcessRespDTO();
        resp.setExecutionRecordId(record.getId());
        resp.setProcessEntryCount(entries.size());
        return resp;
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
        EntitySceneQueryRespVO resp = entityService.queryEntities(
                EntityQueryScene.ENTITIES_BY_MODEL,
                "PAGE",
                "LIGHT",
                null,
                STEP_ENTITY_TYPE,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                1,
                5,
                null,
                null,
                filters,
                null,
                null,
                null,
                null,
                null);
        if (resp == null || resp.getPage() == null || CollectionUtils.isEmpty(resp.getPage().getList())) {
            throw new ServiceException(404, "执行步骤不存在 stepCode=" + stepCode);
        }
        return resp.getPage().getList().get(0).getId();
    }

    private static String resolveRecordType(String entityTypeCode) {
        return StringUtils.hasText(entityTypeCode) ? entityTypeCode.trim() : DEFAULT_RECORD_TYPE;
    }

    /**
     * 读出已有过程列表。只接受列表或 JSON 数组文本；其它形态当空，避免把脏值当一条过程。
     */
    static List<Map<String, Object>> readProcessEntries(Object raw) {
        List<Map<String, Object>> entries = new ArrayList<>();
        if (raw == null) {
            return entries;
        }
        Object parsed = raw;
        if (raw instanceof String text && StringUtils.hasText(text)) {
            parsed = JSON.parse(text);
        }
        if (!(parsed instanceof List<?> list)) {
            return entries;
        }
        for (Object item : list) {
            if (item instanceof Map<?, ?> map) {
                Map<String, Object> copy = new LinkedHashMap<>();
                for (Map.Entry<?, ?> e : map.entrySet()) {
                    if (e.getKey() != null) {
                        copy.put(String.valueOf(e.getKey()), e.getValue());
                    }
                }
                entries.add(copy);
            }
        }
        return entries;
    }

    private static Map<String, Object> toProcessEntry(TaskExecutionAppendProcessReqDTO req) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("receivedAtEpochMs", req.getReceivedAtEpochMs());
        entry.put("messageKind", req.getMessageKind().trim());
        entry.put("protocolQualify", req.getProtocolQualify().trim());
        entry.put("qualifyErrors", req.getQualifyErrors() == null
                ? List.of()
                : List.copyOf(req.getQualifyErrors()));
        if (StringUtils.hasText(req.getSummary())) {
            entry.put("summary", req.getSummary().trim());
        }
        return entry;
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
