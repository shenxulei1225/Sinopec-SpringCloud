package cn.cheers.x.module.dynamicbusiness.service.ledgerstate;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.pagegrant.PageRoleGrantDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.pagegrant.PageRoleGrantMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.system.api.permission.PermissionApi;
import cn.cheers.x.system.api.permission.RoleApi;
import cn.cheers.x.system.enums.permission.RoleCodeEnum;
import cn.cheers.x.workorder.api.WorkOrderApi;
import cn.cheers.x.workorder.api.dto.WorkOrderCreateReqDTO;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 办事状态引擎。
 *
 * 权威：状态定义在 dynamic_ledger_state_machine；当前状态仍是台账字段 handle_status；
 * 脉络只追加 dynamic_ledger_state_trace 里已经发生的一行。
 * 不管作业标准，不管审批流。
 * 禁止：读的时候补未发生的节点；转移失败后仍改状态。
 * 已有脉络的单子锁定第一次写入时的定义版本，后来发布的新版本只给新单。
 */
@Service
public class LedgerStateMachineServiceImpl implements LedgerStateMachineService {

    static final String REPAIR_TYPE = "maintanance";
    static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    static final DateTimeFormatter TRACE_STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    static final String STARTED = "已开始维修";
    static final String FINISHED = "维修已完工";

    /** 配置只能从这份名单里选。新增效果必须先加实现再加入名单。 */
    private static final Set<String> EFFECT_CODES = Set.of(
            "record_accept_time", "save_remark", "create_work_order", "set_status");

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private EntityService entityService;
    @Resource
    private PageRoleGrantMapper pageRoleGrantMapper;
    @Resource
    private PermissionApi permissionApi;
    @Resource
    private RoleApi roleApi;
    @Resource
    private WorkOrderApi workOrderApi;

    @Override
    public List<LedgerTransitionView> listAvailable(String entityTypeCode, Long entityId, String pageCode) {
        String type = requireType(entityTypeCode);
        EntityRespVO entity = requireEntity(entityId, type);
        long machineId = resolveMachineId(type, entityId);
        String from = currentStatus(entity);
        List<LedgerTransitionView> views = new ArrayList<>();
        for (TransitionRow row : loadTransitions(machineId, from)) {
            if (!StringUtils.hasText(row.pageCode) || !row.pageCode.equals(pageCode)) {
                continue;
            }
            if (!allowPage(row.pageCode, row.pageAction)) {
                continue;
            }
            views.add(new LedgerTransitionView(row.toState, stateName(machineId, row.toState),
                    row.requiredFields, row.traceTemplate));
        }
        return views;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(String entityTypeCode, Long entityId, String toState, String pageCode,
                        Map<String, Object> payload) {
        String type = requireType(entityTypeCode);
        EntityRespVO entity = requireEntity(entityId, type);
        long machineId = resolveMachineId(type, entityId);
        int version = resolveVersion(type, entityId);
        TransitionRow row = findPageTransition(machineId, currentStatus(entity), toState, pageCode);
        if (!allowPage(row.pageCode, row.pageAction)) {
            throw new ServiceException(403, "没有该页面的操作权限");
        }
        Map<String, Object> body = payload == null ? Map.of() : payload;
        requireFields(row.requiredFields, body);
        Map<String, Object> staged = applyEffects(row.effectCodes, entity, body);
        staged.put("handle_status", row.toState);
        writeEntity(entity, type, staged);
        appendTrace(type, entityId, version, row.toState, render(row.traceTemplate, body));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordSubmitted(String entityTypeCode, Long entityId) {
        String type = requireType(entityTypeCode);
        if (!allowPage("repair-request", "maintain")) {
            throw new ServiceException(403, "没有该页面的操作权限");
        }
        EntityRespVO entity = requireEntity(entityId, type);
        if (!"submitted".equals(currentStatus(entity))) {
            throw new ServiceException(400, "只有已上报的单子补这一行");
        }
        if (traceCount(type, entityId) > 0) {
            return;
        }
        int version = resolveVersion(type, entityId);
        Object reportTime = first(entity.getBaseFields(), "report_time", "reportTime");
        LocalDateTime when = parseDateTime(reportTime);
        if (when == null) {
            when = LocalDateTime.now();
        }
        insertTrace(type, entityId, version, "submitted", "已上报", when);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void appendProgress(String entityTypeCode, Long entityId, Long workOrderId, String sentence) {
        if (!STARTED.equals(sentence) && !FINISHED.equals(sentence)) {
            throw new ServiceException(400, "这条进度不在开工、完工之内");
        }
        String type = requireType(entityTypeCode);
        EntityRespVO entity = requireEntity(entityId, type);
        if (!"dispatched".equals(currentStatus(entity))) {
            throw new ServiceException(400, "只有已派工的报修才记录维修进度");
        }
        Object linked = first(entity.getBaseFields(), "work_order_id", "workOrderId");
        if (workOrderId == null || linked == null || !String.valueOf(workOrderId).equals(String.valueOf(linked))) {
            throw new ServiceException(400, "这张工单不是这张报修派出去的");
        }
        int version = resolveVersion(type, entityId);
        appendTrace(type, entityId, version, "dispatched", sentence);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeAfterAcceptance(String entityTypeCode, Long entityId) {
        String type = requireType(entityTypeCode);
        EntityRespVO entity = requireEntity(entityId, type);
        long machineId = resolveMachineId(type, entityId);
        int version = resolveVersion(type, entityId);
        String from = currentStatus(entity);
        TransitionRow row = null;
        for (TransitionRow candidate : loadTransitions(machineId, from)) {
            if ("closed".equals(candidate.toState) && !StringUtils.hasText(candidate.pageCode)) {
                row = candidate;
                break;
            }
        }
        if (row == null) {
            throw new ServiceException(400, "当前状态不能因验收关闭");
        }
        Map<String, Object> staged = new LinkedHashMap<>();
        staged.put("handle_status", "closed");
        writeEntity(entity, type, staged);
        appendTrace(type, entityId, version, "closed", row.traceTemplate);
    }

    @Override
    public List<String> listTraceLines(String entityTypeCode, Long entityId) {
        String type = requireType(entityTypeCode);
        Long tenantId = tenantId();
        return jdbcTemplate.query(
                "SELECT occurred_at, sentence FROM dynamic_ledger_state_trace "
                        + "WHERE deleted = FALSE AND tenant_id = ? AND entity_type_code = ? AND entity_id = ? "
                        + "ORDER BY occurred_at, id",
                (rs, i) -> TRACE_STAMP.format(rs.getTimestamp("occurred_at").toLocalDateTime())
                        + " " + rs.getString("sentence"),
                tenantId, type, entityId);
    }

    private Map<String, Object> applyEffects(String effectCodes, EntityRespVO entity, Map<String, Object> payload) {
        Map<String, Object> staged = new LinkedHashMap<>();
        if (!StringUtils.hasText(effectCodes)) {
            return staged;
        }
        for (String code : effectCodes.split(",")) {
            String effect = code.trim();
            if (effect.isEmpty()) {
                continue;
            }
            if (!EFFECT_CODES.contains(effect)) {
                throw new ServiceException(400, "未登记的办事效果：" + effect);
            }
            switch (effect) {
                case "record_accept_time" -> staged.put("handle_time", DATE_TIME.format(LocalDateTime.now()));
                case "save_remark" -> staged.put("handle_remark", text(payload.get("remark")));
                case "create_work_order" -> staged.put("work_order_id", createWorkOrder(entity, payload));
                case "set_status" -> {
                    // 状态由转移统一写入，这个效果只表示「这一跳改的是状态本身」。
                }
                default -> throw new ServiceException(400, "未登记的办事效果：" + effect);
            }
        }
        return staged;
    }

    private Long createWorkOrder(EntityRespVO entity, Map<String, Object> payload) {
        WorkOrderCreateReqDTO req = new WorkOrderCreateReqDTO();
        req.setScope(text(payload.get("scope")));
        req.setTitle(text(payload.get("title")));
        req.setStandardId(longValue(payload.get("standardId")));
        req.setAssigneeTeamId(longValue(payload.get("teamId")));
        Long assignee = longValue(payload.get("assigneeUserId"));
        req.setAssigneeUserId(assignee != null && assignee > 0 ? assignee : null);
        req.setAssetId(longValue(first(entity.getBaseFields(), "facility_id", "facilityId")));
        if (payload.get("assetId") != null) {
            req.setAssetId(longValue(payload.get("assetId")));
        }
        req.setBusinessKey("repair:" + entity.getId());
        req.setRuntimeJobId("source:repair");
        // 当班派单不写预计开始。实际开始记在开工那一行进度上。
        req.setPlannedFinishTime(parseDateTime(payload.get("plannedFinish")));
        return workOrderApi.create(req).getCheckedData();
    }

    private void writeEntity(EntityRespVO current, String entityType, Map<String, Object> overrides) {
        Map<String, Object> prev = current.getBaseFields() == null ? Map.of() : current.getBaseFields();
        Map<String, Object> base = new LinkedHashMap<>(prev);
        base.put("entityTypeCode", entityType);
        for (String key : new ArrayList<>(base.keySet())) {
            if (key.endsWith("_time") || key.endsWith("Time")) {
                base.put(key, coerceDateTime(base.get(key)));
            }
        }
        base.putAll(overrides);
        EntityUpdateReqVO req = new EntityUpdateReqVO();
        req.setId(current.getId());
        req.setRegistryCode(entityType);
        req.setBaseFields(base);
        req.setCustomFields(current.getCustomFields() == null ? Map.of() : current.getCustomFields());
        entityService.update(req);
    }

    private TransitionRow findPageTransition(long machineId, String from, String toState, String pageCode) {
        for (TransitionRow row : loadTransitions(machineId, from)) {
            if (row.toState.equals(toState) && row.pageCode.equals(pageCode)) {
                return row;
            }
        }
        throw new ServiceException(400, "当前状态不能这样做");
    }

    private List<TransitionRow> loadTransitions(long machineId, String from) {
        return jdbcTemplate.query(
                "SELECT to_state, page_code, page_action, required_fields, effect_codes, trace_template "
                        + "FROM dynamic_ledger_transition WHERE deleted = FALSE AND machine_id = ? AND from_state = ? "
                        + "ORDER BY sort_no, id",
                (rs, i) -> new TransitionRow(
                        rs.getString("to_state"),
                        rs.getString("page_code") == null ? "" : rs.getString("page_code"),
                        rs.getString("page_action") == null ? "" : rs.getString("page_action"),
                        rs.getString("required_fields") == null ? "" : rs.getString("required_fields"),
                        rs.getString("effect_codes") == null ? "" : rs.getString("effect_codes"),
                        rs.getString("trace_template")),
                machineId, from);
    }

    private long resolveMachineId(String entityType, Long entityId) {
        int version = resolveVersion(entityType, entityId);
        Long tenantId = tenantId();
        List<Long> ids = jdbcTemplate.query(
                "SELECT id FROM dynamic_ledger_state_machine "
                        + "WHERE deleted = FALSE AND published = TRUE AND tenant_id = ? "
                        + "AND entity_type_code = ? AND version_no = ?",
                (rs, i) -> rs.getLong("id"), tenantId, entityType, version);
        if (ids.isEmpty()) {
            throw new ServiceException(400, "这个台账还没有发布办事状态");
        }
        return ids.get(0);
    }

    /** 已有脉络则沿用第一行记下的版本；新单用当前已发布的最大版本。 */
    private int resolveVersion(String entityType, Long entityId) {
        Long tenantId = tenantId();
        List<Integer> pinned = jdbcTemplate.query(
                "SELECT definition_version FROM dynamic_ledger_state_trace "
                        + "WHERE deleted = FALSE AND tenant_id = ? AND entity_type_code = ? AND entity_id = ? "
                        + "ORDER BY id ASC LIMIT 1",
                (rs, i) -> rs.getInt("definition_version"), tenantId, entityType, entityId);
        if (!pinned.isEmpty()) {
            return pinned.get(0);
        }
        List<Integer> latest = jdbcTemplate.query(
                "SELECT version_no FROM dynamic_ledger_state_machine "
                        + "WHERE deleted = FALSE AND published = TRUE AND tenant_id = ? AND entity_type_code = ? "
                        + "ORDER BY version_no DESC LIMIT 1",
                (rs, i) -> rs.getInt("version_no"), tenantId, entityType);
        if (latest.isEmpty()) {
            throw new ServiceException(400, "这个台账还没有发布办事状态");
        }
        return latest.get(0);
    }

    private String stateName(long machineId, String stateCode) {
        List<String> names = jdbcTemplate.query(
                "SELECT state_name FROM dynamic_ledger_state "
                        + "WHERE deleted = FALSE AND machine_id = ? AND state_code = ? LIMIT 1",
                (rs, i) -> rs.getString("state_name"), machineId, stateCode);
        return names.isEmpty() ? stateCode : names.get(0);
    }

    private void appendTrace(String entityType, Long entityId, int version, String toState, String sentence) {
        insertTrace(entityType, entityId, version, toState, sentence, LocalDateTime.now());
    }

    private void insertTrace(String entityType, Long entityId, int version, String toState,
                             String sentence, LocalDateTime when) {
        jdbcTemplate.update(
                "INSERT INTO dynamic_ledger_state_trace "
                        + "(entity_type_code, entity_id, occurred_at, sentence, to_state, definition_version, "
                        + "creator, updater, tenant_id) VALUES (?,?,?,?,?,?,?,?,?)",
                entityType, entityId, java.sql.Timestamp.valueOf(when), sentence, toState, version,
                String.valueOf(SecurityFrameworkUtils.getLoginUserId()),
                String.valueOf(SecurityFrameworkUtils.getLoginUserId()),
                tenantId());
    }

    private int traceCount(String entityType, Long entityId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM dynamic_ledger_state_trace "
                        + "WHERE deleted = FALSE AND tenant_id = ? AND entity_type_code = ? AND entity_id = ?",
                Integer.class, tenantId(), entityType, entityId);
        return count == null ? 0 : count;
    }

    private boolean allowPage(String pageCode, String action) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null || !StringUtils.hasText(pageCode) || !StringUtils.hasText(action)) {
            return false;
        }
        Boolean elevated = permissionApi.hasAnyRoles(userId, RoleCodeEnum.SUPER_ADMIN.getCode()).getCheckedData();
        if (Boolean.TRUE.equals(elevated)) {
            return true;
        }
        Collection<Long> roleIds = roleApi.getUserRoleIdListByUserId(userId).getCheckedData();
        for (PageRoleGrantDO grant : pageRoleGrantMapper.selectListByRoleIds(roleIds)) {
            if (pageCode.equals(grant.getPageCode()) && action.equals(grant.getAction())) {
                return true;
            }
        }
        return false;
    }

    private void requireFields(String required, Map<String, Object> payload) {
        if (!StringUtils.hasText(required)) {
            return;
        }
        for (String key : required.split(",")) {
            String name = key.trim();
            if (name.isEmpty()) {
                continue;
            }
            Object value = payload.get(name);
            if (value == null || !StringUtils.hasText(String.valueOf(value))) {
                throw new ServiceException(400, "请填写" + fieldLabel(name));
            }
        }
    }

    private static String fieldLabel(String key) {
        return switch (key) {
            case "remark" -> "说明";
            case "teamId" -> "班组";
            case "plannedStart" -> "预计开始";
            case "standardId" -> "作业标准";
            case "title" -> "工单标题";
            default -> key;
        };
    }

    private static String render(String template, Map<String, Object> payload) {
        String team = text(payload.get("teamName"));
        String assignee = text(payload.get("assigneeName"));
        if (!StringUtils.hasText(assignee)) {
            assignee = "待领";
        }
        LocalDateTime start = parseDateTime(payload.get("plannedStart"));
        String startText = start == null ? "" : TRACE_STAMP.format(start);
        return template
                .replace("{班组}", StringUtils.hasText(team) ? team : "")
                .replace("{执行人}", assignee)
                .replace("{开始}", startText)
                .replace("{说明}", text(payload.get("remark")));
    }

    private EntityRespVO requireEntity(Long entityId, String entityType) {
        if (entityId == null || entityId <= 0) {
            throw new ServiceException(400, "单据无效");
        }
        EntityRespVO entity = entityService.get(entityId, entityType);
        if (entity == null) {
            throw new ServiceException(404, "单据不存在");
        }
        return entity;
    }

    private static String currentStatus(EntityRespVO entity) {
        Object value = first(entity.getBaseFields(), "handle_status", "handleStatus");
        if (value == null || !StringUtils.hasText(String.valueOf(value))) {
            throw new ServiceException(400, "这张单还没有办事状态");
        }
        return String.valueOf(value).trim();
    }

    private static String requireType(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, "台账类型不能为空");
        }
        return entityTypeCode.trim();
    }

    private static Long tenantId() {
        return TenantContextHolder.getRequiredTenantId();
    }

    private static Object first(Map<String, Object> bag, String... keys) {
        if (bag == null) {
            return null;
        }
        for (String key : keys) {
            if (bag.get(key) != null) {
                return bag.get(key);
            }
        }
        return null;
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private static Long longValue(Object value) {
        if (value == null || !StringUtils.hasText(String.valueOf(value))) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(value).trim());
    }

    private static LocalDateTime parseDateTime(Object value) {
        if (value == null || !StringUtils.hasText(String.valueOf(value))) {
            return null;
        }
        if (value instanceof LocalDateTime dateTime) {
            return dateTime;
        }
        String text = String.valueOf(value).trim().replace('T', ' ');
        if (text.length() == 16) {
            text = text + ":00";
        }
        try {
            return LocalDateTime.parse(text, DATE_TIME);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private static Object coerceDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            LocalDateTime parsed = parseDateTime(value);
            return parsed == null ? value : DATE_TIME.format(parsed);
        }
        if (value instanceof LocalDateTime dateTime) {
            return DATE_TIME.format(dateTime);
        }
        if (value instanceof Number number) {
            long millis = number.longValue();
            if (millis < 10_000_000_000L) {
                millis = millis * 1000;
            }
            return DATE_TIME.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault()));
        }
        return value;
    }

    private record TransitionRow(
            String toState,
            String pageCode,
            String pageAction,
            String requiredFields,
            String effectCodes,
            String traceTemplate
    ) {
    }
}
