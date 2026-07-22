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
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeContext;
import cn.cheers.x.module.dynamicbusiness.framework.field.EntityTypeFieldLabelHelper;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

        BusinessCapabilityDO existing = businessCapabilityMapper.selectByEntityTypeCode(code);
        long newVersion = existing == null || existing.getVersion() == null ? 1L : existing.getVersion() + 1L;

        String fullJson = buildCapabilityFullJson(code);
        upsertCapabilityFull(code, BusinessCategoryConstants.DYNAMIC, fullJson, newVersion, existing);

        for (String componentCode : SUPPORTED_COMPONENT_CODES) {
            String entityProjectionJson = buildEntityProjectionJson(code, componentCode, newVersion);
            upsertProjection(code, componentCode, BusinessCategoryConstants.KIND_ENTITY, entityProjectionJson, newVersion);
            String modelProjectionJson = buildModelProjectionJson(code, componentCode, newVersion);
            upsertProjection(code, componentCode, BusinessCategoryConstants.KIND_MODEL, modelProjectionJson, newVersion);
        }

        List<ModelDO> models = listModelsForCrudFormRebuild(code);
        for (ModelDO model : models) {
            String formFieldEntityTypeCode = resolveFormFieldEntityTypeCode(code, model);
            String formJson = buildModelCrudFormJson(model.getId(), formFieldEntityTypeCode);
            upsertModelCrudForm(code, model.getId(), formJson, newVersion);
        }
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
        refreshScopedRegistryCrudFormsForModel(model);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshAfterEntityTypeFieldDefinitionChanged(String entityTypeCode) {
        rebuildByEntityTypeCode(requireEntityTypeCode(entityTypeCode));
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
        List<Map<String, Object>> displayFields = buildDisplayFields(entityTypeCode, componentCode);
        List<Map<String, Object>> filterFields = buildFilterFields(entityTypeCode);
        List<String> searchableFieldKeys = collectFieldKeys(entityTypeCode, "searchable");
        List<String> sortableFieldKeys = collectFieldKeys(entityTypeCode, "sortable");
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
        LinkedHashMap<String, Map<String, Object>> byFieldKey = collectFieldMeta(entityTypeCode);
        List<String> keys = new ArrayList<>();
        for (Map<String, Object> meta : byFieldKey.values()) {
            if (Boolean.TRUE.equals(meta.get(flagKey))) {
                keys.add(String.valueOf(meta.get("fieldKey")));
            }
        }
        return keys;
    }

    @SuppressWarnings("unused")
    private String resolveReadEndpoint(String componentCode) {
        return "/dynamicbusiness/business/entities/query-by-scene";
    }

    private List<Map<String, Object>> buildDisplayFields(String entityTypeCode, String componentCode) {
        LinkedHashMap<String, Map<String, Object>> byFieldKey = collectFieldMeta(entityTypeCode);
        mergeBusinessBaseFieldMeta(entityTypeCode, byFieldKey);
        ensureBuiltinDisplayField(byFieldKey, "id", "ID", 0);
        ensureBuiltinDisplayField(byFieldKey, "name", "名称", 1);
        ensureBuiltinDisplayField(byFieldKey, "code", "编码", 2);

        List<Map<String, Object>> displayFields = new ArrayList<>();
        int order = 0;
        for (Map<String, Object> meta : byFieldKey.values()) {
            String fieldKey = String.valueOf(meta.get("fieldKey"));
            if (!isBaseDisplayField(meta, fieldKey)) {
                continue;
            }
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", fieldKey);
            item.put("fieldKey", fieldKey);
            item.put("label", meta.get("label"));
            item.put("renderAs", mapDisplayRenderAs(String.valueOf(meta.get("fieldType"))));
            item.put("sortOrder", meta.get("sortOrder") != null ? meta.get("sortOrder") : order++);
            item.put("defaultVisible", isBaseDisplayField(meta, fieldKey));
            item.put("baseField", isBaseDisplayField(meta, fieldKey));
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
        LinkedHashMap<String, Map<String, Object>> byFieldKey = collectFieldMeta(entityTypeCode);
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
        for (ModelDO model : models) {
            List<ModelFieldAssignmentDO> assigns = modelFieldAssignmentMapper.selectByModelId(model.getId());
            for (ModelFieldAssignmentDO assign : assigns) {
                FieldDO field = fieldMapper.selectById(assign.getFieldId());
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
                    byFieldKey.put(fieldKey, meta);
                    continue;
                }
                existing.put("filterable", Boolean.TRUE.equals(existing.get("filterable"))
                        || Boolean.TRUE.equals(assign.getIsFilterable()));
                existing.put("searchable", Boolean.TRUE.equals(existing.get("searchable"))
                        || Boolean.TRUE.equals(assign.getIsSearchable()));
                existing.put("sortable", Boolean.TRUE.equals(existing.get("sortable"))
                        || Boolean.TRUE.equals(assign.getIsSortable()));
            }
            applyModelGroupMeta(model.getId(), byFieldKey);
        }
        return byFieldKey;
    }

    private void applyModelGroupMeta(Long modelId, LinkedHashMap<String, Map<String, Object>> byFieldKey) {
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
                FieldDO field = fieldMapper.selectById(ref.getFieldId());
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
            if (!meta.containsKey("groupName")) {
                meta.put("groupName", "基础信息");
                meta.put("groupSortOrder", 0);
            }
        }
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
            meta.put("sortable", false);
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

    private CrudFormFieldContext loadCrudFormFieldContext(Long modelId, String entityTypeCode) {
        List<ModelFieldAssignmentDO> assigns = modelFieldAssignmentMapper.selectByModelId(modelId);
        Map<Long, FieldDO> fieldById = new HashMap<>(Math.max(assigns.size(), 1));
        for (ModelFieldAssignmentDO assign : assigns) {
            FieldDO field = fieldMapper.selectById(assign.getFieldId());
            if (field != null) {
                fieldById.put(field.getId(), field);
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
                    // 基础字段库关联：即使尚未走分配行，也要带上 FieldDO.providerCode 供 REF 目标解析
                    if (baseField != null
                            && baseField.getLibraryFieldId() != null
                            && !fieldById.containsKey(baseField.getLibraryFieldId())) {
                        FieldDO libraryField = fieldMapper.selectById(baseField.getLibraryFieldId());
                        if (libraryField != null) {
                            fieldById.put(libraryField.getId(), libraryField);
                        }
                    }
                }
            }
        }
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
        for (Long id : refLibraryIds) {
            RelationFieldLibraryDO lib = relationFieldLibraryMapper.selectById(id);
            if (lib != null) {
                refLibraryById.put(id, lib);
            }
        }
        Map<Long, ModelRelationDO> modelRelationById = new HashMap<>();
        Map<String, String> modelCodeToEntityTypeCode = new HashMap<>();
        for (Long id : modelRelationIds) {
            ModelRelationDO rel = modelRelationMapper.selectById(id);
            if (rel == null) {
                continue;
            }
            modelRelationById.put(id, rel);
            if (!StringUtils.hasText(rel.getTargetModelCode())) {
                continue;
            }
            String modelCode = rel.getTargetModelCode().trim();
            if (modelCodeToEntityTypeCode.containsKey(modelCode)) {
                continue;
            }
            ModelDO targetModel = modelMapper.selectByCode(modelCode);
            if (targetModel != null && StringUtils.hasText(targetModel.getEntityTypeCode())) {
                modelCodeToEntityTypeCode.put(modelCode, targetModel.getEntityTypeCode().trim());
            }
        }
        return new ModelCrudFormFieldAssembler.RefResolveContext(
                refLibraryById, modelRelationById, modelCodeToEntityTypeCode);
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
     * 模型字段变更时，同步刷新指向同一 storage + dataScope 的 SCOPED 入口表单定义。
     */
    private void refreshScopedRegistryCrudFormsForModel(ModelDO model) {
        if (model == null || !StringUtils.hasText(model.getEntityTypeCode())) {
            return;
        }
        List<EntityTypeDO> scopedEntries = entityTypeMapper.selectList(new LambdaQueryWrapperX<EntityTypeDO>()
                .eq(EntityTypeDO::getEntryKind, EntityTypeDO.ENTRY_KIND_SCOPED)
                .eq(EntityTypeDO::getBaseEntityTypeCode, model.getEntityTypeCode().trim())
                .eq(EntityTypeDO::getDeleted, false));
        if (scopedEntries == null || scopedEntries.isEmpty()) {
            return;
        }
        for (EntityTypeDO scopedEntry : scopedEntries) {
            if (scopedEntry == null || !StringUtils.hasText(scopedEntry.getCode())) {
                continue;
            }
            if (!EntityTypeScopeContext.scopesEqual(scopedEntry.getDataScope(), model.getDataScope())) {
                continue;
            }
            refreshSingleModelCrudForm(scopedEntry.getCode().trim(), model.getId());
        }
    }

    /**
     * 能力重建时列出应生成 CRUD 表单的模型：NATIVE 按 registry；SCOPED 按 storage + dataScope。
     */
    private List<ModelDO> listModelsForCrudFormRebuild(String registryEntityTypeCode) {
        EntityTypeScopeContext scope = loadEntityTypeScope(registryEntityTypeCode);
        if (scope != null && scope.isScoped()) {
            return modelMapper.selectByEntityTypeCode(scope.getStorageEntityTypeCode()).stream()
                    .filter(model -> EntityTypeScopeContext.scopesEqual(model.getDataScope(), scope.getDataScope()))
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
        if (scope != null && scope.isScoped()) {
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
        if (scope != null && scope.isScoped()) {
            if (!scope.getStorageEntityTypeCode().equals(model.getEntityTypeCode())) {
                return null;
            }
            if (!EntityTypeScopeContext.scopesEqual(scope.getDataScope(), model.getDataScope())) {
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
