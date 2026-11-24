package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.module.dynamicbusiness.service.facility.FacilityStationQueryService;
import cn.cheers.x.module.dynamicbusiness.service.facility.StationOwnedCatalogSetup;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.capability.BusinessCapabilityService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeService;
import cn.cheers.x.module.dynamicbusiness.service.field.FieldService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelFieldAssignmentService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 在数据管理里准备告警规则目录。
 * 一本目录。高限、低限、开关量各是一个模型，详情只出现这一类要填的数。
 * 实体是某种设备上的一份具体参数。氧气过高、过低各一条。二氧化碳只建过高。
 * 分类沿用告警目录那棵树的名称。每条规则归到其中一个分类。
 * 不在策略上写高于、低于或比较值。策略只去选这里的实体。
 * 原因、后果、联动、允许响应时间写在规则上，不参与比较。
 * 这四项还空着时，按这条规则已经定下的含义补上。人改过的字不再覆盖。
 * 不负责比较读数，也不写告警记录。
 */
@Service
public class AlarmRuleCatalogEnsure {

    public static final String DIRECTORY = "alarm_rule";
    public static final String DIRECTORY_NAME = "告警规则";
    public static final String MODEL_THRESHOLD = "阈值告警";
    /** 读数升高才告警。详情只有「高于这个数告警」和「回落到此数恢复」。 */
    public static final String MODEL_HIGH = "高限告警";
    /** 读数降低才告警。详情只有「低于这个数告警」和「回升到此数恢复」。 */
    public static final String MODEL_LOW = "低限告警";
    /** 等于或不等于一个数才告警。故障、水位开关用这个。 */
    public static final String MODEL_SWITCH = "开关量告警";
    public static final String MODEL_RATE = "变化率告警";
    public static final String MODEL_PATTERN = "异常模式告警";
    public static final String MODEL_COMBINATION = "组合条件告警";
    public static final String FIELD_DEVICE_TYPE = "型号";
    public static final String FIELD_POINT = "点位编号";
    public static final String FIELD_TYPE = "告警类型";
    public static final String FIELD_HIGH = "高于";
    public static final String FIELD_LOW = "低于";
    public static final String FIELD_HIGH_RECOVER = "高于恢复";
    public static final String FIELD_LOW_RECOVER = "低于恢复";
    public static final String FIELD_OP = "比较方式";
    public static final String FIELD_VALUE = "比较值";
    public static final String FIELD_RATE = "变化率限值";
    public static final String FIELD_WINDOW = "时间窗";
    public static final String FIELD_PATTERN = "模式";
    public static final String FIELD_LOGIC = "逻辑";
    public static final String FIELD_CHILDREN = "子规则";
    private static final String GALLERY_TENANT_NAME = "智慧管廊";
    private static final String OP_OPTIONS = """
            [{"label":"大于","value":"GT"},{"label":"大于等于","value":"GTE"},{"label":"小于","value":"LT"},{"label":"小于等于","value":"LTE"},{"label":"等于","value":"EQ"},{"label":"不等于","value":"NEQ"}]""";
    private static final String LOGIC_OPTIONS = """
            [{"label":"并且","value":"AND"},{"label":"或者","value":"OR"}]""";

    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private EntityTypeService entityTypeService;
    @Resource
    private FacilityStationQueryService facilityStationQueryService;
    @Resource
    private StationOwnedCatalogSetup stationOwnedCatalogSetup;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private FieldService fieldService;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ModelService modelService;
    @Resource
    private ModelFieldAssignmentService modelFieldAssignmentService;
    @Resource
    private BusinessCapabilityService businessCapabilityService;
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
    private CategoryTypeMapper categoryTypeMapper;
    @Resource
    private CategoryService categoryService;
    @Resource
    private JdbcTemplate jdbcTemplate;

    @Transactional(rollbackFor = Exception.class)
    public void ensure() {
        if (SecurityFrameworkUtils.getLoginUserId() == null) {
            throw new ServiceException(401, "没有登录用户，告警规则目录没有准备");
        }
        alarmCatalogEnsure.ensure();
        ensureDirectory();
        Long highId = ensureModel(MODEL_HIGH, "读数升高才告警。只填高于这个数告警，和回落后的恢复。");
        Long lowId = ensureModel(MODEL_LOW, "读数降低才告警。只填低于这个数告警，和回升后的恢复。");
        Long switchId = ensureModel(MODEL_SWITCH, "开关量。读数等于或不等于一个数才告警。");
        Long rateId = ensureModel(MODEL_RATE, "读数在一段时间里变得太快");
        Long patternId = ensureModel(MODEL_PATTERN, "读数对上设定的模式");
        Long combinationId = ensureModel(MODEL_COMBINATION, "几条已有规则按并且、或者一起成立");
        assignLimit(highId, FIELD_HIGH, "高于这个数告警", FIELD_HIGH_RECOVER, "回落到此数恢复");
        assignLimit(lowId, FIELD_LOW, "低于这个数告警", FIELD_LOW_RECOVER, "回升到此数恢复");
        assignShared(switchId, true);
        assign(switchId, ensureEnum(FIELD_OP, OP_OPTIONS));
        assign(switchId, ensureText(FIELD_VALUE));
        moveCombinedThresholdRules(highId, lowId, switchId);
        assignShared(rateId, true);
        assign(rateId, ensureText(FIELD_RATE));
        assign(rateId, ensureText(FIELD_WINDOW));
        assign(rateId, ensureEnum(FIELD_OP, OP_OPTIONS));
        assignShared(patternId, true);
        assign(patternId, ensureText(FIELD_PATTERN));
        assignShared(combinationId, false);
        assign(combinationId, ensureEnum(FIELD_LOGIC, LOGIC_OPTIONS));
        assign(combinationId, ensureChildren());
        businessCapabilityService.refreshModelCrudFormDefinition(highId);
        businessCapabilityService.refreshModelCrudFormDefinition(lowId);
        businessCapabilityService.refreshModelCrudFormDefinition(switchId);
        businessCapabilityService.refreshModelCrudFormDefinition(rateId);
        businessCapabilityService.refreshModelCrudFormDefinition(patternId);
        businessCapabilityService.refreshModelCrudFormDefinition(combinationId);
        // 告警记录上的原因、后果、联动按长文本重画。规则准备完，把告警型号的表单一起刷。
        businessCapabilityService.refreshModelCrudFormDefinition(alarmCatalogEnsure.modelId());
        ensureCategories();
        if (isGalleryTenant()) {
            seedGalleryRules();
        }
    }

    /** 按编号找到规则实体。没有就返回空，调用方不另造一条。 */
    public Long entityIdByCode(String code) {
        EntityRespDTO existing = entityRpcFacadeService.getByCode(code, DIRECTORY);
        return existing == null ? null : existing.getId();
    }

    public String fieldCode(String name) {
        if (AlarmTypeCatalog.FIELD_LEVEL.equals(name)) {
            return alarmCatalogEnsure.fieldCode(name);
        }
        FieldDO field = findField(name);
        if (field == null || !StringUtils.hasText(field.getCode())) {
            throw new ServiceException(500, "告警规则目录缺少字段「" + name + "」");
        }
        return field.getCode();
    }

    private void seedGalleryRules() {
        Long stationId = facilityStationQueryService.onlyStationId();
        if (stationId == null) {
            throw new ServiceException(500, "智慧管廊的告警规则还不能建");
        }
        for (RuleSeed seed : thresholdSeeds()) {
            fileRule(modelIdFor(seed), stationId, seed);
        }
    }

    private void fileRule(Long modelId, Long stationId, RuleSeed seed) {
        HelpText help = helpText(seed.code());
        Long entityId = entityIdByCode(seed.code());
        if (entityId == null) {
            EntityCreateReqVO request = new EntityCreateReqVO();
            request.setRegistryCode(DIRECTORY);
            Map<String, Object> baseFields = new LinkedHashMap<>();
            baseFields.put("entityTypeCode", DIRECTORY);
            baseFields.put("modelId", modelId);
            baseFields.put("name", seed.name());
            baseFields.put("code", seed.code());
            baseFields.put("status", 1);
            baseFields.put(FacilityOwningFieldCodes.FIELD_CODE, FacilityOwningFieldCodes.toApiRef(stationId));
            request.setBaseFields(baseFields);
            Map<String, Object> fields = new LinkedHashMap<>();
            fields.put(fieldCode(FIELD_DEVICE_TYPE), seed.deviceType());
            fields.put(fieldCode(FIELD_POINT), seed.point());
            fields.put(fieldCode(FIELD_TYPE), seed.typeCode());
            fields.put(fieldCode(AlarmTypeCatalog.FIELD_LEVEL), seed.level());
            putIfPresent(fields, FIELD_OP, seed.op());
            putIfPresent(fields, FIELD_VALUE, seed.value());
            putIfPresent(fields, FIELD_HIGH, seed.high());
            putIfPresent(fields, FIELD_LOW, seed.low());
            putIfPresent(fields, FIELD_HIGH_RECOVER, seed.highRecover());
            putIfPresent(fields, FIELD_LOW_RECOVER, seed.lowRecover());
            putHelp(fields, help);
            request.setCustomFields(fields);
            entityId = entityService.create(request);
        } else {
            fillBlankHelp(entityId, help);
        }
        Long categoryId = categoryId(categoryCode(seed.categoryCode()));
        if (categoryId == null) {
            throw new ServiceException(500, "告警规则还没有分类「" + seed.categoryCode() + "」");
        }
        entityCategoryRelationService.associate(entityId, categoryId, DIRECTORY);
    }

    /**
     * 分类名称与告警目录那棵树相同。
     * 编号加前缀，避免和告警目录里已有的分类编号撞车。
     */
    private void ensureCategories() {
        CategoryTypeDO categoryType = categoryTypeMapper.selectByCategoryTypeCode(DIRECTORY);
        if (categoryType == null || categoryType.getTopLevelCategoryId() == null) {
            throw new ServiceException(500, "告警规则目录还没有分类根");
        }
        Map<String, Long> ids = new LinkedHashMap<>();
        for (AlarmTypeCatalog.Node node : AlarmTypeCatalog.nodes()) {
            if (node.parentCode() != null) {
                continue;
            }
            ids.put(node.code(), ensureCategory(node, categoryType.getTopLevelCategoryId()));
        }
        for (AlarmTypeCatalog.Node node : AlarmTypeCatalog.nodes()) {
            if (node.parentCode() == null) {
                continue;
            }
            Long parentId = ids.get(node.parentCode());
            if (parentId == null) {
                parentId = categoryId(categoryCode(node.parentCode()));
            }
            ensureCategory(node, parentId);
        }
    }

    private Long ensureCategory(AlarmTypeCatalog.Node node, Long parentId) {
        Long existing = categoryId(categoryCode(node.code()));
        if (existing != null) {
            return existing;
        }
        CategoryCreateReqVO request = new CategoryCreateReqVO();
        request.setCategoryTypeCode(DIRECTORY);
        request.setParentId(parentId);
        request.setCode(categoryCode(node.code()));
        request.setName(node.name());
        request.setSort(node.sort());
        request.setStatus(1);
        request.setDescription(node.name());
        return categoryService.createCategory(request);
    }

    private Long categoryId(String code) {
        List<CategoryDO> rows = categoryMapper.selectByCategoryTypeCode(DIRECTORY);
        if (rows == null) {
            return null;
        }
        for (CategoryDO row : rows) {
            if (row != null && code.equals(row.getCode())) {
                return row.getId();
            }
        }
        return null;
    }

    private static String categoryCode(String alarmTypeCode) {
        return "ar-" + alarmTypeCode;
    }

    private void putIfPresent(Map<String, Object> fields, String name, String value) {
        if (StringUtils.hasText(value)) {
            fields.put(fieldCode(name), value);
        }
    }

    /** 四项还空着才写入。限值、级别、点位保持原样。 */
    private void putHelp(Map<String, Object> fields, HelpText help) {
        if (help == null) {
            return;
        }
        putIfPresent(fields, AlarmTypeCatalog.FIELD_CAUSE, help.cause());
        putIfPresent(fields, AlarmTypeCatalog.FIELD_CONSEQUENCE, help.consequence());
        putIfPresent(fields, AlarmTypeCatalog.FIELD_ACTION, help.linkage());
        putIfPresent(fields, AlarmTypeCatalog.FIELD_RESPONSE_TIME, help.responseTime());
    }

    private void fillBlankHelp(Long entityId, HelpText help) {
        if (help == null) {
            return;
        }
        EntityRespDTO current = entityRpcFacadeService.get(entityId, DIRECTORY);
        if (current == null) {
            throw new ServiceException(500, "告警规则「" + entityId + "」读不到，梳理文字没有写入");
        }
        Map<String, Object> fields = new LinkedHashMap<>();
        if (current.getCustomFields() != null) {
            fields.putAll(current.getCustomFields());
        }
        boolean changed = putIfBlank(fields, AlarmTypeCatalog.FIELD_CAUSE, help.cause());
        changed = putIfBlank(fields, AlarmTypeCatalog.FIELD_CONSEQUENCE, help.consequence()) || changed;
        changed = putIfBlank(fields, AlarmTypeCatalog.FIELD_ACTION, help.linkage()) || changed;
        changed = putIfBlank(fields, AlarmTypeCatalog.FIELD_RESPONSE_TIME, help.responseTime()) || changed;
        if (!changed) {
            return;
        }
        Map<String, Object> storedBase = current.getBaseFields() == null ? Map.of() : current.getBaseFields();
        EntityUpdateReqVO update = new EntityUpdateReqVO();
        update.setId(entityId);
        update.setRegistryCode(DIRECTORY);
        Map<String, Object> baseFields = new LinkedHashMap<>();
        baseFields.put("entityTypeCode", DIRECTORY);
        baseFields.put("modelId", storedBase.get("modelId"));
        baseFields.put("name", storedBase.get("name"));
        baseFields.put("code", storedBase.get("code"));
        baseFields.put("status", storedBase.get("status") == null ? 1 : storedBase.get("status"));
        update.setBaseFields(baseFields);
        update.setCustomFields(fields);
        entityService.update(update);
    }

    private boolean putIfBlank(Map<String, Object> fields, String name, String value) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        String code = fieldCode(name);
        Object current = fields.get(code);
        if (current != null && StringUtils.hasText(String.valueOf(current))) {
            return false;
        }
        fields.put(code, value);
        return true;
    }

    /**
     * 按规则上已经写明的现象和级别补原因、后果、允许响应时间。
     * 联动只写已经明确的一项：温度偏高启动消防。其余规则的联动留空。
     * 允许响应时间按这条规则已有的级别：紧急 1 分钟内，重要 5 分钟内，一般 30 分钟内。
     */
    private static HelpText helpText(String code) {
        return switch (code) {
            case "rule-temp" -> new HelpText(
                    "管廊内温度升高，温湿度检测仪读数高于 40。",
                    "没人处理时高温持续，需要消防介入。",
                    "5 分钟内",
                    "启动消防");
            case "rule-pump-fault" -> new HelpText(
                    "水泵报出故障。",
                    "水泵停转，集水排不出去，水位会继续上涨。",
                    "5 分钟内",
                    null);
            case "rule-fan-fault" -> new HelpText(
                    "风机报出故障。",
                    "通风中断，管廊内空气不能流通。",
                    "5 分钟内",
                    null);
            case "rule-hatch-fault" -> new HelpText(
                    "液压井盖报出故障。",
                    "井盖不能按正常方式开闭，通道受阻。",
                    "5 分钟内",
                    null);
            case "rule-pump-level-low" -> new HelpText(
                    "水泵水位降到低位。",
                    "水泵继续运行会空转。",
                    "30 分钟内",
                    null);
            case "rule-pump-level-high" -> new HelpText(
                    "水泵水位升到高位。",
                    "积水继续上涨，可能漫出集水坑。",
                    "5 分钟内",
                    null);
            case "rule-pump-level-hh" -> new HelpText(
                    "水泵水位升到高高位。",
                    "积水即将漫出，影响管廊。",
                    "立即，不超过 1 分钟",
                    null);
            case "rule-hatch-estop" -> new HelpText(
                    "有人按下液压井盖急停。",
                    "井盖停在当前位置，不能继续开闭。",
                    "立即，不超过 1 分钟",
                    null);
            case "rule-co2" -> new HelpText(
                    "二氧化碳浓度高于每立方米 1000PPM。",
                    "人员停留有窒息风险。",
                    "5 分钟内",
                    null);
            case "rule-o2" -> new HelpText(
                    "氧气浓度高于 23.5%。",
                    "富氧，不能继续停留。",
                    "立即，不超过 1 分钟",
                    null);
            case "rule-o2-low" -> new HelpText(
                    "氧气浓度低于 19.5%。",
                    "缺氧，不能继续停留。",
                    "立即，不超过 1 分钟",
                    null);
            default -> null;
        };
    }

    private record HelpText(String cause, String consequence, String responseTime, String linkage) {
    }

    private void assignShared(Long modelId, boolean withPoint) {
        assign(modelId, fieldCode(FIELD_DEVICE_TYPE));
        assign(modelId, fieldCode(FIELD_TYPE));
        assign(modelId, fieldCode(AlarmTypeCatalog.FIELD_LEVEL));
        assign(modelId, fieldCode(AlarmTypeCatalog.FIELD_CAUSE));
        assign(modelId, fieldCode(AlarmTypeCatalog.FIELD_CONSEQUENCE));
        assign(modelId, fieldCode(AlarmTypeCatalog.FIELD_ACTION));
        assign(modelId, fieldCode(AlarmTypeCatalog.FIELD_RESPONSE_TIME));
        if (withPoint) {
            assign(modelId, fieldCode(FIELD_POINT));
        }
    }

    /** 高限或低限：共用栏加上这一侧的报警线和恢复线。别的一侧和比较方式不挂。 */
    private void assignLimit(Long modelId, String limitName, String limitAlias, String recoverName, String recoverAlias) {
        assignShared(modelId, true);
        assign(modelId, ensureText(limitName));
        assign(modelId, ensureText(recoverName));
        label(modelId, limitName, limitAlias);
        label(modelId, recoverName, recoverAlias);
    }

    /** 详情上的字。字段库名字仍是高于、低于，判断按名字取列。 */
    private void label(Long modelId, String fieldName, String alias) {
        jdbcTemplate.update(
                "UPDATE dynamicbusiness.dynamic_model_field_assignment SET alias = ? "
                        + "WHERE model_id = ? AND field_code = ? AND deleted = false",
                alias, modelId, fieldCode(fieldName));
    }

    /**
     * 旧的阈值告警把两侧和比较方式放在同一条上。
     * 已经写了高于的挪到高限，只写了低于的挪到低限，只写了比较的挪到开关量。
     * 两侧都空、比较也空的留在原模型，不猜它属于哪一类。
     */
    private void moveCombinedThresholdRules(Long highId, Long lowId, Long switchId) {
        ModelDO old = modelMapper.selectByNameAndEntityTypeCode(MODEL_THRESHOLD, DIRECTORY);
        if (old == null || old.getId() == null) {
            return;
        }
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        String table = "dynamicbusiness.ent_alarm_rule_t" + tenantId;
        String high = fieldCode(FIELD_HIGH);
        String low = fieldCode(FIELD_LOW);
        String op = fieldCode(FIELD_OP);
        String value = fieldCode(FIELD_VALUE);
        String filled = "coalesce(btrim(custom_fields ->> ?), '') <> ''";
        String blank = "coalesce(btrim(custom_fields ->> ?), '') = ''";
        jdbcTemplate.update(
                "UPDATE " + table + " SET model_id = ? WHERE deleted = false AND model_id = ? AND " + filled + " AND " + blank,
                highId, old.getId(), high, low);
        jdbcTemplate.update(
                "UPDATE " + table + " SET model_id = ? WHERE deleted = false AND model_id = ? AND " + filled + " AND " + blank,
                lowId, old.getId(), low, high);
        jdbcTemplate.update(
                "UPDATE " + table + " SET model_id = ? WHERE deleted = false AND model_id = ? AND " + blank + " AND " + blank
                        + " AND (" + filled + " OR " + filled + ")",
                switchId, old.getId(), high, low, op, value);
        Integer left = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM " + table + " WHERE deleted = false AND model_id = ?",
                Integer.class, old.getId());
        if (left != null && left == 0) {
            jdbcTemplate.update(
                    "UPDATE dynamicbusiness.dynamic_model SET status = 0, description = ? WHERE id = ?",
                    "已拆成高限告警、低限告警、开关量告警。不再在这一条上同时填两侧和比较方式。",
                    old.getId());
        }
    }

    /** 采集判断和策略名单要扫的模型。含尚未挪走的旧阈值告警。 */
    public java.util.List<Long> judgedModelIds() {
        java.util.List<Long> ids = new java.util.ArrayList<>();
        for (String name : java.util.List.of(MODEL_HIGH, MODEL_LOW, MODEL_SWITCH, MODEL_THRESHOLD)) {
            ModelDO model = modelMapper.selectByNameAndEntityTypeCode(name, DIRECTORY);
            if (model != null && model.getId() != null && !ids.contains(model.getId())) {
                ids.add(model.getId());
            }
        }
        return ids;
    }

    private Long modelIdFor(RuleSeed seed) {
        if (StringUtils.hasText(seed.high())) {
            return ensureModel(MODEL_HIGH, "读数升高才告警。只填高于这个数告警，和回落后的恢复。");
        }
        if (StringUtils.hasText(seed.low())) {
            return ensureModel(MODEL_LOW, "读数降低才告警。只填低于这个数告警，和回升后的恢复。");
        }
        return ensureModel(MODEL_SWITCH, "开关量。读数等于或不等于一个数才告警。");
    }

    private void assign(Long modelId, String fieldCode) {
        FieldDO field = fieldMapper.selectByCode(fieldCode);
        if (field == null || field.getId() == null) {
            throw new ServiceException(500, "字段「" + fieldCode + "」还没进字段库");
        }
        modelFieldAssignmentService.assignFieldToModel(modelId, field.getId(), false, false, false, false, null, null);
    }

    private void ensureDirectory() {
        EntityTypeDO existing = entityTypeMapper.selectByCode(DIRECTORY);
        if (existing != null) {
            if (!DIRECTORY_NAME.equals(existing.getName())) {
                EntityTypeUpdateReqVO update = new EntityTypeUpdateReqVO();
                update.setId(existing.getId());
                update.setName(DIRECTORY_NAME);
                update.setAlias(DIRECTORY_NAME);
                update.setDescription("告警规则在这本目录里建。每种判断方式一个模型。一条实体是某种设备上的一份参数。");
                entityTypeService.update(update);
            }
            return;
        }
        EntityTypeCreateReqVO request = new EntityTypeCreateReqVO();
        request.setSort(82);
        request.setName(DIRECTORY_NAME);
        request.setCode(DIRECTORY);
        request.setAlias(DIRECTORY_NAME);
        request.setStatus("active");
        request.setStorageType("DEDICATED");
        request.setEntryKind("NATIVE");
        request.setGroupName(AlarmTypeCatalog.GROUP_NAME);
        request.setDescription("告警规则在这本目录里建。每种判断方式一个模型。一条实体是某种设备上的一份参数。");
        request.setAssociationFields("{}");
        entityTypeService.create(request);
        stationOwnedCatalogSetup.setUpNewCatalog(DIRECTORY, true);
    }

    private Long ensureModel(String name, String description) {
        ModelDO named = modelMapper.selectByNameAndEntityTypeCode(name, DIRECTORY);
        if (named != null) {
            return named.getId();
        }
        ModelCreateReqVO request = new ModelCreateReqVO();
        request.setName(name);
        request.setEntityTypeCode(DIRECTORY);
        request.setStatus(1);
        request.setGovernanceStatus("COMPANY");
        request.setDescription(description);
        return modelService.createModel(request);
    }

    private String ensureText(String name) {
        FieldDO existing = findField(name);
        if (existing != null) {
            return existing.getCode();
        }
        FieldCreateReqVO request = new FieldCreateReqVO();
        request.setName(name);
        request.setType("TEXT");
        request.setSource("CUSTOM");
        request.setStatus(1);
        request.setDescription(name);
        return createdCode(name, fieldService.createField(request));
    }

    private String ensureEnum(String name, String options) {
        FieldDO existing = findField(name);
        if (existing != null) {
            return existing.getCode();
        }
        FieldCreateReqVO request = new FieldCreateReqVO();
        request.setName(name);
        request.setType("ENUM");
        request.setOptions(options);
        request.setSource("CUSTOM");
        request.setStatus(1);
        request.setDescription(name);
        return createdCode(name, fieldService.createField(request));
    }

    private String ensureChildren() {
        FieldDO existing = findField(FIELD_CHILDREN);
        if (existing != null) {
            return existing.getCode();
        }
        FieldCreateReqVO request = new FieldCreateReqVO();
        request.setName(FIELD_CHILDREN);
        request.setType("ENTITY_REF_MULTI");
        request.setTargetEntityType(DIRECTORY);
        request.setSource("CUSTOM");
        request.setStatus(1);
        request.setDescription("组合条件要一起看的规则实体");
        return createdCode(FIELD_CHILDREN, fieldService.createField(request));
    }

    private String createdCode(String name, Long fieldId) {
        FieldDO created = fieldMapper.selectById(fieldId);
        if (created == null || !StringUtils.hasText(created.getCode())) {
            throw new ServiceException(500, "字段「" + name + "」创建后没有编码");
        }
        return created.getCode();
    }

    private FieldDO findField(String name) {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        List<FieldDO> rows = fieldMapper.selectList(new LambdaQueryWrapperX<FieldDO>()
                .eq(FieldDO::getName, name)
                .eq(FieldDO::getTenantId, tenantId)
                .eq(FieldDO::getDeleted, false));
        if (rows.size() > 1) {
            throw new ServiceException(500, "字段「" + name + "」有多条，对不上要挂的那一列");
        }
        return rows.isEmpty() ? null : rows.get(0);
    }

    private boolean isGalleryTenant() {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        List<String> names = jdbcTemplate.query(
                "SELECT name FROM public.system_tenant WHERE id = ? AND deleted = false",
                (rs, row) -> rs.getString("name"),
                tenantId);
        return names.size() == 1 && GALLERY_TENANT_NAME.equals(names.get(0));
    }

    private static List<RuleSeed> thresholdSeeds() {
        return List.of(
                new RuleSeed("rule-temp", "温湿度检测仪温度偏高", "温湿度检测仪",
                        "FLD-MON-001", null, null, "alarm-meter", "重要", "40", null, null, null, "alarm-meter"),
                new RuleSeed("rule-pump-fault", "水泵故障", "水泵",
                        "F-0c2239bb2c50496a8b802d4c55e31df5", "EQ", "1", "alarm-fault-pump", "重要",
                        null, null, null, null, "alarm-fault-pump"),
                new RuleSeed("rule-fan-fault", "风机故障", "风机",
                        "F-0c2239bb2c50496a8b802d4c55e31df5", "EQ", "1", "alarm-fault-fan", "重要",
                        null, null, null, null, "alarm-fault-fan"),
                new RuleSeed("rule-hatch-fault", "液压井盖故障", "液压井盖",
                        "F-0c2239bb2c50496a8b802d4c55e31df5", "EQ", "1", "alarm-fault-hatch", "重要",
                        null, null, null, null, "alarm-fault-hatch"),
                new RuleSeed("rule-pump-level-low", "水泵水位低", "水泵",
                        "F-acb4067e851b4210aa5c58fe4df5f915", "EQ", "1", "alarm-leak", "一般",
                        null, null, null, null, "alarm-leak"),
                new RuleSeed("rule-pump-level-high", "水泵水位高", "水泵",
                        "F-7bdb03e028f049579dbee3ce64f58365", "EQ", "1", "alarm-leak", "重要",
                        null, null, null, null, "alarm-leak"),
                new RuleSeed("rule-pump-level-hh", "水泵水位高高", "水泵",
                        "F-ba04ffdb8a274188b2e30f74791771f9", "EQ", "1", "alarm-leak", "紧急",
                        null, null, null, null, "alarm-leak"),
                new RuleSeed("rule-hatch-estop", "液压井盖急停", "液压井盖",
                        "F-614f06636e414cfa9a2781722ccd3e71", "EQ", "1", "alarm-operation", "紧急",
                        null, null, null, null, "alarm-operation"),
                new RuleSeed("rule-co2", "二氧化碳浓度过高", "二氧化碳检测仪",
                        "F-99a0879197624d6abe13599826a90a00", null, null, "alarm-gas", "重要",
                        "1000", null, "1000", null, "alarm-gas"),
                new RuleSeed("rule-o2", "氧气浓度过高", "氧气检测仪",
                        "F-b3e866f11e764999914644a024eb01fc", null, null, "alarm-gas", "紧急",
                        "23.5", null, "23.5", null, "alarm-gas"),
                new RuleSeed("rule-o2-low", "氧气浓度过低", "氧气检测仪",
                        "F-b3e866f11e764999914644a024eb01fc", null, null, "alarm-gas", "紧急",
                        null, "19.5", null, "19.5", "alarm-gas")
        );
    }

    private record RuleSeed(String code, String name, String deviceType, String point, String op, String value,
                            String typeCode, String level, String high, String low, String highRecover,
                            String lowRecover, String categoryCode) {
    }
}
