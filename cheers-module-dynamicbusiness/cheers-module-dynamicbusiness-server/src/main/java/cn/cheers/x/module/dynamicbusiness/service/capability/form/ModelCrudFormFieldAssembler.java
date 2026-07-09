package cn.cheers.x.module.dynamicbusiness.service.capability.form;

import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldGroupRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation.RelationFieldLibraryDO;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.cheers.x.module.dynamicbusiness.service.capability.BusinessCategoryConstants;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 组装模型 CRUD 表单字段 JSON（供 model_crud_form_definition.crudFormFields 持久化）。
 *
 * <p>字段范围：业务类型基础字段 + 模型扩展字段；排除系统自动生成、不可编辑的元数据字段。</p>
 * <p>输出结构与前端 {@code RequestBodyField} / {@code parseModelCrudFormPayload} 对齐。</p>
 */
public final class ModelCrudFormFieldAssembler {

    private static final String ASYNC_CHECK_NAME_UNIQUE = "check-entity-name-unique";

    /** 实体/模型元数据：CRUD 表单不展示（由上下文或后端写入）。 */
    private static final Set<String> AUTO_GENERATED_FIELD_CODES = Set.of(
            "id",
            "tenantid", "tenant_id",
            "creator", "createtime", "create_time",
            "updater", "updatetime", "update_time",
            "deleted",
            "entitytypecode", "entity_type_code",
            "businesstypecode", "business_type_code",
            "modelid", "model_id",
            "customfields", "custom_fields",
            "parentid", "parent_id",
            "treepath", "tree_path",
            "sort",
            "guid",
            "attrs");

    private ModelCrudFormFieldAssembler() {
    }

    /** 关联字段目标业务类型解析上下文（ref 库 / 模型关联 / 分配兜底）。 */
    public record RefResolveContext(
            Map<Long, RelationFieldLibraryDO> refLibraryById,
            Map<Long, ModelRelationDO> modelRelationById,
            Map<String, String> modelCodeToEntityTypeCode) {

        public static RefResolveContext empty() {
            return new RefResolveContext(Map.of(), Map.of(), Map.of());
        }
    }

    public static Map<String, Object> buildFormRoot(
            Long modelId,
            String entityTypeCode,
            boolean includeBaseFields,
            List<ModelFieldAssignmentDO> assigns,
            Map<Long, FieldDO> fieldById,
            Map<String, EntityTypeBaseFieldDO> baseFieldByCode,
            List<ModelFieldGroupRespVO> groups,
            RefResolveContext refResolveContext) {
        LinkedHashMap<String, Map<String, Object>> fieldItems = new LinkedHashMap<>();
        Set<String> addedCodes = new HashSet<>();

        putIfAbsent(fieldItems, addedCodes, buildBuiltinNameField());
        putIfAbsent(fieldItems, addedCodes, buildBuiltinStatusField());

        if (includeBaseFields && baseFieldByCode != null && !baseFieldByCode.isEmpty()) {
            List<EntityTypeBaseFieldDO> sortedBaseFields = new ArrayList<>(baseFieldByCode.values());
            sortedBaseFields.sort(Comparator.comparingInt(base -> base.getSortOrder() != null ? base.getSortOrder() : 0));
            for (EntityTypeBaseFieldDO baseField : sortedBaseFields) {
                if (baseField == null || !baseField.isEnabled() || !StringUtils.hasText(baseField.getFieldCode())) {
                    continue;
                }
                String code = baseField.getFieldCode().trim();
                if (shouldSkipField(code, baseField, null)) {
                    continue;
                }
                putIfAbsent(fieldItems, addedCodes, buildBaseFieldItem(entityTypeCode, baseField));
            }
        }

        List<ModelFieldAssignmentDO> sortedAssigns = new ArrayList<>(assigns != null ? assigns : List.of());
        sortedAssigns.sort(Comparator.comparingInt(a -> a.getSort() != null ? a.getSort() : 0));
        for (ModelFieldAssignmentDO assign : sortedAssigns) {
            FieldDO field = fieldById.get(assign.getFieldId());
            if (field == null || !StringUtils.hasText(field.getCode())) {
                continue;
            }
            String code = field.getCode().trim();
            if (addedCodes.contains(code) || shouldSkipField(code, baseFieldByCode.get(code), field)) {
                continue;
            }
            EntityTypeBaseFieldDO baseField = baseFieldByCode != null ? baseFieldByCode.get(code) : null;
            putIfAbsent(
                    fieldItems,
                    addedCodes,
                    buildAssignmentField(entityTypeCode, assign, field, baseField, groups, refResolveContext));
        }

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("entityTypeCode", entityTypeCode);
        root.put("modelId", modelId);
        List<Map<String, Object>> baseFieldDefs = new ArrayList<>();
        List<Map<String, Object>> customFieldDefs = new ArrayList<>();
        for (Map<String, Object> item : fieldItems.values()) {
            if (Boolean.TRUE.equals(item.get("baseField"))) {
                baseFieldDefs.add(item);
            } else {
                customFieldDefs.add(item);
            }
        }
        root.put("baseFieldDefs", baseFieldDefs);
        root.put("customFieldDefs", customFieldDefs);
        Map<String, Object> formLayout = buildFormLayout(groups, fieldById);
        if (formLayout != null) {
            root.put("formLayout", formLayout);
        }
        return root;
    }

    private static boolean shouldSkipField(String fieldCode, EntityTypeBaseFieldDO baseField, FieldDO field) {
        if (!StringUtils.hasText(fieldCode)) {
            return true;
        }
        String code = fieldCode.trim();
        if ("name".equals(code) || "status".equals(code)) {
            return true;
        }
        if (isAutoGeneratedFieldCode(code)) {
            return true;
        }
        if (isAutoGeneratedByConfig(baseField)) {
            return true;
        }
        return isAutoGeneratedExtensionField(field);
    }

    private static boolean isAutoGeneratedFieldCode(String fieldCode) {
        return AUTO_GENERATED_FIELD_CODES.contains(fieldCode.trim().toLowerCase());
    }

    private static boolean isAutoGeneratedByConfig(EntityTypeBaseFieldDO baseField) {
        if (baseField == null) {
            return false;
        }
        JSONObject config = parseJsonObject(baseField.getTypeConfig());
        if (config == null || config.isEmpty()) {
            return false;
        }
        return Boolean.TRUE.equals(config.getBoolean("autoGenerate"))
                || Boolean.TRUE.equals(config.getBoolean("autoGenerated"))
                || Boolean.TRUE.equals(config.getBoolean("readOnly"));
    }

    private static boolean isAutoGeneratedExtensionField(FieldDO field) {
        if (field == null || !StringUtils.hasText(field.getType())) {
            return false;
        }
        String code = field.getCode() != null ? field.getCode().trim() : "";
        if (code.startsWith("MODEL_") && code.endsWith("_id")) {
            return true;
        }
        if (!"SYSTEM".equals(field.getSource())) {
            return false;
        }
        String description = field.getDescription();
        return StringUtils.hasText(description)
                && description.contains("系统自动创建")
                && FieldTypeEnum.isMultiEntityRef(field.getType());
    }

    private static void putIfAbsent(
            LinkedHashMap<String, Map<String, Object>> fieldItems,
            Set<String> addedCodes,
            Map<String, Object> item) {
        Object key = item.get("fieldCode");
        if (key == null) {
            key = item.get("fieldKey");
        }
        if (key == null) {
            return;
        }
        String code = String.valueOf(key).trim();
        if (addedCodes.contains(code)) {
            return;
        }
        fieldItems.put(code, item);
        addedCodes.add(code);
    }

    private static Map<String, Object> buildBuiltinNameField() {
        Map<String, Object> item = baseFieldItem("name", "名称", "TEXT", "input");
        item.put("required", true);
        item.put("maxLength", 200);
        item.put("asyncCheckId", ASYNC_CHECK_NAME_UNIQUE);
        item.put("sort", -100);
        item.put("groupName", "基础信息");
        item.put("groupSortOrder", 0);
        item.put("baseField", true);
        return item;
    }

    private static Map<String, Object> buildBuiltinStatusField() {
        Map<String, Object> item = baseFieldItem("status", "状态", "NUMBER", "select");
        item.put("required", true);
        item.put("sort", -90);
        item.put("groupName", "基础信息");
        item.put("groupSortOrder", 0);
        item.put("baseField", true);
        item.put("optionsSource", Map.of(
                "kind", "static",
                "options", List.of(
                        Map.of("value", 1, "label", "启用"),
                        Map.of("value", 0, "label", "禁用"))));
        return item;
    }

    private static Map<String, Object> buildBaseFieldItem(String entityTypeCode, EntityTypeBaseFieldDO baseField) {
        String code = baseField.getFieldCode().trim();
        String fieldType = StringUtils.hasText(baseField.getDataType()) ? baseField.getDataType().trim().toUpperCase() : "TEXT";
        String label = StringUtils.hasText(baseField.getFieldName()) ? baseField.getFieldName() : code;
        Map<String, Object> item = baseFieldItem(code, label, fieldType, mapControl(fieldType));
        item.put("baseField", true);
        item.put("sort", baseField.getSortOrder() != null ? baseField.getSortOrder() : 0);
        item.put("groupName", "基础信息");
        item.put("groupSortOrder", 0);
        applyBaseFieldTypeConfig(item, baseField);
        applyBaseFieldTypeExtensions(item, entityTypeCode, baseField, fieldType);
        return item;
    }

    private static void applyBaseFieldTypeExtensions(
            Map<String, Object> item, String entityTypeCode, EntityTypeBaseFieldDO baseField, String fieldType) {
        if ("REF_MULTI".equals(fieldType) || FieldTypeEnum.isMultiEntityRef(fieldType)) {
            item.put("renderAs", "ref-picker-multi");
            item.put("valueShape", "array");
        }
        if (FieldTypeEnum.isEntityRef(fieldType) || "REFERENCE".equals(fieldType)) {
            item.put("renderAs", "ref-picker");
            Map<String, Object> binding = new LinkedHashMap<>();
            binding.put("businessCategory", BusinessCategoryConstants.DYNAMIC);
            binding.put("dataKind", BusinessCategoryConstants.KIND_ENTITY);
            binding.put("entityTypeCode", entityTypeCode);
            item.put("refTarget", Map.of(
                    "capabilityBinding", binding,
                    "valueField", "id",
                    "labelField", "name"));
        }
        if (codeStartsWithRel(baseField.getFieldCode())) {
            item.put("renderAs", "ref-picker-multi");
            item.put("valueShape", "array");
        }
    }

    private static boolean codeStartsWithRel(String fieldCode) {
        return StringUtils.hasText(fieldCode) && fieldCode.trim().startsWith("REL_");
    }

    private static Map<String, Object> buildAssignmentField(
            String entityTypeCode,
            ModelFieldAssignmentDO assign,
            FieldDO field,
            EntityTypeBaseFieldDO baseField,
            List<ModelFieldGroupRespVO> groups,
            RefResolveContext refResolveContext) {
        String code = field.getCode().trim();
        String fieldType = resolveFieldType(field, baseField);
        Map<String, Object> item = baseFieldItem(
                code,
                StringUtils.hasText(field.getName()) ? field.getName() : code,
                fieldType,
                mapControl(fieldType));

        item.put("fieldId", field.getId());
        item.put("required", Boolean.TRUE.equals(assign.getRequired()));
        item.put("sort", assign.getSort() != null ? assign.getSort() : 0);
        item.put("baseField", baseField != null);
        if (baseField == null) {
            item.put("fieldSource", "CUSTOM");
        }

        applyValidationRules(item, assign.getValidationRules());
        if (baseField != null) {
            applyBaseFieldTypeConfig(item, baseField);
        }
        applyFieldTypeExtensions(item, entityTypeCode, field, fieldType, assign, refResolveContext);

        applyGroupMeta(item, field.getId(), groups);
        return item;
    }

    private static Map<String, Object> baseFieldItem(
            String fieldCode, String label, String fieldType, String renderAs) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("fieldCode", fieldCode);
        item.put("fieldKey", fieldCode);
        item.put("fieldName", label);
        item.put("label", label);
        item.put("fieldType", fieldType);
        item.put("renderAs", renderAs);
        return item;
    }

    private static String resolveFieldType(FieldDO field, EntityTypeBaseFieldDO baseField) {
        if (baseField != null && StringUtils.hasText(baseField.getDataType())) {
            return baseField.getDataType().trim().toUpperCase();
        }
        return field.getType() != null ? field.getType().trim().toUpperCase() : "TEXT";
    }

    private static String mapControl(String fieldType) {
        if (!StringUtils.hasText(fieldType)) {
            return "input";
        }
        return switch (fieldType.trim().toUpperCase()) {
            case "BOOLEAN" -> "boolean";
            case "ENUM" -> "select";
            case "DATE", "DATETIME", "TIMESTAMP" -> "date";
            case "ENTITY_REF", "REFERENCE" -> "ref-picker";
            case "ENTITY_REF_MULTI", "REF_MULTI" -> "ref-picker-multi";
            default -> "input";
        };
    }

    private static void applyValidationRules(Map<String, Object> item, String validationRulesJson) {
        JSONObject rules = parseJsonObject(validationRulesJson);
        if (rules == null || rules.isEmpty()) {
            return;
        }
        copyNumberRule(item, rules, "maxLength");
        copyNumberRule(item, rules, "minLength");
        copyNumberRule(item, rules, "min");
        copyNumberRule(item, rules, "max");
        if (rules.containsKey("pattern")) {
            item.put("pattern", rules.getString("pattern"));
        }
    }

    private static void applyBaseFieldTypeConfig(Map<String, Object> item, EntityTypeBaseFieldDO baseField) {
        JSONObject config = parseJsonObject(baseField.getTypeConfig());
        if (config == null || config.isEmpty()) {
            return;
        }
        copyNumberRule(item, config, "maxLength");
        copyNumberRule(item, config, "minLength");
        copyNumberRule(item, config, "min");
        copyNumberRule(item, config, "max");
        if (config.containsKey("pattern")) {
            item.put("pattern", config.getString("pattern"));
        }
        if (config.containsKey("options")) {
            putStaticOptions(item, config.get("options"));
        }
    }

    private static void applyFieldTypeExtensions(
            Map<String, Object> item,
            String entityTypeCode,
            FieldDO field,
            String fieldType,
            ModelFieldAssignmentDO assign,
            RefResolveContext refResolveContext) {
        if (FieldTypeEnum.isMultiEntityRef(fieldType)) {
            item.put("renderAs", "ref-picker-multi");
            item.put("valueShape", "array");
            if (field.getMaxRelations() != null && field.getMaxRelations() > 0) {
                item.put("max", field.getMaxRelations());
            }
        }

        if ("ENUM".equals(fieldType) && field.getOptions() != null) {
            putStaticOptions(item, field.getOptions());
        }

        if (FieldTypeEnum.isEntityRef(fieldType) || "REFERENCE".equals(fieldType)) {
            item.put("renderAs", "ref-picker");
            putEntityRefTarget(item, entityTypeCode, assign, refResolveContext);
        }
    }

    private static void putEntityRefTarget(
            Map<String, Object> item,
            String sourceEntityTypeCode,
            ModelFieldAssignmentDO assign,
            RefResolveContext refResolveContext) {
        String targetEntityTypeCode = resolveRefTargetEntityTypeCode(
                sourceEntityTypeCode, assign, refResolveContext);
        Map<String, Object> binding = new LinkedHashMap<>();
        binding.put("businessCategory", BusinessCategoryConstants.DYNAMIC);
        binding.put("dataKind", BusinessCategoryConstants.KIND_ENTITY);
        binding.put("entityTypeCode", targetEntityTypeCode);
        item.put("targetEntityTypeCode", targetEntityTypeCode);
        item.put("refTarget", Map.of(
                "capabilityBinding", binding,
                "valueField", "id",
                "labelField", "name"));
    }

    static String resolveRefTargetEntityTypeCode(
            String sourceEntityTypeCode,
            ModelFieldAssignmentDO assign,
            RefResolveContext refResolveContext) {
        RefResolveContext ctx = refResolveContext != null ? refResolveContext : RefResolveContext.empty();
        if (assign != null && assign.getRefLibraryId() != null) {
            RelationFieldLibraryDO lib = ctx.refLibraryById().get(assign.getRefLibraryId());
            if (lib != null && StringUtils.hasText(lib.getRefEntityType())) {
                return lib.getRefEntityType().trim();
            }
        }
        if (assign != null && assign.getModelRelationId() != null) {
            ModelRelationDO rel = ctx.modelRelationById().get(assign.getModelRelationId());
            if (rel != null && StringUtils.hasText(rel.getTargetModelCode())) {
                String modelCode = rel.getTargetModelCode().trim();
                String targetCode = ctx.modelCodeToEntityTypeCode().get(modelCode);
                if (StringUtils.hasText(targetCode)) {
                    return targetCode.trim();
                }
            }
        }
        if (assign != null && StringUtils.hasText(assign.getTargetEntityType())) {
            return assign.getTargetEntityType().trim();
        }
        return sourceEntityTypeCode;
    }

    private static void putStaticOptions(Map<String, Object> item, Object optionsRaw) {
        List<Map<String, Object>> options = parseOptionsList(optionsRaw);
        if (options.isEmpty()) {
            return;
        }
        item.put("optionsSource", Map.of("kind", "static", "options", options));
        item.put("renderAs", "select");
    }

    private static List<Map<String, Object>> parseOptionsList(Object optionsRaw) {
        List<Map<String, Object>> options = new ArrayList<>();
        if (optionsRaw == null) {
            return options;
        }
        JSONArray array;
        if (optionsRaw instanceof JSONArray jsonArray) {
            array = jsonArray;
        } else if (optionsRaw instanceof String str) {
            if (!StringUtils.hasText(str)) {
                return options;
            }
            try {
                array = JSON.parseArray(str);
            } catch (Exception ignored) {
                return options;
            }
        } else {
            return options;
        }
        if (array == null) {
            return options;
        }
        for (int i = 0; i < array.size(); i++) {
            Object element = array.get(i);
            if (!(element instanceof JSONObject obj)) {
                continue;
            }
            Object value = obj.get("value");
            if (value == null) {
                value = obj.get("code");
            }
            String label = obj.getString("label");
            if (label == null) {
                label = obj.getString("name");
            }
            if (value == null) {
                continue;
            }
            Map<String, Object> option = new LinkedHashMap<>();
            option.put("value", value);
            option.put("label", StringUtils.hasText(label) ? label : String.valueOf(value));
            options.add(option);
        }
        return options;
    }

    private static void applyGroupMeta(
            Map<String, Object> item, Long fieldId, List<ModelFieldGroupRespVO> groups) {
        if (groups == null || groups.isEmpty() || fieldId == null) {
            return;
        }
        groups.sort(Comparator.comparingInt(g -> g.getSort() != null ? g.getSort() : 0));
        for (ModelFieldGroupRespVO group : groups) {
            if (group.getFields() == null) {
                continue;
            }
            for (ModelFieldGroupRespVO.FieldRefVO ref : group.getFields()) {
                if (fieldId.equals(ref.getFieldId()) && group.getId() != null) {
                    item.put("panelId", "group-" + group.getId());
                    if (StringUtils.hasText(group.getName())) {
                        item.put("groupName", group.getName());
                    }
                    return;
                }
            }
        }
    }

    private static Map<String, Object> buildFormLayout(
            List<ModelFieldGroupRespVO> groups, Map<Long, FieldDO> fieldById) {
        if (groups == null || groups.isEmpty()) {
            return null;
        }
        List<Map<String, Object>> panels = new ArrayList<>();
        groups.sort(Comparator.comparingInt(g -> g.getSort() != null ? g.getSort() : 0));
        for (ModelFieldGroupRespVO group : groups) {
            if (group.getId() == null || !StringUtils.hasText(group.getName())) {
                continue;
            }
            List<String> fieldKeys = new ArrayList<>();
            if (group.getFields() != null) {
                group.getFields().stream()
                        .sorted(Comparator.comparingInt(r -> r.getSort() != null ? r.getSort() : 0))
                        .forEach(ref -> {
                            FieldDO field = fieldById.get(ref.getFieldId());
                            if (field != null && StringUtils.hasText(field.getCode())) {
                                fieldKeys.add(field.getCode().trim());
                            }
                        });
            }
            if (fieldKeys.isEmpty()) {
                continue;
            }
            panels.add(Map.of(
                    "id", "group-" + group.getId(),
                    "label", group.getName(),
                    "fieldKeys", fieldKeys));
        }
        if (panels.isEmpty()) {
            return null;
        }
        Map<String, Object> layout = new LinkedHashMap<>();
        layout.put("kind", "tabs");
        layout.put("panels", panels);
        return layout;
    }

    private static void copyNumberRule(Map<String, Object> item, JSONObject rules, String key) {
        if (!rules.containsKey(key)) {
            return;
        }
        Number value = rules.getObject(key, Number.class);
        if (value != null) {
            item.put(key, value);
        }
    }

    private static JSONObject parseJsonObject(String json) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return JSON.parseObject(json);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static String asyncCheckNameUniqueId() {
        return ASYNC_CHECK_NAME_UNIQUE;
    }
}
