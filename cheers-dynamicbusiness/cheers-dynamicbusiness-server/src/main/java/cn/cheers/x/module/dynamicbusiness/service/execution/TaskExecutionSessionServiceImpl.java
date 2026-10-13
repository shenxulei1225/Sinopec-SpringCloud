package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartBatchReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartBatchRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStepSnapshotDTO;
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
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeResolver;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.inspection.TaskExecutionBootstrapService;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 任务模块标准执行会话实现。
 *
 * <p><b>负责</b>：新建这次执行的账；更新这次/某一步的状态；往账里记一条过程。
 * 分析步骤写成告警时，另记一条巡检异常记录并挂到这次任务的执行设备上。</p>
 * <p><b>权威</b>：执行记录与步骤实体；过程写在 custom_fields.process_entries，其它过程字段同袋
 * （task_id / pending_execution_id / execution_status / planned_start）。</p>
 * <p><b>落账类型</b>：请求可以带数据目录编码（例如巡检执行记录这扇门）。
 * 新建、回写、记过程都先换成该目录的底座存储类型，再和型号比对、再写入。
 * 型号挂在底座上，账本行也写底座类型。</p>
 * <p><b>禁止</b>：写域任务表 device* 列；无执行记录时猜任务或设备；用日志冒充已记过程；
 * 站场级执行账缺所属场站时从请求头猜场站；拿目录编码跟型号比对。
 * 生成多条待执行时，步骤清单只写一次，禁止按计划点再各算一遍、各建一遍。</p>
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
    @Resource
    private EntityTypeScopeResolver entityTypeScopeResolver;
    @Resource
    private PatrolAnalysisAlarmRecorder patrolAnalysisAlarmRecorder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskExecutionStartRespDTO start(TaskExecutionStartReqDTO req) {
        if (req.getGapCodes() != null && !req.getGapCodes().isEmpty()) {
            throw new ServiceException(400, "存在 SOP 解析缺口，禁止开跑：" + req.getGapCodes());
        }
        if (req.getTaskDefinitionId() == null) {
            throw new ServiceException(400, "taskDefinitionId 不能为空");
        }
        String entityTypeCode = resolveRecordType(req.getEntityTypeCode());
        Long modelId = resolveModelId(req);

        EntityCreateReqVO createReq = new EntityCreateReqVO();
        Map<String, Object> base = new LinkedHashMap<>();
        base.put("entityTypeCode", entityTypeCode);
        base.put("modelId", modelId);
        base.put("name", StringUtils.hasText(req.getName())
                ? req.getName().trim()
                : "执行-" + req.getTaskDefinitionId());
        base.put("status", 1);
        if (req.getFacilityId() != null && req.getFacilityId() > 0) {
            base.put(FacilityOwningFieldCodes.FIELD_CODE, FacilityOwningFieldCodes.toApiRef(req.getFacilityId()));
        }
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
        bootstrapReq.setEntityTypeCode(entityTypeCode);
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
    public TaskExecutionStartBatchRespDTO startBatch(TaskExecutionStartBatchReqDTO req) {
        if (req.getGapCodes() != null && !req.getGapCodes().isEmpty()) {
            throw new ServiceException(400, "存在 SOP 解析缺口，禁止开跑：" + req.getGapCodes());
        }
        if (req.getTaskDefinitionId() == null) {
            throw new ServiceException(400, "taskDefinitionId 不能为空");
        }
        if (CollectionUtils.isEmpty(req.getOccurrences())) {
            throw new ServiceException(400, "没有待写入的计划点");
        }
        String entityTypeCode = resolveRecordType(req.getEntityTypeCode());
        Long modelId = resolveModelId(req.getModelId(), req.getModelCode());
        ModelDO recordModel = modelMapper.selectById(modelId);
        String recordModelCode = recordModel != null && StringUtils.hasText(recordModel.getCode())
                ? recordModel.getCode()
                : req.getModelCode();

        List<TaskExecutionBootstrapService.PendingAccount> accounts = new ArrayList<>();
        Set<String> seenRefs = new HashSet<>();
        for (TaskExecutionStartBatchReqDTO.Occurrence occurrence : req.getOccurrences()) {
            if (occurrence == null || !StringUtils.hasText(occurrence.getPendingRef())) {
                throw new ServiceException(400, "计划点编号不能为空");
            }
            String pendingRef = occurrence.getPendingRef().trim();
            if (!seenRefs.add(pendingRef)) {
                throw new ServiceException(400, "计划点重复：" + pendingRef);
            }
            accounts.add(new TaskExecutionBootstrapService.PendingAccount(
                    occurrence.getName(),
                    pendingRef,
                    StringUtils.hasText(occurrence.getPlannedStart())
                            ? occurrence.getPlannedStart().trim()
                            : null));
        }
        List<Long> recordIds = taskExecutionBootstrapService.insertPendingRecords(
                entityTypeCode,
                modelId,
                req.getFacilityId(),
                req.getTaskDefinitionId(),
                req.getStandardSnapshot(),
                req.getParameterSnapshot(),
                accounts);
        if (recordIds == null || recordIds.size() != accounts.size()) {
            throw new ServiceException(500, "待执行账写入数量与计划点不一致");
        }
        List<TaskExecutionStartBatchRespDTO.RecordRef> refs = new ArrayList<>();
        for (int i = 0; i < accounts.size(); i++) {
            TaskExecutionStartBatchRespDTO.RecordRef ref = new TaskExecutionStartBatchRespDTO.RecordRef();
            ref.setPendingRef(accounts.get(i).pendingRef());
            ref.setExecutionRecordId(recordIds.get(i));
            refs.add(ref);
        }
        taskExecutionBootstrapService.writeSameStepsOntoRecords(
                entityTypeCode,
                recordModelCode,
                req.getFacilityId(),
                recordIds,
                toBootstrapSteps(req.getSteps()));
        TaskExecutionStartBatchRespDTO resp = new TaskExecutionStartBatchRespDTO();
        resp.setRecords(refs);
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
        Long facilityId = facilityOf(record);
        for (TaskExecutionWritebackReqDTO.StepUpdate stepUpdate : req.getStepUpdates()) {
            applyStepUpdate(record, facilityId, stepUpdate);
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

    private void applyStepUpdate(
            EntityRespVO record, Long facilityId, TaskExecutionWritebackReqDTO.StepUpdate stepUpdate) {
        Long executionRecordId = record.getId();
        Long stepId = stepUpdate.getStepId();
        if (stepId == null) {
            if (!StringUtils.hasText(stepUpdate.getStepCode())) {
                throw new ServiceException(400, "步骤更新须提供 stepId 或 stepCode");
            }
            stepId = findStepIdByCode(executionRecordId, facilityId, stepUpdate.getStepCode().trim());
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
        if (PatrolAnalysisAlarmRecorder.isAlarm(stepUpdate.getResultPayload())) {
            patrolAnalysisAlarmRecorder.record(record, step, stepUpdate.getResultPayload());
        }
    }

    /**
     * 执行步骤是站场级。用这条执行记录上已经写下的所属场站来列，再按步骤编号对上。
     * 没有所属场站就停下，不扫全租户。
     */
    private List<FieldFilterReqVO> stepListFilters(Long executionRecordId, Long facilityId) {
        if (facilityId == null || facilityId <= 0) {
            throw new ServiceException(400, "这条执行记录没有所属场站，不能对回步骤");
        }
        List<FieldFilterReqVO> filters = new ArrayList<>();
        FieldFilterReqVO byFacility = new FieldFilterReqVO();
        byFacility.setFieldCode(FacilityOwningFieldCodes.FIELD_CODE);
        byFacility.setOp("EQ");
        byFacility.setValue(facilityId);
        filters.add(byFacility);
        FieldFilterReqVO byRecord = new FieldFilterReqVO();
        byRecord.setFieldCode("execution_record_id");
        byRecord.setOp("EQ");
        byRecord.setValue(executionRecordId);
        filters.add(byRecord);
        return filters;
    }

    private static Long facilityOf(EntityRespVO record) {
        if (record == null || record.getBaseFields() == null) {
            return null;
        }
        return FacilityOwningFieldCodes.extractId(
                record.getBaseFields().get(FacilityOwningFieldCodes.FIELD_CODE));
    }

    private Long findStepIdByCode(Long executionRecordId, Long facilityId, String stepCode) {
        List<FieldFilterReqVO> filters = stepListFilters(executionRecordId, facilityId);
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

    @Override
    public List<TaskExecutionStepSnapshotDTO> listSteps(Long executionRecordId) {
        if (executionRecordId == null) {
            return List.of();
        }
        EntityRespVO record = entityService.get(executionRecordId, DEFAULT_RECORD_TYPE);
        if (record == null || record.getId() == null) {
            throw new ServiceException(404, "执行记录不存在：" + executionRecordId);
        }
        List<FieldFilterReqVO> filters = stepListFilters(executionRecordId, facilityOf(record));
        EntitySceneQueryRespVO resp = entityService.queryEntities(
                EntityQueryScene.ENTITIES_BY_MODEL,
                "PAGE",
                "FULL",
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
                500,
                null,
                null,
                filters,
                null,
                null,
                null,
                null,
                null);
        if (resp == null || resp.getPage() == null || CollectionUtils.isEmpty(resp.getPage().getList())) {
            return List.of();
        }
        List<TaskExecutionStepSnapshotDTO> steps = new ArrayList<>();
        for (EntityRespVO row : resp.getPage().getList()) {
            if (row == null || row.getId() == null) {
                continue;
            }
            steps.add(toStepSnapshot(row));
        }
        return steps;
    }

    private static TaskExecutionStepSnapshotDTO toStepSnapshot(EntityRespVO row) {
        Map<String, Object> base = row.getBaseFields() == null ? Map.of() : row.getBaseFields();
        TaskExecutionStepSnapshotDTO snapshot = new TaskExecutionStepSnapshotDTO();
        snapshot.setId(row.getId());
        snapshot.setStepCode(textOf(base.get("step_code")));
        snapshot.setStepTitle(textOf(base.get("step_title")));
        snapshot.setStepStatus(textOf(base.get("step_status")));
        Long parentId = longOf(base.get("parentId"));
        if (parentId == null) {
            parentId = longOf(base.get("parent_id"));
        }
        snapshot.setParentId(parentId);
        Map<String, Object> resultPayload = payloadOf(base.get("result_payload"));
        snapshot.setResultPayload(resultPayload);
        snapshot.setSource(payloadOf(resultPayload.get("source")));
        return snapshot;
    }

    private static String textOf(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    private static Long longOf(Object raw) {
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw instanceof Map<?, ?> map) {
            return longOf(map.get("id"));
        }
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                return Long.parseLong(text.trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private static Map<String, Object> payloadOf(Object raw) {
        if (raw instanceof Map<?, ?> map) {
            Map<String, Object> copy = new LinkedHashMap<>();
            map.forEach((key, value) -> {
                if (key != null) {
                    copy.put(String.valueOf(key), value);
                }
            });
            return copy;
        }
        if (raw instanceof String text && StringUtils.hasText(text)) {
            Object parsed = JSON.parse(text);
            if (parsed instanceof Map<?, ?> map) {
                return payloadOf(map);
            }
        }
        return Map.of();
    }

    /**
     * 执行账落在底座存储类型上。
     * 传入巡检执行记录这扇门时，换成底座任务执行记录再读写，避免目录编码和型号对不上。
     */
    private String resolveRecordType(String entityTypeCode) {
        String raw = StringUtils.hasText(entityTypeCode) ? entityTypeCode.trim() : DEFAULT_RECORD_TYPE;
        String storage = entityTypeScopeResolver.resolveStorageEntityTypeCode(raw);
        if (!StringUtils.hasText(storage)) {
            throw new ServiceException(400, "执行记录类型无法落到存储类型：" + raw);
        }
        return storage.trim();
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
        return resolveModelId(req.getModelId(), req.getModelCode());
    }

    private Long resolveModelId(Long modelId, String modelCode) {
        if (modelId != null) {
            return modelId;
        }
        if (!StringUtils.hasText(modelCode)) {
            throw new ServiceException(400, "须提供 modelId 或 modelCode");
        }
        ModelDO model = modelMapper.selectByCode(modelCode.trim());
        if (model == null || model.getId() == null) {
            throw new ServiceException(500, "缺少执行记录型号：" + modelCode);
        }
        return model.getId();
    }

    /**
     * 新建一条待执行账，并带上这批共用的快照。
     * 所属场站只收调用方已经写下的场站，不从请求头猜。
     */
    private Long createPendingRecord(
            String entityTypeCode,
            Long modelId,
            Long taskDefinitionId,
            Long facilityId,
            String name,
            String pendingRef,
            Object standardSnapshot,
            Object parameterSnapshot) {
        EntityCreateReqVO createReq = new EntityCreateReqVO();
        Map<String, Object> base = new LinkedHashMap<>();
        base.put("entityTypeCode", entityTypeCode);
        base.put("modelId", modelId);
        base.put("name", StringUtils.hasText(name) ? name.trim() : "执行-" + taskDefinitionId);
        base.put("status", 1);
        if (facilityId != null && facilityId > 0) {
            base.put(FacilityOwningFieldCodes.FIELD_CODE, FacilityOwningFieldCodes.toApiRef(facilityId));
        }
        createReq.setBaseFields(base);

        Map<String, Object> custom = new LinkedHashMap<>();
        custom.put("task_id", taskDefinitionId);
        custom.put("pending_execution_id", pendingRef);
        custom.put("execution_status", STATUS_PENDING);
        custom.put("standard_snapshot", standardSnapshot);
        if (parameterSnapshot != null) {
            custom.put("parameter_snapshot", parameterSnapshot);
        }
        custom.put("gap_codes", List.of());
        createReq.setCustomFields(custom);
        return entityService.create(createReq);
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
