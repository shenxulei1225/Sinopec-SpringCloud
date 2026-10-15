package cn.cheers.x.module.dynamicbusiness.service.inspection;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapRespVO;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.execution.ExecutionAccountStepTree;
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
 * <p><b>负责</b>：把快照和当时的步骤图写进这一笔执行账。</p>
 * <p><b>权威</b>：步骤图写入执行账 {@code step_tree_json}；逐步结果写入同一笔账的 {@code step_results}；
 * 快照写入 {@code standard_snapshot}/{@code parameter_snapshot}/{@code gap_codes}。</p>
 * <p><b>不负责</b>：复合动作展开、SOP merge（调用方先展开再传 steps）。不另建步骤行。</p>
 * <p><b>禁止</b>：在此调用路径/算路服务；有 gapCodes 时拒绝开跑；父步骤还没出现时静默当根；
 * 缺步骤图时留空账。所属场站只认这笔执行账上已经写下的那一个。</p>
 */
@Service
public class TaskExecutionBootstrapServiceImpl implements TaskExecutionBootstrapService {

    public static final String EXECUTION_RECORD_TYPE = "task_excution_record";

    @Resource
    private EntityService entityService;

    @Resource
    private TaskExecutionStepBatchWriter taskExecutionStepBatchWriter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskExecutionBootstrapRespVO bootstrap(TaskExecutionBootstrapReqVO req) {
        if (req.getGapCodes() != null && !req.getGapCodes().isEmpty()) {
            throw new ServiceException(400, "存在 SOP 解析缺口，禁止开跑：" + req.getGapCodes());
        }
        String recordType = StringUtils.hasText(req.getEntityTypeCode())
                ? req.getEntityTypeCode().trim()
                : EXECUTION_RECORD_TYPE;
        EntityRespVO record = entityService.get(req.getExecutionRecordId(), recordType);
        if (record == null || record.getId() == null) {
            throw new ServiceException(404, "执行记录不存在：" + req.getExecutionRecordId());
        }

        List<Map<String, Object>> stepTree = ExecutionAccountStepTree.freeze(req.getSteps());
        Map<String, Object> custom = new LinkedHashMap<>();
        if (record.getCustomFields() != null) {
            custom.putAll(record.getCustomFields());
        }
        custom.put("standard_snapshot", req.getStandardSnapshot());
        if (req.getParameterSnapshot() != null) {
            custom.put("parameter_snapshot", req.getParameterSnapshot());
        }
        custom.put("gap_codes", List.of());
        ExecutionAccountStepTree.putFrozen(custom, stepTree);
        EntityUpdateReqVO updateReq = new EntityUpdateReqVO();
        updateReq.setId(record.getId());
        Map<String, Object> base = new LinkedHashMap<>();
        if (record.getBaseFields() != null) {
            base.putAll(record.getBaseFields());
        }
        base.put("entityTypeCode", recordType);
        base.put("modelId", record.getModelId());
        if (record.getName() != null) {
            base.put("name", record.getName());
        }
        updateReq.setBaseFields(base);
        updateReq.setCustomFields(custom);
        entityService.update(updateReq);

        TaskExecutionBootstrapRespVO resp = new TaskExecutionBootstrapRespVO();
        resp.setExecutionRecordId(req.getExecutionRecordId());
        List<Long> stepIds = new ArrayList<>();
        for (int i = 1; i <= stepTree.size(); i++) {
            stepIds.add((long) i);
        }
        resp.setStepIds(stepIds);
        return resp;
    }

    @Override
    public void writeSameStepsOntoRecords(
            String recordType,
            String recordModelCode,
            Long facilityId,
            List<Long> executionRecordIds,
            List<TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO> steps) {
        if (executionRecordIds == null || executionRecordIds.isEmpty()) {
            throw new ServiceException(400, "没有待写入的执行账");
        }
        List<Map<String, Object>> stepTree = ExecutionAccountStepTree.freeze(steps);
        for (Long recordId : executionRecordIds) {
            EntityRespVO record = entityService.get(recordId, recordType);
            if (record == null || record.getId() == null) {
                throw new ServiceException(404, "执行记录不存在：" + recordId);
            }
            Map<String, Object> custom = new LinkedHashMap<>();
            if (record.getCustomFields() != null) {
                custom.putAll(record.getCustomFields());
            }
            ExecutionAccountStepTree.putFrozen(custom, stepTree);
            EntityUpdateReqVO updateReq = new EntityUpdateReqVO();
            updateReq.setId(record.getId());
            Map<String, Object> base = new LinkedHashMap<>();
            if (record.getBaseFields() != null) {
                base.putAll(record.getBaseFields());
            }
            base.put("entityTypeCode", recordType);
            if (record.getModelId() != null) {
                base.put("modelId", record.getModelId());
            }
            if (record.getName() != null) {
                base.put("name", record.getName());
            }
            updateReq.setBaseFields(base);
            updateReq.setCustomFields(custom);
            entityService.update(updateReq);
        }
    }

    @Override
    public List<Long> insertPendingRecords(
            String recordType,
            Long modelId,
            Long facilityId,
            Long taskDefinitionId,
            Object standardSnapshot,
            Object parameterSnapshot,
            List<PendingAccount> accounts,
            List<TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO> steps) {
        return taskExecutionStepBatchWriter.insertPendingRecords(
                recordType, modelId, facilityId, taskDefinitionId,
                standardSnapshot, parameterSnapshot, accounts, steps);
    }
}
