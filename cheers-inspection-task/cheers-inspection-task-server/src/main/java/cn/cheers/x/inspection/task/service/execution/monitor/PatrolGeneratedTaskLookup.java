package cn.cheers.x.inspection.task.service.execution.monitor;

import cn.cheers.x.device.protocolgateway.api.dto.PatrolOpenRunSnapshotKeys;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.api.dto.PatrolGeneratedTaskActionDTO;
import cn.cheers.x.inspection.task.api.dto.PatrolGeneratedTaskDownlinkDTO;
import cn.cheers.x.inspection.task.api.dto.PatrolGeneratedTaskOptionDTO;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.service.execution.openrun.PatrolOpenRunSnapshotSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 给协议监控找出这台设备当前要下发的已生成任务。
 * <p>负责：读待执行或正在执行的账，带上执行记录编号、计划时刻、动作清单，以及生成时记下的指令包。
 * <p>不负责：再翻译指令包、把上报写成完成。
 * <p>禁止：没有到点的待执行时编一个任务编号。
 */
@Service
public class PatrolGeneratedTaskLookup {

    /**
     * 账本行上写下的存储类型。目录「巡检任务执行记录」只是入口，行上不是这个目录编码。
     */
    static final String RECORD_TYPE = "task_excution_record";
    private static final ZoneId PLAN_ZONE = ZoneId.of("Asia/Shanghai");

    private final EntityRpcApi entityRpcApi;
    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final ObjectMapper objectMapper;

    public PatrolGeneratedTaskLookup(
            EntityRpcApi entityRpcApi,
            PatrolTaskEntityStore patrolTaskEntityStore,
            ObjectMapper objectMapper
    ) {
        this.entityRpcApi = entityRpcApi;
        this.patrolTaskEntityStore = patrolTaskEntityStore;
        this.objectMapper = objectMapper;
    }

    public PatrolGeneratedTaskDownlinkDTO forDevice(String deviceId) {
        PatrolGeneratedTaskSelector.Candidate chosen;
        try {
            chosen = PatrolGeneratedTaskSelector.choose(
                    candidates(loadRecords()), deviceId, OffsetDateTime.now(PLAN_ZONE));
        } catch (IllegalArgumentException ex) {
            throw ServiceExceptionUtil.invalidParamException(ex.getMessage());
        }
        return toDownlink(deviceId, requireRecord(chosen.id()));
    }

    /**
     * 这台设备待执行和正在执行的已生成任务，供监控页点选。
     * 还没到点的也在里面。已结束的不列。
     */
    public List<PatrolGeneratedTaskOptionDTO> listForDevice(String deviceId) {
        List<EntityRespDTO> records = loadRecords();
        Map<Long, EntityRespDTO> byId = new LinkedHashMap<>();
        for (EntityRespDTO record : records) {
            if (record != null && record.getId() != null) {
                byId.put(record.getId(), record);
            }
        }
        List<PatrolGeneratedTaskOptionDTO> options = new ArrayList<>();
        for (PatrolGeneratedTaskSelector.Candidate candidate : PatrolGeneratedTaskSelector.listed(
                candidates(records), deviceId)) {
            EntityRespDTO record = byId.get(candidate.id());
            if (record == null) {
                continue;
            }
            Map<String, Object> fields = record.getCustomFields() == null ? Map.of() : record.getCustomFields();
            PatrolGeneratedTaskOptionDTO option = new PatrolGeneratedTaskOptionDTO();
            option.setExecutionRecordId(record.getId());
            option.setRecordName(text(mapOf(record.getBaseFields()).get("name")));
            option.setExecutionStatus(candidate.status());
            option.setPlannedStart(text(fields.get("planned_start")));
            option.setMediaStreamUrl(text(mapOf(fields.get("standard_snapshot"))
                    .get(PatrolOpenRunSnapshotKeys.TASK_RECORD_INGEST_URL)));
            options.add(option);
        }
        return options;
    }

    /** 人在监控页点中的那一条。必须属于这台设备，并且仍是待执行或正在执行。 */
    public PatrolGeneratedTaskDownlinkDTO forRecord(String deviceId, long recordId) {
        try {
            PatrolGeneratedTaskSelector.requireListed(candidates(loadRecords()), deviceId, recordId);
        } catch (IllegalArgumentException ex) {
            throw ServiceExceptionUtil.invalidParamException(ex.getMessage());
        }
        return toDownlink(deviceId, requireRecord(recordId));
    }

    private PatrolGeneratedTaskDownlinkDTO toDownlink(String deviceId, EntityRespDTO record) {
        Map<String, Object> fields = record.getCustomFields() == null ? Map.of() : record.getCustomFields();
        Object snapshot = fields.get("standard_snapshot");
        String protocol = text(mapOf(snapshot).get(PatrolOpenRunSnapshotSupport.KEY_PROTOCOL_VERSION));
        if (!StringUtils.hasText(protocol)) {
            throw ServiceExceptionUtil.invalidParamException("这条执行记录没有记下对接协议");
        }
        Long taskDefinitionId = longOf(mapOf(snapshot).get(PatrolOpenRunSnapshotSupport.KEY_TASK_DEFINITION_ID));
        ExecutionDeviceBinding binding = bindingOf(taskDefinitionId);
        PatrolGeneratedTaskDownlinkDTO dto = new PatrolGeneratedTaskDownlinkDTO();
        dto.setExecutionRecordId(record.getId());
        dto.setRecordName(text(mapOf(record.getBaseFields()).get("name")));
        dto.setProtocolVersion(protocol.trim());
        dto.setLogicalDeviceId(deviceId.trim());
        dto.setExecutorEquipmentId(binding == null ? null : binding.getEquipmentId());
        dto.setMediaStreamUrl(text(mapOf(snapshot).get(PatrolOpenRunSnapshotKeys.TASK_RECORD_INGEST_URL)));
        dto.setExecutionTimeEpochSeconds(plannedEpochSeconds(fields.get("planned_start")));
        dto.setActions(actionsOf(snapshot));
        StoredCommandPackage stored = storedCommandPackage(snapshot, objectMapper);
        if (stored != null) {
            dto.setCommandPackagesJson(stored.packagesJson());
            dto.setCommandTemplateId(stored.templateId());
        }
        return dto;
    }

    /**
     * 这次往来要记进的执行记录。
     * 报文编号已经出现在某条执行记录上时，直接用它。
     * 大疆主题上的网关序列号或飞机序列号，先对到恰好一台设备的设备编号，再按同一条规则选。
     * 一条正在执行就用它。否则只有一条已经到点的待执行才用它。还没到点的不参与。
     */
    public PatrolGeneratedTaskSelector.RunningMatch forExchange(String deviceId) {
        try {
            List<PatrolGeneratedTaskSelector.Candidate> rows = candidates(loadRecords());
            OffsetDateTime now = OffsetDateTime.now(PLAN_ZONE);
            if (hasLedgerRow(rows, deviceId)) {
                return PatrolGeneratedTaskSelector.forExchange(rows, deviceId, now);
            }
            EquipmentLedgerIdentity.Match identity = EquipmentLedgerIdentity.resolve(deviceId, loadDeviceIdentities());
            if (identity.ambiguous()) {
                return PatrolGeneratedTaskSelector.RunningMatch.skipped("多台设备登记了同一个序列号，这次往来未记入任务");
            }
            String ledgerId = identity.code();
            if (ledgerId != null && deviceId != null && !ledgerId.equals(deviceId.trim())) {
                return PatrolGeneratedTaskSelector.forExchange(rows, ledgerId, now);
            }
            return PatrolGeneratedTaskSelector.forExchange(rows, deviceId, now);
        } catch (RuntimeException ex) {
            String message = ex.getMessage();
            return PatrolGeneratedTaskSelector.RunningMatch.skipped(
                    message == null || message.isBlank() ? "读不到巡检执行记录" : message);
        }
    }

    private List<EntityRespDTO> loadRecords() {
        CommonResult<List<EntityRespDTO>> listed = entityRpcApi.listEntities(RECORD_TYPE, null);
        if (listed == null || !listed.isSuccess() || listed.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    listed != null && StringUtils.hasText(listed.getMsg()) ? listed.getMsg() : "读不到巡检执行记录");
        }
        return listed.getData();
    }

    private EntityRespDTO requireRecord(long id) {
        CommonResult<EntityRespDTO> loaded = entityRpcApi.getEntity(id, RECORD_TYPE);
        if (loaded == null || !loaded.isSuccess() || loaded.getData() == null || loaded.getData().getId() == null) {
            throw ServiceExceptionUtil.invalidParamException("执行记录不存在：" + id);
        }
        return loaded.getData();
    }

    private ExecutionDeviceBinding bindingOf(Long taskDefinitionId) {
        if (taskDefinitionId == null) {
            return null;
        }
        PatrolTaskDraft task = patrolTaskEntityStore.require(taskDefinitionId);
        return task == null ? null : task.executionDeviceBinding();
    }

    private static boolean hasLedgerRow(List<PatrolGeneratedTaskSelector.Candidate> rows, String deviceId) {
        if (deviceId == null || deviceId.isBlank() || rows == null) {
            return false;
        }
        String wanted = deviceId.trim();
        for (PatrolGeneratedTaskSelector.Candidate row : rows) {
            if (row != null && wanted.equals(row.deviceId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设备编号、出厂序列号、飞机序列号。序列号空着的不拿来对报文。
     */
    private List<EquipmentLedgerIdentity.Device> loadDeviceIdentities() {
        CommonResult<List<EntityRespDTO>> listed = entityRpcApi.listEntities("equipment", null);
        if (listed == null || !listed.isSuccess() || listed.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    listed != null && StringUtils.hasText(listed.getMsg()) ? listed.getMsg() : "读不到巡检设备");
        }
        List<EquipmentLedgerIdentity.Device> rows = new ArrayList<>();
        for (EntityRespDTO row : listed.getData()) {
            if (row == null) {
                continue;
            }
            Map<String, Object> base = row.getBaseFields() == null ? Map.of() : row.getBaseFields();
            Map<String, Object> custom = row.getCustomFields() == null ? Map.of() : row.getCustomFields();
            String code = firstText(text(base.get("code")), text(custom.get("code")));
            if (!StringUtils.hasText(code)) {
                continue;
            }
            rows.add(new EquipmentLedgerIdentity.Device(
                    code,
                    firstText(text(base.get("serial_number")), text(custom.get("serial_number"))),
                    firstText(text(base.get("aircraft_sn")), text(custom.get("aircraft_sn")))));
        }
        return rows;
    }

    private static List<PatrolGeneratedTaskSelector.Candidate> candidates(List<EntityRespDTO> records) {
        List<PatrolGeneratedTaskSelector.Candidate> rows = new ArrayList<>();
        for (EntityRespDTO record : records) {
            if (record == null || record.getId() == null || record.getCustomFields() == null) {
                continue;
            }
            Map<String, Object> fields = record.getCustomFields();
            Map<String, Object> snapshot = mapOf(fields.get("standard_snapshot"));
            rows.add(new PatrolGeneratedTaskSelector.Candidate(
                    record.getId(),
                    text(mapOf(record.getBaseFields()).get("name")),
                    text(fields.get("execution_status")),
                    text(snapshot.get(PatrolOpenRunSnapshotSupport.KEY_LOGICAL_DEVICE_ID)),
                    plannedStart(fields.get("planned_start"))));
        }
        return rows;
    }

    /**
     * 生成任务时落下的巡检指令包。没有这一段时返回空，调用方再决定要不要重译。
     * 多段包按原顺序接成一份。正文不是数组就失败，不另编一份。
     */
    static StoredCommandPackage storedCommandPackage(Object snapshot, ObjectMapper objectMapper) {
        Object raw = mapOf(snapshot).get(PatrolOpenRunSnapshotKeys.DISPATCH_DOWNLINK_PIECES);
        if (!(raw instanceof List<?> list) || list.isEmpty()) {
            return null;
        }
        ArrayNode steps = objectMapper.createArrayNode();
        String templateId = null;
        boolean sawPackage = false;
        for (Object item : list) {
            if (!(item instanceof Map<?, ?> map)) {
                continue;
            }
            if (!"package".equalsIgnoreCase(text(map.get("kind")))) {
                continue;
            }
            String packages = text(map.get("packagesJson"));
            if (!StringUtils.hasText(packages)) {
                continue;
            }
            sawPackage = true;
            if (!StringUtils.hasText(templateId)) {
                templateId = text(map.get("templateId"));
            }
            JsonNode node;
            try {
                node = objectMapper.readTree(packages);
            } catch (Exception ex) {
                throw ServiceExceptionUtil.invalidParamException("这条执行记录上的指令包读不出来");
            }
            if (node == null || !node.isArray()) {
                throw ServiceExceptionUtil.invalidParamException("这条执行记录上的指令包不是动作列表");
            }
            node.forEach(steps::add);
        }
        if (!sawPackage) {
            return null;
        }
        if (steps.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("这条执行记录没有生成指令包");
        }
        try {
            return new StoredCommandPackage(objectMapper.writeValueAsString(steps), templateId);
        } catch (Exception ex) {
            throw ServiceExceptionUtil.invalidParamException("这条执行记录上的指令包读不出来");
        }
    }

    record StoredCommandPackage(String packagesJson, String templateId) {
    }

    private static List<PatrolGeneratedTaskActionDTO> actionsOf(Object snapshot) {
        List<PatrolGeneratedTaskActionDTO> actions = new ArrayList<>();
        Object raw = mapOf(snapshot).get(PatrolOpenRunSnapshotSupport.KEY_DISPATCH_ACTIONS);
        if (!(raw instanceof List<?> list)) {
            throw ServiceExceptionUtil.invalidParamException("这条执行记录没有可下发的动作");
        }
        for (Object item : list) {
            if (!(item instanceof Map<?, ?> map)) {
                continue;
            }
            Long actionId = longOf(map.get("actionId"));
            if (actionId == null) {
                throw ServiceExceptionUtil.invalidParamException("这条执行记录的动作缺少编号");
            }
            PatrolGeneratedTaskActionDTO action = new PatrolGeneratedTaskActionDTO();
            action.setActionId(actionId);
            action.setActionCode(text(map.get("actionCode")));
            action.setParams(copyMap(map.get("params")));
            actions.add(action);
        }
        if (actions.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("这条执行记录没有可下发的动作");
        }
        return actions;
    }

    private static Long plannedEpochSeconds(Object raw) {
        OffsetDateTime planned = plannedStart(raw);
        if (planned == null) {
            throw ServiceExceptionUtil.invalidParamException("这条执行记录没有计划时刻");
        }
        return planned.toEpochSecond();
    }

    private static OffsetDateTime plannedStart(Object raw) {
        String text = text(raw);
        if (!StringUtils.hasText(text)) {
            return null;
        }
        try {
            return OffsetDateTime.parse(text).atZoneSameInstant(PLAN_ZONE).toOffsetDateTime();
        } catch (DateTimeException ex) {
            return null;
        }
    }

    private static Map<String, Object> mapOf(Object raw) {
        if (!(raw instanceof Map<?, ?> map)) {
            return Map.of();
        }
        Map<String, Object> copy = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() != null) {
                copy.put(String.valueOf(entry.getKey()), entry.getValue());
            }
        }
        return copy;
    }

    private static Map<String, Object> copyMap(Object raw) {
        return raw instanceof Map<?, ?> ? mapOf(raw) : Map.of();
    }

    private static Long longOf(Object raw) {
        if (raw instanceof Number number) {
            return number.longValue();
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

    private static String text(Object raw) {
        return raw == null ? null : String.valueOf(raw).trim();
    }

    private static String firstText(String primary, String fallback) {
        if (StringUtils.hasText(primary)) {
            return primary;
        }
        return StringUtils.hasText(fallback) ? fallback : null;
    }
}
