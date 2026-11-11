package cn.cheers.x.module.dynamicbusiness.service.repair;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.pagegrant.PageRoleGrantDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.pagegrant.PageRoleGrantMapper;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.UserAccountDTO;
import cn.cheers.x.module.dynamicbusiness.service.directory.UserAccountDirectoryQueryService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.system.api.permission.PermissionApi;
import cn.cheers.x.system.api.permission.RoleApi;
import cn.cheers.x.system.enums.permission.RoleCodeEnum;
import cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo.WorkOrderCatalogCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.service.workorder.WorkOrderCatalogService;
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
import java.util.Map;
import java.util.Set;

/**
 * 报修单状态写在报修单上，工单状态写在工单上。
 *
 * 受理、回退、派工直接改报修单的处置状态，并追加一行给报修人看的进度。回退必须写原因。
 * 派工在工单目录新建一条维修型号的实体，报修单只记下它的编号。开工只加进度，报修单保持已派工。
 * 完工时没有验收审批，所以报修单改为已关闭。这里不调用流程引擎。
 */
@Service
public class RepairRecordServiceImpl implements RepairRecordService {

    static final String REPAIR_TYPE = "maintanance";
    static final String ACCEPT_PAGE = "repair-accept";
    static final String REPORT_PAGE = "repair-request";
    static final String MAINTAIN = "maintain";
    static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    static final DateTimeFormatter TRACE_STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    static final Set<String> OPEN_FOR_ACCEPT = Set.of("submitted", "need_info");

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
    private WorkOrderCatalogService workOrderCatalogService;
    @Resource
    private UserAccountDirectoryQueryService userAccountDirectoryQueryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordSubmitted(Long repairId) {
        requireReportPage();
        EntityRespVO entity = requireRepair(repairId);
        requireStatus(entity, Set.of("submitted"), "只有已上报的单子补这一行");
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM dynamic_ledger_state_trace "
                        + "WHERE entity_type_code = ? AND entity_id = ? AND tenant_id = ? AND deleted = FALSE",
                Integer.class, REPAIR_TYPE, repairId, TenantContextHolder.getRequiredTenantId());
        if (count != null && count > 0) {
            return;
        }
        appendTrace(repairId, "submitted", withPerson("上报", currentUserName()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void accept(Long repairId) {
        requireAcceptPage();
        EntityRespVO entity = requireRepair(repairId);
        requireStatus(entity, OPEN_FOR_ACCEPT, "只有已上报或已回退的单可以受理");
        Map<String, Object> staged = new LinkedHashMap<>();
        staged.put("handle_status", "accepted");
        staged.put("handle_time", DATE_TIME.format(LocalDateTime.now()));
        writeEntity(entity, staged);
        appendTrace(repairId, "accepted", withPerson("受理", currentUserName()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long repairId, String remark) {
        requireAcceptPage();
        String note = requireRemark(remark);
        EntityRespVO entity = requireRepair(repairId);
        requireStatus(entity, OPEN_FOR_ACCEPT, "只有已上报或已回退的单可以驳回");
        Map<String, Object> staged = new LinkedHashMap<>();
        staged.put("handle_status", "rejected");
        staged.put("handle_remark", note);
        writeEntity(entity, staged);
        appendTrace(repairId, "rejected", "已驳回：" + note);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void needInfo(Long repairId, String remark) {
        requireAcceptPage();
        if (!StringUtils.hasText(remark)) {
            throw new ServiceException(400, "请填写回退原因");
        }
        String note = remark.trim();
        EntityRespVO entity = requireRepair(repairId);
        if (hasWorkOrder(entity)) {
            throw new ServiceException(400, "已经派工的报修不能回退");
        }
        requireStatus(entity, Set.of("submitted", "need_info", "accepted"), "只有还没派工的报修可以回退");
        Map<String, Object> staged = new LinkedHashMap<>();
        staged.put("handle_status", "need_info");
        staged.put("handle_remark", note);
        writeEntity(entity, staged);
        appendTrace(repairId, "need_info", "已回退：" + note);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resubmit(Long repairId) {
        requireReportPage();
        EntityRespVO entity = requireRepair(repairId);
        requireStatus(entity, Set.of("need_info"), "只有已回退的单可以交回");
        writeEntity(entity, Map.of("handle_status", "submitted"));
        appendTrace(repairId, "submitted", "已补充材料");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dispatch(Long repairId, Map<String, Object> payload) {
        requireAcceptPage();
        EntityRespVO entity = requireRepair(repairId);
        requireStatus(entity, Set.of("accepted"), "只有已受理的单可以派工");
        Map<String, Object> body = new LinkedHashMap<>();
        if (payload != null) {
            body.putAll(payload);
        }
        // 执行时间由派单人写下。空着或认不出就不派，不用当前时刻代替。
        if (parseDateTime(body.get("handlingTime")) == null) {
            throw new ServiceException(400, "请填写执行时间");
        }
        Long workOrderId = createWorkOrder(entity, body);
        Map<String, Object> staged = new LinkedHashMap<>();
        staged.put("handle_status", "dispatched");
        staged.put("work_order_id", workOrderId);
        writeEntity(entity, staged);
        appendTrace(repairId, "dispatched", renderDispatchSentence(body));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void noteStarted(Long repairId, Long workOrderId) {
        EntityRespVO entity = requireRepair(repairId);
        requireLinkedWorkOrder(entity, workOrderId);
        requireStatus(entity, Set.of("dispatched"), "只有已派工的报修才记录开工");
        appendTrace(repairId, "dispatched", "已开始维修");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishFromWorkOrder(Long repairId, Long workOrderId) {
        EntityRespVO entity = requireRepair(repairId);
        requireLinkedWorkOrder(entity, workOrderId);
        requireStatus(entity, Set.of("dispatched"), "只有已派工的报修才随工单完工关闭");
        appendTrace(repairId, "dispatched", "维修已完工");
        Map<String, Object> staged = new LinkedHashMap<>();
        staged.put("handle_status", "closed");
        writeEntity(entity, staged);
        appendTrace(repairId, "closed", "已关闭");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void noteAssignee(Long repairId, Long workOrderId, String personName, boolean reassign) {
        EntityRespVO entity = requireRepair(repairId);
        requireLinkedWorkOrder(entity, workOrderId);
        requireStatus(entity, Set.of("dispatched"), "只有已派工的报修才记录人员变更");
        if (!StringUtils.hasText(personName)) {
            throw new ServiceException(400, "请填写人员");
        }
        String name = personName.trim();
        appendTrace(repairId, "dispatched", reassign ? "已改派给" + name : "已由" + name + "领走");
    }

    private Long createWorkOrder(EntityRespVO entity, Map<String, Object> payload) {
        String workContent = text(payload.get("workContent"));
        if (!StringUtils.hasText(workContent)) {
            throw new ServiceException(400, "请填写工作内容");
        }
        Long facilityId = longValue(first(entity.getBaseFields(), "facility_id", "facilityId"));
        if (facilityId == null) {
            throw new ServiceException(400, "这张报修还没有所属场站");
        }
        WorkOrderCatalogCreateReqVO req = new WorkOrderCatalogCreateReqVO();
        String domain = text(payload.get("scope"));
        req.setDomain(StringUtils.hasText(domain) ? domain : "维修");
        req.setTitle(text(payload.get("title")));
        req.setWorkContent(workContent);
        req.setTeamId(longValue(payload.get("teamId")));
        Long assignee = longValue(payload.get("assigneeUserId"));
        req.setAssigneeUserId(assignee != null && assignee > 0 ? assignee : null);
        // 设备只认报修或派工传来的设备编号。不把所属场站写进设备引用。
        Long equipmentId = longValue(payload.get("assetId"));
        if (equipmentId == null) {
            equipmentId = longValue(first(entity.getBaseFields(), "equipment_id", "equipmentId"));
        }
        req.setEquipmentId(equipmentId);
        req.setRepairId(entity.getId());
        req.setFacilityId(facilityId);
        // 执行时间必须是派单人写下的。认不出就失败，不改用当前时刻。
        LocalDateTime handling = parseDateTime(payload.get("handlingTime"));
        if (handling == null) {
            throw new ServiceException(400, "请填写执行时间");
        }
        req.setHandlingTime(DATE_TIME.format(handling));
        return workOrderCatalogService.create(req);
    }

    private void writeEntity(EntityRespVO current, Map<String, Object> overrides) {
        Map<String, Object> prev = current.getBaseFields() == null ? Map.of() : current.getBaseFields();
        Map<String, Object> base = new LinkedHashMap<>(prev);
        base.put("entityTypeCode", REPAIR_TYPE);
        for (String key : new ArrayList<>(base.keySet())) {
            if (key.endsWith("_time") || key.endsWith("Time")) {
                base.put(key, coerceDateTime(base.get(key)));
            }
        }
        base.putAll(overrides);
        EntityUpdateReqVO req = new EntityUpdateReqVO();
        req.setId(current.getId());
        req.setRegistryCode(REPAIR_TYPE);
        req.setBaseFields(base);
        req.setCustomFields(current.getCustomFields() == null ? Map.of() : current.getCustomFields());
        entityService.update(req);
    }

    private void appendTrace(Long repairId, String toState, String sentence) {
        LocalDateTime when = LocalDateTime.now();
        jdbcTemplate.update(
                "INSERT INTO dynamic_ledger_state_trace "
                        + "(entity_type_code, entity_id, occurred_at, sentence, to_state, definition_version, "
                        + "creator, updater, tenant_id) VALUES (?,?,?,?,?,?,?,?,?)",
                REPAIR_TYPE, repairId, java.sql.Timestamp.valueOf(when), sentence, toState, 1,
                String.valueOf(SecurityFrameworkUtils.getLoginUserId()),
                String.valueOf(SecurityFrameworkUtils.getLoginUserId()),
                TenantContextHolder.getRequiredTenantId());
    }

    private void requireAcceptPage() {
        requirePage(ACCEPT_PAGE);
    }

    /** 报修人补材料并交回，认「我的报修」的维护权限。 */
    private void requireReportPage() {
        requirePage(REPORT_PAGE);
    }

    private void requirePage(String pageCode) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            throw new ServiceException(403, "没有该页面的操作权限");
        }
        Boolean elevated = permissionApi.hasAnyRoles(userId, RoleCodeEnum.SUPER_ADMIN.getCode()).getCheckedData();
        if (Boolean.TRUE.equals(elevated)) {
            return;
        }
        Collection<Long> roleIds = roleApi.getUserRoleIdListByUserId(userId).getCheckedData();
        for (PageRoleGrantDO grant : pageRoleGrantMapper.selectListByRoleIds(roleIds)) {
            if (pageCode.equals(grant.getPageCode()) && MAINTAIN.equals(grant.getAction())) {
                return;
            }
        }
        throw new ServiceException(403, "没有该页面的操作权限");
    }

    private EntityRespVO requireRepair(Long repairId) {
        if (repairId == null || repairId <= 0) {
            throw new ServiceException(400, "报修单无效");
        }
        EntityRespVO entity = entityService.get(repairId, REPAIR_TYPE);
        if (entity == null) {
            throw new ServiceException(404, "报修单不存在");
        }
        return entity;
    }

    private static void requireStatus(EntityRespVO entity, Set<String> allowed, String message) {
        Object value = first(entity.getBaseFields(), "handle_status", "handleStatus");
        String status = value == null ? "" : String.valueOf(value).trim();
        if (!allowed.contains(status)) {
            throw new ServiceException(400, message);
        }
    }

    /** 已经写上工单编号的报修不能再回退。 */
    private static boolean hasWorkOrder(EntityRespVO entity) {
        Object linked = first(entity.getBaseFields(), "work_order_id", "workOrderId");
        if (linked instanceof Map<?, ?> map) {
            linked = map.get("id");
        }
        try {
            Long id = longValue(linked);
            return id != null && id > 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private static void requireLinkedWorkOrder(EntityRespVO entity, Long workOrderId) {
        Object linked = first(entity.getBaseFields(), "work_order_id", "workOrderId");
        if (workOrderId == null || linked == null || !String.valueOf(workOrderId).equals(String.valueOf(linked))) {
            throw new ServiceException(400, "这张工单不是这张报修派出去的");
        }
    }

    private static String requireRemark(String remark) {
        if (!StringUtils.hasText(remark)) {
            throw new ServiceException(400, "请填写说明");
        }
        return remark.trim();
    }

    private static String renderDispatchSentence(Map<String, Object> payload) {
        String team = text(payload.get("teamName"));
        String assignee = text(payload.get("assigneeName"));
        String who = StringUtils.hasText(assignee) ? assignee : team;
        LocalDateTime when = parseDateTime(payload.get("handlingTime"));
        String whenText = when == null ? "" : TRACE_STAMP.format(when);
        if (!StringUtils.hasText(who)) {
            return StringUtils.hasText(whenText) ? "预计于 " + whenText + "处理" : "预计处理";
        }
        return StringUtils.hasText(whenText)
                ? who + " 预计于 " + whenText + "处理"
                : who + " 预计处理";
    }

    /** 脉络里写目录里的名字。上报人、受理人是当时登录的这个人。 */
    private String currentUserName() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null || userId <= 0) {
            return "";
        }
        UserAccountDTO user = userAccountDirectoryQueryService.getUserAccount(userId);
        return user == null || !StringUtils.hasText(user.getNickname()) ? "" : user.getNickname().trim();
    }

    private static String withPerson(String action, String name) {
        return StringUtils.hasText(name) ? action + " " + name : action;
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

    /** 详情里的关联是 {id, name}。派工只取里面的编号。纯数字也认。对不上就返回空，不把整段对象当成数字。 */
    private static Long longValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            long parsed = number.longValue();
            return parsed > 0 ? parsed : null;
        }
        if (value instanceof Map<?, ?> map) {
            return longValue(map.get("id"));
        }
        String raw = String.valueOf(value).trim();
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        try {
            long parsed = Long.parseLong(raw);
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static LocalDateTime parseDateTime(Object value) {
        if (value == null || !StringUtils.hasText(String.valueOf(value))) {
            return null;
        }
        if (value instanceof LocalDateTime dateTime) {
            return dateTime;
        }
        String raw = String.valueOf(value).trim().replace('T', ' ');
        if (raw.length() == 16) {
            raw = raw + ":00";
        }
        try {
            return LocalDateTime.parse(raw, DATE_TIME);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private static Object coerceDateTime(Object value) {
        if (value == null || value instanceof String) {
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
}
