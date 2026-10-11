package cn.cheers.x.inspection.task.service.execution.monitor;

import cn.cheers.x.device.protocolgateway.api.dto.PatrolOpenRunSnapshotKeys;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.api.dto.PatrolGeneratedTaskActionDTO;
import cn.cheers.x.inspection.task.api.dto.PatrolGeneratedTaskDownlinkDTO;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.service.execution.openrun.PatrolOpenRunSnapshotSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
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
 * <p>负责：读待执行或正在执行的账，带上执行记录编号、计划时刻和动作清单。
 * <p>不负责：翻译指令包、把上报写成完成。
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

    public PatrolGeneratedTaskLookup(EntityRpcApi entityRpcApi, PatrolTaskEntityStore patrolTaskEntityStore) {
        this.entityRpcApi = entityRpcApi;
        this.patrolTaskEntityStore = patrolTaskEntityStore;
    }

    public PatrolGeneratedTaskDownlinkDTO forDevice(String deviceId) {
        PatrolGeneratedTaskSelector.Candidate chosen;
        try {
            chosen = PatrolGeneratedTaskSelector.choose(
                    candidates(loadRecords()), deviceId, OffsetDateTime.now(PLAN_ZONE));
        } catch (IllegalArgumentException ex) {
            throw ServiceExceptionUtil.invalidParamException(ex.getMessage());
        }
        EntityRespDTO record = requireRecord(chosen.id());
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
        return dto;
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
}
