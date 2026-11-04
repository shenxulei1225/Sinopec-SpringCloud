package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryDeleteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityDeleteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldBatchAssignReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.strategy.ConditionStrategyDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.strategy.ConditionStrategyMapper;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.capability.BusinessCapabilityService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.BaseFieldLibrarySyncService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeBaseFieldService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.FacilityOwningFieldEnsureService;
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
 * 在数据管理里准备策略目录。
 * 目录、分类、字段、型号走数据管理的创建。一条策略是这本目录里的一条记录。
 * 分类「告警」每个租户都有。人在目录里新建告警策略，数据处理只读这一类。
 * 分类「巡检」只建在智慧巡检。那几条是机器人和无人机巡检的策略。
 * 智慧管廊的巡检是人工巡检，不建「巡检」，也不放那几条。
 * 巡检执行仍读条件策略那一行。这里不另写一套执行条件。
 * 智慧巡检的每一座站各放一份，打开哪座站都能看到这六条。
 * 告警在子数据类型「告警策略」里选条件和是否执行。
 * 告警规则不选，表示这个条件下这台设备上的规则都不执行。选了几条就只不执行这几条。
 */
@Service
public class AlarmStrategyCatalogEnsure {

    public static final String DIRECTORY = "alarm_strategy";
    public static final String DIRECTORY_NAME = "策略";
    /** 这本目录是各业务共用的基础数据。告警在子数据类型「告警策略」里配置。 */
    public static final String DIRECTORY_DESCRIPTION = "这本目录是各业务共用的策略。告警打开「告警策略」。巡检仍读原来的执行配置。";
    public static final String MODEL_NAME = "告警策略";
    public static final String CATEGORY_ALARM = "告警";
    public static final String CATEGORY_DEVICE = "设备告警策略";
    public static final String CODE_DEVICE = "strategy-device";
    public static final String CATEGORY_INSPECTION = "巡检";
    public static final String CODE_ALARM = "strategy-alarm";
    public static final String CODE_INSPECTION = "strategy-inspection";
    /** 机器人和无人机巡检所在的租户。智慧管廊不使用这一类。 */
    private static final String INSPECTION_TENANT_NAME = "智慧巡检";
    /** 告警记录所在的租户。按设备类型建告警策略。 */
    private static final String GALLERY_TENANT_NAME = "智慧管廊";
    public static final String FIELD_POINT = "点位编号";
    public static final String FIELD_OP = "比较方式";
    public static final String FIELD_VALUE = "比较值";
    public static final String FIELD_TYPE = "告警类型";
    public static final String FIELD_DEVICES = "适用设备";
    /** 表单上的型号。值是型号名称，例如温湿度检测仪。不复用设备账上的数字分类「设备类型」。 */
    public static final String FIELD_DEVICE_TYPE = "型号";
    public static final String FIELD_HIGH = "高于";
    public static final String FIELD_LOW = "低于";
    public static final String FIELD_HIGH_RECOVER = "高于恢复";
    public static final String FIELD_LOW_RECOVER = "低于恢复";
    /** 不选表示这台设备上的全部告警规则。选了几条就只对这几条。执行还是不执行看「成立之后」。 */
    public static final String FIELD_RULES = "告警规则";
    public static final String FIELD_RULES_DESCRIPTION =
            "不选，就是这台设备上的全部告警规则。选了几条，就只对这几条。执行还是不执行，看上面「成立之后」。";
    private static final String EQUIPMENT = "equipment";

    private static final String OP_OPTIONS = """
            [{"label":"大于","value":"GT"},{"label":"大于等于","value":"GTE"},{"label":"小于","value":"LT"},{"label":"小于等于","value":"LTE"},{"label":"等于","value":"EQ"},{"label":"不等于","value":"NEQ"}]""";

    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private EntityTypeService entityTypeService;
    @Resource
    private FacilityOwningFieldEnsureService facilityOwningFieldEnsureService;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private FieldService fieldService;
    @Resource
    private EntityTypeBaseFieldMapper baseFieldMapper;
    @Resource
    private EntityTypeBaseFieldService entityTypeBaseFieldService;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ModelService modelService;
    @Resource
    private BaseFieldLibrarySyncService baseFieldLibrarySyncService;
    @Resource
    private BusinessCapabilityService businessCapabilityService;
    @Resource
    private AlarmCatalogEnsure alarmCatalogEnsure;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private CategoryService categoryService;
    @Resource
    private ConditionStrategyMapper conditionStrategyMapper;
    @Resource
    private EntityService entityService;
    @Resource
    private EntityRpcFacadeService entityRpcFacadeService;
    @Resource
    private EntityCategoryRelationService entityCategoryRelationService;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private AlarmRuleCatalogEnsure alarmRuleCatalogEnsure;
    @Resource
    private AlarmBehaviorCatalogEnsure alarmBehaviorCatalogEnsure;
    @Resource
    private AlarmDomainCatalogEnsure alarmDomainCatalogEnsure;
    @Resource
    private ModelFieldAssignmentService modelFieldAssignmentService;

    @Transactional(rollbackFor = Exception.class)
    public void ensure() {
        if (SecurityFrameworkUtils.getLoginUserId() == null) {
            throw new ServiceException(401, "没有登录用户，策略目录没有准备");
        }
        alarmCatalogEnsure.ensure();
        ensureDirectory();
        attach(ensureText(FIELD_POINT), FIELD_POINT);
        attach(ensureEnum(FIELD_OP, OP_OPTIONS), FIELD_OP);
        attach(ensureText(FIELD_VALUE), FIELD_VALUE);
        attach(ensureEnum(FIELD_TYPE, typeOptions()), FIELD_TYPE);
        attach(alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_LEVEL), AlarmTypeCatalog.FIELD_LEVEL);
        attach(ensureDevices(), FIELD_DEVICES);
        attach(ensureText(FIELD_DEVICE_TYPE), FIELD_DEVICE_TYPE);
        attach(ensureText(FIELD_HIGH), FIELD_HIGH);
        attach(ensureText(FIELD_LOW), FIELD_LOW);
        attach(ensureText(FIELD_HIGH_RECOVER), FIELD_HIGH_RECOVER);
        attach(ensureText(FIELD_LOW_RECOVER), FIELD_LOW_RECOVER);
        Long modelId = ensureModel();
        hideConditionColumns(modelId);
        businessCapabilityService.refreshModelCrudFormDefinition(modelId);
        alarmRuleCatalogEnsure.ensure();
        alarmBehaviorCatalogEnsure.ensure();
        attach(ensureRulePicker(), FIELD_RULES);
        assignRuleField(modelId, fieldCode(FIELD_RULES));
        alarmDomainCatalogEnsure.ensure();
        ensureAlarmCategory();
        deviceCategoryId();
        if (isInspectionTenant()) {
            ensureInspectionCategory();
            seedInspectionRecords(modelId);
        } else {
            withdrawInspectionFiling();
        }
        if (isGalleryTenant()) {
            retireGalleryRulePickers();
            alarmBehaviorCatalogEnsure.seedGallery(
                    alarmCategoryId(), deviceCategoryId(), facilityOwningFieldEnsureService.onlyStationId());
        }
    }

    /**
     * 「告警」分类的编号。没有这一类时返回空，调用方不拿别的分类顶上。
     */
    public Long alarmCategoryId() {
        return categoryId(CODE_ALARM);
    }

    /**
     * 告警策略这一页左边的分类叫「告警策略」。设备告警策略是它的下级。
     * 以后别的告警策略再挂在同一级下面。
     */
    public Long deviceCategoryId() {
        Long folderId = categoryId(AlarmDomainCatalogEnsure.CODE + "_dir");
        if (folderId == null) {
            throw new ServiceException(500, "告警策略的分类还没有准备好，不能挂设备告警策略");
        }
        Long id = categoryId(CODE_DEVICE);
        if (id == null) {
            ensureCategory(folderId, CATEGORY_DEVICE, CODE_DEVICE, 1);
            id = categoryId(CODE_DEVICE);
        }
        if (id == null) {
            throw new ServiceException(500, "策略目录还没有设备告警策略分类");
        }
        CategoryDO category = categoryByCode(CODE_DEVICE);
        if (category != null
                && (!CATEGORY_DEVICE.equals(category.getName()) || !folderId.equals(category.getParentId()))) {
            CategoryUpdateReqVO update = new CategoryUpdateReqVO();
            update.setId(category.getId());
            update.setName(CATEGORY_DEVICE);
            update.setCode(category.getCode());
            update.setCategoryTypeCode(DIRECTORY);
            update.setParentId(folderId);
            update.setStatus(category.getStatus() == null ? 1 : category.getStatus());
            update.setSort(category.getSort());
            categoryService.updateCategory(update);
        }
        return id;
    }

    /** 告警规则是关联字段。批量分配才会写下目标目录。 */
    private void assignRuleField(Long modelId, String fieldCode) {
        FieldDO field = fieldMapper.selectByCode(fieldCode);
        if (field == null || field.getId() == null) {
            throw new ServiceException(500, "策略目录缺少字段「" + FIELD_RULES + "」");
        }
        ModelFieldBatchAssignReqVO request = new ModelFieldBatchAssignReqVO();
        request.setModelId(modelId);
        ModelFieldBatchAssignReqVO.FieldAssignmentItem item = new ModelFieldBatchAssignReqVO.FieldAssignmentItem();
        item.setFieldId(field.getId());
        item.setRequired(false);
        item.setIsFilterable(true);
        item.setTargetEntityType(AlarmRuleCatalogEnsure.DIRECTORY);
        request.setFieldAssignments(List.of(item));
        modelFieldAssignmentService.assignFieldsToModel(request);
    }

    private CategoryDO categoryByCode(String code) {
        for (CategoryDO row : categoryMapper.selectByCategoryTypeCode(DIRECTORY)) {
            if (code.equals(row.getCode()) && !Boolean.TRUE.equals(row.getDeleted())) {
                return row;
            }
        }
        return null;
    }

    /** 表单上这一列的字段编码。没有这一列就失败，不另找一列。 */
    public String fieldCode(String name) {
        if (AlarmTypeCatalog.FIELD_LEVEL.equals(name)) {
            return alarmCatalogEnsure.fieldCode(name);
        }
        FieldDO field = findField(name);
        if (field == null || !StringUtils.hasText(field.getCode())) {
            throw new ServiceException(500, "告警策略目录缺少字段「" + name + "」");
        }
        return field.getCode();
    }

    private void ensureDirectory() {
        EntityTypeDO existing = entityTypeMapper.selectByCode(DIRECTORY);
        if (existing != null) {
            facilityOwningFieldEnsureService.ensureForEntityTypeCode(DIRECTORY);
            if (!DIRECTORY_NAME.equals(existing.getName()) || !DIRECTORY_DESCRIPTION.equals(existing.getDescription())) {
                EntityTypeUpdateReqVO update = new EntityTypeUpdateReqVO();
                update.setId(existing.getId());
                update.setName(DIRECTORY_NAME);
                update.setAlias(DIRECTORY_NAME);
                update.setDescription(DIRECTORY_DESCRIPTION);
                entityTypeService.update(update);
            }
            return;
        }
        EntityTypeCreateReqVO request = new EntityTypeCreateReqVO();
        request.setSort(81);
        request.setName(DIRECTORY_NAME);
        request.setCode(DIRECTORY);
        request.setAlias(DIRECTORY_NAME);
        request.setStatus("active");
        request.setStorageType("DEDICATED");
        request.setEntryKind("NATIVE");
        request.setWorkScope(EntityTypeDO.WORK_SCOPE_FACILITY);
        request.setGroupName(AlarmTypeCatalog.GROUP_NAME);
        request.setDescription(DIRECTORY_DESCRIPTION);
        request.setAssociationFields("{}");
        entityTypeService.create(request);
    }

    private void ensureAlarmCategory() {
        Long parentId = topLevelCategoryId();
        if (parentId == null) {
            throw new ServiceException(500, "策略目录还没有分类顶层，不能挂告警");
        }
        ensureCategory(parentId, CATEGORY_ALARM, CODE_ALARM, 1);
    }

    private void ensureInspectionCategory() {
        Long parentId = topLevelCategoryId();
        if (parentId == null) {
            throw new ServiceException(500, "策略目录还没有分类顶层，不能挂巡检");
        }
        ensureCategory(parentId, CATEGORY_INSPECTION, CODE_INSPECTION, 2);
    }

    /**
     * 当前租户是不是智慧巡检。
     * 名字以租户表为准。对不上就不建巡检，也不把已有策略留在这个租户。
     */
    private boolean isInspectionTenant() {
        return INSPECTION_TENANT_NAME.equals(currentTenantName());
    }

    private boolean isGalleryTenant() {
        return GALLERY_TENANT_NAME.equals(currentTenantName());
    }

    private String currentTenantName() {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        List<String> names = jdbcTemplate.query(
                "SELECT name FROM public.system_tenant WHERE id = ? AND deleted = false",
                (rs, row) -> rs.getString("name"),
                tenantId);
        return names.size() == 1 ? names.get(0) : "";
    }

    private void ensureCategory(Long parentId, String name, String code, int sort) {
        if (categoryId(code) != null) {
            return;
        }
        CategoryCreateReqVO request = new CategoryCreateReqVO();
        request.setParentId(parentId);
        request.setName(name);
        request.setCode(code);
        request.setCategoryTypeCode(DIRECTORY);
        request.setSort(sort);
        request.setStatus(1);
        categoryService.createCategory(request);
    }

    /**
     * 条件策略表里已经有的行，归到智慧巡检的「巡检」。
     * 每一座站各一份，所属场站写成这座站。打开哪座站都能看到这六条。
     * 名字从那一行来。不把执行条件抄进这条记录。
     */
    private void seedInspectionRecords(Long modelId) {
        Long categoryId = categoryId(CODE_INSPECTION);
        if (categoryId == null || modelId == null) {
            throw new ServiceException(500, "策略目录还没有巡检分类，不能归入已有策略");
        }
        List<Long> stationIds = facilityOwningFieldEnsureService.listStationIds();
        if (stationIds.isEmpty()) {
            throw new ServiceException(500, "智慧巡检还没有站，不能把巡检策略归到站上");
        }
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        for (ConditionStrategyDO strategy : conditionStrategyMapper.selectVisibleAll(tenantId)) {
            if (strategy.getId() == null || !StringUtils.hasText(strategy.getName())) {
                continue;
            }
            for (Long stationId : stationIds) {
                fileInspectionRecord(modelId, categoryId, strategy, stationId);
            }
        }
    }

    private void fileInspectionRecord(Long modelId, Long categoryId, ConditionStrategyDO strategy, Long stationId) {
        String code = "cs-" + strategy.getId() + "-" + stationId;
        EntityRespDTO existing = entityRpcFacadeService.getByCode(code, DIRECTORY);
        Long entityId = existing == null ? null : existing.getId();
        if (entityId == null) {
            EntityCreateReqVO request = new EntityCreateReqVO();
            request.setRegistryCode(DIRECTORY);
            Map<String, Object> baseFields = new LinkedHashMap<>();
            baseFields.put("entityTypeCode", DIRECTORY);
            baseFields.put("modelId", modelId);
            baseFields.put("name", strategy.getName());
            baseFields.put("code", code);
            baseFields.put("status", 1);
            baseFields.put(FacilityOwningFieldCodes.FIELD_CODE, FacilityOwningFieldCodes.toApiRef(stationId));
            request.setBaseFields(baseFields);
            entityId = entityService.create(request);
        }
        entityCategoryRelationService.associate(entityId, categoryId, DIRECTORY);
    }

    /**
     * 智慧管廊等租户上如果已经归过巡检，把那几条和「巡检」分类去掉。
     * 只删按条件策略编号归进来的记录，含每一座站那一份。不删人在「告警」里新建的策略。
     */
    private void withdrawInspectionFiling() {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        List<Long> stationIds = facilityOwningFieldEnsureService.listStationIds();
        for (ConditionStrategyDO strategy : conditionStrategyMapper.selectVisibleAll(tenantId)) {
            if (strategy.getId() == null) {
                continue;
            }
            deleteFiledStrategy("cs-" + strategy.getId());
            for (Long stationId : stationIds) {
                deleteFiledStrategy("cs-" + strategy.getId() + "-" + stationId);
            }
        }
        Long categoryId = categoryId(CODE_INSPECTION);
        if (categoryId == null) {
            return;
        }
        CategoryDeleteReqVO deleteCategory = new CategoryDeleteReqVO();
        deleteCategory.setId(categoryId);
        deleteCategory.setCategoryTypeCode(DIRECTORY);
        deleteCategory.setCascade(true);
        deleteCategory.setForceDelete(true);
        categoryService.deleteCategory(deleteCategory);
    }

    /**
     * 智慧管廊按设备型号建一条告警策略，多选已经建好的规则实体。
     * 条件留在规则上。先前把高于、比较值写在策略上的记录删掉。
     */
    /** 按型号再勾一遍规则的旧记录删掉。判断改读告警规则上的型号。 */
    private void retireGalleryRulePickers() {
        for (String code : retiredDeviceAlarmCodes()) {
            deleteFiledStrategy(code);
        }
        for (GalleryAlarm alarm : galleryAlarms()) {
            deleteFiledStrategy(alarm.code());
        }
        for (String code : List.of("st-th", "st-pump", "st-fan", "st-hatch", "st-co2", "st-o2")) {
            deleteFiledStrategy(code);
        }
    }

    private String ensureRulePicker() {
        FieldDO existing = findField(FIELD_RULES);
        if (existing != null) {
            if (!"ENTITY_REF_MULTI".equalsIgnoreCase(existing.getType())) {
                throw new ServiceException(400, "字段「告警规则」已经存在，但不是多选关联");
            }
            if (!FIELD_RULES_DESCRIPTION.equals(existing.getDescription())) {
                if ("SYSTEM".equalsIgnoreCase(existing.getSource())) {
                    throw new ServiceException(400, "字段「告警规则」是系统字段，不能在启动时改说明");
                }
                FieldUpdateReqVO update = new FieldUpdateReqVO();
                update.setId(existing.getId());
                update.setName(existing.getName());
                update.setType(existing.getType());
                update.setSource(existing.getSource());
                update.setStatus(existing.getStatus() == null ? 1 : existing.getStatus());
                update.setDescription(FIELD_RULES_DESCRIPTION);
                update.setOptions(existing.getOptions());
                fieldService.updateField(update);
            }
            return existing.getCode();
        }
        FieldCreateReqVO request = new FieldCreateReqVO();
        request.setName(FIELD_RULES);
        request.setType("ENTITY_REF_MULTI");
        request.setTargetEntityType(AlarmRuleCatalogEnsure.DIRECTORY);
        request.setSource("CUSTOM");
        request.setStatus(1);
        request.setDescription(FIELD_RULES_DESCRIPTION);
        return createdCode(FIELD_RULES, fieldService.createField(request));
    }

    /**
     * 条件列留在告警规则的模型上。策略模型不再展示这些列。
     * 所属场站和型号仍留在策略上。系统字段卸不掉就留下。
     */
    private void hideConditionColumns(Long modelId) {
        for (String name : List.of(FIELD_POINT, FIELD_OP, FIELD_VALUE, FIELD_TYPE, FIELD_DEVICES,
                FIELD_HIGH, FIELD_LOW, FIELD_HIGH_RECOVER, FIELD_LOW_RECOVER)) {
            FieldDO field = findField(name);
            if (field == null || field.getId() == null) {
                continue;
            }
            try {
                modelFieldAssignmentService.unassignFieldFromModel(modelId, field.getId());
            } catch (ServiceException ex) {
                // 系统字段不能从型号卸下，策略上仍能看见这一列。
            }
        }
    }

    private static List<String> retiredDeviceAlarmCodes() {
        return List.of(
                "alm-5549-pt-101",
                "alm-5552-tt-201",
                "alm-5550-ft-301",
                "alm-5551-lk-11",
                "alm-5553-lk-12",
                "alm-5552-lt-5",
                "alm-5550-ae-16",
                "alm-5549-task-9",
                "alm-5551-task-3");
    }

    private static List<GalleryAlarm> galleryAlarms() {
        return List.of(
                new GalleryAlarm("alm-type-temp", "温湿度检测仪温度偏高", "温湿度检测仪",
                        "FLD-MON-001", null, null, "alarm-meter", "重要", "40", null, null, null),
                new GalleryAlarm("alm-type-pump-fault", "水泵故障", "水泵",
                        "F-0c2239bb2c50496a8b802d4c55e31df5", "EQ", "1", "alarm-fault-pump", "重要",
                        null, null, null, null),
                new GalleryAlarm("alm-type-fan-fault", "风机故障", "风机",
                        "F-0c2239bb2c50496a8b802d4c55e31df5", "EQ", "1", "alarm-fault-fan", "重要",
                        null, null, null, null),
                new GalleryAlarm("alm-type-hatch-fault", "液压井盖故障", "液压井盖",
                        "F-0c2239bb2c50496a8b802d4c55e31df5", "EQ", "1", "alarm-fault-hatch", "重要",
                        null, null, null, null),
                new GalleryAlarm("alm-type-pump-level-low", "水泵水位低", "水泵",
                        "F-acb4067e851b4210aa5c58fe4df5f915", "EQ", "1", "alarm-leak", "一般",
                        null, null, null, null),
                new GalleryAlarm("alm-type-pump-level-high", "水泵水位高", "水泵",
                        "F-7bdb03e028f049579dbee3ce64f58365", "EQ", "1", "alarm-leak", "重要",
                        null, null, null, null),
                new GalleryAlarm("alm-type-pump-level-hh", "水泵水位高高", "水泵",
                        "F-ba04ffdb8a274188b2e30f74791771f9", "EQ", "1", "alarm-leak", "紧急",
                        null, null, null, null),
                new GalleryAlarm("alm-type-hatch-estop", "液压井盖急停", "液压井盖",
                        "F-614f06636e414cfa9a2781722ccd3e71", "EQ", "1", "alarm-operation", "紧急",
                        null, null, null, null),
                new GalleryAlarm("alm-type-co2", "二氧化碳检测仪", "二氧化碳检测仪",
                        "F-99a0879197624d6abe13599826a90a00", null, null, "alarm-gas", "重要",
                        null, null, null, null),
                new GalleryAlarm("alm-type-o2", "氧气检测仪", "氧气检测仪",
                        "F-b3e866f11e764999914644a024eb01fc", null, null, "alarm-gas", "紧急",
                        null, null, null, null)
        );
    }

    private record GalleryAlarm(String code, String name, String deviceType, String point, String op, String value,
                                String typeCode, String level, String high, String low, String highRecover,
                                String lowRecover) {
    }

    private void deleteFiledStrategy(String code) {
        EntityRespDTO existing = entityRpcFacadeService.getByCode(code, DIRECTORY);
        if (existing == null || existing.getId() == null) {
            return;
        }
        EntityDeleteReqVO delete = new EntityDeleteReqVO();
        delete.setId(existing.getId());
        delete.setEntityTypeCode(DIRECTORY);
        delete.setRegistryCode(DIRECTORY);
        delete.setForceDelete(true);
        entityService.delete(delete);
    }

    private Long categoryId(String code) {
        for (CategoryDO row : categoryMapper.selectByCategoryTypeCode(DIRECTORY)) {
            if (code.equals(row.getCode()) && !Boolean.TRUE.equals(row.getDeleted())) {
                return row.getId();
            }
        }
        return null;
    }

    private Long topLevelCategoryId() {
        for (CategoryDO row : categoryMapper.selectByCategoryTypeCode(DIRECTORY)) {
            if (row.getParentId() == null || row.getParentId() == 0L) {
                return row.getId();
            }
        }
        return null;
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
            if (!"ENUM".equalsIgnoreCase(existing.getType())) {
                throw new ServiceException(400, "字段「" + name + "」已经存在，但不是选项");
            }
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

    private String ensureDevices() {
        FieldDO existing = findField(FIELD_DEVICES);
        if (existing != null) {
            if (!"ENTITY_REF_MULTI".equalsIgnoreCase(existing.getType())) {
                throw new ServiceException(400, "字段「适用设备」已经存在，但不是多选关联");
            }
            return existing.getCode();
        }
        FieldCreateReqVO request = new FieldCreateReqVO();
        request.setName(FIELD_DEVICES);
        request.setType("ENTITY_REF_MULTI");
        request.setTargetEntityType(EQUIPMENT);
        request.setSource("CUSTOM");
        request.setStatus(1);
        request.setDescription("这条策略要看哪些设备。没选则不对任何设备判断。");
        return createdCode(FIELD_DEVICES, fieldService.createField(request));
    }

    private String createdCode(String name, Long fieldId) {
        FieldDO created = fieldMapper.selectById(fieldId);
        if (created == null || !StringUtils.hasText(created.getCode())) {
            throw new ServiceException(500, "字段「" + name + "」创建后没有编码");
        }
        return created.getCode();
    }

    private void attach(String fieldCode, String name) {
        if (baseFieldMapper.selectByEntityTypeCodeAndFieldCode(DIRECTORY, fieldCode) != null) {
            return;
        }
        FieldDO libraryField = fieldMapper.selectByCode(fieldCode);
        if (libraryField == null || libraryField.getId() == null) {
            throw new ServiceException(400, "字段库缺少 " + fieldCode);
        }
        EntityTypeBaseFieldSaveReqVO request = new EntityTypeBaseFieldSaveReqVO();
        request.setEntityTypeCode(DIRECTORY);
        request.setLibraryFieldId(libraryField.getId());
        request.setRequired(false);
        request.setIsSearchable(false);
        request.setIsFilterable(true);
        request.setIsSortable(false);
        request.setFieldName(name);
        request.setDescription(name);
        entityTypeBaseFieldService.createBaseField(request);
    }

    private Long ensureModel() {
        ModelDO named = modelMapper.selectByNameAndEntityTypeCode(MODEL_NAME, DIRECTORY);
        if (named != null) {
            baseFieldLibrarySyncService.assignAllBaseFieldsToModel(named.getId());
            return named.getId();
        }
        ModelCreateReqVO request = new ModelCreateReqVO();
        request.setName(MODEL_NAME);
        request.setEntityTypeCode(DIRECTORY);
        request.setStatus(1);
        request.setGovernanceStatus("COMPANY");
        request.setDescription("一条告警策略");
        Long modelId = modelService.createModel(request);
        baseFieldLibrarySyncService.assignAllBaseFieldsToModel(modelId);
        return modelId;
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

    private static String typeOptions() {
        StringBuilder json = new StringBuilder("[");
        List<AlarmTypeCatalog.Node> nodes = AlarmTypeCatalog.nodes();
        for (int i = 0; i < nodes.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            AlarmTypeCatalog.Node node = nodes.get(i);
            json.append("{\"label\":\"").append(node.name()).append("\",\"value\":\"")
                    .append(node.code()).append("\"}");
        }
        json.append(']');
        return json.toString();
    }
}
