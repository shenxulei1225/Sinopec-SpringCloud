package cn.cheers.x.module.dynamicbusiness.service.inspection;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 开跑 bootstrap 实现。
 *
 * <p><b>负责</b>：一次执行记录锁定快照 + 批量创建步骤树（含 parent_id）。</p>
 * <p><b>权威</b>：步骤行写入 {@code task_execution_step}；快照写入执行记录 custom_fields
 * （{@code standard_snapshot}/{@code parameter_snapshot}/{@code gap_codes}）；
 * 父子关系以 draft.parentStepCode → 已创建步骤 id 写入实体 {@code parentId}。</p>
 * <p><b>不负责</b>：复合动作展开、SOP merge（调用方先展开再传 steps）。</p>
 * <p><b>禁止</b>：在此调用路径/算路服务；有 gapCodes 时拒绝开跑；父 stepCode 缺失时静默当根。</p>
 */
@Service
public class TaskExecutionBootstrapServiceImpl implements TaskExecutionBootstrapService {

    public static final String EXECUTION_RECORD_TYPE = "task_excution_record";
    public static final String STEP_ENTITY_TYPE = "task_execution_step";
    public static final String STEP_MODEL_CODE = "task_execution_step";

    @Resource
    private EntityService entityService;

    @Resource
    private ModelMapper modelMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskExecutionBootstrapRespVO bootstrap(TaskExecutionBootstrapReqVO req) {
        if (req.getGapCodes() != null && !req.getGapCodes().isEmpty()) {
            throw new ServiceException(400, "存在 SOP 解析缺口，禁止开跑：" + req.getGapCodes());
        }
        EntityRespVO record = entityService.get(req.getExecutionRecordId(), EXECUTION_RECORD_TYPE);
        if (record == null || record.getId() == null) {
            throw new ServiceException(404, "执行记录不存在：" + req.getExecutionRecordId());
        }

        ModelDO stepModel = modelMapper.selectByCode(STEP_MODEL_CODE);
        if (stepModel == null || stepModel.getId() == null) {
            throw new ServiceException(500, "缺少型号 task_execution_step，请先跑 task-execution seed");
        }

        // 锁定快照到执行记录 custom_fields（过程字段权威在开跑写入，不读路径补）
        Map<String, Object> custom = new LinkedHashMap<>();
        if (record.getCustomFields() != null) {
            custom.putAll(record.getCustomFields());
        }
        custom.put("standard_snapshot", req.getStandardSnapshot());
        if (req.getParameterSnapshot() != null) {
            custom.put("parameter_snapshot", req.getParameterSnapshot());
        }
        custom.put("gap_codes", List.of());
        EntityUpdateReqVO updateReq = new EntityUpdateReqVO();
        updateReq.setId(record.getId());
        Map<String, Object> base = new LinkedHashMap<>();
        if (record.getBaseFields() != null) {
            base.putAll(record.getBaseFields());
        }
        base.put("entityTypeCode", EXECUTION_RECORD_TYPE);
        base.put("modelId", record.getModelId());
        if (record.getName() != null) {
            base.put("name", record.getName());
        }
        updateReq.setBaseFields(base);
        updateReq.setCustomFields(custom);
        entityService.update(updateReq);

        // 按列表顺序创建：父须出现在子之前；stepCode → id 映射后写 parentId
        List<Long> stepIds = new ArrayList<>();
        Map<String, Long> stepCodeToId = new LinkedHashMap<>();
        for (TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO draft : req.getSteps()) {
            if (draft == null || !StringUtils.hasText(draft.getStepCode())) {
                throw new ServiceException(400, "步骤 draft 缺少 stepCode");
            }
            if (stepCodeToId.containsKey(draft.getStepCode())) {
                throw new ServiceException(400, "步骤 stepCode 重复：" + draft.getStepCode());
            }

            Long parentId = null;
            if (StringUtils.hasText(draft.getParentStepCode())) {
                parentId = stepCodeToId.get(draft.getParentStepCode());
                if (parentId == null) {
                    throw new ServiceException(400,
                            "步骤 parentStepCode 未找到或顺序错误（父须在子之前）："
                                    + draft.getParentStepCode());
                }
            }

            EntityCreateReqVO createReq = new EntityCreateReqVO();
            Map<String, Object> stepBase = new LinkedHashMap<>();
            stepBase.put("entityTypeCode", STEP_ENTITY_TYPE);
            stepBase.put("modelId", stepModel.getId());
            stepBase.put("name", draft.getName());
            stepBase.put("status", 1);
            stepBase.put("execution_record_id", req.getExecutionRecordId());
            stepBase.put("step_code", draft.getStepCode());
            stepBase.put("step_order", draft.getStepOrder());
            stepBase.put("step_title", draft.getStepTitle() != null ? draft.getStepTitle() : draft.getName());
            stepBase.put("step_type", draft.getStepType() != null ? draft.getStepType() : "sop_step");
            stepBase.put("step_required", draft.getStepRequired() == null || draft.getStepRequired());
            stepBase.put("step_status", "pending");
            if (parentId != null) {
                stepBase.put("parentId", parentId);
            }
            if (draft.getSource() != null) {
                stepBase.put("result_payload", JSON.toJSONString(Map.of("source", draft.getSource())));
            }
            createReq.setBaseFields(stepBase);
            Long stepId = entityService.create(createReq);
            stepCodeToId.put(draft.getStepCode(), stepId);
            stepIds.add(stepId);
        }

        TaskExecutionBootstrapRespVO resp = new TaskExecutionBootstrapRespVO();
        resp.setExecutionRecordId(req.getExecutionRecordId());
        resp.setStepIds(stepIds);
        return resp;
    }
}
