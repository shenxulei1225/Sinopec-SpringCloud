package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldBatchAssignReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.capability.DynamicBusinessPluginManifestDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.capability.DynamicBusinessPluginManifestMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.devicehistory.DeviceCollectionPointQuery;
import cn.cheers.x.module.dynamicbusiness.framework.field.StructuredFieldSemantics;
import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames;
import cn.cheers.x.module.dynamicbusiness.service.capability.BusinessCapabilityService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeService;
import cn.cheers.x.module.dynamicbusiness.service.field.FieldService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelFieldAssignmentService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 告警策略是策略这本基础数据下面的子数据类型。
 * 告警相关的策略都在这一域。设备告警策略是分类「告警策略」的下级。
 * 人打开设备告警策略，先选适用型号是全部还是某几个，再多选系统已经能判断的条件。
 * 条件是读方向布尔采集点的工程值，0 或 1。网关已经转好，这里不读点位说明。
 * 同一个采集点出现在多个型号上时不带型号名。只属于一个型号的点带上型号名。
 * 这些条件选有一条就成立，或要全部才成立。成立之后选执行还是不执行下面列出的告警规则。
 * 不让人填比较方式。检修、启动没有对应的点，不列。已经写成告警规则的点不重复列。写点不列。
 * 不读巡检的执行配置，也不写告警记录。
 */
@Service
public class AlarmDomainCatalogEnsure {

    public static final String CODE = "strategy_alarm";
    public static final String NAME = "告警策略";
    public static final String DOMAIN = "alarm";
    public static final String MODEL = "设备告警策略";
    /** 上一版型号名。启动时改成设备告警策略，避免再留一个旧型号。 */
    private static final String PREVIOUS_MODEL = "设备策略";
    private static final String OLDER_MODEL = "规则是否执行";
    public static final String FIELD_MODELS = "适用型号";
    public static final String FIELD_CONDITION = "条件";
    /** 上一版栏名。启动时改成现在的说法，避免再留一栏旧名。 */
    private static final String PREVIOUS_MATCH = "条件关系";
    private static final String PREVIOUS_EXECUTE = "是否执行";
    public static final String FIELD_MATCH = "这些条件";
    public static final String FIELD_EXECUTE = "成立之后";
    public static final String MATCH_ALL = "ALL";
    public static final String MATCH_ANY = "ANY";
    public static final String MODELS_ALL = "ALL";

    private static final String EXECUTE_OPTIONS = """
            [{"label":"就执行告警规则里勾的这几条","value":"RUN"},{"label":"就不执行告警规则里勾的这几条","value":"SKIP"}]""";
    private static final String MATCH_OPTIONS = """
            [{"label":"条件里有一条成立","value":"ANY"},{"label":"条件里要全部成立","value":"ALL"}]""";

    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private EntityTypeService entityTypeService;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ModelService modelService;
    @Resource
    private ModelFieldAssignmentService modelFieldAssignmentService;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private DynamicBusinessPluginManifestMapper pluginManifestMapper;
    @Resource
    private FieldService fieldService;
    @Resource
    private BusinessCapabilityService businessCapabilityService;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private EntityRepository entityRepository;
    @Resource
    private DeviceCollectionPointQuery collectionPointQuery;
    @Resource
    private AlarmRuleCatalogEnsure alarmRuleCatalogEnsure;

    /** 子数据类型和设备告警策略型号。还没有可判断的条件时，只建入口，不建空下拉。 */
    public void ensure() {
        ensureEntry();
        List<AlarmConditionCatalog.PointSentence> points = loadPointSentences();
        String options = conditionOptions(points);
        String condition = ensureCondition(options);
        String models = ensureModels(modelOptions(points));
        String match = ensureMatch();
        String execute = ensureExecute();
        if (condition == null || models == null) {
            return;
        }
        Long modelId = ensureModel();
        trim(modelId);
        assign(modelId, models);
        assign(modelId, condition);
        assign(modelId, match);
        assign(modelId, execute);
        assign(modelId, fieldCode(AlarmStrategyCatalogEnsure.FIELD_RULES));
        markStructureSemantics();
        ensureStructurePlugin();
        orderFields(modelId);
        businessCapabilityService.refreshModelCrudFormDefinition(modelId);
    }

    /**
     * 条件加与或是一块结构化字段。要执行的告警规则是另一块。
     * 用途写在已有列上。详情按用途合成一块来画。判断仍读原来的列。
     * 不另建列，也不按列名在读路径猜用途。
     */
    private void markStructureSemantics() {
        markSemantic(FIELD_CONDITION, StructuredFieldSemantics.CONDITION_GROUP);
        markSemantic(FIELD_MATCH, StructuredFieldSemantics.CONDITION_GROUP);
        markSemantic(AlarmStrategyCatalogEnsure.FIELD_RULES, StructuredFieldSemantics.EXECUTION_RULES);
        markSemantic(FIELD_EXECUTE, StructuredFieldSemantics.EXECUTION_RULES);
    }

    /** 详情插件按这份清单启用。没有这一行，条件组和执行规则仍停在介绍区的文本行里。 */
    private void ensureStructurePlugin() {
        DynamicBusinessPluginManifestDO existing = pluginManifestMapper.selectByPluginId("strategy-structure");
        if (existing != null) {
            return;
        }
        DynamicBusinessPluginManifestDO created = new DynamicBusinessPluginManifestDO();
        created.setPluginId("strategy-structure");
        created.setVersion("1.0.0");
        created.setEnabled(true);
        created.setPlatformRange(">=1.0.0");
        created.setEntry("framework/strategy-structure/plugin/manifest.ts");
        created.setDependsOn("[]");
        created.setPermissions("[\"structured-fields:register\"]");
        created.setSemanticContributions("[\"CONDITION_GROUP\",\"EXECUTION_RULES\"]");
        pluginManifestMapper.insert(created);
    }

    private void markSemantic(String name, String semantic) {
        FieldDO field = findField(name);
        if (field == null || semantic.equals(field.getSemanticType())) {
            return;
        }
        field.setSemanticType(semantic);
        fieldMapper.updateById(field);
    }

    /** 告警策略这个型号。还没建好时失败，调用方不改去用运行状态。 */
    public Long modelId() {
        ModelDO model = modelMapper.selectByNameAndEntityTypeCode(MODEL, AlarmStrategyCatalogEnsure.DIRECTORY);
        if (model == null || model.getId() == null) {
            throw new ServiceException(500, "告警策略还没有「" + MODEL + "」");
        }
        return model.getId();
    }

    public String modelsFieldCode() {
        return fieldCode(FIELD_MODELS);
    }

    public String conditionFieldCode() {
        return fieldCode(FIELD_CONDITION);
    }

    public String matchFieldCode() {
        return fieldCode(FIELD_MATCH);
    }

    public String executeFieldCode() {
        return fieldCode(FIELD_EXECUTE);
    }

    /** 读方向布尔采集点，每个点有工程值 0 和 1。已经写成告警规则的点不进条件。 */
    public List<AlarmConditionCatalog.PointSentence> loadPointSentences() {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        String binding = TenantPhysicalTableNames.entityPhysicalTable("point_binding");
        String equipment = TenantPhysicalTableNames.entityPhysicalTable("equipment");
        if (!tableExists(binding) || !tableExists(equipment)) {
            return List.of();
        }
        java.util.Set<String> ruled = ruledPointCodes();
        List<AlarmConditionCatalog.PointSentence> sentences = new java.util.ArrayList<>();
        for (DeviceCollectionPointQuery.BooleanReadPoint point : collectionPointQuery.listBooleanReadPoints(tenantId)) {
            if (point.fieldCode() == null || ruled.contains(point.fieldCode())) {
                continue;
            }
            AlarmPointMeaning.phrases(point.description()).forEach((side, phrase) -> sentences.add(
                    new AlarmConditionCatalog.PointSentence(
                            point.modelId(), point.modelName(), point.fieldCode(), side, point.fieldName(), phrase)));
        }
        return sentences;
    }

    /** 告警规则已经在判断的点。这些点用「还开着」，不再当成策略里的 0 或 1。 */
    private java.util.Set<String> ruledPointCodes() {
        java.util.Set<String> codes = new java.util.LinkedHashSet<>();
        ModelDO threshold = modelMapper.selectByNameAndEntityTypeCode("阈值告警", AlarmRuleCatalogEnsure.DIRECTORY);
        if (threshold == null || threshold.getId() == null) {
            return codes;
        }
        String pointField = alarmRuleCatalogEnsure.fieldCode(AlarmRuleCatalogEnsure.FIELD_POINT);
        for (EntityDO rule : entityRepository.findByModelId(threshold.getId(), AlarmRuleCatalogEnsure.DIRECTORY)) {
            if (rule.getCustomFields() == null || !Integer.valueOf(1).equals(rule.getStatus())) {
                continue;
            }
            Object value = rule.getCustomFields().get(pointField);
            if (value != null && !String.valueOf(value).isBlank()) {
                codes.add(String.valueOf(value));
            }
        }
        return codes;
    }

    /**
     * 下拉里实际出现的那条条件编号。
     * 同一个采集点在多个型号上时，返回不带型号的编号。
     * 这个点不是读方向的布尔采集点，或已经是告警规则的点，就失败。
     */
    public String publishedCondition(String equipmentModelName, String pointName, String side) {
        ModelDO equipment = modelMapper.selectByNameAndEntityTypeCode(equipmentModelName, "equipment");
        FieldDO point = findField(pointName);
        if (equipment == null || equipment.getId() == null || point == null || point.getCode() == null) {
            throw new ServiceException(500, "点位「" + pointName + "」对不上，设备告警策略不能用它");
        }
        String specific = "bit:" + equipment.getId() + ":" + point.getCode() + ":" + side;
        String shared = "bit:" + point.getCode() + ":" + side;
        for (AlarmConditionCatalog.Published published : AlarmConditionCatalog.conditions(loadPointSentences())) {
            if (shared.equals(published.code()) || specific.equals(published.code())) {
                return published.code();
            }
        }
        throw new ServiceException(500, "点位「" + pointName + "」不是可读的布尔采集点，设备告警策略不能用它");
    }

    /** 设备型号的编号。对不上就失败，不另编一个。 */
    public long equipmentModelId(String equipmentModelName) {
        ModelDO equipment = modelMapper.selectByNameAndEntityTypeCode(equipmentModelName, "equipment");
        if (equipment == null || equipment.getId() == null) {
            throw new ServiceException(500, "设备型号「" + equipmentModelName + "」对不上，设备告警策略不能用它");
        }
        return equipment.getId();
    }

    private void ensureEntry() {
        EntityTypeDO existing = entityTypeMapper.selectByCode(CODE);
        if (existing != null) {
            if (!entryDescription().equals(existing.getDescription())) {
                EntityTypeUpdateReqVO update = new EntityTypeUpdateReqVO();
                update.setId(existing.getId());
                update.setDescription(entryDescription());
                entityTypeService.update(update);
            }
            return;
        }
        EntityTypeCreateReqVO request = new EntityTypeCreateReqVO();
        request.setSort(82);
        request.setName(NAME);
        request.setCode(CODE);
        request.setAlias(NAME);
        request.setStatus("active");
        request.setEntryKind(EntityTypeDO.ENTRY_KIND_DOMAIN);
        request.setBaseEntityTypeCode(AlarmStrategyCatalogEnsure.DIRECTORY);
        request.setDomain(DOMAIN);
        request.setWorkScope(EntityTypeDO.WORK_SCOPE_FACILITY);
        request.setGroupName(AlarmTypeCatalog.GROUP_NAME);
        request.setDescription(entryDescription());
        request.setAssociationFields("{}");
        entityTypeService.create(request);
    }

    private Long ensureModel() {
        Long kept = alignModel(MODEL);
        if (kept != null) {
            return kept;
        }
        Long previous = alignModel(PREVIOUS_MODEL);
        if (previous != null) {
            return previous;
        }
        Long older = alignModel(OLDER_MODEL);
        if (older != null) {
            return older;
        }
        ModelCreateReqVO request = new ModelCreateReqVO();
        request.setName(MODEL);
        request.setEntityTypeCode(AlarmStrategyCatalogEnsure.DIRECTORY);
        request.setDomain(DOMAIN);
        request.setStatus(1);
        request.setGovernanceStatus("COMPANY");
        request.setDescription(modelDescription());
        return modelService.createModel(request);
    }

    /** 已有型号改成「设备告警策略」，并写上告警域。没有这一名就返回空。改名走型号更新。 */
    private Long alignModel(String currentName) {
        ModelDO row = modelMapper.selectByNameAndEntityTypeCode(currentName, AlarmStrategyCatalogEnsure.DIRECTORY);
        if (row == null || row.getId() == null) {
            return null;
        }
        if (MODEL.equals(row.getName())
                && DOMAIN.equals(row.getDomain())
                && modelDescription().equals(row.getDescription())) {
            return row.getId();
        }
        ModelUpdateReqVO update = new ModelUpdateReqVO();
        update.setId(row.getId());
        update.setName(MODEL);
        update.setEntityTypeCode(AlarmStrategyCatalogEnsure.DIRECTORY);
        update.setDescription(modelDescription());
        update.setStatus(row.getStatus() == null ? 1 : row.getStatus());
        update.setGovernanceStatus(row.getGovernanceStatus());
        update.setDomain(DOMAIN);
        modelService.updateModel(update);
        return row.getId();
    }

    /** 自定义字段的名称、类型、说明和下拉走字段更新。系统字段不从这里改。 */
    private void saveCustomField(FieldDO existing, String name, String type, String description, String options) {
        if ("SYSTEM".equalsIgnoreCase(existing.getSource())) {
            throw new ServiceException(400, "字段「" + existing.getName() + "」是系统字段，不能在启动时改");
        }
        if (name.equals(existing.getName())
                && type.equals(existing.getType())
                && java.util.Objects.equals(description, existing.getDescription())
                && java.util.Objects.equals(options, existing.getOptions())) {
            return;
        }
        FieldUpdateReqVO update = new FieldUpdateReqVO();
        update.setId(existing.getId());
        update.setName(name);
        update.setType(type);
        update.setSource(existing.getSource());
        update.setStatus(existing.getStatus() == null ? 1 : existing.getStatus());
        update.setDescription(description);
        update.setOptions(options);
        update.setUnit(existing.getUnit());
        fieldService.updateField(update);
    }

    private static String entryDescription() {
        return "告警相关的策略都在这里。设备告警策略挂在告警策略分类下面。以后别的对象再各自加分类。";
    }

    private static String modelDescription() {
        return "针对设备的告警策略。先选适用型号和条件。这些条件有一条就成立，或要全部才成立。成立之后，执行或不执行下面列出的告警规则。";
    }

    private void trim(Long modelId) {
        for (String name : List.of(
                AlarmStrategyCatalogEnsure.FIELD_POINT,
                AlarmStrategyCatalogEnsure.FIELD_OP,
                AlarmStrategyCatalogEnsure.FIELD_VALUE,
                AlarmStrategyCatalogEnsure.FIELD_TYPE,
                AlarmStrategyCatalogEnsure.FIELD_DEVICES,
                AlarmStrategyCatalogEnsure.FIELD_DEVICE_TYPE,
                AlarmStrategyCatalogEnsure.FIELD_HIGH,
                AlarmStrategyCatalogEnsure.FIELD_LOW,
                AlarmStrategyCatalogEnsure.FIELD_HIGH_RECOVER,
                AlarmStrategyCatalogEnsure.FIELD_LOW_RECOVER,
                AlarmTypeCatalog.FIELD_LEVEL,
                AlarmBehaviorCatalogEnsure.FIELD_STATE_POINT,
                AlarmBehaviorCatalogEnsure.FIELD_ACTION,
                AlarmBehaviorCatalogEnsure.FIELD_LEAD,
                AlarmBehaviorCatalogEnsure.FIELD_HELD)) {
            FieldDO field = findField(name);
            if (field == null || field.getId() == null) {
                continue;
            }
            try {
                modelFieldAssignmentService.unassignFieldFromModel(modelId, field.getId());
            } catch (ServiceException ex) {
                // 系统字段卸不掉。判断不读它。
            }
        }
    }

    private void assign(Long modelId, String fieldCode) {
        FieldDO field = fieldMapper.selectByCode(fieldCode);
        if (field == null || field.getId() == null) {
            throw new ServiceException(500, "策略目录缺少字段 " + fieldCode);
        }
        boolean filterable = field.getType() != null
                && (field.getType().toUpperCase().startsWith("ENTITY_REF")
                || "ENUM".equalsIgnoreCase(field.getType())
                || "MULTI_SELECT".equalsIgnoreCase(field.getType()));
        if (field.getType() != null && field.getType().toUpperCase().startsWith("ENTITY_REF")) {
            ModelFieldBatchAssignReqVO request = new ModelFieldBatchAssignReqVO();
            request.setModelId(modelId);
            ModelFieldBatchAssignReqVO.FieldAssignmentItem item = new ModelFieldBatchAssignReqVO.FieldAssignmentItem();
            item.setFieldId(field.getId());
            item.setRequired(false);
            item.setIsFilterable(filterable);
            request.setFieldAssignments(List.of(item));
            modelFieldAssignmentService.assignFieldsToModel(request);
            return;
        }
        modelFieldAssignmentService.assignFieldToModel(
                modelId, field.getId(), false, false, filterable, false, null, null);
    }

    /** 还没有条件时返回空，不建一个空下拉。已有的单选改成多选。 */
    private String ensureCondition(String options) {
        FieldDO existing = findField(FIELD_CONDITION);
        if (existing != null) {
            saveCustomField(existing, existing.getName(), "MULTI_SELECT",
                    "可多选。先选适用型号，这里只列出这些型号的点。文字是这个点上已经写明的意思。",
                    options.isBlank() ? existing.getOptions() : options);
            return existing.getCode();
        }
        if (options.isBlank()) {
            return null;
        }
        FieldCreateReqVO request = new FieldCreateReqVO();
        request.setName(FIELD_CONDITION);
        request.setType("MULTI_SELECT");
        request.setOptions(options);
        request.setSource("CUSTOM");
        request.setStatus(1);
        request.setDescription("可多选。先选适用型号，这里只列出这些型号的点。文字是这个点上已经写明的意思。");
        return created(fieldService.createField(request));
    }

    /** 还没有型号时返回空。选全部，或选几个具体型号。 */
    private String ensureModels(String options) {
        FieldDO existing = findField(FIELD_MODELS);
        if (existing != null) {
            if (!options.isBlank()) {
                saveCustomField(existing, existing.getName(), existing.getType(), existing.getDescription(), options);
            }
            return existing.getCode();
        }
        if (options.isBlank()) {
            return null;
        }
        FieldCreateReqVO request = new FieldCreateReqVO();
        request.setName(FIELD_MODELS);
        request.setType("MULTI_SELECT");
        request.setOptions(options);
        request.setSource("CUSTOM");
        request.setStatus(1);
        request.setDescription("选全部时，各型号说明一致的条件对有这个点的型号都生效。选了具体型号，这条只对这些型号生效。");
        return created(fieldService.createField(request));
    }

    private String ensureMatch() {
        return ensureChoice(
                FIELD_MATCH,
                PREVIOUS_MATCH,
                MATCH_OPTIONS,
                "条件这一栏里勾了哪几条，就看哪几条。有一条成立即可，或要全部成立。");
    }

    private String ensureExecute() {
        return ensureChoice(
                FIELD_EXECUTE,
                PREVIOUS_EXECUTE,
                EXECUTE_OPTIONS,
                "条件成立之后，对告警规则这一栏里勾的那几条执行，或不执行。不执行时不再新开。已经开着的仍可关掉。");
    }

    /** 栏已经在时改名称和选项。存的仍是原来的值，已填的记录不用重选。 */
    private String ensureChoice(String name, String previousName, String options, String description) {
        FieldDO existing = findField(name);
        if (existing == null) {
            existing = findField(previousName);
        }
        if (existing != null) {
            saveCustomField(existing, name, "ENUM", description, options);
            return existing.getCode();
        }
        FieldCreateReqVO request = new FieldCreateReqVO();
        request.setName(name);
        request.setType("ENUM");
        request.setOptions(options);
        request.setSource("CUSTOM");
        request.setStatus(1);
        request.setDescription(description);
        return created(fieldService.createField(request));
    }

    private String conditionOptions(List<AlarmConditionCatalog.PointSentence> points) {
        JSONArray options = new JSONArray();
        for (AlarmConditionCatalog.Published published : AlarmConditionCatalog.conditions(points)) {
            option(options, published.label(), published.wideLabel(), published.code(), published.modelIds());
        }
        appendOpenOptions(options);
        return options.isEmpty() ? "" : options.toJSONString();
    }

    private String modelOptions(List<AlarmConditionCatalog.PointSentence> points) {
        JSONArray options = new JSONArray();
        option(options, "全部", MODELS_ALL);
        java.util.Set<Long> seen = new java.util.LinkedHashSet<>();
        for (AlarmConditionCatalog.PointSentence point : points) {
            if (!seen.add(point.modelId())) {
                continue;
            }
            option(options, point.modelName(), String.valueOf(point.modelId()));
        }
        return options.size() <= 1 ? "" : options.toJSONString();
    }

    private void appendOpenOptions(JSONArray options) {
        ModelDO threshold = modelMapper.selectByNameAndEntityTypeCode("阈值告警", AlarmRuleCatalogEnsure.DIRECTORY);
        if (threshold == null || threshold.getId() == null) {
            return;
        }
        List<EntityDO> rules = entityRepository.findByModelId(threshold.getId(), AlarmRuleCatalogEnsure.DIRECTORY);
        for (EntityDO rule : rules) {
            if (rule.getId() == null || !Integer.valueOf(1).equals(rule.getStatus())) {
                continue;
            }
            if (rule.getName() == null || rule.getName().isBlank()) {
                continue;
            }
            String label = rule.getName() + "还开着";
            java.util.List<String> models = new java.util.ArrayList<>();
            String modelName = text(rule, alarmRuleCatalogEnsure.fieldCode(AlarmRuleCatalogEnsure.FIELD_DEVICE_TYPE));
            if (modelName != null) {
                ModelDO equipment = modelMapper.selectByNameAndEntityTypeCode(modelName, "equipment");
                if (equipment != null && equipment.getId() != null) {
                    models.add(String.valueOf(equipment.getId()));
                }
            }
            option(options, label, label, "open:" + rule.getId(), models);
        }
    }

    private static void option(JSONArray options, String label, String value) {
        option(options, label, label, value, java.util.List.of());
    }

    private static void option(JSONArray options, String label, String wideLabel, String value,
                               java.util.List<String> models) {
        for (int i = 0; i < options.size(); i++) {
            if (value.equals(options.getJSONObject(i).getString("value"))) {
                return;
            }
        }
        JSONObject item = new JSONObject();
        item.put("label", label);
        item.put("wideLabel", wideLabel);
        item.put("value", value);
        if (models != null && !models.isEmpty()) {
            item.put("models", models);
        }
        options.add(item);
    }

    /** 适用型号在前，条件跟在后面。人先选型号，再看这个型号的条件。 */
    private void orderFields(Long modelId) {
        ModelFieldBatchAssignReqVO request = new ModelFieldBatchAssignReqVO();
        request.setModelId(modelId);
        java.util.List<ModelFieldBatchAssignReqVO.FieldAssignmentItem> items = new java.util.ArrayList<>();
        int sort = 10;
        for (String code : java.util.List.of(
                fieldCode(FIELD_MODELS),
                fieldCode(FIELD_CONDITION),
                fieldCode(FIELD_MATCH),
                fieldCode(FIELD_EXECUTE),
                fieldCode(AlarmStrategyCatalogEnsure.FIELD_RULES))) {
            FieldDO field = fieldMapper.selectByCode(code);
            if (field == null || field.getId() == null) {
                continue;
            }
            ModelFieldBatchAssignReqVO.FieldAssignmentItem item = new ModelFieldBatchAssignReqVO.FieldAssignmentItem();
            item.setFieldId(field.getId());
            item.setRequired(false);
            item.setSort(sort);
            // 告警规则是策略目录上默认不进详情的基础字段。这一型号要让人看见执行哪几条。
            if (AlarmStrategyCatalogEnsure.FIELD_RULES.equals(field.getName())) {
                item.setValidationRules(
                        "{\"createVisible\":true,\"editVisible\":true,\"detailVisible\":true}");
            }
            sort += 10;
            items.add(item);
        }
        if (items.isEmpty()) {
            return;
        }
        request.setFieldAssignments(items);
        modelFieldAssignmentService.assignFieldsToModel(request);
    }

    private static String text(EntityDO row, String fieldCode) {
        if (row.getCustomFields() == null || fieldCode == null) {
            return null;
        }
        Object value = row.getCustomFields().get(fieldCode);
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }

    private boolean tableExists(String table) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'dynamicbusiness' AND table_name = ?",
                Integer.class, table);
        return count != null && count > 0;
    }

    private String fieldCode(String name) {
        FieldDO field = findField(name);
        if (field == null || field.getCode() == null || field.getCode().isBlank()) {
            throw new ServiceException(500, "策略目录缺少字段「" + name + "」");
        }
        return field.getCode();
    }

    private FieldDO findField(String name) {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        List<FieldDO> rows = fieldMapper.selectList(new LambdaQueryWrapperX<FieldDO>()
                .eq(FieldDO::getName, name)
                .eq(FieldDO::getTenantId, tenantId)
                .eq(FieldDO::getDeleted, false));
        if (rows.size() > 1) {
            throw new ServiceException(500, "字段「" + name + "」有多条，对不上");
        }
        return rows.isEmpty() ? null : rows.get(0);
    }

    private String created(Long fieldId) {
        FieldDO created = fieldMapper.selectById(fieldId);
        if (created == null || created.getCode() == null || created.getCode().isBlank()) {
            throw new ServiceException(500, "条件字段创建后没有编码");
        }
        return created.getCode();
    }
}
