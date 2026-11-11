package cn.cheers.x.module.dynamicbusiness.service.workorder;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo.WorkOrderCatalogCompleteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo.WorkOrderCatalogCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo.WorkOrderCatalogPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo.WorkOrderCatalogRespVO;
import cn.cheers.x.module.dynamicbusiness.dataprocess.AlarmTypeCatalog;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryUserRelationService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 派工、开工、完工都写数据目录 work_order 的实体。
 *
 * 管什么：这条实体上的状态、时间和维修型号字段。
 * 不管什么：报修脉络。报修侧在派工成功后自己记下编号。维修故障报告的原因和措施。
 * 同一条告警的复核工单和维修工单各用自己的编码，互不覆盖。
 * 复核工单完工时，把属实、误报或重复写回来源告警。告警上已经有复核结果时，不再改那一条。
 * 禁止：再写 wo_work_order；读的时候补一条空的原因。告警转入时把告警编号写进报修那一栏。复核工单完工时改填原因和措施。执行人页混进未派报修或别人的单。所属场站、设备和报修只写数字。
 */
@Service
public class WorkOrderCatalogServiceImpl implements WorkOrderCatalogService {

    static final String CATALOG = "work_order";
    static final String STATUS_DISPATCHED = "DISPATCHED";
    static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    static final String STATUS_COMPLETED = "COMPLETED";
    static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String NAME_EQUIPMENT = "设备";
    private static final String NAME_REPAIR = "报修";
    private static final String NAME_TEAM = "班组";
    private static final String NAME_ASSIGNEE = "执行人";
    private static final String NAME_CONTENT = "工作内容";
    private static final String NAME_HANDLING = "处理时刻";
    private static final String NAME_PLAN_START = "计划开始";
    private static final String NAME_PLAN_FINISH = "计划结束";
    private static final String NAME_ACTUAL_START = "实际开工";
    private static final String NAME_ACTUAL_FINISH = "实际完工";
    private static final String NAME_STATUS = "工单状态";
    private static final String NAME_PHENOMENON = "故障现象";
    private static final String NAME_CAUSE = "原因";
    private static final String NAME_REMEDY = "措施";
    private static final String NAME_LABOR = "工时";
    private static final String NAME_SPARE = "备件";

    @Resource
    private EntityService entityService;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private CategoryUserRelationService categoryUserRelationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(WorkOrderCatalogCreateReqVO reqVO) {
        String domain = requireDomain(reqVO.getDomain());
        Long alarmId = reqVO.getAlarmId() != null && reqVO.getAlarmId() > 0 ? reqVO.getAlarmId() : null;
        boolean reviewOrder = alarmId != null && "review".equals(reqVO.getAlarmOrderKind());
        if (alarmId != null && reqVO.getRepairId() != null && reqVO.getRepairId() > 0) {
            throw new ServiceException(400, "告警转入的工单不要再填报修");
        }
        if (alarmId != null) {
            Long existing = findWorkOrderByCode(alarmWorkOrderCode(alarmId, reviewOrder));
            if (existing != null) {
                return existing;
            }
        }
        boolean hasPlanStart = StringUtils.hasText(reqVO.getPlannedStartTime());
        boolean hasPlanFinish = StringUtils.hasText(reqVO.getPlannedFinishTime());
        if (hasPlanStart != hasPlanFinish) {
            throw new ServiceException(400, "计划开始和计划结束要一起填");
        }
        ModelDO model = requireModel(domain);
        Map<String, String> codes = fieldCodes(model.getId());
        Map<String, Object> custom = new LinkedHashMap<>();
        put(custom, codes, NAME_CONTENT, reqVO.getWorkContent().trim());
        put(custom, codes, NAME_TEAM, reqVO.getTeamId());
        put(custom, codes, NAME_STATUS, STATUS_DISPATCHED);
        if (reqVO.getAssigneeUserId() != null && reqVO.getAssigneeUserId() > 0) {
            if (reqVO.getTeamId() == null || reqVO.getTeamId() <= 0) {
                throw new ServiceException(400, "请先选择班组");
            }
            List<Long> members = categoryUserRelationService.listUserIds(reqVO.getTeamId());
            if (members == null || !members.contains(reqVO.getAssigneeUserId())) {
                throw new ServiceException(400, "只能指定这个班组里的人");
            }
            put(custom, codes, NAME_ASSIGNEE, reqVO.getAssigneeUserId());
        }
        if (reqVO.getEquipmentId() != null && reqVO.getEquipmentId() > 0) {
            put(custom, codes, NAME_EQUIPMENT, entityRef("equipment", reqVO.getEquipmentId()));
        }
        if (reqVO.getRepairId() != null && reqVO.getRepairId() > 0) {
            put(custom, codes, NAME_REPAIR, entityRef("maintanance", reqVO.getRepairId()));
        }
        putIfText(custom, codes, NAME_HANDLING, reqVO.getHandlingTime());
        putIfText(custom, codes, NAME_PLAN_START, reqVO.getPlannedStartTime());
        putIfText(custom, codes, NAME_PLAN_FINISH, reqVO.getPlannedFinishTime());
        if ("维修".equals(domain)) {
            put(custom, codes, NAME_PHENOMENON, reqVO.getWorkContent().trim());
        }

        Map<String, Object> base = new LinkedHashMap<>();
        base.put("entityTypeCode", CATALOG);
        base.put("modelId", model.getId());
        base.put("name", reqVO.getTitle().trim());
        base.put("status", 1);
        // 所属场站是关联。要写上场站类型和编号。只写数字会被拒绝。
        base.put("facility_id", entityRef("facility", reqVO.getFacilityId()));
        // 复核工单和维修工单分开编码。同一用途再次转入时，交回已经生成的那一张。
        if (alarmId != null) {
            base.put("code", alarmWorkOrderCode(alarmId, reviewOrder));
        }

        EntityCreateReqVO create = new EntityCreateReqVO();
        create.setRegistryCode(CATALOG);
        create.setBaseFields(base);
        create.setCustomFields(custom);
        Long id = entityService.create(create);
        // 有报修的脉络写在报修上。没有报修时，派单这一行记在工单自己身上。
        if (reqVO.getRepairId() == null || reqVO.getRepairId() <= 0) {
            appendWorkOrderTrace(id, "dispatched", arrangeSentence(reqVO.getHandlingTime()));
        }
        return id;
    }

    @Override
    public WorkOrderCatalogRespVO get(Long id) {
        return toResp(requireEntity(id));
    }

    @Override
    public PageResult<WorkOrderCatalogRespVO> page(WorkOrderCatalogPageReqVO reqVO) {
        String domain = requireDomain(reqVO.getDomain());
        int pageSize = reqVO.getPageSize() == null ? 200 : Math.min(reqVO.getPageSize(), 200);
        String table = TenantPhysicalTableNames.entityPhysicalTable(CATALOG);
        List<WorkOrderCatalogRespVO> rows;
        try {
            rows = jdbcTemplate.query(
                    "SELECT id, name, domain, custom_fields, create_time FROM " + table
                            + " WHERE deleted = FALSE AND domain = ? AND facility_id = ?"
                            + " ORDER BY create_time DESC, id DESC LIMIT ?",
                    (rs, rowNum) -> {
                        WorkOrderCatalogRespVO vo = mapRow(
                                rs.getLong("id"),
                                rs.getString("name"),
                                rs.getString("domain"),
                                parseCustom(rs.getString("custom_fields")));
                        vo.setCreateTime(rs.getString("create_time"));
                        return vo;
                    },
                    domain, reqVO.getFacilityId(), pageSize);
        } catch (DataAccessException ex) {
            throw new ServiceException(400, "工单目录还没有建好");
        }
        List<WorkOrderCatalogRespVO> filtered = new ArrayList<>();
        for (WorkOrderCatalogRespVO row : rows) {
            if (Boolean.TRUE.equals(reqVO.getUnassignedOnly()) && row.getAssigneeUserId() != null) {
                continue;
            }
            if (Boolean.TRUE.equals(reqVO.getScheduledOnly())
                    && (!StringUtils.hasText(row.getPlannedStartTime())
                    || !StringUtils.hasText(row.getPlannedFinishTime()))) {
                continue;
            }
            filtered.add(row);
        }
        if (Boolean.TRUE.equals(reqVO.getForExecutor())) {
            Long userId = SecurityFrameworkUtils.getLoginUserId();
            if (userId == null) {
                throw new ServiceException(403, "请先登录");
            }
            Set<Long> myTeams = new HashSet<>(categoryUserRelationService.listCategoryIds(userId));
            filtered.removeIf(row -> !visibleToExecutor(row, userId, myTeams));
        }
        return new PageResult<>(filtered, (long) filtered.size());
    }

    /** 执行人只看派给自己的单，以及派到自己班组、还没有人接的单。 */
    private static boolean visibleToExecutor(WorkOrderCatalogRespVO row, Long userId, Set<Long> myTeams) {
        if (userId.equals(row.getAssigneeUserId())) {
            return true;
        }
        return row.getAssigneeUserId() == null
                && row.getAssigneeTeamId() != null
                && myTeams.contains(row.getAssigneeTeamId());
    }

    /** 工单上已经挂了报修。这种单的脉络写在报修上，这里不再写第二份。 */
    private boolean hasRepair(EntityRespVO entity, Map<String, String> codes) {
        return longValue(custom(entity).get(codes.get(NAME_REPAIR))) != null;
    }

    /** 没有报修时的派单句。有执行时间就写进这句话。 */
    private static String arrangeSentence(String handlingTime) {
        if (!StringUtils.hasText(handlingTime)) {
            return "预计处理";
        }
        String when = handlingTime.trim();
        if (when.length() >= 16) {
            when = when.substring(0, 16);
        }
        return "预计于 " + when + "处理";
    }

    /** 没有报修的工单，把已经发生的一步追加到自己的脉络。不补还没发生的步骤。 */
    private void appendWorkOrderTrace(Long workOrderId, String toState, String sentence) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        String actor = userId == null ? "" : String.valueOf(userId);
        jdbcTemplate.update(
                "INSERT INTO dynamic_ledger_state_trace "
                        + "(entity_type_code, entity_id, occurred_at, sentence, to_state, definition_version, "
                        + "creator, updater, tenant_id) VALUES (?,?,?,?,?,?,?,?,?)",
                CATALOG, workOrderId, java.sql.Timestamp.valueOf(LocalDateTime.now()), sentence, toState, 1,
                actor, actor, TenantContextHolder.getRequiredTenantId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void start(Long id) {
        EntityRespVO entity = requireEntity(id);
        Map<String, String> codes = fieldCodes(modelId(entity));
        String status = text(custom(entity), codes.get(NAME_STATUS));
        if (!STATUS_DISPATCHED.equals(status)) {
            throw new ServiceException(400, "只有已派工的单可以开工");
        }
        Map<String, Object> patch = new LinkedHashMap<>();
        patch.put(codes.get(NAME_STATUS), STATUS_IN_PROGRESS);
        requireCode(codes, NAME_ACTUAL_START);
        patch.put(codes.get(NAME_ACTUAL_START), LocalDateTime.now().format(DATE_TIME));
        boolean linkedRepair = hasRepair(entity, codes);
        saveCustom(entity, patch);
        if (!linkedRepair) {
            appendWorkOrderTrace(id, "dispatched", "已开始维修");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(WorkOrderCatalogCompleteReqVO reqVO) {
        EntityRespVO entity = requireEntity(reqVO.getId());
        Map<String, String> codes = fieldCodes(modelId(entity));
        String status = text(custom(entity), codes.get(NAME_STATUS));
        if (!STATUS_IN_PROGRESS.equals(status)) {
            throw new ServiceException(400, "只有进行中的单可以完工");
        }
        Map<String, Object> patch = new LinkedHashMap<>();
        Long reviewAlarmId = reviewAlarmId(text(entity.getBaseFields(), "code"));
        if (reviewAlarmId != null) {
            // 复核工单借用维修型号落库。完工填的是复核结果，不填故障报告的原因和措施。
            writeReviewResultToAlarm(reviewAlarmId, reqVO.getReviewResult());
        } else if ("维修".equals(domainOf(entity))) {
            String cause = reqVO.getCause() == null ? "" : reqVO.getCause().trim();
            String remedy = reqVO.getRemedy() == null ? "" : reqVO.getRemedy().trim();
            if (!StringUtils.hasText(cause)) {
                throw new ServiceException(400, "请填写故障原因");
            }
            if (!StringUtils.hasText(remedy)) {
                throw new ServiceException(400, "请填写处理措施");
            }
            put(patch, codes, NAME_CAUSE, cause);
            put(patch, codes, NAME_REMEDY, remedy);
            if (StringUtils.hasText(reqVO.getLaborHours())) {
                put(patch, codes, NAME_LABOR, reqVO.getLaborHours().trim());
            }
            if (reqVO.getSparePartId() != null && reqVO.getSparePartId() > 0) {
                put(patch, codes, NAME_SPARE, reqVO.getSparePartId());
            }
        }
        patch.put(requireCode(codes, NAME_STATUS), STATUS_COMPLETED);
        patch.put(requireCode(codes, NAME_ACTUAL_FINISH), LocalDateTime.now().format(DATE_TIME));
        boolean linkedRepair = hasRepair(entity, codes);
        saveCustom(entity, patch);
        if (!linkedRepair) {
            appendWorkOrderTrace(reqVO.getId(), "dispatched", "维修已完工");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claim(Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            throw new ServiceException(403, "没有该页面的操作权限");
        }
        assign(id, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assign(Long id, Long assigneeUserId) {
        if (assigneeUserId == null || assigneeUserId <= 0) {
            throw new ServiceException(400, "请选择执行人");
        }
        EntityRespVO entity = requireEntity(id);
        Map<String, String> codes = fieldCodes(modelId(entity));
        Long teamId = longValue(custom(entity).get(codes.get(NAME_TEAM)));
        if (teamId == null) {
            throw new ServiceException(400, "这张单还没有班组");
        }
        List<Long> members = categoryUserRelationService.listUserIds(teamId);
        if (members == null || !members.contains(assigneeUserId)) {
            throw new ServiceException(400, "只有这个班组里的人可以认领或被指定");
        }
        String status = text(custom(entity), codes.get(NAME_STATUS));
        if (STATUS_COMPLETED.equals(status)) {
            throw new ServiceException(400, "已经完工的单不能改派");
        }
        Map<String, Object> patch = new LinkedHashMap<>();
        patch.put(requireCode(codes, NAME_ASSIGNEE), assigneeUserId);
        saveCustom(entity, patch);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void release(Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            throw new ServiceException(403, "没有该页面的操作权限");
        }
        EntityRespVO entity = requireEntity(id);
        Map<String, String> codes = fieldCodes(modelId(entity));
        Map<String, Object> custom = new LinkedHashMap<>(custom(entity));
        Long assignee = longValue(custom.get(codes.get(NAME_ASSIGNEE)));
        if (assignee == null || !assignee.equals(userId)) {
            throw new ServiceException(400, "只有当前执行人可以退回");
        }
        if (!STATUS_DISPATCHED.equals(text(custom, codes.get(NAME_STATUS)))) {
            throw new ServiceException(400, "已经开工的单不能退回");
        }
        custom.remove(requireCode(codes, NAME_ASSIGNEE));
        replaceCustom(entity, custom);
    }

    private WorkOrderCatalogRespVO toResp(EntityRespVO entity) {
        Map<String, String> codes = fieldCodes(modelId(entity));
        WorkOrderCatalogRespVO vo = mapRow(entity.getId(), entity.getName(), domainOf(entity), custom(entity));
        vo.setWorkContent(text(custom(entity), codes.get(NAME_CONTENT)));
        Long reviewAlarmId = reviewAlarmId(text(entity.getBaseFields(), "code"));
        if (reviewAlarmId != null) {
            vo.setSourceAlarmId(reviewAlarmId);
            vo.setAlarmOrderKind("review");
        }
        return vo;
    }

    private WorkOrderCatalogRespVO mapRow(Long id, String name, String domain, Map<String, Object> custom) {
        ModelDO model = requireModel(domain);
        Map<String, String> codes = fieldCodes(model.getId());
        WorkOrderCatalogRespVO vo = new WorkOrderCatalogRespVO();
        vo.setId(id);
        vo.setWoNo("WO-" + id);
        vo.setTitle(name);
        vo.setScope(domain);
        vo.setWorkContent(text(custom, codes.get(NAME_CONTENT)));
        vo.setStatus(text(custom, codes.get(NAME_STATUS)));
        vo.setAssigneeTeamId(longValue(custom.get(codes.get(NAME_TEAM))));
        vo.setAssigneeUserId(longValue(custom.get(codes.get(NAME_ASSIGNEE))));
        vo.setAssetId(longValue(custom.get(codes.get(NAME_EQUIPMENT))));
        vo.setHandlingTime(text(custom, codes.get(NAME_HANDLING)));
        vo.setPlannedStartTime(text(custom, codes.get(NAME_PLAN_START)));
        vo.setPlannedFinishTime(text(custom, codes.get(NAME_PLAN_FINISH)));
        vo.setActualStartTime(text(custom, codes.get(NAME_ACTUAL_START)));
        vo.setActualFinishTime(text(custom, codes.get(NAME_ACTUAL_FINISH)));
        vo.setPhenomenon(text(custom, codes.get(NAME_PHENOMENON)));
        vo.setCause(text(custom, codes.get(NAME_CAUSE)));
        vo.setRemedy(text(custom, codes.get(NAME_REMEDY)));
        vo.setLaborHours(text(custom, codes.get(NAME_LABOR)));
        Long repairId = longValue(custom.get(codes.get(NAME_REPAIR)));
        vo.setBusinessKey(repairId == null ? "manual" : "repair:" + repairId);
        return vo;
    }

    private void saveCustom(EntityRespVO entity, Map<String, Object> patch) {
        Map<String, Object> base = new LinkedHashMap<>();
        if (entity.getBaseFields() != null) {
            base.putAll(entity.getBaseFields());
        }
        base.put("entityTypeCode", CATALOG);
        base.put("modelId", modelId(entity));
        if (!StringUtils.hasText(text(base, "name"))) {
            base.put("name", entity.getName());
        }
        if (base.get("status") == null) {
            base.put("status", 1);
        }
        Map<String, Object> custom = new LinkedHashMap<>(custom(entity));
        custom.putAll(patch);
        EntityUpdateReqVO update = new EntityUpdateReqVO();
        update.setId(entity.getId());
        update.setRegistryCode(CATALOG);
        update.setBaseFields(base);
        update.setCustomFields(custom);
        entityService.update(update);
    }

    /** 整包替换工单内容。退回要删掉执行人，合并补丁做不到。 */
    private void replaceCustom(EntityRespVO entity, Map<String, Object> custom) {
        Map<String, Object> base = new LinkedHashMap<>();
        if (entity.getBaseFields() != null) {
            base.putAll(entity.getBaseFields());
        }
        base.put("entityTypeCode", CATALOG);
        base.put("modelId", modelId(entity));
        if (!StringUtils.hasText(text(base, "name"))) {
            base.put("name", entity.getName());
        }
        if (base.get("status") == null) {
            base.put("status", 1);
        }
        EntityUpdateReqVO update = new EntityUpdateReqVO();
        update.setId(entity.getId());
        update.setRegistryCode(CATALOG);
        update.setBaseFields(base);
        update.setCustomFields(custom);
        entityService.update(update);
    }

    private ModelDO requireModel(String domain) {
        List<ModelDO> models = modelMapper.selectList(new LambdaQueryWrapper<ModelDO>()
                .eq(ModelDO::getEntityTypeCode, CATALOG)
                .eq(ModelDO::getDomain, domain));
        ModelDO matched = null;
        for (ModelDO model : models) {
            if (domain.equals(model.getName())) {
                return model;
            }
            matched = model;
        }
        if (matched != null) {
            return matched;
        }
        throw new ServiceException(400, "工单目录里还没有「" + domain + "」型号");
    }

    private Map<String, String> fieldCodes(Long modelId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT a.field_code, f.name FROM dynamicbusiness.dynamic_model_field_assignment a "
                        + "JOIN dynamicbusiness.dynamic_field f ON f.id = a.field_id "
                        + "WHERE a.model_id = ? AND a.deleted = FALSE AND f.deleted = FALSE",
                modelId);
        Map<String, String> codes = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String name = text(row, "name");
            String code = text(row, "field_code");
            if (StringUtils.hasText(name) && StringUtils.hasText(code)) {
                codes.put(name, code);
            }
        }
        return codes;
    }

    private EntityRespVO requireEntity(Long id) {
        if (id == null || id <= 0) {
            throw new ServiceException(400, "工单无效");
        }
        EntityRespVO entity = entityService.get(id, CATALOG);
        if (entity == null) {
            throw new ServiceException(404, "工单不存在");
        }
        return entity;
    }

    private String domainOf(EntityRespVO entity) {
        String domain = text(entity.getBaseFields(), "domain");
        if (StringUtils.hasText(domain)) {
            return domain;
        }
        ModelDO model = modelMapper.selectById(modelId(entity));
        if (model == null || !StringUtils.hasText(model.getDomain())) {
            throw new ServiceException(400, "这张工单还没有业务域");
        }
        return model.getDomain();
    }

    private static Long modelId(EntityRespVO entity) {
        Long id = longValue(entity.getBaseFields() == null ? null : entity.getBaseFields().get("modelId"));
        if (id == null) {
            id = longValue(entity.getBaseFields() == null ? null : entity.getBaseFields().get("model_id"));
        }
        if (id == null) {
            throw new ServiceException(400, "这张工单还没有型号");
        }
        return id;
    }

    private static Map<String, Object> custom(EntityRespVO entity) {
        return entity.getCustomFields() == null ? Map.of() : entity.getCustomFields();
    }

    /** 关联字段保存时要带上类型和编号。只写编号，保存会拒绝。 */
    private static Map<String, Object> entityRef(String entityTypeCode, Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        Map<String, Object> ref = new LinkedHashMap<>();
        ref.put("entityTypeCode", entityTypeCode);
        ref.put("id", id);
        return ref;
    }

    private static void put(Map<String, Object> target, Map<String, String> codes, String name, Object value) {
        target.put(requireCode(codes, name), value);
    }

    private static void putIfText(Map<String, Object> target, Map<String, String> codes, String name, String value) {
        if (StringUtils.hasText(value)) {
            put(target, codes, name, value.trim());
        }
    }

    private static String requireCode(Map<String, String> codes, String name) {
        String code = codes.get(name);
        if (!StringUtils.hasText(code)) {
            throw new ServiceException(400, "这个型号还没有字段「" + name + "」");
        }
        return code;
    }

    /** 告警转入的工单用这个编码。复核和维修各一张，不是给人看的标题。 */
    static String alarmWorkOrderCode(Long alarmId, boolean reviewOrder) {
        return (reviewOrder ? "review-alarm-" : "alarm-") + alarmId;
    }

    /** 复核工单编码里的告警编号。维修工单编码不从这里取。 */
    static Long reviewAlarmId(String code) {
        String prefix = "review-alarm-";
        if (!StringUtils.hasText(code) || !code.startsWith(prefix)) {
            return null;
        }
        try {
            long id = Long.parseLong(code.substring(prefix.length()));
            return id > 0 ? id : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * 执行人在复核工单上选的属实、误报或重复，写回来源告警。
     * 告警上已经有复核结果时不改。缺复核要写的栏时失败，工单也不完工。
     */
    private void writeReviewResultToAlarm(Long alarmId, String reviewResult) {
        String result = reviewResult == null ? "" : reviewResult.trim();
        if (!"属实".equals(result) && !"误报".equals(result) && !"重复".equals(result)) {
            throw new ServiceException(400, "请选择复核结果");
        }
        EntityRespVO alarm = entityService.get(alarmId, AlarmTypeCatalog.DIRECTORY);
        if (alarm == null) {
            throw new ServiceException(400, "没有找到这条告警");
        }
        Map<String, String> codes = fieldCodes(alarmModelId(alarm));
        String handlingCode = codes.get(AlarmTypeCatalog.FIELD_HANDLING);
        String reviewCode = codes.get(AlarmTypeCatalog.FIELD_REVIEW);
        String traceCode = codes.get(AlarmTypeCatalog.FIELD_TRACE);
        if (!StringUtils.hasText(handlingCode) || !StringUtils.hasText(reviewCode) || !StringUtils.hasText(traceCode)) {
            throw new ServiceException(400, "这条告警缺复核结果要写的栏");
        }
        Map<String, Object> base = new LinkedHashMap<>();
        if (alarm.getBaseFields() != null) {
            base.putAll(alarm.getBaseFields());
        }
        Map<String, Object> custom = new LinkedHashMap<>(custom(alarm));
        String currentReview = text(custom, reviewCode);
        if (!StringUtils.hasText(currentReview)) {
            currentReview = text(base, reviewCode);
        }
        if ("已复核".equals(currentReview)) {
            return;
        }
        String status = "属实".equals(result) ? "复核中" : "已处理";
        String previous = text(custom, traceCode);
        if (!StringUtils.hasText(previous)) {
            previous = text(base, traceCode);
        }
        String line = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                + " 复核工单完工。结果" + result + "。复核改为已复核。状态改为" + status + "。";
        custom.put(handlingCode, status);
        custom.put(reviewCode, "已复核");
        custom.put(traceCode, StringUtils.hasText(previous) ? previous + "\n" + line : line);
        base.remove(handlingCode);
        base.remove(reviewCode);
        base.remove(traceCode);
        base.put("entityTypeCode", AlarmTypeCatalog.DIRECTORY);
        EntityUpdateReqVO update = new EntityUpdateReqVO();
        update.setId(alarmId);
        update.setRegistryCode(AlarmTypeCatalog.DIRECTORY);
        update.setBaseFields(base);
        update.setCustomFields(custom);
        entityService.update(update);
    }

    private static Long alarmModelId(EntityRespVO alarm) {
        Long id = longValue(alarm.getBaseFields() == null ? null : alarm.getBaseFields().get("modelId"));
        if (id == null) {
            id = longValue(alarm.getBaseFields() == null ? null : alarm.getBaseFields().get("model_id"));
        }
        if (id == null) {
            throw new ServiceException(400, "这条告警还没有型号");
        }
        return id;
    }

    private Long findWorkOrderByCode(String code) {
        String table = TenantPhysicalTableNames.entityPhysicalTable(CATALOG);
        try {
            List<Long> ids = jdbcTemplate.query(
                    "SELECT id FROM " + table + " WHERE deleted = FALSE AND code = ? ORDER BY id DESC LIMIT 1",
                    (rs, rowNum) -> rs.getLong("id"),
                    code);
            return ids.isEmpty() ? null : ids.get(0);
        } catch (DataAccessException ex) {
            throw new ServiceException(400, "工单目录还没有建好");
        }
    }

    private static String requireDomain(String domain) {
        String value = domain == null ? "" : domain.trim();
        if (!StringUtils.hasText(value)) {
            throw new ServiceException(400, "请选择业务域");
        }
        return value;
    }

    private static Map<String, Object> parseCustom(String raw) {
        if (!StringUtils.hasText(raw)) {
            return new LinkedHashMap<>();
        }
        JSONObject obj = JSONUtil.parseObj(raw);
        Map<String, Object> map = new LinkedHashMap<>();
        for (String key : obj.keySet()) {
            map.put(key, obj.get(key));
        }
        return map;
    }

    private static String text(Map<String, ?> map, String key) {
        if (map == null || key == null) {
            return null;
        }
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() || "null".equals(text) ? null : text;
    }

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
        try {
            long parsed = Long.parseLong(String.valueOf(value).trim());
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
