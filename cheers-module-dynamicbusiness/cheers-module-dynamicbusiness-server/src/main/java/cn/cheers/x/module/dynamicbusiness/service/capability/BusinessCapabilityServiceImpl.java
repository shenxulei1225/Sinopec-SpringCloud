package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.BusinessTypeSimpleVO;
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
import cn.cheers.x.module.dynamicbusiness.dal.mysql.capability.BusinessCapabilityMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.capability.CapabilityComponentProjectionMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.capability.ModelCrudFormDefinitionMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.BusinessTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.businesstype.BusinessTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.service.businesstype.BusinessTypeService;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldGroupRespVO;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelFieldGroupService;
import cn.cheers.x.module.dynamicbusiness.service.capability.projection.CapabilityBlockProjectionBuilder;
import cn.cheers.x.module.dynamicbusiness.service.capability.system.SystemCapabilityCatalog;
import cn.cheers.x.module.dynamicbusiness.service.capability.system.SystemCapabilityDefinition;
import cn.cheers.x.module.dynamicbusiness.service.capability.system.SystemCapabilityProjectionBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
 *   <li>读路径严格按 businessTypeCode / componentCode / modelId 三组主键访问；</li>
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
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private BusinessTypeService businessTypeService;
    @Resource
    private ModelFieldGroupService modelFieldGroupService;
    @Resource
    private BusinessTypeBaseFieldMapper businessTypeBaseFieldMapper;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public List<BusinessCapabilitySummaryRespVO> listCapabilitySummaries(String businessCategory) {
        if (StringUtils.hasText(businessCategory)) {
            BusinessCategoryConstants.requireKnownCategory(businessCategory.trim());
        }

        Map<String, String> dynamicNameMap = businessTypeService.listSimple().stream()
                .collect(Collectors.toMap(BusinessTypeSimpleVO::getCode, BusinessTypeSimpleVO::getName, (a, b) -> a));

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
        vo.setBusinessTypeCode(item.getBusinessTypeCode());
        vo.setVersion(item.getVersion());
        vo.setSupportedDataKinds(resolveSupportedDataKinds(category));

        if (BusinessCategoryConstants.isSystem(category)) {
            vo.setBusinessTypeName(SystemCapabilityCatalog.find(item.getBusinessTypeCode())
                    .map(SystemCapabilityDefinition::getBusinessTypeName)
                    .orElse(item.getBusinessTypeCode()));
        } else {
            vo.setBusinessTypeName(dynamicNameMap.getOrDefault(item.getBusinessTypeCode(), item.getBusinessTypeCode()));
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
    public BusinessCapabilityFullRespVO getCapabilityFull(String businessTypeCode) {
        String code = requireBusinessTypeCode(businessTypeCode);
        BusinessCapabilityDO data = businessCapabilityMapper.selectByBusinessTypeCode(code);
        if (data == null) {
            throw new ServiceException(404, "未找到能力全集，businessTypeCode=" + code);
        }
        BusinessCapabilityFullRespVO vo = new BusinessCapabilityFullRespVO();
        vo.setBusinessTypeCode(data.getBusinessTypeCode());
        vo.setCapabilityFull(parseCapabilityFull(data.getCapabilityFull(), code));
        vo.setVersion(data.getVersion());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CapabilityComponentProjectionRespVO getProjection(
            String businessTypeCode, String componentCode, String dataKind) {
        String code = requireBusinessTypeCode(businessTypeCode);
        String comp = requireComponentCode(componentCode);
        String kind = requireDataKind(dataKind, code);
        CapabilityComponentProjectionDO data = capabilityComponentProjectionMapper
                .selectByBusinessTypeComponentAndDataKind(code, comp, kind);
        if (data == null) {
            log.info("[getProjection][投影缺失，触发重建][businessTypeCode={}][componentCode={}][dataKind={}]",
                    code, comp, kind);
            triggerRebuild(code);
            data = capabilityComponentProjectionMapper.selectByBusinessTypeComponentAndDataKind(code, comp, kind);
        } else if (isLegacyProjectionFormat(data.getComponentInterface())) {
            log.info("[getProjection][检测到旧版 read/write 投影，触发重建][businessTypeCode={}][componentCode={}][dataKind={}]",
                    code, comp, kind);
            triggerRebuild(code);
            data = capabilityComponentProjectionMapper.selectByBusinessTypeComponentAndDataKind(code, comp, kind);
        }
        if (data == null) {
            throw new ServiceException(404,
                    "未找到能力投影，businessTypeCode=" + code + ", componentCode=" + comp + ", dataKind=" + kind);
        }
        CapabilityComponentProjectionRespVO vo = new CapabilityComponentProjectionRespVO();
        vo.setBusinessTypeCode(data.getBusinessTypeCode());
        vo.setComponentCode(data.getComponentCode());
        vo.setDataKind(data.getDataKind());
        vo.setComponentInterface(data.getComponentInterface());
        vo.setVersion(data.getVersion());
        return vo;
    }

    @Override
    public ModelCrudFormDefinitionRespVO getModelCrudFormDefinition(String businessTypeCode, Long modelId) {
        String code = requireBusinessTypeCode(businessTypeCode);
        if (modelId == null) {
            throw new ServiceException(400, "modelId 不能为空");
        }
        ModelCrudFormDefinitionDO data = modelCrudFormDefinitionMapper.selectByBusinessTypeAndModel(code, modelId);
        if (data == null) {
            throw new ServiceException(404, "未找到模型 CRUD 表单定义，businessTypeCode=" + code + ", modelId=" + modelId);
        }
        ModelCrudFormDefinitionRespVO vo = new ModelCrudFormDefinitionRespVO();
        vo.setBusinessTypeCode(data.getBusinessTypeCode());
        vo.setModelId(data.getModelId());
        vo.setCrudFormFields(data.getCrudFormFields());
        vo.setVersion(data.getVersion());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebuildByBusinessTypeCode(String businessTypeCode) {
        String code = requireBusinessTypeCode(businessTypeCode);
        if (SystemCapabilityCatalog.isSystemCapability(code)) {
            rebuildSystemCapability(code);
            return;
        }

        BusinessCapabilityDO existing = businessCapabilityMapper.selectByBusinessTypeCode(code);
        long newVersion = existing == null || existing.getVersion() == null ? 1L : existing.getVersion() + 1L;

        String fullJson = buildCapabilityFullJson(code);
        upsertCapabilityFull(code, BusinessCategoryConstants.DYNAMIC, fullJson, newVersion, existing);

        for (String componentCode : SUPPORTED_COMPONENT_CODES) {
            String entityProjectionJson = buildEntityProjectionJson(code, componentCode, newVersion);
            upsertProjection(code, componentCode, BusinessCategoryConstants.KIND_ENTITY, entityProjectionJson, newVersion);
            String modelProjectionJson = buildModelProjectionJson(code, componentCode, newVersion);
            upsertProjection(code, componentCode, BusinessCategoryConstants.KIND_MODEL, modelProjectionJson, newVersion);
        }

        List<ModelDO> models = modelMapper.selectByBusinessTypeCode(code);
        for (ModelDO model : models) {
            String formJson = buildModelCrudFormJson(model.getId(), code);
            upsertModelCrudForm(code, model.getId(), formJson, newVersion);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebuildAllSystemCapabilities() {
        for (SystemCapabilityDefinition definition : SystemCapabilityCatalog.all()) {
            rebuildSystemCapability(definition.getBusinessTypeCode());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebuildSystemCapability(String businessTypeCode) {
        String code = requireBusinessTypeCode(businessTypeCode);
        SystemCapabilityDefinition definition = SystemCapabilityCatalog.find(code)
                .orElseThrow(() -> new ServiceException(404, "未找到系统业务能力定义，businessTypeCode=" + code));

        BusinessCapabilityDO existing = businessCapabilityMapper.selectByBusinessTypeCode(code);
        long newVersion = existing == null || existing.getVersion() == null ? 1L : existing.getVersion() + 1L;

        String fullJson = SystemCapabilityProjectionBuilder.buildCapabilityFullJson(definition, newVersion, objectMapper);
        upsertCapabilityFull(code, BusinessCategoryConstants.SYSTEM, fullJson, newVersion, existing);

        for (String componentCode : SUPPORTED_COMPONENT_CODES) {
            String projectionJson = SystemCapabilityProjectionBuilder.buildProjectionJson(
                    definition, componentCode, newVersion, objectMapper);
            upsertProjection(code, componentCode, BusinessCategoryConstants.KIND_ENTITY, projectionJson, newVersion);
        }
    }

    private void triggerRebuild(String businessTypeCode) {
        if (SystemCapabilityCatalog.isSystemCapability(businessTypeCode)) {
            rebuildSystemCapability(businessTypeCode);
            return;
        }
        rebuildByBusinessTypeCode(businessTypeCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebuildByModelId(Long modelId) {
        if (modelId == null) {
            throw new ServiceException(400, "modelId 不能为空");
        }
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null || !StringUtils.hasText(model.getBusinessTypeCode())) {
            throw new ServiceException(404, "未找到模型或模型缺少 businessTypeCode，modelId=" + modelId);
        }
        rebuildByBusinessTypeCode(model.getBusinessTypeCode());
    }

    /**
     * 构建能力全集 JSON。
     */
    private String buildCapabilityFullJson(String businessTypeCode) {
        List<ModelDO> models = modelMapper.selectByBusinessTypeCode(businessTypeCode);
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("businessTypeCode", businessTypeCode);
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
    private String buildEntityProjectionJson(String businessTypeCode, String componentCode, Long version) {
        List<Map<String, Object>> displayFields = buildDisplayFields(businessTypeCode, componentCode);
        List<Map<String, Object>> filterFields = buildFilterFields(businessTypeCode);
        List<String> searchableFieldKeys = collectFieldKeys(businessTypeCode, "searchable");
        List<String> sortableFieldKeys = collectFieldKeys(businessTypeCode, "sortable");
        Map<String, Object> projection = CapabilityBlockProjectionBuilder.buildDynamicEntity(
                businessTypeCode,
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
    private String buildModelProjectionJson(String businessTypeCode, String componentCode, Long version) {
        List<Map<String, Object>> displayFields = buildModelDisplayFields(componentCode);
        List<Map<String, Object>> filterFields = buildModelFilterFields();
        List<String> searchableFieldKeys = List.of("name", "code");
        List<String> sortableFieldKeys = List.of("name", "code", "status");
        Map<String, Object> projection = CapabilityBlockProjectionBuilder.buildDynamicModel(
                businessTypeCode,
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
        statusFilter.put("control", "select");
        statusFilter.put("sortOrder", 0);
        statusFilter.put("bindTo", "field-filter");
        statusFilter.put("searchable", false);
        statusFilter.put("sortable", true);
        statusFilter.put("defaultVisible", true);
        return List.of(statusFilter);
    }

    private List<String> collectFieldKeys(String businessTypeCode, String flagKey) {
        LinkedHashMap<String, Map<String, Object>> byFieldKey = collectFieldMeta(businessTypeCode);
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

    private List<Map<String, Object>> buildDisplayFields(String businessTypeCode, String componentCode) {
        LinkedHashMap<String, Map<String, Object>> byFieldKey = collectFieldMeta(businessTypeCode);
        mergeBusinessBaseFieldMeta(businessTypeCode, byFieldKey);
        ensureBuiltinDisplayField(byFieldKey, "id", "ID", 0);
        ensureBuiltinDisplayField(byFieldKey, "name", "名称", 1);
        ensureBuiltinDisplayField(byFieldKey, "code", "编码", 2);

        List<Map<String, Object>> displayFields = new ArrayList<>();
        int order = 0;
        for (Map<String, Object> meta : byFieldKey.values()) {
            String fieldKey = String.valueOf(meta.get("fieldKey"));
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", fieldKey);
            item.put("fieldKey", fieldKey);
            item.put("label", meta.get("label"));
            item.put("renderAs", mapDisplayRenderAs(String.valueOf(meta.get("fieldType"))));
            item.put("sortOrder", meta.get("sortOrder") != null ? meta.get("sortOrder") : order++);
            item.put("defaultVisible", isBaseDisplayField(meta, fieldKey));
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

    private List<Map<String, Object>> buildFilterFields(String businessTypeCode) {
        LinkedHashMap<String, Map<String, Object>> byFieldKey = collectFieldMeta(businessTypeCode);
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
            filter.put("control", mapFilterControl(String.valueOf(meta.get("fieldType"))));
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

    private LinkedHashMap<String, Map<String, Object>> collectFieldMeta(String businessTypeCode) {
        LinkedHashMap<String, Map<String, Object>> byFieldKey = new LinkedHashMap<>();
        List<ModelDO> models = modelMapper.selectByBusinessTypeCode(businessTypeCode);
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

    private void mergeBusinessBaseFieldMeta(String businessTypeCode, LinkedHashMap<String, Map<String, Object>> byFieldKey) {
        List<BusinessTypeBaseFieldDO> baseFields = businessTypeBaseFieldMapper.selectByBusinessTypeCode(businessTypeCode);
        if (baseFields == null || baseFields.isEmpty()) {
            return;
        }
        int order = 0;
        for (BusinessTypeBaseFieldDO baseField : baseFields) {
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
    private String buildModelCrudFormJson(Long modelId, String businessTypeCode) {
        List<ModelFieldAssignmentDO> assigns = modelFieldAssignmentMapper.selectByModelId(modelId);
        List<Map<String, Object>> fields = new ArrayList<>(assigns.size());
        for (ModelFieldAssignmentDO assign : assigns) {
            FieldDO field = fieldMapper.selectById(assign.getFieldId());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("fieldId", assign.getFieldId());
            item.put("fieldCode", field != null ? field.getCode() : null);
            item.put("fieldName", field != null ? field.getName() : null);
            item.put("fieldType", field != null ? field.getType() : null);
            item.put("required", assign.getRequired());
            item.put("filterable", assign.getIsFilterable());
            item.put("searchable", assign.getIsSearchable());
            item.put("sortable", assign.getIsSortable());
            item.put("sort", assign.getSort());
            fields.add(item);
        }
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("businessTypeCode", businessTypeCode);
        root.put("modelId", modelId);
        root.put("fields", fields);
        return toJson(root);
    }

    /**
     * 能力全集 UPSERT。
     */
    private void upsertCapabilityFull(
            String businessTypeCode,
            String businessCategory,
            String fullJson,
            Long version,
            BusinessCapabilityDO existing) {
        BusinessCapabilityDO data = existing == null ? new BusinessCapabilityDO() : existing;
        data.setBusinessTypeCode(businessTypeCode);
        data.setBusinessCategory(businessCategory);
        data.setCapabilityFull(fullJson);
        data.setVersion(version);
        if (existing == null) {
            businessCapabilityMapper.insert(data);
        } else {
            businessCapabilityMapper.updateById(data);
        }
    }

    /**
     * 投影 UPSERT。
     */
    private void upsertProjection(
            String businessTypeCode,
            String componentCode,
            String dataKind,
            String projectionJson,
            Long version) {
        CapabilityComponentProjectionDO existing = capabilityComponentProjectionMapper
                .selectByBusinessTypeComponentAndDataKind(businessTypeCode, componentCode, dataKind);
        CapabilityComponentProjectionDO data = existing == null ? new CapabilityComponentProjectionDO() : existing;
        data.setBusinessTypeCode(businessTypeCode);
        data.setComponentCode(componentCode);
        data.setDataKind(dataKind);
        data.setComponentInterface(projectionJson);
        data.setVersion(version);
        if (existing == null) {
            capabilityComponentProjectionMapper.insert(data);
        } else {
            capabilityComponentProjectionMapper.updateById(data);
        }
    }

    /**
     * 模型 CRUD 表单定义 UPSERT。
     */
    private void upsertModelCrudForm(String businessTypeCode, Long modelId, String formJson, Long version) {
        ModelCrudFormDefinitionDO existing = modelCrudFormDefinitionMapper
                .selectByBusinessTypeAndModel(businessTypeCode, modelId);
        ModelCrudFormDefinitionDO data = existing == null ? new ModelCrudFormDefinitionDO() : existing;
        data.setBusinessTypeCode(businessTypeCode);
        data.setModelId(modelId);
        data.setCrudFormFields(formJson);
        data.setVersion(version);
        if (existing == null) {
            modelCrudFormDefinitionMapper.insert(data);
        } else {
            modelCrudFormDefinitionMapper.updateById(data);
        }
    }

    private String requireBusinessTypeCode(String businessTypeCode) {
        if (!StringUtils.hasText(businessTypeCode)) {
            throw new ServiceException(400, "businessTypeCode 不能为空");
        }
        return businessTypeCode.trim();
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

    private String requireDataKind(String dataKind, String businessTypeCode) {
        String kind = StringUtils.hasText(dataKind)
                ? dataKind.trim()
                : BusinessCategoryConstants.KIND_ENTITY;
        if (!BusinessCategoryConstants.KIND_MODEL.equals(kind)
                && !BusinessCategoryConstants.KIND_ENTITY.equals(kind)) {
            throw new ServiceException(400, "dataKind 须为 model 或 entity");
        }
        if (SystemCapabilityCatalog.isSystemCapability(businessTypeCode)) {
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
    private BusinessCapabilityFullContract parseCapabilityFull(String rawJson, String businessTypeCode) {
        try {
            return objectMapper.readValue(rawJson, BusinessCapabilityFullContract.class);
        } catch (JsonProcessingException ex) {
            log.error("[parseCapabilityFull][能力 JSON 反序列化失败][businessTypeCode={}]", businessTypeCode, ex);
            throw new ServiceException(500, "能力全集结构解析失败");
        }
    }
}
