package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeSimpleVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.BusinessCapabilityFullRespVO;
import cn.cheers.x.module.dynamicbusiness.service.capability.contract.BusinessCapabilityFullContract;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.BusinessCapabilitySummaryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.CapabilityComponentProjectionRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.ModelCrudFormDefinitionRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.capability.BusinessCapabilityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.capability.CapabilityComponentProjectionDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.capability.ModelCrudFormDefinitionDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation.RelationFieldLibraryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.capability.BusinessCapabilityMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.capability.CapabilityComponentProjectionMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.capability.ModelCrudFormDefinitionMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.relation.RelationFieldLibraryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.StorageTypeEnum;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeService;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldGroupRespVO;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelFieldGroupService;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityBaseFieldColumnNames;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeContext;
import cn.cheers.x.module.dynamicbusiness.framework.field.EntityTypeFieldLabelHelper;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.cheers.x.module.dynamicbusiness.service.capability.form.ModelCrudFormFieldAssembler;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.service.capability.projection.CapabilityBlockProjectionBuilder;
import cn.cheers.x.module.dynamicbusiness.service.capability.system.SystemCapabilityCatalog;
import cn.cheers.x.module.dynamicbusiness.service.capability.system.SystemCapabilityDefinition;
import cn.cheers.x.module.dynamicbusiness.service.capability.system.SystemCapabilityProjectionBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 业务能力服务实现。
 *
 * <p>实现策略：</p>
 * <ul>
 *   <li>读路径严格按 entityTypeCode / componentCode / modelId 三组主键访问；</li>
 *   <li>不接受 dataSourceKey 形式的兼容参数；</li>
 *   <li>重建时同批写入 business_capability、capability_component_projection、model_crud_form_definition。</li>
 * </ul>
 *
 * <p>注意：当前实现生成的是“稳定契约骨架”，保证接口索引与结构先对齐定稿。
 * 后续若引入更细粒度端点构造器，可在不改变主键模型的前提下替换 JSON 生成逻辑。</p>
 */
@Service
@Slf4j
public class BusinessCapabilityServiceImpl implements BusinessCapabilityService {

    private static final Set<String> SUPPORTED_COMPONENT_CODES = Set.of("list", "tree", "table", "card");
    /** 实体内置列：列表默认展示 */
    private static final Set<String> BUILTIN_BASE_DISPLAY_KEYS = Set.of("id", "name", "code");

    @Resource
    private BusinessCapabilityMapper businessCapabilityMapper;
    @Resource
    private CapabilityComponentProjectionMapper capabilityComponentProjectionMapper;
    @Resource
    private ModelCrudFormDefinitionMapper modelCrudFormDefinitionMapper;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ModelRelationMapper modelRelationMapper;
    @Resource
    private RelationFieldLibraryMapper relationFieldLibraryMapper;
    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private EntityTypeService entityTypeService;
    @Resource
    private ModelFieldGroupService modelFieldGroupService;
    @Resource
    private EntityTypeBaseFieldMapper entityTypeBaseFieldMapper;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public List<BusinessCapabilitySummaryRespVO> listCapabilitySummaries(String businessCategory) {
        if (StringUtils.hasText(businessCategory)) {
            BusinessCategoryConstants.requireKnownCategory(businessCategory.trim());
        }

        Map<String, String> dynamicNameMap = entityTypeService.listSimple().stream()
                .collect(Collectors.toMap(EntityTypeSimpleVO::getCode, EntityTypeSimpleVO::getName, (a, b) -> a));

        return businessCapabilityMapper.selectAllOrderByCode(
                StringUtils.hasText(businessCategory) ? businessCategory.trim() : null
        ).stream().map(item -> toSummaryVo(item, dynamicNameMap)).toList();
    }

    private BusinessCapabilitySummaryRespVO toSummaryVo(
            BusinessCapabilityDO item,
            Map<String, String> dynamicNameMap) {
        BusinessCapabilitySummaryRespVO vo = new BusinessCapabilitySummaryRespVO();
        String category = StringUtils.hasText(item.getBusinessCategory())
                ? item.getBusinessCategory().trim()
                : BusinessCategoryConstants.DYNAMIC;
        vo.setBusinessCategory(category);
        vo.setEntityTypeCode(item.getEntityTypeCode());
        vo.setVersion(item.getVersion());
        vo.setSupportedDataKinds(resolveSupportedDataKinds(category));

        if (BusinessCategoryConstants.isSystem(category)) {
            vo.setEntityTypeName(SystemCapabilityCatalog.find(item.getEntityTypeCode())
                    .map(SystemCapabilityDefinition::getEntityTypeName)
                    .orElse(item.getEntityTypeCode()));
        } else {
            vo.setEntityTypeName(dynamicNameMap.getOrDefault(item.getEntityTypeCode(), item.getEntityTypeCode()));
        }
        return vo;
    }

    private List<String> resolveSupportedDataKinds(String businessCategory) {
        if (BusinessCategoryConstants.isSystem(businessCategory)) {
            return List.of(BusinessCategoryConstants.KIND_ENTITY);
        }
        return List.of(BusinessCategoryConstants.KIND_MODEL, BusinessCategoryConstants.KIND_ENTITY);
    }

    @Override
    public BusinessCapabilityFullRespVO getCapabilityFull(String entityTypeCode) {
        String code = requireEntityTypeCode(entityTypeCode);
        BusinessCapabilityDO data = businessCapabilityMapper.selectByEntityTypeCode(code);
        if (data == null) {
            throw new ServiceException(404, "未找到能力全集，entityTypeCode=" + code);
        }
        BusinessCapabilityFullRespVO vo = new BusinessCapabilityFullRespVO();
        vo.setEntityTypeCode(data.getEntityTypeCode());
        vo.setCapabilityFull(parseCapabilityFull(data.getCapabilityFull(), code));
        vo.setVersion(data.getVersion());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CapabilityComponentProjectionRespVO getProjection(
            String entityTypeCode, String componentCode, String dataKind) {
        String code = requireEntityTypeCode(entityTypeCode);
        String comp = requireComponentCode(componentCode);
        String kind = requireDataKind(dataKind, code);
        CapabilityComponentProjectionDO data = capabilityComponentProjectionMapper
                .selectByEntityTypeComponentAndDataKind(code, comp, kind);
        if (data == null) {
            log.info("[getProjection][投影缺失，触发重建][entityTypeCode={}][componentCode={}][dataKind={}]",
                    code, comp, kind);
            triggerRebuild(code);
            data = capabilityComponentProjectionMapper.selectByEntityTypeComponentAndDataKind(code, comp, kind);
        } else if (isLegacyProjectionFormat(data.getComponentInterface())) {
            log.info("[getProjection][检测到旧版 read/write 投影，触发重建][entityTypeCode={}][componentCode={}][dataKind={}]",
                    code, comp, kind);
            triggerRebuild(code);
            data = capabilityComponentProjectionMapper.selectByEntityTypeComponentAndDataKind(code, comp, kind);
        } else if (isBrokenDynamicTreeProjection(data.getComponentInterface(), comp, kind)) {
            log.info("[getProjection][动态树读 URL 异常，触发重建][entityTypeCode={}][componentCode={}][dataKind={}]",
                    code, comp, kind);
            triggerRebuild(code);
            data = capabilityComponentProjectionMapper.selectByEntityTypeComponentAndDataKind(code, comp, kind);
        }
        if (data == null) {
            throw new ServiceException(404,
                    "未找到能力投影，entityTypeCode=" + code + ", componentCode=" + comp + ", dataKind=" + kind);
        }
        CapabilityComponentProjectionRespVO vo = new CapabilityComponentProjectionRespVO();
        vo.setEntityTypeCode(data.getEntityTypeCode());
        vo.setComponentCode(data.getComponentCode());
        vo.setDataKind(data.getDataKind());
        vo.setComponentInterface(data.getComponentInterface());
        vo.setVersion(data.getVersion());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelCrudFormDefinitionRespVO getModelCrudFormDefinition(String entityTypeCode, Long modelId) {
        String code = requireEntityTypeCode(entityTypeCode);
        if (modelId == null) {
            throw new ServiceException(400, "modelId 不能为空");
        }
        ModelDO model = modelMapper.selectByIdIncludingDeleted(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在: " + modelId);
        }
        if (Boolean.TRUE.equals(model.getDeleted())) {
            throw new ServiceException(400, "模型已删除，禁止加载表单或变更: " + modelId);
        }
        // 按最新字段库 / 分配重建，避免关联目标仅改在字段库后仍读到过期表单（新建下拉为空）。
        refreshSingleModelCrudForm(code, modelId);
        ModelCrudFormDefinitionDO data = modelCrudFormDefinitionMapper.selectByEntityTypeAndModel(code, modelId);
        if (data == null) {
            throw new ServiceException(404, "未找到模型 CRUD 表单定义，entityTypeCode=" + code + ", modelId=" + modelId);
        }
        ModelCrudFormDefinitionRespVO vo = new ModelCrudFormDefinitionRespVO();
        vo.setEntityTypeCode(data.getEntityTypeCode());
        vo.setModelId(data.getModelId());
        vo.setCrudFormFields(data.getCrudFormFields());
        vo.setVersion(data.getVersion());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebuildByEntityTypeCode(String entityTypeCode) {
        String code = requireEntityTypeCode(entityTypeCode);
        if (SystemCapabilityCatalog.isSystemCapability(code)) {
            rebuildSystemCapability(code);
            return;
        }
        long newVersion = rebuildCapabilityAndProjections(code);
        List<ModelDO> models = listModelsForCrudFormRebuild(code);
        rebuildModelCrudFormsBatched(code, models, newVersion);
    }

    /**
     * 仅重建能力全集与组件投影（list/tree/table/card），不写各型号 CRUD 表单。
     * 固定列变更走此路径：列表投影需立刻更新；表单定义在打开时按需重建。
     *
     * @return 新写入的能力版本号
     */
    private long rebuildCapabilityAndProjections(String entityTypeCode) {
        String code = requireEntityTypeCode(entityTypeCode);
        BusinessCapabilityDO existing = businessCapabilityMapper.selectByEntityTypeCode(code);
        long newVersion = existing == null || existing.getVersion() == null ? 1L : existing.getVersion() + 1L;

        String fullJson = buildCapabilityFullJson(code);
        upsertCapabilityFull(code, BusinessCategoryConstants.DYNAMIC, fullJson, newVersion, existing);

        LinkedHashMap<String, Map<String, Object>> fieldMeta = collectFieldMeta(code);
        for (String componentCode : SUPPORTED_COMPONENT_CODES) {
            String entityProjectionJson = buildEntityProjectionJson(code, componentCode, newVersion, fieldMeta);
            upsertProjection(code, componentCode, BusinessCategoryConstants.KIND_ENTITY, entityProjectionJson, newVersion);
            String modelProjectionJson = buildModelProjectionJson(code, componentCode, newVersion);
            upsertProjection(code, componentCode, BusinessCategoryConstants.KIND_MODEL, modelProjectionJson, newVersion);
        }
        return newVersion;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebuildAllSystemCapabilities() {
        for (SystemCapabilityDefinition definition : SystemCapabilityCatalog.all()) {
            rebuildSystemCapability(definition.getEntityTypeCode());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebuildSystemCapability(String entityTypeCode) {
        String code = requireEntityTypeCode(entityTypeCode);
        SystemCapabilityDefinition definition = SystemCapabilityCatalog.find(code)
                .orElseThrow(() -> new ServiceException(404, "未找到系统业务能力定义，entityTypeCode=" + code));

        BusinessCapabilityDO existing = businessCapabilityMapper.selectByEntityTypeCode(code);
        long newVersion = existing == null || existing.getVersion() == null ? 1L : existing.getVersion() + 1L;

        String fullJson = SystemCapabilityProjectionBuilder.buildCapabilityFullJson(definition, newVersion, objectMapper);
        upsertCapabilityFull(code, BusinessCategoryConstants.SYSTEM, fullJson, newVersion, existing);

        for (String componentCode : SUPPORTED_COMPONENT_CODES) {
            String projectionJson = SystemCapabilityProjectionBuilder.buildProjectionJson(
                    definition, componentCode, newVersion, objectMapper);
            upsertProjection(code, componentCode, BusinessCategoryConstants.KIND_ENTITY, projectionJson, newVersion);
        }
    }

    private void triggerRebuild(String entityTypeCode) {
        if (SystemCapabilityCatalog.isSystemCapability(entityTypeCode)) {
            rebuildSystemCapability(entityTypeCode);
            return;
        }
        rebuildByEntityTypeCode(entityTypeCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebuildByModelId(Long modelId) {
        if (modelId == null) {
            throw new ServiceException(400, "modelId 不能为空");
        }
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null || !StringUtils.hasText(model.getEntityTypeCode())) {
            throw new ServiceException(404, "未找到模型或模型缺少 entityTypeCode，modelId=" + modelId);
        }
        rebuildByEntityTypeCode(model.getEntityTypeCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshModelCrudFormDefinition(Long modelId) {
        if (modelId == null) {
            throw new ServiceException(400, "modelId 不能为空");
        }
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null || !StringUtils.hasText(model.getEntityTypeCode())) {
            throw new ServiceException(404, "未找到模型或模型缺少 entityTypeCode，modelId=" + modelId);
        }
        String storageCode = model.getEntityTypeCode().trim();
        refreshSingleModelCrudForm(storageCode, modelId);
        refreshDomainRegistryCrudFormsForModel(model);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshAfterEntityTypeFieldDefinitionChanged(String entityTypeCode) {
        String code = requireEntityTypeCode(entityTypeCode);
        if (SystemCapabilityCatalog.isSystemCapability(code)) {
            rebuildSystemCapability(code);
            return;
        }
        // 固定列变更：只刷能力/列表投影。各型号 CRUD 表单在 getModelCrudFormDefinition 读路径按需重建，
        // 避免对上百型号整份写表单拖垮 save-batch。
        rebuildCapabilityAndProjections(code);
    }

    /**
     * 构建能力全集 JSON。
     */
    private String buildCapabilityFullJson(String entityTypeCode) {
        List<ModelDO> models = modelMapper.selectByEntityTypeCode(entityTypeCode);
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("entityTypeCode", entityTypeCode);
        root.put("businessCategory", BusinessCategoryConstants.DYNAMIC);
        root.put("capabilityVersion", 1);
        root.put("components", List.of("list", "tree", "table", "card"));
        List<Map<String, Object>> modelItems = models.stream().map(model -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("modelId", model.getId());
            map.put("modelCode", model.getCode());
            map.put("modelName", model.getName());
            map.put("status", model.getStatus());
            map.put("sort", model.getSort());
            return map;
        }).toList();
        Map<String, Object> modelSummary = new LinkedHashMap<>();
        modelSummary.put("modelCount", models.size());
        modelSummary.put("models", modelItems);
        root.put("modelSummary", modelSummary);
        return toJson(root);
    }

    /**
     * 构建 entity 组件投影 JSON（能力块模型：getList / filter / search / create 等）。
     */
    private String buildEntityProjectionJson(String entityTypeCode, String componentCode, Long version) {
        return buildEntityProjectionJson(entityTypeCode, componentCode, version, collectFieldMeta(entityTypeCode));
    }

    private String buildEntityProjectionJson(
            String entityTypeCode,
            String componentCode,
            Long version,
            LinkedHashMap<String, Map<String, Object>> fieldMeta) {
        List<Map<String, Object>> displayFields = buildDisplayFields(entityTypeCode, componentCode, fieldMeta);
        List<Map<String, Object>> filterFields = buildFilterFields(fieldMeta);
        List<String> searchableFieldKeys = collectFieldKeys(fieldMeta, "searchable");
        List<String> sortableFieldKeys = collectSortableFieldKeys(fieldMeta);
        Map<String, Object> projection = CapabilityBlockProjectionBuilder.buildDynamicEntity(
                entityTypeCode,
                componentCode,
                version,
                displayFields,
                filterFields,
                searchableFieldKeys,
                sortableFieldKeys);
        return toJson(projection);
    }

    /**
     * 构建 model 组件投影 JSON（模型目录：page-models 读路径，无实体 CRUD）。
     */
    private String buildModelProjectionJson(String entityTypeCode, String componentCode, Long version) {
        List<Map<String, Object>> displayFields = buildModelDisplayFields(componentCode);
        List<Map<String, Object>> filterFields = buildModelFilterFields();
        List<String> searchableFieldKeys = List.of("name", "code");
        List<String> sortableFieldKeys = List.of("name", "code", "status");
        Map<String, Object> projection = CapabilityBlockProjectionBuilder.buildDynamicModel(
                entityTypeCode,
                componentCode,
                version,
                displayFields,
                filterFields,
                searchableFieldKeys,
                sortableFieldKeys);
        return toJson(projection);
    }

    private List<Map<String, Object>> buildModelDisplayFields(String componentCode) {
        LinkedHashMap<String, Map<String, Object>> byFieldKey = new LinkedHashMap<>();
        ensureBuiltinDisplayField(byFieldKey, "id", "ID", 0);
        ensureBuiltinDisplayField(byFieldKey, "name", "名称", 1);
        ensureBuiltinDisplayField(byFieldKey, "code", "编码", 2);
        ensureBuiltinDisplayField(byFieldKey, "status", "状态", 3);

        List<Map<String, Object>> displayFields = new ArrayList<>();
        int order = 0;
        for (Map<String, Object> meta : byFieldKey.values()) {
            String fieldKey = String.valueOf(meta.get("fieldKey"));
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", fieldKey);
            item.put("fieldKey", fieldKey);
            item.put("label", meta.get("label"));
            item.put("renderAs", "text");
            item.put("sortOrder", meta.get("sortOrder") != null ? meta.get("sortOrder") : order++);
            item.put("defaultVisible", true);
            item.put("baseField", true);
            item.put("applicableViews", List.of(componentCode));
            displayFields.add(item);
        }
        return displayFields;
    }

    private List<Map<String, Object>> buildModelFilterFields() {
        Map<String, Object> statusFilter = new LinkedHashMap<>();
        statusFilter.put("id", "status");
        statusFilter.put("fieldKey", "status");
        statusFilter.put("label", "状态");
        statusFilter.put("renderAs", "select");
        statusFilter.put("sortOrder", 0);
        statusFilter.put("bindTo", "field-filter");
        statusFilter.put("searchable", false);
        statusFilter.put("sortable", true);
        statusFilter.put("defaultVisible", true);
        return List.of(statusFilter);
    }

    private List<String> collectFieldKeys(String entityTypeCode, String flagKey) {
        return collectFieldKeys(collectFieldMeta(entityTypeCode), flagKey);
    }

    private List<String> collectFieldKeys(LinkedHashMap<String, Map<String, Object>> byFieldKey, String flagKey) {
        List<String> keys = new ArrayList<>();
        for (Map<String, Object> meta : byFieldKey.values()) {
            if (Boolean.TRUE.equals(meta.get(flagKey))) {
                keys.add(String.valueOf(meta.get("fieldKey")));
            }
        }
        return keys;
    }

    /**
     * 可排序字段进入能力投影 sort.fields：核心列、勾选可排序的基础字段，以及勾选可排序的扩展字段（EVA 索引可排）。
     */
    private List<String> collectSortableFieldKeys(LinkedHashMap<String, Map<String, Object>> byFieldKey) {
        LinkedHashSet<String> keys = new LinkedHashSet<>();
        for (Map<String, Object> meta : byFieldKey.values()) {
            if (!Boolean.TRUE.equals(meta.get("sortable"))) {
                continue;
            }
            String fieldKey = String.valueOf(meta.get("fieldKey"));
            if (StringUtils.hasText(fieldKey)) {
                keys.add(fieldKey);
            }
        }
        if (!keys.contains("name")) {
            keys.add("name");
        }
        return new ArrayList<>(keys);
    }

    @SuppressWarnings("unused")
    private String resolveReadEndpoint(String componentCode) {
        return "/dynamicbusiness/business/entities/query-by-scene";
    }

    private List<Map<String, Object>> buildDisplayFields(String entityTypeCode, String componentCode) {
        return buildDisplayFields(entityTypeCode, componentCode, collectFieldMeta(entityTypeCode));
    }

    private List<Map<String, Object>> buildDisplayFields(
            String entityTypeCode,
            String componentCode,
            LinkedHashMap<String, Map<String, Object>> sourceMeta) {
        LinkedHashMap<String, Map<String, Object>> byFieldKey = deepCopyFieldMeta(sourceMeta);
        mergeBusinessBaseFieldMeta(entityTypeCode, byFieldKey);
        ensureBuiltinDisplayField(byFieldKey, "id", "ID", 0);
        ensureBuiltinDisplayField(byFieldKey, "name", "名称", 1);
        ensureBuiltinDisplayField(byFieldKey, "code", "编码", 2);

        List<Map<String, Object>> displayFields = new ArrayList<>();
        int order = 0;
        for (Map<String, Object> meta : byFieldKey.values()) {
            String fieldKey = String.valueOf(meta.get("fieldKey"));
            // 列表展示列 + 全量字段目录（含非基础字段）；列表 UI 用 defaultVisible 控制默认列
            boolean visibleByDefault = isBaseDisplayField(meta, fieldKey);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", fieldKey);
            item.put("fieldKey", fieldKey);
            item.put("label", meta.get("label"));
            item.put("renderAs", mapDisplayRenderAs(String.valueOf(meta.get("fieldType"))));
            item.put("sortOrder", meta.get("sortOrder") != null ? meta.get("sortOrder") : order++);
            item.put("defaultVisible", visibleByDefault);
            item.put("baseField", Boolean.TRUE.equals(meta.get("baseField")) || BUILTIN_BASE_DISPLAY_KEYS.contains(fieldKey));
            item.put("applicableViews", List.of(componentCode));
            if (meta.get("groupId") != null) {
                item.put("groupId", meta.get("groupId"));
            }
            if (meta.get("groupName") != null) {
                item.put("groupName", meta.get("groupName"));
            }
            if (meta.get("groupSortOrder") != null) {
                item.put("groupSortOrder", meta.get("groupSortOrder"));
            }
            copyFieldSemanticsToProjectionItem(meta, item);
            displayFields.add(item);
        }
        displayFields.sort(displayFieldOrderComparator());
        return displayFields;
    }

    private Comparator<Map<String, Object>> displayFieldOrderComparator() {
        return Comparator
                .comparingInt((Map<String, Object> item) -> item.get("groupSortOrder") instanceof Integer groupSort
                        ? groupSort
                        : Integer.MAX_VALUE)
                .thenComparingInt(item -> item.get("sortOrder") instanceof Integer sort
                        ? sort
                        : Integer.MAX_VALUE)
                .thenComparing(item -> String.valueOf(item.get("fieldKey")));
    }

    private String mapDisplayRenderAs(String fieldType) {
        if (!StringUtils.hasText(fieldType)) {
            return "text";
        }
        return switch (fieldType.trim().toUpperCase()) {
            case "BOOLEAN" -> "boolean";
            case "ENUM" -> "dict";
            case "DATE", "DATETIME", "TIMESTAMP" -> "datetime";
            default -> "text";
        };
    }

    private List<Map<String, Object>> buildFilterFields(String entityTypeCode) {
        return buildFilterFields(collectFieldMeta(entityTypeCode));
    }

    private List<Map<String, Object>> buildFilterFields(LinkedHashMap<String, Map<String, Object>> byFieldKey) {
        List<Map<String, Object>> filters = new ArrayList<>();
        int order = 0;
        for (Map<String, Object> meta : byFieldKey.values()) {
            if (!Boolean.TRUE.equals(meta.get("filterable"))) {
                continue;
            }
            Map<String, Object> filter = new LinkedHashMap<>();
            String fieldKey = String.valueOf(meta.get("fieldKey"));
            filter.put("id", fieldKey);
            filter.put("fieldKey", fieldKey);
            filter.put("label", meta.get("label"));
            filter.put("renderAs", mapFilterControl(String.valueOf(meta.get("fieldType"))));
            filter.put("sortOrder", meta.get("sortOrder") != null ? meta.get("sortOrder") : order++);
            filter.put("bindTo", "field-filter");
            filter.put("searchable", Boolean.TRUE.equals(meta.get("searchable")));
            filter.put("sortable", Boolean.TRUE.equals(meta.get("sortable")));
            filter.put("defaultVisible", true);
            copyFieldSemanticsToProjectionItem(meta, filter);
            filters.add(filter);
        }
        filters.sort(Comparator.comparingInt(item -> item.get("sortOrder") instanceof Integer sort
                ? sort
                : Integer.MAX_VALUE));
        return filters;
    }

    private LinkedHashMap<String, Map<String, Object>> collectFieldMeta(String entityTypeCode) {
        LinkedHashMap<String, Map<String, Object>> byFieldKey = new LinkedHashMap<>();
        List<ModelDO> models = modelMapper.selectByEntityTypeCode(entityTypeCode);
        if (models == null || models.isEmpty()) {
            return byFieldKey;
        }
        List<Long> modelIds = models.stream().map(ModelDO::getId).filter(Objects::nonNull).toList();
        List<ModelFieldAssignmentDO> allAssigns = modelFieldAssignmentMapper.selectByModelIds(modelIds);
        Map<Long, List<ModelFieldAssignmentDO>> assignsByModelId = groupAssignmentsByModelId(allAssigns);

        Set<Long> fieldIds = new HashSet<>();
        for (ModelFieldAssignmentDO assign : allAssigns) {
            if (assign.getFieldId() != null) {
                fieldIds.add(assign.getFieldId());
            }
        }
        Map<Long, FieldDO> fieldById = loadFieldMapByIds(fieldIds);

        for (ModelDO model : models) {
            List<ModelFieldAssignmentDO> assigns =
                    assignsByModelId.getOrDefault(model.getId(), List.of());
            for (ModelFieldAssignmentDO assign : assigns) {
                FieldDO field = fieldById.get(assign.getFieldId());
                if (field == null || !StringUtils.hasText(field.getCode())) {
                    continue;
                }
                String fieldKey = field.getCode().trim();
                Map<String, Object> existing = byFieldKey.get(fieldKey);
                if (existing == null) {
                    Map<String, Object> meta = new LinkedHashMap<>();
                    meta.put("fieldKey", fieldKey);
                    meta.put("label", StringUtils.hasText(field.getName()) ? field.getName() : fieldKey);
                    meta.put("fieldType", field.getType());
                    meta.put("filterable", Boolean.TRUE.equals(assign.getIsFilterable()));
                    meta.put("searchable", Boolean.TRUE.equals(assign.getIsSearchable()));
                    meta.put("sortable", Boolean.TRUE.equals(assign.getIsSortable()));
                    meta.put("sortOrder", assign.getSort() != null ? assign.getSort() : 0);
                    meta.put("baseField", false);
                    applyFieldSemanticsAndRefTarget(meta, field);
                    byFieldKey.put(fieldKey, meta);
                    continue;
                }
                existing.put("filterable", Boolean.TRUE.equals(existing.get("filterable"))
                        || Boolean.TRUE.equals(assign.getIsFilterable()));
                existing.put("searchable", Boolean.TRUE.equals(existing.get("searchable"))
                        || Boolean.TRUE.equals(assign.getIsSearchable()));
                existing.put("sortable", Boolean.TRUE.equals(existing.get("sortable"))
                        || Boolean.TRUE.equals(assign.getIsSortable()));
                if (!existing.containsKey("semanticType") && !existing.containsKey("targetEntityTypeCode")) {
                    applyFieldSemanticsAndRefTarget(existing, field);
                }
            }
            applyModelGroupMeta(model.getId(), byFieldKey, fieldById);
        }
        return byFieldKey;
    }

    private void applyModelGroupMeta(
            Long modelId,
            LinkedHashMap<String, Map<String, Object>> byFieldKey,
            Map<Long, FieldDO> fieldById) {
        List<ModelFieldGroupRespVO> groups;
        try {
            groups = modelFieldGroupService.listModelFieldGroupsByModelId(modelId);
        } catch (Exception ex) {
            log.debug("skip model group meta for modelId={}: {}", modelId, ex.getMessage());
            return;
        }
        if (groups == null || groups.isEmpty()) {
            return;
        }
        groups.sort(Comparator.comparingInt(group -> group.getSort() != null ? group.getSort() : 0));
        for (ModelFieldGroupRespVO group : groups) {
            if (group.getFields() == null || group.getFields().isEmpty()) {
                continue;
            }
            int groupSort = group.getSort() != null ? group.getSort() : 0;
            for (ModelFieldGroupRespVO.FieldRefVO ref : group.getFields()) {
                if (ref.getFieldId() == null) {
                    continue;
                }
                FieldDO field = fieldById.get(ref.getFieldId());
                if (field == null || !StringUtils.hasText(field.getCode())) {
                    continue;
                }
                String fieldKey = field.getCode().trim();
                Map<String, Object> meta = byFieldKey.get(fieldKey);
                if (meta == null) {
                    continue;
                }
                int fieldSort = ref.getSort() != null ? ref.getSort() : 0;
                Integer existingGroupSort = meta.get("groupSortOrder") instanceof Integer value ? value : null;
                Integer existingSort = meta.get("sortOrder") instanceof Integer value ? value : null;
                if (existingGroupSort == null
                        || groupSort < existingGroupSort
                        || (groupSort == existingGroupSort && (existingSort == null || fieldSort < existingSort))) {
                    meta.put("groupId", group.getId());
                    meta.put("groupName", group.getName());
                    meta.put("groupSortOrder", groupSort);
                    meta.put("sortOrder", fieldSort);
                }
            }
        }
    }

    private void mergeBusinessBaseFieldMeta(String entityTypeCode, LinkedHashMap<String, Map<String, Object>> byFieldKey) {
        List<EntityTypeBaseFieldDO> baseFields = entityTypeBaseFieldMapper.selectByEntityTypeCode(entityTypeCode);
        if (baseFields == null || baseFields.isEmpty()) {
            return;
        }
        int order = 0;
        for (EntityTypeBaseFieldDO baseField : baseFields) {
            if (baseField == null || !StringUtils.hasText(baseField.getFieldCode())) {
                continue;
            }
            String fieldKey = baseField.getFieldCode().trim();
            Map<String, Object> meta = byFieldKey.computeIfAbsent(fieldKey, key -> new LinkedHashMap<>());
            meta.put("fieldKey", fieldKey);
            meta.put("label", StringUtils.hasText(baseField.getFieldName()) ? baseField.getFieldName() : fieldKey);
            meta.put("fieldType", StringUtils.hasText(baseField.getDataType()) ? baseField.getDataType() : "TEXT");
            meta.put("sortOrder", baseField.getSortOrder() != null ? baseField.getSortOrder() : order++);
            meta.put("baseField", true);
            meta.put("filterable", Boolean.TRUE.equals(baseField.getIsFilterable())
                    || Boolean.TRUE.equals(meta.get("filterable")));
            meta.put("searchable", Boolean.TRUE.equals(baseField.getIsSearchable())
                    || Boolean.TRUE.equals(meta.get("searchable")));
            meta.put("sortable", Boolean.TRUE.equals(baseField.getIsSortable())
                    || Boolean.TRUE.equals(meta.get("sortable")));
            if (!meta.containsKey("groupName")) {
                meta.put("groupName", "基础信息");
                meta.put("groupSortOrder", 0);
            }
        }
        enrichBaseFieldSemanticsFromLibrary(byFieldKey);
    }

    /** 基础字段行可能无 FieldDO 上下文：按 field_code 回查字段库补 semantic / REF 目标。 */
    private void enrichBaseFieldSemanticsFromLibrary(LinkedHashMap<String, Map<String, Object>> byFieldKey) {
        if (byFieldKey == null || byFieldKey.isEmpty()) {
            return;
        }
        List<String> codesNeedingEnrich = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : byFieldKey.entrySet()) {
            Map<String, Object> meta = entry.getValue();
            if (meta == null) {
                continue;
            }
            if (meta.containsKey("semanticType") && meta.containsKey("targetEntityTypeCode")) {
                continue;
            }
            if (Boolean.TRUE.equals(meta.get("baseField")) || needsSemanticEnrich(meta)) {
                codesNeedingEnrich.add(entry.getKey());
            }
        }
        if (codesNeedingEnrich.isEmpty()) {
            return;
        }
        Map<String, FieldDO> fieldByCode = loadFieldMapByCodes(codesNeedingEnrich);
        for (String code : codesNeedingEnrich) {
            Map<String, Object> meta = byFieldKey.get(code);
            if (meta == null) {
                continue;
            }
            applyFieldSemanticsAndRefTarget(meta, fieldByCode.get(code));
        }
    }

    private static boolean needsSemanticEnrich(Map<String, Object> meta) {
        return !meta.containsKey("semanticType")
                || (isEntityRefFieldType(String.valueOf(meta.get("fieldType")))
                && !meta.containsKey("targetEntityTypeCode"));
    }

    private static boolean isEntityRefFieldType(String fieldType) {
        if (!StringUtils.hasText(fieldType)) {
            return false;
        }
        String t = fieldType.trim();
        return FieldTypeEnum.isEntityRef(t) || "REFERENCE".equalsIgnoreCase(t) || "REF".equalsIgnoreCase(t);
    }

    private void applyFieldSemanticsAndRefTarget(Map<String, Object> meta, FieldDO field) {
        if (meta == null) {
            return;
        }
        String fieldKey = String.valueOf(meta.get("fieldKey"));
        String semantic = null;
        if (field != null && StringUtils.hasText(field.getSemanticType())) {
            semantic = field.getSemanticType().trim();
        }
        if (!StringUtils.hasText(semantic)) {
            semantic = EntityBaseFieldColumnNames.inferSemanticType(fieldKey);
        }
        if (StringUtils.hasText(semantic) && !meta.containsKey("semanticType")) {
            meta.put("semanticType", semantic);
        }

        String fieldType = field != null && StringUtils.hasText(field.getType())
                ? field.getType()
                : String.valueOf(meta.get("fieldType"));
        if (!isEntityRefFieldType(fieldType)) {
            return;
        }
        if (meta.containsKey("targetEntityTypeCode")) {
            return;
        }
        String target = ModelCrudFormFieldAssembler.resolveTargetEntityTypeFromField(field);
        if (!StringUtils.hasText(target)) {
            target = EntityBaseFieldColumnNames.inferRefTargetEntityType(fieldKey);
        }
        if (!StringUtils.hasText(target) && StringUtils.hasText(semantic)
                && !semantic.contains("_") && semantic.length() < 64) {
            // ENTITY_REF 且 semantic_type=facility/zone 等业务类型编码
            target = semantic;
        }
        if (!StringUtils.hasText(target)) {
            return;
        }
        meta.put("targetEntityTypeCode", target.trim());
        Map<String, Object> binding = new LinkedHashMap<>();
        binding.put("businessCategory", BusinessCategoryConstants.DYNAMIC);
        binding.put("dataKind", BusinessCategoryConstants.KIND_ENTITY);
        binding.put("entityTypeCode", target.trim());
        meta.put("refTarget", Map.of(
                "capabilityBinding", binding,
                "valueField", "id",
                "labelField", "name"));
    }

    private static void copyFieldSemanticsToProjectionItem(Map<String, Object> meta, Map<String, Object> item) {
        if (meta.get("semanticType") != null) {
            item.put("semanticType", meta.get("semanticType"));
        }
        if (meta.get("targetEntityTypeCode") != null) {
            item.put("targetEntityTypeCode", meta.get("targetEntityTypeCode"));
        }
        if (meta.get("refTarget") != null) {
            item.put("refTarget", meta.get("refTarget"));
        }
    }

    private Map<String, FieldDO> loadFieldMapByCodes(Collection<String> codes) {
        Map<String, FieldDO> byCode = new HashMap<>();
        if (codes == null || codes.isEmpty()) {
            return byCode;
        }
        for (String code : codes) {
            if (!StringUtils.hasText(code)) {
                continue;
            }
            FieldDO field = fieldMapper.selectByCode(code.trim());
            if (field != null && StringUtils.hasText(field.getCode())) {
                byCode.put(field.getCode().trim(), field);
            }
        }
        return byCode;
    }

    private boolean isBaseDisplayField(Map<String, Object> meta, String fieldKey) {
        if (BUILTIN_BASE_DISPLAY_KEYS.contains(fieldKey)) {
            return true;
        }
        return Boolean.TRUE.equals(meta.get("baseField"));
    }

    private void ensureBuiltinDisplayField(
            LinkedHashMap<String, Map<String, Object>> byFieldKey,
            String fieldKey,
            String label,
            int sortOrder) {
        byFieldKey.computeIfAbsent(fieldKey, key -> {
            Map<String, Object> meta = new LinkedHashMap<>();
            meta.put("fieldKey", key);
            meta.put("label", label);
            meta.put("fieldType", "TEXT");
            meta.put("sortOrder", sortOrder);
            meta.put("groupName", "基础信息");
            meta.put("groupSortOrder", 0);
            meta.put("filterable", false);
            meta.put("searchable", false);
            // name 默认允许表头排序；其余内置列默认不可排，由基础字段 / 分配覆盖
            meta.put("sortable", "name".equals(key));
            meta.put("baseField", true);
            return meta;
        });
        Map<String, Object> meta = byFieldKey.get(fieldKey);
        if (meta != null) {
            meta.put("baseField", true);
            if (meta.get("groupSortOrder") == null) {
                meta.put("groupName", "基础信息");
                meta.put("groupSortOrder", 0);
            }
        }
    }

    private String mapFilterControl(String fieldType) {
        if (!StringUtils.hasText(fieldType)) {
            return "input";
        }
        return switch (fieldType.trim().toUpperCase()) {
            case "BOOLEAN" -> "boolean";
            case "ENUM" -> "select";
            case "DATE", "DATETIME", "TIMESTAMP" -> "date";
            case "NUMBER", "INTEGER", "DECIMAL" -> "input";
            case "ENTITY_REF", "REFERENCE", "REF" -> "ref-picker";
            case "ENTITY_REF_MULTI", "REF_MULTI", "BATCH_ENTITY_REF" -> "ref-picker-multi";
            default -> "input";
        };
    }

    /**
     * 构建模型 CRUD 表单定义 JSON。
     */
    private String buildModelCrudFormJson(Long modelId, String entityTypeCode) {
        CrudFormFieldContext context = loadCrudFormFieldContext(modelId, entityTypeCode);
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode);
        Map<String, String> platformFieldLabels = EntityTypeFieldLabelHelper.readLabels(entityType);
        Map<String, Object> root = ModelCrudFormFieldAssembler.buildFormRoot(
                modelId,
                entityTypeCode,
                context.includeBaseFields(),
                context.assigns(),
                context.fieldById(),
                context.baseFieldByCode(),
                context.groups(),
                context.refResolveContext(),
                platformFieldLabels);
        return toJson(root);
    }

    /**
     * 按类型批量重建各型号 CRUD 表单：分配/字段库/固定列/REF 一次拉齐，再按型号组装 JSON。
     */
    private void rebuildModelCrudFormsBatched(String registryEntityTypeCode, List<ModelDO> models, long version) {
        if (models == null || models.isEmpty()) {
            return;
        }
        List<Long> modelIds = models.stream().map(ModelDO::getId).filter(Objects::nonNull).toList();
        List<ModelFieldAssignmentDO> allAssigns = modelFieldAssignmentMapper.selectByModelIds(modelIds);
        Map<Long, List<ModelFieldAssignmentDO>> assignsByModelId = groupAssignmentsByModelId(allAssigns);

        Set<Long> fieldIds = new HashSet<>();
        for (ModelFieldAssignmentDO assign : allAssigns) {
            if (assign != null && assign.getFieldId() != null) {
                fieldIds.add(assign.getFieldId());
            }
        }

        Map<String, Map<String, EntityTypeBaseFieldDO>> baseFieldsByEntityType = new HashMap<>();
        Map<String, Boolean> includeBaseByEntityType = new HashMap<>();
        Map<String, Map<String, String>> platformLabelsByEntityType = new HashMap<>();
        for (ModelDO model : models) {
            String formFieldEntityTypeCode = resolveFormFieldEntityTypeCode(registryEntityTypeCode, model);
            if (!StringUtils.hasText(formFieldEntityTypeCode)) {
                continue;
            }
            includeBaseByEntityType.computeIfAbsent(formFieldEntityTypeCode, this::shouldIncludeBaseFields);
            if (Boolean.TRUE.equals(includeBaseByEntityType.get(formFieldEntityTypeCode))
                    && !baseFieldsByEntityType.containsKey(formFieldEntityTypeCode)) {
                Map<String, EntityTypeBaseFieldDO> baseFieldByCode = new LinkedHashMap<>();
                List<EntityTypeBaseFieldDO> baseFields =
                        entityTypeBaseFieldMapper.selectByEntityTypeCode(formFieldEntityTypeCode);
                if (baseFields != null) {
                    for (EntityTypeBaseFieldDO baseField : baseFields) {
                        if (baseField != null && StringUtils.hasText(baseField.getFieldCode())) {
                            baseFieldByCode.put(baseField.getFieldCode().trim(), baseField);
                        }
                        if (baseField != null && baseField.getLibraryFieldId() != null) {
                            fieldIds.add(baseField.getLibraryFieldId());
                        }
                    }
                }
                baseFieldsByEntityType.put(formFieldEntityTypeCode, baseFieldByCode);
            }
            platformLabelsByEntityType.computeIfAbsent(formFieldEntityTypeCode, code -> {
                EntityTypeDO entityType = entityTypeMapper.selectByCode(code);
                return EntityTypeFieldLabelHelper.readLabels(entityType);
            });
        }

        Map<Long, FieldDO> fieldById = loadFieldMapByIds(fieldIds);
        ModelCrudFormFieldAssembler.RefResolveContext sharedRefContext = loadRefResolveContext(allAssigns);

        for (ModelDO model : models) {
            String formFieldEntityTypeCode = resolveFormFieldEntityTypeCode(registryEntityTypeCode, model);
            List<ModelFieldAssignmentDO> assigns =
                    assignsByModelId.getOrDefault(model.getId(), List.of());
            boolean includeBaseFields = Boolean.TRUE.equals(includeBaseByEntityType.get(formFieldEntityTypeCode));
            Map<String, EntityTypeBaseFieldDO> baseFieldByCode = includeBaseFields
                    ? baseFieldsByEntityType.getOrDefault(formFieldEntityTypeCode, Map.of())
                    : Map.of();
            List<ModelFieldGroupRespVO> groups;
            try {
                groups = modelFieldGroupService.listModelFieldGroupsByModelId(model.getId());
            } catch (Exception ex) {
                log.debug("skip model field groups for modelId={}: {}", model.getId(), ex.getMessage());
                groups = List.of();
            }
            Map<String, Object> root = ModelCrudFormFieldAssembler.buildFormRoot(
                    model.getId(),
                    formFieldEntityTypeCode,
                    includeBaseFields,
                    assigns,
                    fieldById,
                    baseFieldByCode,
                    groups,
                    sharedRefContext,
                    platformLabelsByEntityType.getOrDefault(formFieldEntityTypeCode, Map.of()));
            upsertModelCrudForm(registryEntityTypeCode, model.getId(), toJson(root), version);
        }
    }

    private CrudFormFieldContext loadCrudFormFieldContext(Long modelId, String entityTypeCode) {
        List<ModelFieldAssignmentDO> assigns = modelFieldAssignmentMapper.selectByModelId(modelId);
        Set<Long> fieldIds = new HashSet<>();
        for (ModelFieldAssignmentDO assign : assigns) {
            if (assign.getFieldId() != null) {
                fieldIds.add(assign.getFieldId());
            }
        }
        boolean includeBaseFields = shouldIncludeBaseFields(entityTypeCode);
        Map<String, EntityTypeBaseFieldDO> baseFieldByCode = new LinkedHashMap<>();
        if (includeBaseFields) {
            List<EntityTypeBaseFieldDO> baseFields = entityTypeBaseFieldMapper.selectByEntityTypeCode(entityTypeCode);
            if (baseFields != null) {
                for (EntityTypeBaseFieldDO baseField : baseFields) {
                    if (baseField != null && StringUtils.hasText(baseField.getFieldCode())) {
                        baseFieldByCode.put(baseField.getFieldCode().trim(), baseField);
                    }
                    if (baseField != null && baseField.getLibraryFieldId() != null) {
                        fieldIds.add(baseField.getLibraryFieldId());
                    }
                }
            }
        }
        Map<Long, FieldDO> fieldById = loadFieldMapByIds(fieldIds);
        List<ModelFieldGroupRespVO> groups;
        try {
            groups = modelFieldGroupService.listModelFieldGroupsByModelId(modelId);
        } catch (Exception ex) {
            log.debug("skip model field groups for modelId={}: {}", modelId, ex.getMessage());
            groups = List.of();
        }
        return new CrudFormFieldContext(
                includeBaseFields,
                assigns,
                fieldById,
                baseFieldByCode,
                groups,
                loadRefResolveContext(assigns));
    }

    private ModelCrudFormFieldAssembler.RefResolveContext loadRefResolveContext(
            List<ModelFieldAssignmentDO> assigns) {
        if (assigns == null || assigns.isEmpty()) {
            return ModelCrudFormFieldAssembler.RefResolveContext.empty();
        }
        Set<Long> refLibraryIds = new HashSet<>();
        Set<Long> modelRelationIds = new HashSet<>();
        for (ModelFieldAssignmentDO assign : assigns) {
            if (assign.getRefLibraryId() != null) {
                refLibraryIds.add(assign.getRefLibraryId());
            }
            if (assign.getModelRelationId() != null) {
                modelRelationIds.add(assign.getModelRelationId());
            }
        }
        Map<Long, RelationFieldLibraryDO> refLibraryById = new HashMap<>();
        if (!refLibraryIds.isEmpty()) {
            for (RelationFieldLibraryDO lib : relationFieldLibraryMapper.selectByIds(refLibraryIds)) {
                if (lib != null && lib.getId() != null) {
                    refLibraryById.put(lib.getId(), lib);
                }
            }
        }
        Map<Long, ModelRelationDO> modelRelationById = new HashMap<>();
        Map<String, String> modelCodeToEntityTypeCode = new HashMap<>();
        if (!modelRelationIds.isEmpty()) {
            List<ModelRelationDO> relations = modelRelationMapper.selectByIds(modelRelationIds);
            Set<String> targetModelCodes = new HashSet<>();
            for (ModelRelationDO rel : relations) {
                if (rel == null || rel.getId() == null) {
                    continue;
                }
                modelRelationById.put(rel.getId(), rel);
                if (StringUtils.hasText(rel.getTargetModelCode())) {
                    targetModelCodes.add(rel.getTargetModelCode().trim());
                }
            }
            if (!targetModelCodes.isEmpty()) {
                List<ModelDO> targetModels = modelMapper.selectList(new LambdaQueryWrapperX<ModelDO>()
                        .in(ModelDO::getCode, targetModelCodes));
                for (ModelDO targetModel : targetModels) {
                    if (targetModel != null
                            && StringUtils.hasText(targetModel.getCode())
                            && StringUtils.hasText(targetModel.getEntityTypeCode())) {
                        modelCodeToEntityTypeCode.put(
                                targetModel.getCode().trim(),
                                targetModel.getEntityTypeCode().trim());
                    }
                }
            }
        }
        return new ModelCrudFormFieldAssembler.RefResolveContext(
                refLibraryById, modelRelationById, modelCodeToEntityTypeCode);
    }

    private Map<Long, FieldDO> loadFieldMapByIds(Collection<Long> fieldIds) {
        if (fieldIds == null || fieldIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, FieldDO> fieldById = new HashMap<>(Math.max(fieldIds.size(), 1));
        for (FieldDO field : fieldMapper.selectByIds(fieldIds)) {
            if (field != null && field.getId() != null) {
                fieldById.put(field.getId(), field);
            }
        }
        return fieldById;
    }

    private Map<Long, List<ModelFieldAssignmentDO>> groupAssignmentsByModelId(
            List<ModelFieldAssignmentDO> assigns) {
        Map<Long, List<ModelFieldAssignmentDO>> byModelId = new HashMap<>();
        if (assigns == null) {
            return byModelId;
        }
        for (ModelFieldAssignmentDO assign : assigns) {
            if (assign == null || assign.getModelId() == null) {
                continue;
            }
            byModelId.computeIfAbsent(assign.getModelId(), key -> new ArrayList<>()).add(assign);
        }
        return byModelId;
    }

    private LinkedHashMap<String, Map<String, Object>> deepCopyFieldMeta(
            LinkedHashMap<String, Map<String, Object>> source) {
        LinkedHashMap<String, Map<String, Object>> copy = new LinkedHashMap<>();
        if (source == null) {
            return copy;
        }
        for (Map.Entry<String, Map<String, Object>> entry : source.entrySet()) {
            copy.put(entry.getKey(), new LinkedHashMap<>(entry.getValue()));
        }
        return copy;
    }

    private boolean shouldIncludeBaseFields(String entityTypeCode) {
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode);
        if (entityType == null || !StringUtils.hasText(entityType.getStorageType())) {
            return false;
        }
        StorageTypeEnum storageType = StorageTypeEnum.getByCode(entityType.getStorageType());
        return storageType != null && storageType.isDedicated();
    }

    private record CrudFormFieldContext(
            boolean includeBaseFields,
            List<ModelFieldAssignmentDO> assigns,
            Map<Long, FieldDO> fieldById,
            Map<String, EntityTypeBaseFieldDO> baseFieldByCode,
            List<ModelFieldGroupRespVO> groups,
            ModelCrudFormFieldAssembler.RefResolveContext refResolveContext) {
    }

    /**
     * 能力全集 UPSERT。
     */
    private void upsertCapabilityFull(
            String entityTypeCode,
            String businessCategory,
            String fullJson,
            Long version,
            BusinessCapabilityDO existing) {
        BusinessCapabilityDO data = existing == null ? new BusinessCapabilityDO() : existing;
        data.setEntityTypeCode(entityTypeCode);
        data.setBusinessCategory(businessCategory);
        data.setCapabilityFull(fullJson);
        data.setVersion(version);
        if (existing == null) {
            fillInsertAuditFields(data);
            businessCapabilityMapper.insert(data);
        } else {
            businessCapabilityMapper.updateById(data);
        }
    }

    /**
     * 投影 UPSERT。
     */
    private void upsertProjection(
            String entityTypeCode,
            String componentCode,
            String dataKind,
            String projectionJson,
            Long version) {
        CapabilityComponentProjectionDO existing = capabilityComponentProjectionMapper
                .selectByEntityTypeComponentAndDataKind(entityTypeCode, componentCode, dataKind);
        CapabilityComponentProjectionDO data = existing == null ? new CapabilityComponentProjectionDO() : existing;
        data.setEntityTypeCode(entityTypeCode);
        data.setComponentCode(componentCode);
        data.setDataKind(dataKind);
        data.setComponentInterface(projectionJson);
        data.setVersion(version);
        if (existing == null) {
            fillInsertAuditFields(data);
            capabilityComponentProjectionMapper.insert(data);
        } else {
            capabilityComponentProjectionMapper.updateById(data);
        }
    }

    /**
     * 模型 CRUD 表单定义 UPSERT。
     */
    private void upsertModelCrudForm(String entityTypeCode, Long modelId, String formJson, Long version) {
        ModelCrudFormDefinitionDO existing = modelCrudFormDefinitionMapper
                .selectByEntityTypeAndModel(entityTypeCode, modelId);
        ModelCrudFormDefinitionDO data = existing == null ? new ModelCrudFormDefinitionDO() : existing;
        data.setEntityTypeCode(entityTypeCode);
        data.setModelId(modelId);
        data.setCrudFormFields(formJson);
        data.setVersion(version);
        if (existing == null) {
            fillInsertAuditFields(data);
            modelCrudFormDefinitionMapper.insert(data);
        } else {
            modelCrudFormDefinitionMapper.updateById(data);
        }
    }

    /** 无登录上下文时 MyBatis 自动填充不会写入 creator，插入前显式补齐审计字段。 */
    private void fillInsertAuditFields(BaseDO data) {
        LocalDateTime now = LocalDateTime.now();
        if (data.getCreateTime() == null) {
            data.setCreateTime(now);
        }
        if (data.getUpdateTime() == null) {
            data.setUpdateTime(now);
        }
        if (!StringUtils.hasText(data.getCreator())) {
            Long userId = SecurityFrameworkUtils.getLoginUserId();
            data.setCreator(userId != null ? userId.toString() : "0");
        }
        if (!StringUtils.hasText(data.getUpdater())) {
            Long userId = SecurityFrameworkUtils.getLoginUserId();
            data.setUpdater(userId != null ? userId.toString() : "0");
        }
    }

    /** 仅刷新单个模型的 CRUD 表单定义（避免全量能力重建）。 */
    private void refreshSingleModelCrudForm(String registryEntityTypeCode, Long modelId) {
        String registryCode = requireEntityTypeCode(registryEntityTypeCode);
        ModelCrudFormResolveContext context = resolveModelCrudFormContext(registryCode, modelId);
        if (context == null) {
            log.warn("[refreshSingleModelCrudForm][model 与入口不匹配，跳过][registryEntityTypeCode={}][modelId={}]",
                    registryCode, modelId);
            return;
        }
        BusinessCapabilityDO existing = businessCapabilityMapper.selectByEntityTypeCode(registryCode);
        long version = existing != null && existing.getVersion() != null ? existing.getVersion() : 1L;
        String formJson = buildModelCrudFormJson(modelId, context.formFieldEntityTypeCode());
        upsertModelCrudForm(registryCode, modelId, formJson, version);
    }

    /**
     * 模型字段变更时，同步刷新指向同一 storage + domain 的 DOMAIN 入口表单定义。
     */
    private void refreshDomainRegistryCrudFormsForModel(ModelDO model) {
        if (model == null || !StringUtils.hasText(model.getEntityTypeCode())) {
            return;
        }
        List<EntityTypeDO> domainEntries = entityTypeMapper.selectList(new LambdaQueryWrapperX<EntityTypeDO>()
                .eq(EntityTypeDO::getEntryKind, EntityTypeDO.ENTRY_KIND_DOMAIN)
                .eq(EntityTypeDO::getBaseEntityTypeCode, model.getEntityTypeCode().trim())
                .eq(EntityTypeDO::getDeleted, false));
        if (domainEntries == null || domainEntries.isEmpty()) {
            return;
        }
        for (EntityTypeDO domainEntry : domainEntries) {
            if (domainEntry == null || !StringUtils.hasText(domainEntry.getCode())) {
                continue;
            }
            if (!EntityTypeScopeContext.domainsEqual(domainEntry.getDomain(), model.getDomain())) {
                continue;
            }
            refreshSingleModelCrudForm(domainEntry.getCode().trim(), model.getId());
        }
    }

    /**
     * 能力重建时列出应生成 CRUD 表单的模型：NATIVE 按 registry；DOMAIN 按 storage + domain。
     */
    private List<ModelDO> listModelsForCrudFormRebuild(String registryEntityTypeCode) {
        EntityTypeScopeContext scope = loadEntityTypeScope(registryEntityTypeCode);
        if (scope != null && scope.isDomainEntry()) {
            return modelMapper.selectByEntityTypeCode(scope.getStorageEntityTypeCode()).stream()
                    .filter(model -> EntityTypeScopeContext.domainsEqual(model.getDomain(), scope.getDomain()))
                    .toList();
        }
        return modelMapper.selectByEntityTypeCode(registryEntityTypeCode);
    }

    private EntityTypeScopeContext loadEntityTypeScope(String registryEntityTypeCode) {
        EntityTypeDO entityType = entityTypeMapper.selectByCode(registryEntityTypeCode);
        return EntityTypeScopeContext.from(entityType);
    }

    private String resolveFormFieldEntityTypeCode(String registryEntityTypeCode, ModelDO model) {
        EntityTypeScopeContext scope = loadEntityTypeScope(registryEntityTypeCode);
        if (scope != null && scope.isDomainEntry()) {
            return scope.getStorageEntityTypeCode();
        }
        return model.getEntityTypeCode();
    }

    /**
     * 校验 registry 入口与 modelId 是否匹配，并返回表单字段应使用的 storage entityTypeCode。
     */
    private ModelCrudFormResolveContext resolveModelCrudFormContext(String registryEntityTypeCode, Long modelId) {
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null || !StringUtils.hasText(model.getEntityTypeCode())) {
            return null;
        }
        EntityTypeScopeContext scope = loadEntityTypeScope(registryEntityTypeCode);
        if (scope != null && scope.isDomainEntry()) {
            if (!scope.getStorageEntityTypeCode().equals(model.getEntityTypeCode())) {
                return null;
            }
            if (!EntityTypeScopeContext.domainsEqual(scope.getDomain(), model.getDomain())) {
                return null;
            }
            return new ModelCrudFormResolveContext(registryEntityTypeCode, scope.getStorageEntityTypeCode());
        }
        if (!registryEntityTypeCode.equals(model.getEntityTypeCode())) {
            return null;
        }
        return new ModelCrudFormResolveContext(registryEntityTypeCode, registryEntityTypeCode);
    }

    private record ModelCrudFormResolveContext(String registryEntityTypeCode, String formFieldEntityTypeCode) {
    }

    private String requireEntityTypeCode(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        return entityTypeCode.trim();
    }

    private String requireComponentCode(String componentCode) {
        if (!StringUtils.hasText(componentCode)) {
            throw new ServiceException(400, "componentCode 不能为空");
        }
        String value = componentCode.trim();
        if (!SUPPORTED_COMPONENT_CODES.contains(value)) {
            throw new ServiceException(400, "不支持的 componentCode：" + value);
        }
        return value;
    }

    private String requireDataKind(String dataKind, String entityTypeCode) {
        String kind = StringUtils.hasText(dataKind)
                ? dataKind.trim()
                : BusinessCategoryConstants.KIND_ENTITY;
        if (!BusinessCategoryConstants.KIND_MODEL.equals(kind)
                && !BusinessCategoryConstants.KIND_ENTITY.equals(kind)) {
            throw new ServiceException(400, "dataKind 须为 model 或 entity");
        }
        if (SystemCapabilityCatalog.isSystemCapability(entityTypeCode)) {
            return BusinessCategoryConstants.KIND_ENTITY;
        }
        return kind;
    }

    /**
     * 旧版 POC 投影使用 read/write 二分结构；定稿后应为 getList/getTree 等能力块。
     */
    private boolean isLegacyProjectionFormat(String componentInterface) {
        if (!StringUtils.hasText(componentInterface)) {
            return false;
        }
        String json = componentInterface.trim();
        return json.contains("\"read\"") && !json.contains("\"getList\"");
    }

    /**
     * dynamic model/entity 树投影误用分类树读 URL 或缺少读端点（历史 POC 数据）。
     * 分类域树（system/category）不在此判定，其 category/tree 为正确端点。
     */
    private boolean isBrokenDynamicTreeProjection(String componentInterface, String componentCode, String dataKind) {
        if (!"tree".equals(componentCode)) {
            return false;
        }
        if (!BusinessCategoryConstants.KIND_MODEL.equals(dataKind)
                && !BusinessCategoryConstants.KIND_ENTITY.equals(dataKind)) {
            return false;
        }
        if (!StringUtils.hasText(componentInterface)) {
            return true;
        }
        String json = componentInterface.trim();
        if (json.contains("/dynamicbusiness/category/tree") || json.contains("/system/category/tree")) {
            return true;
        }
        if (BusinessCategoryConstants.KIND_ENTITY.equals(dataKind) && !json.contains("query-by-scene")) {
            return true;
        }
        if (BusinessCategoryConstants.KIND_MODEL.equals(dataKind) && !json.contains("page-models")) {
            return true;
        }
        return false;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            log.error("[toJson][能力 JSON 序列化失败]", ex);
            throw new ServiceException(500, "能力 JSON 序列化失败");
        }
    }

    /**
     * 解析能力全集 JSON 为结构体。
     */
    private BusinessCapabilityFullContract parseCapabilityFull(String rawJson, String entityTypeCode) {
        try {
            return objectMapper.readValue(rawJson, BusinessCapabilityFullContract.class);
        } catch (JsonProcessingException ex) {
            log.error("[parseCapabilityFull][能力 JSON 反序列化失败][entityTypeCode={}]", entityTypeCode, ex);
            throw new ServiceException(500, "能力全集结构解析失败");
        }
    }
}
