package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendPoseReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionMarkProcessReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionMarkProcessRespDTO;
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
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityCacheEvictionService;
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
 * <p><b>负责</b>：新建这次执行的账；更新这次/某一步的状态；往账里记一条过程；
 * 重新生成前物理删除还没开始的执行账、步骤和关系行，不留删除标记。
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
    @Resource
    private PendingExecutionPhysicalDeleter pendingExecutionPhysicalDeleter;
    @Resource
    private EntityCacheEvictionService entityCacheEvictionService;

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
        List<TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO> steps = toBootstrapSteps(req.getSteps());
        List<Long> recordIds = taskExecutionBootstrapService.insertPendingRecords(
                entityTypeCode,
                modelId,
                req.getFacilityId(),
                req.getTaskDefinitionId(),
                req.getStandardSnapshot(),
                req.getParameterSnapshot(),
                accounts,
                steps);
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
            record.setCustomFields(custom);
        }

        if (CollectionUtils.isEmpty(req.getStepUpdates())) {
            return;
        }
        for (TaskExecutionWritebackReqDTO.StepUpdate stepUpdate : req.getStepUpdates()) {
            applyStepUpdate(record, entityTypeCode, stepUpdate);
        }
    }

    /**
     * 重新生成待执行账时，物理删掉还没开始的旧账。
     * 步骤有上下级时整棵删掉。关系行和分类挂靠一并删掉，不留删除标记。
     * 已经开始的不删。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePendingExecution(Long executionRecordId) {
        if (executionRecordId == null) {
            throw new ServiceException(400, "执行账编号不能为空");
        }
        EntityRespVO record = entityService.get(executionRecordId, DEFAULT_RECORD_TYPE);
        if (record == null || record.getId() == null) {
            throw new ServiceException(404, "执行记录不存在：" + executionRecordId);
        }
        String status = textOf(record.getCustomFields() == null
                ? null
                : record.getCustomFields().get("execution_status"));
        if (!STATUS_PENDING.equalsIgnoreCase(status)) {
            throw new ServiceException(400, "这条执行账已经开始，重新生成时不能删除：" + executionRecordId);
        }
        Long facilityId = facilityOf(record);
        List<Long> stepIds = listStepIds(executionRecordId, facilityId);
        pendingExecutionPhysicalDeleter.delete(executionRecordId, stepIds);
        entityCacheEvictionService.evictEntityCaches(record.getModelId(), DEFAULT_RECORD_TYPE);
        ModelDO stepModel = modelMapper.selectByCode(STEP_ENTITY_TYPE);
        if (stepModel != null && stepModel.getId() != null) {
            entityCacheEvictionService.evictEntityCaches(stepModel.getId(), STEP_ENTITY_TYPE);
        }
        entityCacheEvictionService.evictEntity(executionRecordId);
        for (Long stepId : stepIds) {
            entityCacheEvictionService.evictEntity(stepId);
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
        rememberMedia(custom, req.getMediaFileUrl());
        updateReq.setCustomFields(custom);
        entityService.update(updateReq);

        TaskExecutionAppendProcessRespDTO resp = new TaskExecutionAppendProcessRespDTO();
        resp.setExecutionRecordId(record.getId());
        resp.setProcessEntryCount(entries.size());
        return resp;
    }

    /**
     * 按消息编号改已有下发事件的状态。
     * 已经是设备已响应时，回执不再把它改回设备已接收。
     * 找不到这一条就 updated 为假，不新造一行。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskExecutionMarkProcessRespDTO markProcessEvent(TaskExecutionMarkProcessReqDTO req) {
        if (req.getExecutionRecordId() == null || !StringUtils.hasText(req.getMsgId())) {
            throw new ServiceException(400, "改事件状态必须带这次执行的账本编号和消息编号");
        }
        String entityTypeCode = resolveRecordType(req.getEntityTypeCode());
        EntityRespVO record = entityService.get(req.getExecutionRecordId(), entityTypeCode);
        if (record == null || record.getId() == null) {
            throw new ServiceException(404, "执行记录不存在：" + req.getExecutionRecordId());
        }
        Map<String, Object> custom = copyMap(record.getCustomFields());
        List<Map<String, Object>> entries = readProcessEntries(custom.get(PROCESS_ENTRIES_KEY));
        Map<String, Object> target = null;
        for (int i = entries.size() - 1; i >= 0; i--) {
            Map<String, Object> entry = entries.get(i);
            if (req.getMsgId().trim().equals(String.valueOf(entry.get("msgId")).trim())) {
                target = entry;
                break;
            }
        }
        if (target == null) {
            TaskExecutionMarkProcessRespDTO missing = new TaskExecutionMarkProcessRespDTO();
            missing.setUpdated(false);
            return missing;
        }
        String current = target.get("eventStatus") == null ? "" : String.valueOf(target.get("eventStatus")).trim();
        String next = req.getEventStatus().trim();
        if ("设备已响应".equals(current) && "设备已接收".equals(next)) {
            return markProcessNotice(target, true);
        }
        target.put("eventStatus", next);
        if (StringUtils.hasText(req.getOutcome())) {
            target.put("outcome", req.getOutcome().trim());
        }
        // 同号答卷的电量、位置写回这一行。回执不带字段，不覆盖已经写下的内容。
        Map<String, Object> incoming = businessFields(req.getFields());
        if (incoming != null) {
            target.put("fields", incoming);
        }
        custom.put(PROCESS_ENTRIES_KEY, entries);
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
        updateReq.setCustomFields(custom);
        entityService.update(updateReq);
        return markProcessNotice(target, true);
    }

    /**
     * 答卷里要给页面看的字段。去掉指令阶段。没有业务字段时返回空，调用方不覆盖原行。
     */
    private static Map<String, Object> businessFields(Map<String, Object> raw) {
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        Map<String, Object> copy = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : raw.entrySet()) {
            if (entry.getKey() == null || "commandPhase".equals(entry.getKey())) {
                continue;
            }
            copy.put(entry.getKey(), entry.getValue());
        }
        return copy.isEmpty() ? null : copy;
    }

    /** 把账上这一行现在的名称和状态交出去。页面按这一行更新，不另造一份显示。 */
    private static TaskExecutionMarkProcessRespDTO markProcessNotice(Map<String, Object> target, boolean updated) {
        TaskExecutionMarkProcessRespDTO resp = new TaskExecutionMarkProcessRespDTO();
        resp.setUpdated(updated);
        resp.setMessageKind(textOrNull(target.get("messageKind")));
        resp.setSummary(textOrNull(target.get("summary")));
        resp.setEventStatus(textOrNull(target.get("eventStatus")));
        resp.setOutcome(textOrNull(target.get("outcome")));
        resp.setMsgId(textOrNull(target.get("msgId")));
        resp.setDeviceId(textOrNull(target.get("deviceId")));
        resp.setDirection(textOrNull(target.get("direction")));
        Object receivedAt = target.get("receivedAtEpochMs");
        if (receivedAt instanceof Number number) {
            resp.setReceivedAtEpochMs(number.longValue());
        }
        Object fields = target.get("fields");
        if (fields instanceof Map<?, ?> map) {
            Map<String, Object> copy = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() != null) {
                    copy.put(String.valueOf(entry.getKey()), entry.getValue());
                }
            }
            resp.setFields(copy);
        }
        return resp;
    }

    private static String textOrNull(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() || "null".equals(text) ? null : text;
    }

    /** 回放位置单独成列。过程列表仍只放指令往来。样本过多时丢掉最早的点。 */
    private static final String REPLAY_POSE_KEY = "replay_pose";
    private static final int REPLAY_POSE_LIMIT = 8000;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void appendPose(TaskExecutionAppendPoseReqDTO req) {
        if (req.getExecutionRecordId() == null) {
            throw new ServiceException(400, "记回放位置必须带这次执行的账本编号");
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
        List<Map<String, Object>> poses = readProcessEntries(custom.get(REPLAY_POSE_KEY));
        Map<String, Object> point = new LinkedHashMap<>();
        point.put("t", req.getT());
        point.put("x", req.getX());
        point.put("y", req.getY());
        point.put("z", req.getZ());
        point.put("yaw", req.getYaw() == null ? 0 : req.getYaw());
        point.put("frameRef", StringUtils.hasText(req.getFrameRef()) ? req.getFrameRef().trim() : "wgs84");
        poses.add(point);
        if (poses.size() > REPLAY_POSE_LIMIT) {
            poses.subList(0, poses.size() - REPLAY_POSE_LIMIT).clear();
        }
        custom.put(REPLAY_POSE_KEY, poses);
        updateReq.setCustomFields(custom);
        entityService.update(updateReq);
    }

    private static void rememberMedia(Map<String, Object> custom, String mediaFileUrl) {
        if (!StringUtils.hasText(mediaFileUrl)) {
            return;
        }
        Object raw = custom.get("task_patrol_record_media");
        Map<String, Object> media = new LinkedHashMap<>();
        if (raw instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() != null) {
                    media.put(String.valueOf(entry.getKey()), entry.getValue());
                }
            }
        }
        media.put("recordFileUrl", mediaFileUrl.trim());
        custom.put("task_patrol_record_media", media);
    }

    /**
     * 回写只改这笔执行账上该步骤编号的结果。
     * 步骤图里没有这一步就报错，不新建步骤行。
     */
    private void applyStepUpdate(
            EntityRespVO record, String entityTypeCode, TaskExecutionWritebackReqDTO.StepUpdate stepUpdate) {
        if (!StringUtils.hasText(stepUpdate.getStepCode())) {
            throw new ServiceException(400, "回写须带步骤编号");
        }
        if (!StringUtils.hasText(stepUpdate.getStatus())) {
            throw new ServiceException(400, "回写须带步骤状态");
        }
        Map<String, Object> custom = copyMap(record.getCustomFields());
        custom.put(
                ExecutionAccountStepTree.FIELD_STEP_RESULTS,
                ExecutionAccountStepTree.mergeResult(
                        custom,
                        stepUpdate.getStepCode(),
                        stepUpdate.getStatus(),
                        stepUpdate.getResultPayload()));
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
        updateReq.setCustomFields(custom);
        entityService.update(updateReq);
        record.setCustomFields(custom);
        if (PatrolAnalysisAlarmRecorder.isAlarm(stepUpdate.getResultPayload())) {
            Map<String, Object> alarmPayload = new LinkedHashMap<>();
            if (stepUpdate.getResultPayload() != null) {
                alarmPayload.putAll(stepUpdate.getResultPayload());
            }
            alarmPayload.put("stepCode", stepUpdate.getStepCode());
            patrolAnalysisAlarmRecorder.record(
                    record,
                    ExecutionAccountStepTree.inspectionItemTitle(custom, stepUpdate.getStepCode()),
                    alarmPayload);
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

    /** 删账前先收齐旧步骤行编号。新账没有步骤行时这里是空的。 */
    private List<Long> listStepIds(Long executionRecordId, Long facilityId) {
        List<Long> ids = new ArrayList<>();
        int pageNo = 1;
        final int pageSize = 200;
        List<FieldFilterReqVO> filters = stepListFilters(executionRecordId, facilityId);
        while (true) {
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
                    pageNo,
                    pageSize,
                    null,
                    null,
                    filters,
                    null,
                    null,
                    null,
                    null,
                    null);
            if (resp == null || resp.getPage() == null || CollectionUtils.isEmpty(resp.getPage().getList())) {
                return ids;
            }
            for (EntityRespVO row : resp.getPage().getList()) {
                if (row != null && row.getId() != null) {
                    ids.add(row.getId());
                }
            }
            if (resp.getPage().getList().size() < pageSize) {
                return ids;
            }
            pageNo++;
        }
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
        return ExecutionAccountStepTree.project(record.getCustomFields());
    }

    private static String textOf(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
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
        if (StringUtils.hasText(req.getDirection())) {
            entry.put("direction", req.getDirection().trim());
        }
        if (StringUtils.hasText(req.getPayloadJson())) {
            entry.put("payloadJson", req.getPayloadJson());
        }
        if (StringUtils.hasText(req.getEventStatus())) {
            entry.put("eventStatus", req.getEventStatus().trim());
        }
        if (req.getActionId() != null) {
            entry.put("actionId", req.getActionId());
        }
        if (StringUtils.hasText(req.getMsgId())) {
            entry.put("msgId", req.getMsgId().trim());
        }
        if (StringUtils.hasText(req.getDeviceId())) {
            entry.put("deviceId", req.getDeviceId().trim());
        }
        if (StringUtils.hasText(req.getOutcome())) {
            entry.put("outcome", req.getOutcome().trim());
        }
        if (req.getFields() != null && !req.getFields().isEmpty()) {
            entry.put("fields", req.getFields());
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
