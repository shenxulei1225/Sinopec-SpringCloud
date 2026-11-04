package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.framework.common.enums.UserTypeEnum;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.security.core.LoginUser;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.service.alarm.AlarmLifecycleEventPublisher;
import cn.cheers.x.system.api.user.AdminUserApi;
import cn.cheers.x.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 把已经决定要记的告警写进告警目录。
 * 类型必须是这本目录里的分类。缺类型、缺位置、位置没有所属场站，都不写。
 * 同一台设备同一条规则还开着时，仍用原来那一条。
 * 新记录写下产生这一行，复核是未复核。再次出现和关闭再追加一行。
 * 产生和消除按告警规则引用的事件主题写入 outbox。告警写入不查询联动。
 * 发出去的主题内容带告警设备和告警位置，写入设备、位置的编号和名称。名称在发出时译好。缺名称不发。
 * 原因、后果、联动、允许响应时间从告警规则抄当时的原文。规则上没写或没有对应规则，这四项空着。
 * 不判断阈值。不在读列表时补脉络。不编这四项的文字。
 * 缺少发布事件引用时整次写入失败，禁止记完告警后直接调用联动。
 */
@Service
@Slf4j
public class AlarmRecordWriter {

    private static final String SUPER_USERNAME = "super";
    private static final ZoneId CLOCK = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter CLOCK_TEXT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Resource
    private AlarmCatalogEnsure alarmCatalogEnsure;
    @Resource
    private EntityService entityService;
    @Resource
    private EntityRpcFacadeService entityRpcFacadeService;
    @Resource
    private EntityCategoryRelationService entityCategoryRelationService;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private AlarmLifecycleEventPublisher alarmLifecycleEventPublisher;

    @Transactional(rollbackFor = Exception.class)
    public void open(long tenantId, Long categoryId, String categoryCode, String recordCode,
                     String name, String level, String content, long deviceId, long locationId,
                     String triggerValue, String thresholdValue, String pointCode, Long ruleId) {
        if (locationId <= 0 || deviceId <= 0) {
            throw new ServiceException(400, "告警缺少设备或位置，不记");
        }
        inTenant(tenantId, () -> {
            alarmCatalogEnsure.ensure();
            long stationId = stationOf(locationId);
            Long typeId = resolveType(categoryId, categoryCode);
            EntityRespDTO existing = entityRpcFacadeService.getByCode(recordCode, AlarmTypeCatalog.DIRECTORY);
            Map<String, Object> fields = fields(level, content, deviceId, locationId, triggerValue, thresholdValue, pointCode,
                    AlarmTypeCatalog.HANDLING_OPEN, ruleId);
            String traceCode = alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_TRACE);
            String reviewCode = alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_REVIEW);
            Long recordId;
            if (existing != null && existing.getId() != null) {
                fields.put(traceCode, appendTrace(text(existing.getCustomFields(), traceCode), "再次出现。状态改为未处理。"));
                String previousReview = text(existing.getCustomFields(), reviewCode);
                fields.put(reviewCode, previousReview.isBlank()
                        ? AlarmTypeCatalog.REVIEW_PENDING
                        : previousReview);
                rewrite(existing.getId(), name, recordCode, fields);
                recordId = existing.getId();
            } else {
                fields.put(traceCode, traceLine("产生。"));
                fields.put(reviewCode, AlarmTypeCatalog.REVIEW_PENDING);
                EntityCreateReqVO request = new EntityCreateReqVO();
                request.setRegistryCode(AlarmTypeCatalog.DIRECTORY);
                Map<String, Object> baseFields = new LinkedHashMap<>();
                baseFields.put("entityTypeCode", AlarmTypeCatalog.DIRECTORY);
                baseFields.put("modelId", alarmCatalogEnsure.modelId());
                baseFields.put("name", clip(name));
                baseFields.put("code", recordCode);
                baseFields.put("status", 1);
                baseFields.put(FacilityOwningFieldCodes.FIELD_CODE, FacilityOwningFieldCodes.toApiRef(stationId));
                request.setBaseFields(baseFields);
                request.setCustomFields(fields);
                recordId = entityService.create(request);
                entityCategoryRelationService.associate(recordId, typeId, AlarmTypeCatalog.DIRECTORY);
            }
            Long topicRuleId = alarmRuleId(ruleId);
            if (topicRuleId != null) {
                alarmLifecycleEventPublisher.publish(
                        tenantId, stationId, topicRuleId, recordId, deviceId,
                        AlarmTypeCatalog.PHASE_RAISED,
                        eventFacts(level, content, deviceId, locationId, triggerValue, thresholdValue, pointCode));
            }
        });
    }

    /**
     * 告警消除。先把记录改成已关闭，再按消除去对联动。
     * ruleId 是这次消除的告警规则。没有规则编号时只关记录，不对联动。
     */
    @Transactional(rollbackFor = Exception.class)
    public void close(long tenantId, String recordCode, Long ruleId) {
        inTenant(tenantId, () -> {
            alarmCatalogEnsure.ensure();
            EntityRespDTO existing = entityRpcFacadeService.getByCode(recordCode, AlarmTypeCatalog.DIRECTORY);
            if (existing == null || existing.getId() == null) {
                throw new ServiceException(404, "没有找到要关闭的告警");
            }
            String handlingCode = alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_HANDLING);
            Map<String, Object> fields = new LinkedHashMap<>();
            if (existing.getCustomFields() != null) {
                fields.putAll(existing.getCustomFields());
            }
            fillStoredAlarmFields(existing, fields);
            fields.put(handlingCode, alarmCatalogEnsure.fit(AlarmTypeCatalog.FIELD_HANDLING, AlarmTypeCatalog.HANDLING_CLOSED));
            String traceCode = alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_TRACE);
            fields.put(traceCode, appendTrace(text(fields, traceCode), "关闭。状态改为已处理。"));
            String name = text(existing.getBaseFields(), "name");
            rewrite(existing.getId(), name, recordCode, fields);
            publishClear(tenantId, existing, fields, ruleId);
        });
    }

    /** 消除与事件 outbox 同事务写入。没有规则编号的历史记录只关闭，不编事件主题。 */
    private void publishClear(long tenantId, EntityRespDTO existing, Map<String, Object> fields, Long ruleId) {
        Long topicRuleId = alarmRuleId(ruleId);
        if (topicRuleId == null) {
            return;
        }
        Long stationId = FacilityOwningFieldCodes.extractId(
                existing.getBaseFields() == null ? null : existing.getBaseFields().get(FacilityOwningFieldCodes.FIELD_CODE));
        if (stationId == null) {
            throw new ServiceException(400, "告警消除没有所属场站，不能发布事件");
        }
        Long equipmentId = FacilityOwningFieldCodes.extractId(
                fields.get(alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_DEVICE)));
        if (equipmentId == null) {
            throw new ServiceException(400, "告警消除没有来源设备，不能发布事件");
        }
        alarmLifecycleEventPublisher.publish(
                tenantId, stationId, topicRuleId, existing.getId(), equipmentId,
                AlarmTypeCatalog.PHASE_CLEARED, eventFactsFromStored(fields));
    }

    /**
     * 低于这一侧的信号键是规则编号的相反数，用来和高于共用一条规则时分开记账。
     * 发布事件仍用告警规则本身的编号去解析主题。
     */
    private static Long alarmRuleId(Long signalKey) {
        if (signalKey == null || signalKey == 0L) {
            return null;
        }
        return Math.abs(signalKey);
    }

    /**
     * 告警设备和位置写在专用列里，详情读到基础字段。消除时要把这些格补进这一份，不能只看扩展字段。
     */
    private void fillStoredAlarmFields(EntityRespDTO existing, Map<String, Object> fields) {
        if (existing.getBaseFields() == null) {
            return;
        }
        for (String name : List.of(
                AlarmTypeCatalog.FIELD_DEVICE,
                AlarmTypeCatalog.FIELD_LOCATION,
                AlarmTypeCatalog.FIELD_LEVEL,
                AlarmTypeCatalog.FIELD_CONTENT,
                AlarmTypeCatalog.FIELD_POINT,
                AlarmTypeCatalog.FIELD_TRIGGER,
                AlarmTypeCatalog.FIELD_THRESHOLD,
                AlarmTypeCatalog.FIELD_HANDLING,
                AlarmTypeCatalog.FIELD_TRACE,
                AlarmTypeCatalog.FIELD_REVIEW,
                AlarmTypeCatalog.FIELD_CAUSE,
                AlarmTypeCatalog.FIELD_CONSEQUENCE,
                AlarmTypeCatalog.FIELD_ACTION,
                AlarmTypeCatalog.FIELD_RESPONSE_TIME,
                AlarmTypeCatalog.FIELD_SHELF_UNTIL)) {
            String code = alarmCatalogEnsure.fieldCode(name);
            if (code == null || code.isBlank() || fields.get(code) != null) {
                continue;
            }
            Object value = existing.getBaseFields().get(code);
            if (value != null) {
                fields.put(code, value);
            }
        }
    }

    private Map<String, Object> eventFactsFromStored(Map<String, Object> fields) {
        Map<String, Object> event = new LinkedHashMap<>();
        Long deviceId = FacilityOwningFieldCodes.extractId(
                fields.get(alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_DEVICE)));
        Long locationId = FacilityOwningFieldCodes.extractId(
                fields.get(alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_LOCATION)));
        if (deviceId == null || locationId == null) {
            throw new ServiceException(400, "告警消除没有设备或位置，不能发布事件");
        }
        event.put("device", namedRef(deviceId, "equipment", "告警事件没有设备名称，不发"));
        event.put("location", namedRef(locationId, "zone", "告警事件没有位置名称，不发"));
        copyFact(event, "level", fields, AlarmTypeCatalog.FIELD_LEVEL);
        copyFact(event, "content", fields, AlarmTypeCatalog.FIELD_CONTENT);
        copyFact(event, "pointCode", fields, AlarmTypeCatalog.FIELD_POINT);
        copyFact(event, "triggerValue", fields, AlarmTypeCatalog.FIELD_TRIGGER);
        copyFact(event, "thresholdValue", fields, AlarmTypeCatalog.FIELD_THRESHOLD);
        return event;
    }

    /**
     * 人在这条告警上填了还没到的搁置时间。到点之前不再报出来。
     * 没有这条记录，或时间已过，返回 false，调用方照常记账。
     */
    public boolean shelved(long tenantId, String recordCode) {
        AtomicBoolean held = new AtomicBoolean(false);
        try {
            inTenant(tenantId, () -> held.set(shelfStillAhead(recordCode)));
        } catch (RuntimeException ex) {
            return false;
        }
        return held.get();
    }

    /**
     * 同一台设备上，这条规则的告警还开着。已处理的不算开着。
     * 没有记录则返回 false。
     */
    public boolean stillOpen(long tenantId, String recordCode) {
        AtomicBoolean open = new AtomicBoolean(false);
        try {
            inTenant(tenantId, () -> open.set(recordStillOpen(recordCode)));
        } catch (RuntimeException ex) {
            return false;
        }
        return open.get();
    }

    private boolean shelfStillAhead(String recordCode) {
        EntityRespDTO existing = entityRpcFacadeService.getByCode(recordCode, AlarmTypeCatalog.DIRECTORY);
        if (existing == null) {
            return false;
        }
        String raw = text(existing.getCustomFields(), alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_SHELF_UNTIL));
        LocalDateTime until = parseShelf(raw);
        return until != null && until.isAfter(LocalDateTime.now(CLOCK));
    }

    private boolean recordStillOpen(String recordCode) {
        EntityRespDTO existing = entityRpcFacadeService.getByCode(recordCode, AlarmTypeCatalog.DIRECTORY);
        if (existing == null || existing.getId() == null) {
            return false;
        }
        String handling = text(existing.getCustomFields(), alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_HANDLING));
        return !AlarmTypeCatalog.HANDLING_CLOSED.equals(handling) && !"已处理".equals(handling);
    }

    private static LocalDateTime parseShelf(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String text = raw.trim();
        for (DateTimeFormatter formatter : List.of(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME)) {
            try {
                return LocalDateTime.parse(text, formatter);
            } catch (DateTimeParseException ignored) {
                // 换下一种常见写法。
            }
        }
        return null;
    }
    private long stationOf(long locationId) {
        EntityRespDTO location = entityRpcFacadeService.get(locationId, "zone");
        Long stationId = FacilityOwningFieldCodes.extractId(
                location == null || location.getBaseFields() == null
                        ? null
                        : location.getBaseFields().get(FacilityOwningFieldCodes.FIELD_CODE));
        if (stationId == null) {
            throw new ServiceException(400, "告警位置没有所属场站，不记");
        }
        return stationId;
    }

    private Long resolveType(Long categoryId, String categoryCode) {
        if (categoryId != null && categoryId > 0) {
            CategoryDO category = categoryMapper.selectById(categoryId);
            if (category == null || !AlarmTypeCatalog.DIRECTORY.equals(category.getCategoryTypeCode())) {
                throw new ServiceException(400, "告警类型不在告警目录里");
            }
            return category.getId();
        }
        if (categoryCode == null || categoryCode.isBlank()) {
            throw new ServiceException(400, "告警缺少类型，不记");
        }
        return alarmCatalogEnsure.categoryId(categoryCode);
    }

    private Map<String, Object> eventFacts(
            String level, String content, long deviceId, long locationId,
            String triggerValue, String thresholdValue, String pointCode
    ) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("device", namedRef(deviceId, "equipment", "告警事件没有设备名称，不发"));
        event.put("location", namedRef(locationId, "zone", "告警事件没有位置名称，不发"));
        putFact(event, "level", level);
        putFact(event, "content", content);
        putFact(event, "pointCode", pointCode);
        putFact(event, "triggerValue", triggerValue);
        putFact(event, "thresholdValue", thresholdValue);
        return event;
    }

    /**
     * 主题内容里的引用。编号给人下拉选，名称在发出时写好，人不用再查点位编码。
     */
    static Map<String, Object> namedEntityRef(String entityTypeCode, long id, String name) {
        if (entityTypeCode == null || entityTypeCode.isBlank() || id <= 0
                || name == null || name.isBlank()) {
            throw new ServiceException(400, "告警事件引用缺少编号或名称，不发");
        }
        Map<String, Object> ref = new LinkedHashMap<>();
        ref.put("entityTypeCode", entityTypeCode);
        ref.put("id", id);
        ref.put("name", name.trim());
        return ref;
    }

    private Map<String, Object> namedRef(long id, String entityTypeCode, String missingMessage) {
        EntityRespDTO row = entityRpcFacadeService.get(id, entityTypeCode);
        if (row == null || row.getId() == null) {
            throw new ServiceException(400, missingMessage);
        }
        String name = text(row.getBaseFields(), "name");
        if (name == null || name.isBlank()) {
            throw new ServiceException(400, missingMessage);
        }
        return namedEntityRef(entityTypeCode, row.getId(), name);
    }

    private static void putFact(Map<String, Object> event, String key, String value) {
        if (value != null && !value.isBlank()) {
            event.put(key, value.trim());
        }
    }

    private void copyFact(Map<String, Object> event, String key, Map<String, Object> fields, String fieldName) {
        putFact(event, key, text(fields, alarmCatalogEnsure.fieldCode(fieldName)));
    }

    private Map<String, Object> fields(String level, String content, long deviceId, long locationId,
                                       String triggerValue, String thresholdValue, String pointCode, String handling,
                                       Long ruleId) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_LEVEL),
                alarmCatalogEnsure.fit(AlarmTypeCatalog.FIELD_LEVEL, level));
        fields.put(alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_CONTENT), textOrEmpty(content));
        fields.put(alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_DEVICE), Long.toString(deviceId));
        fields.put(alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_LOCATION), Long.toString(locationId));
        fields.put(alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_TRIGGER), textOrEmpty(triggerValue));
        fields.put(alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_THRESHOLD), textOrEmpty(thresholdValue));
        fields.put(alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_POINT), textOrEmpty(pointCode));
        fields.put(alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_HANDLING),
                alarmCatalogEnsure.fit(AlarmTypeCatalog.FIELD_HANDLING, handling));
        copyRuleHelp(fields, ruleId);
        return fields;
    }

    /** 四项的原文在告警规则上。这里只抄当时写下的字。没有规则、或规则上是空的，告警上就空着。联动也按规则上的原文抄。 */
    private void copyRuleHelp(Map<String, Object> fields, Long ruleId) {
        EntityRespDTO rule = ruleId == null || ruleId <= 0
                ? null
                : entityRpcFacadeService.get(ruleId, AlarmRuleCatalogEnsure.DIRECTORY);
        Map<String, Object> source = rule == null ? null : rule.getCustomFields();
        for (String name : List.of(
                AlarmTypeCatalog.FIELD_CAUSE,
                AlarmTypeCatalog.FIELD_CONSEQUENCE,
                AlarmTypeCatalog.FIELD_ACTION,
                AlarmTypeCatalog.FIELD_RESPONSE_TIME)) {
            String code = alarmCatalogEnsure.fieldCode(name);
            fields.put(code, text(source, code));
        }
    }

    private void rewrite(Long id, String name, String recordCode, Map<String, Object> fields) {
        EntityUpdateReqVO request = new EntityUpdateReqVO();
        request.setId(id);
        request.setRegistryCode(AlarmTypeCatalog.DIRECTORY);
        Map<String, Object> baseFields = new LinkedHashMap<>();
        baseFields.put("entityTypeCode", AlarmTypeCatalog.DIRECTORY);
        baseFields.put("modelId", alarmCatalogEnsure.modelId());
        baseFields.put("name", clip(name));
        baseFields.put("code", recordCode);
        baseFields.put("status", 1);
        request.setBaseFields(baseFields);
        request.setCustomFields(fields);
        entityService.update(request);
    }

    private void inTenant(long tenantId, Runnable work) {
        boolean installed = SecurityFrameworkUtils.getLoginUserId() == null;
        if (installed) {
            LoginUser loginUser = new LoginUser();
            loginUser.setId(superUserId());
            loginUser.setUserType(UserTypeEnum.ADMIN.getValue());
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList()));
        }
        try {
            TenantUtils.execute(tenantId, work);
        } finally {
            if (installed) {
                SecurityContextHolder.clearContext();
            }
        }
    }

    private long superUserId() {
        try {
            AdminUserRespDTO user = adminUserApi.getUser(1L).getCheckedData();
            if (user != null && user.getId() != null && SUPER_USERNAME.equals(user.getUsername())) {
                return user.getId();
            }
        } catch (RuntimeException ignored) {
            // 读不到时仍用编号 1，和启动准备一致。
        }
        return 1L;
    }

    private static String text(Map<String, Object> fields, String key) {
        if (fields == null || fields.get(key) == null) {
            return "";
        }
        return String.valueOf(fields.get(key));
    }

    private static String textOrEmpty(String value) {
        return value == null ? "" : value;
    }

    /** 脉络追加一行。已有内容留着，不覆盖。 */
    private static String appendTrace(String previous, String what) {
        String line = traceLine(what);
        if (previous == null || previous.isBlank()) {
            return line;
        }
        return previous.stripTrailing() + "\n" + line;
    }

    private static String traceLine(String what) {
        return LocalDateTime.now(CLOCK).format(CLOCK_TEXT) + " " + what;
    }

    private static String clip(String name) {
        if (name == null || name.isBlank()) {
            return "告警";
        }
        String trimmed = name.trim();
        return trimmed.length() <= 100 ? trimmed : trimmed.substring(0, 100);
    }
}
