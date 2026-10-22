package cn.cheers.x.inspection.task.service.execution.openrun;

import cn.cheers.x.device.protocolgateway.api.dto.MissionDownlinkPieceDTO;
import cn.cheers.x.device.protocolgateway.api.dto.MissionTranslateCommitRespDTO;
import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.service.execution.steptree.PatrolTaskStepTreeReader;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepNode;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeAssembler;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeParser;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.inspection.task.service.task.Go2RtcStreamRegistrar;
import cn.cheers.x.inspection.task.service.task.PatrolTaskRecordIngestAllocator;
import cn.cheers.x.inspection.task.service.task.TaskPatrolRecordingSupport;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityWriteReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartBatchReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartBatchRespDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cn.cheers.x.inspection.task.service.execution.impl.InspectionDeviceUplinkServiceImpl.PATROL_RECORD_TYPE;
import static cn.cheers.x.inspection.task.service.execution.impl.InspectionTaskStartExecutionServiceImpl.PATROL_EXEC_MODEL_CODE;
import static cn.cheers.x.inspection.task.service.execution.openrun.PatrolOpenRunSnapshotSupport.SOURCE_MATERIALIZE;

/**
 * 生成任务占窗定稿后写「待执行」：把步骤树上已经写下的参数写入执行账。执行任务只读这份账，不再读设备。
 * <p>负责：assemble（读步骤图上的参数）；一次写下本批还没有账的计划点；时刻和账的对照一次写回任务。
 * 名称用任务名加计划执行时刻。计划时刻按北京时间写入账上的 planned_start，并带上 +08:00，不把世界时间的钟面写进去。
 * 更新任务时先删除这条任务上还没开始的旧执行账和步骤，再写下新的；已经开始或已经执行的留下。
 * <p>生成任务时调用网关 commit-translate，把下行与航线编号写入 standard_snapshot；开跑只下发。
 * 东古、机器人在写下待执行账之前，把本批收流名字一次登记到流媒体。
 * 更新任务时，这条任务以前登记、这次计划点不再使用的名字先从流媒体拿掉。已经开始或已经执行的名字留下。登记失败则不写账。
 * <p>禁止：执行任务时再读设备参数重算；把协议 opcode 写进步骤树；
 * 待执行账所属场站从请求头猜，不写总任务已有的场站；名称里写计划点编号冒充给用户看的记录名。
 */
@Service
@RequiredArgsConstructor
public class PatrolOpenRunMaterializeService {

    /** 与 dynamicbusiness 执行账 custom_fields.execution_status 取值对齐，禁止跨模块依赖 server 实现类 */
    private static final String EXECUTION_STATUS_PENDING = "pending";
    /** 与 {@link PatrolScheduleSlotExecutionWritebackService} 写入字段一致 */
    private static final String PENDING_EXECUTION_ID_FIELD = "pending_execution_id";
    /** 与业务表上的钟点一致：北京时间，文字里带 +08:00。 */
    private static final ZoneId PLAN_ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter EXECUTION_NAME_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    /** 账上的计划时刻：到秒，并带 +08:00。 */
    private static final DateTimeFormatter STORED_PLAN_START =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final PatrolTaskStepTreeReader taskStepTreeCatalog;
    private final TaskExecutionSessionApi taskExecutionSessionApi;
    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;
    private final PatrolMissionTranslateService patrolMissionTranslateService;
    private final PatrolTaskRecordIngestAllocator patrolTaskRecordIngestAllocator;
    private final Go2RtcStreamRegistrar go2RtcStreamRegistrar;

    /**
     * 生成任务成功后，为本批计划时刻写待执行账。
     * 先删除还没开始的旧执行账。指令和航线只正式翻译一次，写入每个新执行点。
     * 东古、机器人仍按计划点分配收流地址，并写进该点快照和报文，不再按点重译。
     * 已经开始或已经执行的计划点不删除、不重写。
     */
    public void materializeAllSlots(PatrolTaskDraft task, List<ScheduleSlotDTO> slots) {
        if (task == null || task.id() == null || CollectionUtils.isEmpty(slots)) {
            return;
        }
        deleteUnexecutedPlansBeforeReplace(task);
        List<ScheduleSlotDTO> pendingSlots = new ArrayList<>();
        for (ScheduleSlotDTO slot : slots) {
            if (slot == null || !StringUtils.hasText(slot.getSlotId())) {
                continue;
            }
            String slotId = slot.getSlotId().trim();
            if (alreadyPending(task.id(), slotId)) {
                continue;
            }
            if (!StringUtils.hasText(slot.getPlannedStart())) {
                throw ServiceExceptionUtil.invalidParamException("计划点缺少计划执行时间，不能生成待执行");
            }
            pendingSlots.add(slot);
        }
        if (pendingSlots.isEmpty()) {
            return;
        }
        pendingSlots.sort(Comparator.comparing(slot -> plannedDateTime(slot.getPlannedStart())));
        if (task.facilityId() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务没有所属场站，不能生成待执行");
        }
        TaskStepTreeAssembler.AssembledOpenRun assembled = assembleForTask(task);
        ExecutionDeviceBinding binding = requireCompleteBinding(task);
        boolean serverIngest = TaskPatrolRecordingSupport.usesServerIngest(binding.getProtocolCode());
        String translatedIngestUrl = serverIngest
                ? patrolTaskRecordIngestAllocator.allocate(task.id(), pendingSlots.get(0).getSlotId().trim())
                : null;
        MissionTranslateCommitRespDTO committed = patrolMissionTranslateService.commitForMaterialize(
                task,
                PatrolMissionTranslateService.materializeStorageKey(task.id()),
                translatedIngestUrl);
        Map<String, Long> written = serverIngest
                ? materializeIngestSnapshots(task, pendingSlots, binding, assembled, committed, translatedIngestUrl)
                : materializeSlotsSharedSnapshot(task, pendingSlots, binding, assembled, committed);
        patrolTaskEntityStore.putPendingExecutionRecordIds(task.id(), written);
    }

    /**
     * 东古、机器人：指令只翻译一次。每个执行点仍有自己的收流地址。
     * 先把这条任务不再使用的旧收流名字从流媒体拿掉，再登记本批计划点。已经开始或已经执行的名字留下。
     * 然后把已翻译报文里的那一个地址换成该点的地址。不重新翻译，也不另生成航线。
     */
    private Map<String, Long> materializeIngestSnapshots(
            PatrolTaskDraft task,
            List<ScheduleSlotDTO> pendingSlots,
            ExecutionDeviceBinding binding,
            TaskStepTreeAssembler.AssembledOpenRun assembled,
            MissionTranslateCommitRespDTO committed,
            String translatedIngestUrl) {
        List<String> streamNames = new ArrayList<>();
        for (ScheduleSlotDTO slot : pendingSlots) {
            streamNames.add(patrolTaskRecordIngestAllocator.streamName(task.id(), slot.getSlotId().trim()));
        }
        streamNames.addAll(streamNamesStillInUse(task.id()));
        go2RtcStreamRegistrar.replaceTaskReceiveSlots(task.id(), streamNames);
        Map<String, Long> written = new LinkedHashMap<>();
        for (ScheduleSlotDTO slot : pendingSlots) {
            String slotId = slot.getSlotId().trim();
            String ingestUrl = patrolTaskRecordIngestAllocator.allocate(task.id(), slotId);
            String streamName = patrolTaskRecordIngestAllocator.streamName(task.id(), slotId);
            Map<String, Object> extras = Map.of(
                    TaskPatrolRecordingSupport.SNAPSHOT_TASK_RECORD_INGEST_URL, ingestUrl,
                    TaskPatrolRecordingSupport.SNAPSHOT_TASK_RECORD_STREAM_NAME, streamName);
            written.put(slotId, startSinglePendingOccurrence(
                    task,
                    slot,
                    binding,
                    assembled,
                    assembled.dispatchActions(),
                    retargetIngest(committed, translatedIngestUrl, ingestUrl),
                    extras));
        }
        return written;
    }

    /** 各执行点检查内容相同，一次翻译的结果写入本批每一条待执行账。 */
    private Map<String, Long> materializeSlotsSharedSnapshot(
            PatrolTaskDraft task,
            List<ScheduleSlotDTO> pendingSlots,
            ExecutionDeviceBinding binding,
            TaskStepTreeAssembler.AssembledOpenRun assembled,
            MissionTranslateCommitRespDTO committedDownlink) {
        TaskExecutionStartBatchReqDTO req = new TaskExecutionStartBatchReqDTO();
        req.setTaskDefinitionId(task.id());
        req.setEntityTypeCode(PATROL_RECORD_TYPE);
        req.setModelCode(PATROL_EXEC_MODEL_CODE);
        req.setFacilityId(task.facilityId());
        req.setStandardSnapshot(PatrolOpenRunSnapshotSupport.buildStandardSnapshot(
                task.id(), binding, assembled, SOURCE_MATERIALIZE, committedDownlink));
        req.setSteps(assembled.sessionSteps());
        List<TaskExecutionStartBatchReqDTO.Occurrence> occurrences = new ArrayList<>();
        for (ScheduleSlotDTO slot : pendingSlots) {
            occurrences.add(toOccurrence(task, slot));
        }
        req.setOccurrences(occurrences);
        return mergeBatchStartResult(req, pendingSlots.size());
    }

    /**
     * 已翻译报文里的收流地址换成这个执行点的地址。
     * 地址相同，或报文里没有这个地址时，沿用原报文。
     */
    private static MissionTranslateCommitRespDTO retargetIngest(
            MissionTranslateCommitRespDTO committed, String translatedUrl, String slotUrl) {
        if (committed == null
                || !StringUtils.hasText(translatedUrl)
                || !StringUtils.hasText(slotUrl)
                || translatedUrl.equals(slotUrl)
                || committed.pieces() == null) {
            return committed;
        }
        List<MissionDownlinkPieceDTO> pieces = new ArrayList<>();
        for (MissionDownlinkPieceDTO piece : committed.pieces()) {
            if (piece == null) {
                continue;
            }
            pieces.add(new MissionDownlinkPieceDTO(
                    piece.kind(),
                    piece.opcode(),
                    piece.msgId(),
                    replaceIngestUrl(piece.wireJson(), translatedUrl, slotUrl),
                    piece.mqttTopic(),
                    piece.deviceId(),
                    piece.templateId(),
                    replaceIngestUrl(piece.packagesJson(), translatedUrl, slotUrl)));
        }
        return new MissionTranslateCommitRespDTO(
                pieces, committed.waylineFileId(), committed.waylineFingerprint());
    }

    private static String replaceIngestUrl(String text, String translatedUrl, String slotUrl) {
        if (text == null || !text.contains(translatedUrl)) {
            return text;
        }
        return text.replace(translatedUrl, slotUrl);
    }

    private Long startSinglePendingOccurrence(
            PatrolTaskDraft task,
            ScheduleSlotDTO slot,
            ExecutionDeviceBinding binding,
            TaskStepTreeAssembler.AssembledOpenRun assembled,
            List<DispatchAction> dispatchActions,
            MissionTranslateCommitRespDTO committedDownlink,
            Map<String, Object> extraSnapshotFields) {
        TaskExecutionStartBatchReqDTO req = new TaskExecutionStartBatchReqDTO();
        req.setTaskDefinitionId(task.id());
        req.setEntityTypeCode(PATROL_RECORD_TYPE);
        req.setModelCode(PATROL_EXEC_MODEL_CODE);
        req.setFacilityId(task.facilityId());
        req.setStandardSnapshot(PatrolOpenRunSnapshotSupport.buildStandardSnapshot(
                task.id(),
                binding,
                assembled,
                dispatchActions,
                SOURCE_MATERIALIZE,
                committedDownlink,
                extraSnapshotFields));
        req.setSteps(assembled.sessionSteps());
        req.setOccurrences(List.of(toOccurrence(task, slot)));
        Map<String, Long> one = mergeBatchStartResult(req, 1);
        return one.values().iterator().next();
    }

    private Map<String, Long> mergeBatchStartResult(TaskExecutionStartBatchReqDTO req, int expectedCount) {
        CommonResult<TaskExecutionStartBatchRespDTO> started = taskExecutionSessionApi.startBatch(req);
        if (started == null || !started.isSuccess() || started.getData() == null
                || CollectionUtils.isEmpty(started.getData().getRecords())) {
            throw ServiceExceptionUtil.invalidParamException(
                    started != null && StringUtils.hasText(started.getMsg())
                            ? started.getMsg()
                            : "物化待执行执行账失败");
        }
        Map<String, Long> written = new LinkedHashMap<>();
        for (TaskExecutionStartBatchRespDTO.RecordRef ref : started.getData().getRecords()) {
            if (ref == null || !StringUtils.hasText(ref.getPendingRef()) || ref.getExecutionRecordId() == null) {
                throw ServiceExceptionUtil.invalidParamException("物化待执行执行账失败");
            }
            written.put(ref.getPendingRef().trim(), ref.getExecutionRecordId());
        }
        if (written.size() != expectedCount) {
            throw ServiceExceptionUtil.invalidParamException("物化待执行执行账数量与计划点不一致");
        }
        return written;
    }

    private static TaskExecutionStartBatchReqDTO.Occurrence toOccurrence(PatrolTaskDraft task, ScheduleSlotDTO slot) {
        String slotId = slot.getSlotId().trim();
        String plannedStart = storedPlanStart(slot.getPlannedStart());
        TaskExecutionStartBatchReqDTO.Occurrence occurrence = new TaskExecutionStartBatchReqDTO.Occurrence();
        occurrence.setPendingRef(slotId);
        occurrence.setPlannedStart(plannedStart);
        occurrence.setName(buildExecutionName(task, plannedStart));
        return occurrence;
    }

    /**
     * 已经开始或已经执行的计划点，收流名字还要留在流媒体上。
     * 还没开始的、以及已经取消的，不放进留下名单。它们的名字由这次登记从流媒体拿掉。
     */
    private List<String> streamNamesStillInUse(Long taskId) {
        Map<String, Long> existing = patrolTaskEntityStore.pendingExecutionRecordIds(taskId);
        List<String> names = new ArrayList<>();
        if (existing == null || existing.isEmpty()) {
            return names;
        }
        for (Long recordId : existing.values()) {
            EntityRespDTO record = optionalRecord(recordId);
            if (record == null || !keepsRegisteredStream(record)) {
                continue;
            }
            collectStreamName(names, record.getCustomFields().get("standard_snapshot"),
                    TaskPatrolRecordingSupport.SNAPSHOT_TASK_RECORD_STREAM_NAME);
            collectStreamName(names, record.getCustomFields().get(
                    TaskPatrolRecordingSupport.CUSTOM_FIELD_TASK_PATROL_RECORD_MEDIA), "streamName");
        }
        return names;
    }

    /** 待执行和已取消的计划点不再占用收流名字。已经开始、已经完成的留下。 */
    private static boolean keepsRegisteredStream(EntityRespDTO record) {
        if (record.getCustomFields() == null) {
            return false;
        }
        String status = text(record.getCustomFields().get("execution_status"));
        if (!StringUtils.hasText(status)) {
            return false;
        }
        String normalized = status.trim().toLowerCase();
        return !"pending".equals(normalized) && !"cancelled".equals(normalized);
    }

    private void collectStreamName(List<String> names, Object carrier, String key) {
        String value = nestedText(carrier, key);
        if (!StringUtils.hasText(value) || names.contains(value)) {
            return;
        }
        names.add(value);
    }

    private String nestedText(Object carrier, String key) {
        if (carrier instanceof Map<?, ?> map) {
            Object raw = map.get(key);
            return raw == null ? "" : String.valueOf(raw).trim();
        }
        if (!(carrier instanceof String text) || !StringUtils.hasText(text)) {
            return "";
        }
        try {
            com.fasterxml.jackson.databind.JsonNode node = objectMapper.readTree(text);
            if (node == null || !node.has(key) || node.get(key).isNull()) {
                return "";
            }
            return node.get(key).asText("").trim();
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 再次生成待执行账之前，删掉这条任务上还没开始的旧执行账。
     * 已经开始或已经执行的留下。
     */
    private void deleteUnexecutedPlansBeforeReplace(PatrolTaskDraft task) {
        Map<String, Long> existing = patrolTaskEntityStore.pendingExecutionRecordIds(task.id());
        if (existing == null || existing.isEmpty()) {
            return;
        }
        List<String> removedSlots = new ArrayList<>();
        for (Map.Entry<String, Long> entry : existing.entrySet()) {
            EntityRespDTO record = requirePendingRecord(entry.getValue());
            if (!isPending(record)) {
                continue;
            }
            CommonResult<Boolean> deleted = taskExecutionSessionApi.deletePending(entry.getValue());
            if (deleted == null || !deleted.isSuccess()) {
                throw ServiceExceptionUtil.invalidParamException(
                        deleted != null && StringUtils.hasText(deleted.getMsg())
                                ? deleted.getMsg()
                                : "删除原待执行账失败");
            }
            removedSlots.add(entry.getKey());
        }
        patrolTaskEntityStore.dropPendingExecutionSlots(task.id(), removedSlots);
    }

    private EntityRespDTO requirePendingRecord(Long recordId) {
        CommonResult<EntityRespDTO> result = entityRpcApi.getEntity(recordId, PATROL_RECORD_TYPE);
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException("原计划点的待执行账不存在：" + recordId);
        }
        return result.getData();
    }

    private boolean alreadyPending(Long taskId, String slotId) {
        Long existing = patrolTaskEntityStore.pendingExecutionRecordId(taskId, slotId);
        if (existing == null) {
            return false;
        }
        EntityRespDTO record = optionalRecord(existing);
        return record != null && isPending(record);
    }

    /**
     * 执行任务前读取生成任务时写下的待执行快照。没有这份快照就报错，不在执行时再读设备参数现算。
     */
    public FrozenOpenRun requireFrozen(PatrolTaskDraft task, String slotId) {
        if (task == null || task.id() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务不存在");
        }
        if (!StringUtils.hasText(slotId)) {
            throw ServiceExceptionUtil.invalidParamException("执行任务必须指定计划点，或请先生成任务");
        }
        Long recordId = patrolTaskEntityStore.pendingExecutionRecordId(task.id(), slotId.trim());
        if (recordId == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    "该计划点尚未准备好待执行记录，请先生成任务");
        }
        EntityRespDTO record = entityRpcApi.getEntity(recordId, PATROL_RECORD_TYPE).getCheckedData();
        if (record == null || record.getCustomFields() == null) {
            throw ServiceExceptionUtil.invalidParamException("待执行执行账不存在：" + recordId);
        }
        String status = text(record.getCustomFields().get("execution_status"));
        if (!EXECUTION_STATUS_PENDING.equalsIgnoreCase(status)) {
            throw ServiceExceptionUtil.invalidParamException(
                    "该计划点对应的执行账已不是待执行状态，不能重复开跑");
        }
        Object pendingRef = record.getCustomFields().get(PENDING_EXECUTION_ID_FIELD);
        if (pendingRef != null && !slotId.trim().equals(String.valueOf(pendingRef).trim())) {
            throw ServiceExceptionUtil.invalidParamException("待执行执行账与计划点不匹配");
        }
        Object standardSnapshot = record.getCustomFields().get("standard_snapshot");
        List<DispatchAction> dispatchActions = PatrolOpenRunSnapshotSupport.parseDispatchActions(standardSnapshot);
        ExecutionDeviceBinding binding = requireCompleteBinding(task);
        return new FrozenOpenRun(recordId, binding, dispatchActions, standardSnapshot);
    }

    /**
     * 大疆这一次的航线文件编号和内容签名写回待执行账的快照。
     * 字段与生成任务时写入的 waylineFileId、waylineFingerprint 相同。
     * 东古和机器人没有这份文件，不调用。
     * 不改步骤树，不重算动作。
     */
    public void rememberWaylineFile(Long recordId, String fileId, String fingerprint) {
        if (recordId == null || !StringUtils.hasText(fileId) || !StringUtils.hasText(fingerprint)) {
            throw ServiceExceptionUtil.invalidParamException("航线文件编号和签名都要写回执行账");
        }
        EntityRespDTO record = entityRpcApi.getEntity(recordId, PATROL_RECORD_TYPE).getCheckedData();
        if (record == null || record.getCustomFields() == null) {
            throw ServiceExceptionUtil.invalidParamException("待执行执行账不存在：" + recordId);
        }
        Object raw = record.getCustomFields().get("standard_snapshot");
        Map<String, Object> snapshot = raw instanceof Map<?, ?> map ? new LinkedHashMap<>() : new LinkedHashMap<>();
        if (raw instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() != null) {
                    snapshot.put(String.valueOf(entry.getKey()), entry.getValue());
                }
            }
        }
        snapshot.put(PatrolOpenRunSnapshotSupport.KEY_WAYLINE_FILE_ID, fileId.trim());
        snapshot.put(PatrolOpenRunSnapshotSupport.KEY_WAYLINE_FINGERPRINT, fingerprint.trim());
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setId(recordId);
        write.setEntityTypeCode(PATROL_RECORD_TYPE);
        write.setModelCode(PATROL_EXEC_MODEL_CODE);
        write.setFields(Map.of("standard_snapshot", snapshot));
        CommonResult<Boolean> result = entityRpcApi.updateEntityFields(write);
        if (result == null || !result.isSuccess()) {
            throw ServiceExceptionUtil.invalidParamException("航线文件编号没有写入执行账");
        }
    }

    TaskStepTreeAssembler.AssembledOpenRun assembleForTask(PatrolTaskDraft task) {
        // previewOrchestration 刚 generate 并 writeStepTree 后传入的 draft，优先消费内存态，避免二次 RPC 读不到刚写入的树
        List<TaskStepNode> nodes = TaskStepTreeParser.parse(task.stepTree(), objectMapper);
        if (nodes.isEmpty()) {
            nodes = taskStepTreeCatalog.loadPatrolTaskStepTree(task.id());
        }
        String protocol = task.executionDeviceBinding() == null
                ? null
                : task.executionDeviceBinding().getProtocolCode();
        return TaskStepTreeAssembler.assemble(nodes, taskStepTreeCatalog::resolveActionId, protocol);
    }

    private static String buildExecutionName(PatrolTaskDraft task, String plannedStart) {
        String base = StringUtils.hasText(task.name()) ? task.name().trim() : "巡检执行-" + task.id();
        return base + " " + plannedDateTime(plannedStart).format(EXECUTION_NAME_TIME);
    }

    /**
     * 计划点上的计划执行时间，收成北京时间的钟面。缺或格式不对就停。
     * 带 Z 的世界时间先换成同一时刻的北京时间，不拿 01:00 这种钟面当计划时刻。
     */
    private static LocalDateTime plannedDateTime(String plannedStart) {
        return planOffset(plannedStart).toLocalDateTime();
    }

    /**
     * 写入执行账的计划时刻：北京时间钟点，并带 +08:00。
     */
    private static String storedPlanStart(String plannedStart) {
        return STORED_PLAN_START.format(planOffset(plannedStart));
    }

    private static OffsetDateTime planOffset(String plannedStart) {
        if (!StringUtils.hasText(plannedStart)) {
            throw ServiceExceptionUtil.invalidParamException("计划点缺少计划执行时间，不能生成待执行");
        }
        String text = plannedStart.trim();
        try {
            return OffsetDateTime.parse(text).atZoneSameInstant(PLAN_ZONE).toOffsetDateTime();
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDateTime.parse(text).atZone(PLAN_ZONE).toOffsetDateTime();
            } catch (DateTimeParseException ex) {
                throw ServiceExceptionUtil.invalidParamException("计划执行时间格式不正确：" + text);
            }
        }
    }

    private EntityRespDTO optionalRecord(Long recordId) {
        CommonResult<EntityRespDTO> result = entityRpcApi.getEntity(recordId, PATROL_RECORD_TYPE);
        if (result == null || !result.isSuccess()) {
            return null;
        }
        return result.getData();
    }

    private static boolean isPending(EntityRespDTO record) {
        if (record.getCustomFields() == null) {
            return false;
        }
        return EXECUTION_STATUS_PENDING.equalsIgnoreCase(text(record.getCustomFields().get("execution_status")));
    }

    private static ExecutionDeviceBinding requireCompleteBinding(PatrolTaskDraft task) {
        ExecutionDeviceBinding binding = task.executionDeviceBinding();
        if (binding == null
                || binding.getEquipmentId() == null
                || !StringUtils.hasText(binding.getProtocolCode())
                || !StringUtils.hasText(binding.getLogicalDeviceId())) {
            throw ServiceExceptionUtil.invalidParamException(
                    "任务未绑定执行设备（须含 equipmentId、protocolCode、logicalDeviceId）");
        }
        return binding;
    }

    private static String text(Object raw) {
        return raw == null ? null : String.valueOf(raw).trim();
    }

    /**
     * @param executionRecordId  物化时已建的 pending 执行账
     * @param dispatchActions    冻结的动作 + 业务参数（网关开跑时再翻译）
     * @param standardSnapshot   执行账 custom_fields.standard_snapshot 原文
     */
    public record FrozenOpenRun(
            Long executionRecordId,
            ExecutionDeviceBinding binding,
            List<DispatchAction> dispatchActions,
            Object standardSnapshot
    ) {
    }
}
