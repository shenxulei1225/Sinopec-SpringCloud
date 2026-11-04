package cn.cheers.x.module.dynamicbusiness.service.alarmlinkageverify;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.alarmlinkageverify.vo.AlarmLinkageVerifyDeviceRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.alarmlinkageverify.vo.AlarmLinkageVerifySnapshotRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.alarmlinkageverify.vo.AlarmLinkageVerifySnapshotRespVO.AlarmRow;
import cn.cheers.x.module.dynamicbusiness.controller.admin.alarmlinkageverify.vo.AlarmLinkageVerifySnapshotRespVO.EventRow;
import cn.cheers.x.module.dynamicbusiness.controller.admin.alarmlinkageverify.vo.AlarmLinkageVerifySnapshotRespVO.ItemRow;
import cn.cheers.x.module.dynamicbusiness.controller.admin.alarmlinkageverify.vo.AlarmLinkageVerifySnapshotRespVO.LinkageRow;
import cn.cheers.x.module.dynamicbusiness.controller.admin.alarmlinkageverify.vo.AlarmLinkageVerifySnapshotRespVO.PointRow;
import cn.cheers.x.module.dynamicbusiness.controller.admin.alarmlinkageverify.vo.AlarmLinkageVerifySnapshotRespVO.ThresholdRow;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.dataprocess.AlarmCatalogEnsure;
import cn.cheers.x.module.dynamicbusiness.dataprocess.AlarmRuleCatalogEnsure;
import cn.cheers.x.module.dynamicbusiness.dataprocess.AlarmTypeCatalog;
import cn.cheers.x.module.dynamicbusiness.dataprocess.PointRuleSource;
import cn.cheers.x.module.dynamicbusiness.devicehistory.DeviceCollectionPoint;
import cn.cheers.x.module.dynamicbusiness.devicehistory.DeviceCollectionPointQuery;
import cn.cheers.x.module.dynamicbusiness.devicehistory.GatewaySessionWrites;
import cn.cheers.x.module.dynamicbusiness.service.action.ActionFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.action.execution.ActionExecutionDO;
import cn.cheers.x.module.dynamicbusiness.service.action.execution.ActionExecutionMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import cn.cheers.x.module.dynamicbusiness.service.linkage.LinkageExecutionDO;
import cn.cheers.x.module.dynamicbusiness.service.linkage.LinkageExecutionItemDO;
import cn.cheers.x.module.dynamicbusiness.service.linkage.LinkageExecutionItemMapper;
import cn.cheers.x.module.dynamicbusiness.service.linkage.LinkageExecutionMapper;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventOutboxDO;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventOutboxMapper;
import cn.cheers.x.module.dynamicbusiness.service.linkage.LinkageFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.strategy.PointProcessingRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 验证页只读查询。
 *
 * <p>管什么：列出带采集点的设备，再按人选中的那一台读已经发出的事件、采集点、告警限值、告警记录和联动执行。卡片给人看已经写下的明细。读数回到恢复线时，告警卡片写成已消除。OPC UA 服务器在不在线只认采集那条会话。</p>
 * <p>不管什么：不上报读数，不判断告警，不调用联动入口，不补目录，不编设备名单，不另写一套阈值，不另开一条探测连接，不编节点标识，不改处理状态词表。</p>
 */
@Service
@RequiredArgsConstructor
public class AlarmLinkageVerifyQueryService {

    private static final int RECENT = 20;
    private static final String EQUIPMENT = "equipment";
    private static final String EVENT_TOPIC = "event_topic";

    private final DeviceCollectionPointQuery collectionPoints;
    private final PointRuleSource pointRules;
    private final EntityRpcFacadeService entityRpcFacadeService;
    private final EntityRepository entityRepository;
    private final AlarmCatalogEnsure alarmCatalogEnsure;
    private final LinkageExecutionMapper linkageExecutionMapper;
    private final LinkageExecutionItemMapper linkageExecutionItemMapper;
    private final ActionExecutionMapper actionExecutionMapper;
    private final GatewaySessionWrites gatewaySession;
    private final PlatformEventOutboxMapper eventOutbox;

    public List<AlarmLinkageVerifyDeviceRespVO> devices() {
        long tenantId = TenantContextHolder.getRequiredTenantId();
        List<AlarmLinkageVerifyDeviceRespVO> rows = new ArrayList<>();
        for (DeviceCollectionPointQuery.CollectionDevice device : collectionPoints.listDevices(tenantId)) {
            AlarmLinkageVerifyDeviceRespVO row = new AlarmLinkageVerifyDeviceRespVO();
            row.setDeviceId(device.deviceId());
            row.setDeviceName(blankToEmpty(device.deviceName()));
            row.setModelName(blankToEmpty(device.modelName()));
            rows.add(row);
        }
        return List.copyOf(rows);
    }

    public AlarmLinkageVerifySnapshotRespVO snapshot(long deviceId) {
        if (deviceId <= 0) {
            throw new ServiceException(400, "没有设备编号");
        }
        long tenantId = TenantContextHolder.getRequiredTenantId();
        AlarmLinkageVerifySnapshotRespVO snapshot = new AlarmLinkageVerifySnapshotRespVO();
        snapshot.setDeviceId(deviceId);
        snapshot.setDeviceName(entityName(deviceId, EQUIPMENT));
        snapshot.setPoints(pointsOf(tenantId, deviceId));
        List<EventRow> events = eventsOf(deviceId);
        snapshot.setEvents(events);
        snapshot.setAlarms(alarmsOf(deviceId, events));
        snapshot.setLinkages(linkagesOf(deviceId));
        snapshot.setOpcUaServerOnline(gatewaySession.attached());
        return snapshot;
    }

    private List<PointRow> pointsOf(long tenantId, long deviceId) {
        Map<Long, Long> models = pointRules.deviceModels() == null ? Map.of() : pointRules.deviceModels();
        Map<Long, Set<Long>> bound = pointRules.boundDevices() == null ? Map.of() : pointRules.boundDevices();
        List<PointProcessingRule> rules = pointRules.load() == null ? List.of() : pointRules.load();
        List<PointRow> rows = new ArrayList<>();
        for (DeviceCollectionPoint point : collectionPoints.list(tenantId, deviceId)) {
            PointRow row = new PointRow();
            row.setPointCode(point.getPointCode());
            row.setPointName(point.getPointName());
            row.setUnit(point.getUnit());
            row.setThresholds(thresholdsOf(point.getPointCode(), deviceId, models, bound, rules));
            rows.add(row);
        }
        return rows;
    }

    /**
     * 判断侧按型号套告警规则。这里只把会套到这个点上的限值给人看。
     * 型号对不上或没有限值时返回空列表，不编一个数字。
     */
    private List<ThresholdRow> thresholdsOf(String pointCode, long deviceId, Map<Long, Long> models,
                                            Map<Long, Set<Long>> bound, List<PointProcessingRule> rules) {
        List<ThresholdRow> rows = new ArrayList<>();
        for (PointProcessingRule rule : rules) {
            if (rule == null || pointCode == null || !pointCode.equals(rule.getPointFieldCode())) {
                continue;
            }
            if (!PointProcessingRule.THRESHOLD.equals(rule.getSignalKind())
                    && !PointProcessingRule.ALARM_FLAG.equals(rule.getSignalKind())) {
                continue;
            }
            if (!matchesDevice(rule, deviceId, models, bound)) {
                continue;
            }
            ThresholdRow row = new ThresholdRow();
            row.setRuleName(entityName(rule.getId(), AlarmRuleCatalogEnsure.DIRECTORY));
            row.setSignalKind(rule.getSignalKind());
            row.setCompareOp(rule.getCompareOp() == null ? "" : rule.getCompareOp());
            row.setCompareLabel(compareLabel(rule.getCompareOp()));
            row.setThreshold(rule.getThreshold());
            row.setRecovery(rule.getRecovery());
            rows.add(row);
        }
        return List.copyOf(rows);
    }

    /** 和判断侧同一条：写了型号就只套这种型号；没写型号才看已经挂上的设备。 */
    private static boolean matchesDevice(PointProcessingRule rule, long deviceId,
                                         Map<Long, Long> models, Map<Long, Set<Long>> bound) {
        if (rule.getDeviceModelId() != null) {
            Long modelId = models.get(deviceId);
            return rule.getDeviceModelId().equals(modelId);
        }
        Set<Long> devices = bound.get(rule.getId());
        return devices != null && devices.contains(deviceId);
    }

    private static String compareLabel(String op) {
        if (op == null) {
            return "";
        }
        return switch (op) {
            case "GT" -> "大于";
            case "GTE" -> "大于等于";
            case "LT" -> "小于";
            case "LTE" -> "小于等于";
            case "EQ" -> "等于";
            case "NEQ" -> "不等于";
            default -> op;
        };
    }

    private List<EventRow> eventsOf(long deviceId) {
        List<EventRow> rows = new ArrayList<>();
        for (PlatformEventOutboxDO row : eventOutbox.selectRecentAlarmEvents(EQUIPMENT, deviceId, RECENT)) {
            rows.add(toEventRow(row));
        }
        return List.copyOf(rows);
    }

    private EventRow toEventRow(PlatformEventOutboxDO row) {
        EventRow event = new EventRow();
        event.setId(row.getId());
        event.setEventTopicId(row.getEventTopicId());
        event.setEventTopicName(entityName(row.getEventTopicId(), EVENT_TOPIC));
        event.setPhase(blankToEmpty(row.getPhase()));
        event.setPhaseLabel(phaseLabel(row.getPhase()));
        event.setAlarmRecordId(alarmRecordId(row.getPayload()));
        event.setOccurredAt(row.getOccurredAt());
        event.setDeliveryStatus(deliveryLabel(row.getStatus()));
        event.setEventCode(blankToEmpty(row.getEventCode()));
        event.setLastError(blankToEmpty(row.getLastError()));
        event.setContent(payloadText(row.getPayload(), "content"));
        event.setTriggerValue(payloadText(row.getPayload(), "triggerValue"));
        event.setThresholdValue(payloadText(row.getPayload(), "thresholdValue"));
        event.setPointCode(payloadText(row.getPayload(), "pointCode"));
        event.setSourceDeviceName(namedRefName(row.getPayload(), "device"));
        event.setLocationName(namedRefName(row.getPayload(), "location"));
        return event;
    }

    private static String phaseLabel(String phase) {
        if ("CLEARED".equals(phase)) {
            return "恢复正常";
        }
        if ("RAISED".equals(phase)) {
            return "告警发生";
        }
        return phase == null ? "" : phase;
    }

    private static String deliveryLabel(String status) {
        if ("PROCESSED".equals(status)) {
            return "已发出";
        }
        if ("PENDING".equals(status) || "PROCESSING".equals(status)) {
            return "待发出";
        }
        if ("FAILED".equals(status)) {
            return "发出失败";
        }
        return status == null ? "" : status;
    }

    private static String payloadText(Map<String, Object> payload, String key) {
        if (payload == null || payload.get(key) == null) {
            return "";
        }
        return String.valueOf(payload.get(key)).trim();
    }

    private static String namedRefName(Map<String, Object> payload, String key) {
        if (payload == null) {
            return "";
        }
        Object raw = payload.get(key);
        if (!(raw instanceof Map<?, ?> map)) {
            return "";
        }
        Object name = map.get("name");
        return name == null ? "" : String.valueOf(name).trim();
    }

    private static Long alarmRecordId(Map<String, Object> payload) {
        if (payload == null) {
            return null;
        }
        Object raw = payload.get("alarmRecordId");
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw instanceof String text && !text.isBlank()) {
            try {
                return Long.parseLong(text.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    /**
     * 读数回到恢复线时，消除事件是权威。卡片写成已消除，不把词表里的已处理当成人工处理完。
     */
    private static String handlingOf(String stored, Long alarmId, List<EventRow> events) {
        if (alarmId == null || events == null) {
            return stored;
        }
        for (EventRow event : events) {
            if (event.getAlarmRecordId() == null || !alarmId.equals(event.getAlarmRecordId())) {
                continue;
            }
            if ("CLEARED".equals(event.getPhase())) {
                return "已消除";
            }
            return stored;
        }
        return stored;
    }

    private List<AlarmRow> alarmsOf(long deviceId, List<EventRow> events) {
        String deviceField = fieldCode(AlarmTypeCatalog.FIELD_DEVICE);
        List<AlarmRow> rows = new ArrayList<>();
        for (EntityRespDTO entity : entityRpcFacadeService.list(AlarmTypeCatalog.DIRECTORY, null)) {
            Long matchedDeviceId = extractDeviceId(fieldValue(entity, deviceField));
            if (matchedDeviceId == null || matchedDeviceId != deviceId) {
                continue;
            }
            AlarmRow row = new AlarmRow();
            row.setId(entity.getId());
            row.setName(text(entity.getBaseFields(), "name"));
            row.setHandling(handlingOf(alarmText(entity, AlarmTypeCatalog.FIELD_HANDLING), entity.getId(), events));
            row.setContent(alarmText(entity, AlarmTypeCatalog.FIELD_CONTENT));
            row.setTriggerValue(alarmText(entity, AlarmTypeCatalog.FIELD_TRIGGER));
            row.setThresholdValue(alarmText(entity, AlarmTypeCatalog.FIELD_THRESHOLD));
            row.setLevel(alarmText(entity, AlarmTypeCatalog.FIELD_LEVEL));
            row.setPoint(alarmText(entity, AlarmTypeCatalog.FIELD_POINT));
            row.setCause(alarmText(entity, AlarmTypeCatalog.FIELD_CAUSE));
            row.setConsequence(alarmText(entity, AlarmTypeCatalog.FIELD_CONSEQUENCE));
            row.setReview(alarmText(entity, AlarmTypeCatalog.FIELD_REVIEW));
            row.setTrace(alarmText(entity, AlarmTypeCatalog.FIELD_TRACE));
            row.setUpdateTime(entity.getUpdateTime());
            rows.add(row);
        }
        rows.sort(Comparator.comparing(AlarmRow::getUpdateTime, Comparator.nullsLast(LocalDateTime::compareTo)).reversed());
        if (rows.size() > RECENT) {
            return List.copyOf(rows.subList(0, RECENT));
        }
        return List.copyOf(rows);
    }

    private List<LinkageRow> linkagesOf(long deviceId) {
        List<LinkageRow> rows = new ArrayList<>();
        for (LinkageExecutionDO execution : linkageExecutionMapper.selectRecentBySource(EQUIPMENT, deviceId, RECENT)) {
            rows.add(toLinkageRow(execution));
        }
        return rows;
    }

    private LinkageRow toLinkageRow(LinkageExecutionDO execution) {
        LinkageRow row = new LinkageRow();
        row.setId(execution.getId());
        row.setLinkageName(entityName(execution.getLinkageRuleId(), LinkageFieldCodes.LINKAGE_DIRECTORY));
        row.setEventTopicId(execution.getEventTopicId());
        row.setEventTopicName(entityName(execution.getEventTopicId(), EVENT_TOPIC));
        row.setStatus(execution.getStatus());
        row.setLastError(execution.getLastError());
        row.setCreateTime(execution.getCreateTime());
        row.setItems(itemsOf(execution.getId()));
        return row;
    }

    private List<ItemRow> itemsOf(long executionId) {
        List<ItemRow> rows = new ArrayList<>();
        for (LinkageExecutionItemDO item : linkageExecutionItemMapper.selectByLinkageExecutionId(executionId)) {
            ItemRow row = new ItemRow();
            row.setPairIndex(item.getPairIndex());
            row.setActionExecutionId(item.getActionExecutionId());
            row.setStatus(item.getStatus());
            row.setLastError(item.getLastError());
            fillAction(row, item.getActionExecutionId());
            rows.add(row);
        }
        return rows;
    }

    private void fillAction(ItemRow row, Long actionExecutionId) {
        if (actionExecutionId == null || actionExecutionId <= 0) {
            return;
        }
        ActionExecutionDO action = actionExecutionMapper.selectById(actionExecutionId);
        if (action == null) {
            return;
        }
        row.setActionId(action.getActionId());
        row.setTargetEntityId(action.getTargetEntityId());
        row.setActionName(entityName(action.getActionId(), ActionFieldCodes.ENTITY_TYPE_CODE));
        String targetType = blankToEmpty(action.getTargetEntityTypeCode());
        row.setTargetName(entityName(action.getTargetEntityId(), targetType.isEmpty() ? EQUIPMENT : targetType));
        if (row.getLastError() == null || row.getLastError().isBlank()) {
            row.setLastError(action.getErrorMessage());
        }
        if (action.getStatus() != null && !action.getStatus().isBlank()) {
            row.setStatus(action.getStatus());
        }
    }

    private String alarmText(EntityRespDTO entity, String fieldName) {
        return text(fieldValue(entity, fieldCode(fieldName)));
    }

    private String entityName(Long id, String type) {
        if (id == null || id <= 0) {
            return "";
        }
        EntityDO entity = entityRepository.findById(id, type);
        if (entity == null || entity.getName() == null) {
            return "";
        }
        return entity.getName();
    }

    private String fieldCode(String name) {
        return alarmCatalogEnsure.fieldCode(name);
    }

    private static Object fieldValue(EntityRespDTO entity, String field) {
        Object custom = entity.getCustomFields() == null ? null : entity.getCustomFields().get(field);
        if (custom != null) {
            return custom;
        }
        return entity.getBaseFields() == null ? null : entity.getBaseFields().get(field);
    }

    private static String text(Object raw) {
        if (raw == null) {
            return "";
        }
        return String.valueOf(raw).trim();
    }

    private static String text(Map<String, Object> fields, String key) {
        if (fields == null || fields.get(key) == null) {
            return "";
        }
        return String.valueOf(fields.get(key)).trim();
    }

    private static String blankToEmpty(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    /** 告警设备格子里可能是编号，也可能是引用对象。认不出就不当这台设备。 */
    private static Long extractDeviceId(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number number) {
            long id = number.longValue();
            return id > 0 ? id : null;
        }
        if (raw instanceof Map<?, ?> map) {
            Object id = map.get("id");
            if (id == null) {
                id = map.get("entityId");
            }
            return extractDeviceId(id);
        }
        String value = String.valueOf(raw).trim();
        if (value.isEmpty()) {
            return null;
        }
        try {
            long id = Long.parseLong(value);
            return id > 0 ? id : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
