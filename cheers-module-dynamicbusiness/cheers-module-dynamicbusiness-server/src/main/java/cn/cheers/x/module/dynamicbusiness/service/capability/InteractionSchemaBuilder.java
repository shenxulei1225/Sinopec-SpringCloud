package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 契约 endpoint.requestBody：描述 CRUD 等写接口的请求体字段（供前端弹窗渲染表单）。
 */
public final class InteractionSchemaBuilder {

    private InteractionSchemaBuilder() {
    }

    public static Map<String, Object> requestBody(List<Map<String, Object>> fields) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("fields", fields);
        return body;
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static Map<String, Object> field(
            String fieldKey,
            String label,
            String control,
            boolean required,
            boolean readOnly,
            String dictType,
            String refTargetKey) {
        return field(fieldKey, label, control, required, readOnly, dictType, refTargetKey, null);
    }

    public static Map<String, Object> field(
            String fieldKey,
            String label,
            String control,
            boolean required,
            boolean readOnly,
            String dictType,
            String refTargetKey,
            String validationRulesJson) {
        Map<String, Object> f = new LinkedHashMap<>();
        f.put("fieldKey", fieldKey);
        f.put("label", label);
        f.put("control", control);
        f.put("required", required);
        if (readOnly) {
            f.put("readOnly", true);
        }
        if (dictType != null) {
            f.put("dictType", dictType);
        }
        if (refTargetKey != null) {
            Map<String, Object> ref = new LinkedHashMap<>();
            ref.put("queryContractKey", refTargetKey);
            ref.put("instanceKey", refTargetKey);
            ref.put("valueField", "id");
            ref.put("labelField", "name");
            f.put("refTarget", ref);
        }
        applyValidationRules(f, validationRulesJson);
        String enumOptionsJson = dictType != null && dictType.trim().startsWith("[") ? dictType : null;
        String realDictType = enumOptionsJson != null ? null : dictType;
        FieldOptionsSourceBuilder.attachOptionsSource(f, control, realDictType, refTargetKey, enumOptionsJson);
        return f;
    }

    @SuppressWarnings("unchecked")
    private static void applyValidationRules(Map<String, Object> field, String validationRulesJson) {
        if (StrUtil.isBlank(validationRulesJson)) {
            return;
        }
        try {
            Map<String, Object> rules = OBJECT_MAPPER.readValue(validationRulesJson, new TypeReference<>() {});
            copyRule(rules, field, "maxLength");
            copyRule(rules, field, "minLength");
            copyRule(rules, field, "min");
            copyRule(rules, field, "max");
            copyRule(rules, field, "pattern");
        } catch (Exception ignored) {
            // 非法 JSON 忽略，提交时由后端兜底
        }
    }

    private static void copyRule(Map<String, Object> rules, Map<String, Object> field, String key) {
        if (rules.containsKey(key)) {
            field.put(key, rules.get(key));
        }
    }

    /** System 资源：由 filters + displays 推导写表单（delete 仅 id） */
    public static List<Map<String, Object>> buildSystemWriteFields(
            SystemCapabilityResourceDef def, String purpose) {
        if ("delete".equals(purpose)) {
            return List.of(field("id", "编号", "input", true, false, null, null));
        }
        List<Map<String, Object>> fields = new ArrayList<>();
        if ("update".equals(purpose)) {
            fields.add(field("id", "编号", "input", true, true, null, null));
        }
        List<String> seen = new ArrayList<>();
        for (SystemCapabilityResourceDef.FilterSpec spec : def.filters()) {
            if (seen.contains(spec.fieldKey())) {
                continue;
            }
            seen.add(spec.fieldKey());
            fields.add(field(
                    spec.fieldKey(),
                    spec.label(),
                    spec.control(),
                    false,
                    false,
                    spec.dictType(),
                    spec.refTargetKey()));
        }
        for (SystemCapabilityResourceDef.DisplaySpec spec : def.displayFields()) {
            if (seen.contains(spec.fieldKey())) {
                continue;
            }
            if ("id".equals(spec.fieldKey())) {
                continue;
            }
            seen.add(spec.fieldKey());
            String control = "dict".equals(spec.renderAs()) ? "dict" : "input";
            fields.add(field(spec.fieldKey(), spec.label(), control, false, false,
                    "dict".equals(spec.renderAs()) ? "common_status" : null, null));
        }
        appendSystemWriteExtras(def, purpose, fields, seen);
        return fields;
    }

    private static void appendSystemWriteExtras(
            SystemCapabilityResourceDef def, String purpose, List<Map<String, Object>> fields, List<String> seen) {
        if ("delete".equals(purpose)) {
            return;
        }
        switch (def.resourceCode()) {
            case "dept" -> {
                addIfAbsent(fields, seen, field("parentId", "上级部门", "ref-picker", false, false, null, "system:dept"));
                addIfAbsent(fields, seen, field("sort", "显示顺序", "input", true, false, null, null));
                addIfAbsent(fields, seen, field("leaderUserId", "负责人", "ref-picker", false, false, null, "system:user"));
                addIfAbsent(fields, seen, field("phone", "联系电话", "input", false, false, null, null));
                addIfAbsent(fields, seen, field("email", "邮箱", "input", false, false, null, null));
            }
            case "user" -> {
                addIfAbsent(fields, seen, field("nickname", "昵称", "input", false, false, null, null));
                addIfAbsent(fields, seen, field("password", "密码", "input", "create".equals(purpose), false, null, null));
            }
            case "menu" -> {
                addIfAbsent(fields, seen, field("parentId", "上级菜单", "ref-picker", false, false, null, "system:menu"));
                addIfAbsent(fields, seen, field("sort", "显示顺序", "input", true, false, null, null));
            }
            default -> {
                // 无额外字段
            }
        }
    }

    private static void addIfAbsent(
            List<Map<String, Object>> fields, List<String> seen, Map<String, Object> field) {
        String key = String.valueOf(field.get("fieldKey"));
        if (!seen.contains(key)) {
            seen.add(key);
            fields.add(field);
        }
    }

    /**
     * 动态实体：由模型字段分配动态生成写表单（字段变更后 rebuild 即更新 CRUD 页面 schema）。
     */
    public static List<Map<String, Object>> buildEntityWriteFieldsFromAssignments(
            List<ModelFieldAssignmentRespVO> assignments,
            String purpose,
            Function<ModelFieldAssignmentRespVO, String> refKeyResolver) {
        if ("delete".equals(purpose)) {
            return List.of(field("id", "编号", "input", true, false, null, null));
        }
        List<Map<String, Object>> fields = new ArrayList<>();
        if ("update".equals(purpose)) {
            fields.add(field("id", "编号", "input", true, true, null, null));
        }
        if (assignments == null) {
            return fields;
        }
        List<String> seen = new ArrayList<>();
        for (ModelFieldAssignmentRespVO item : assignments) {
            if (item == null || item.getField() == null) {
                continue;
            }
            if (Boolean.FALSE.equals(item.getEditable())) {
                continue;
            }
            FieldRespVO field = item.getField();
            String fieldKey = field.getCode();
            if ("id".equals(fieldKey) || seen.contains(fieldKey)) {
                continue;
            }
            seen.add(fieldKey);
            String control = resolveWriteControl(field.getType());
            boolean required = Boolean.TRUE.equals(item.getRequired());
            String refKey = FieldTypeEnum.isEntityRef(field.getType()) && refKeyResolver != null
                    ? refKeyResolver.apply(item) : null;
            String optionsJson = "ENUM".equals(field.getType()) ? field.getOptions() : null;
            String dictForOptions = "dict".equals(control) && !"ENUM".equals(field.getType())
                    ? field.getOptions() : null;
            fields.add(field(
                    fieldKey,
                    field.getName(),
                    control,
                    required,
                    false,
                    dictForOptions,
                    refKey,
                    item.getValidationRules()));
            Map<String, Object> last = fields.get(fields.size() - 1);
            applyWriteFieldMeta(last, field.getType(), control);
            if ("ENUM".equals(field.getType()) && optionsJson != null) {
                FieldOptionsSourceBuilder.attachOptionsSource(last, "dict", null, null, optionsJson);
            }
        }
        return fields;
    }

    private static void applyWriteFieldMeta(Map<String, Object> fieldMap, String fieldType, String control) {
        if ("ENTITY_REF_MULTI".equals(fieldType) || "ref-picker-multi".equals(control)) {
            fieldMap.put("control", "ref-picker-multi");
            fieldMap.put("valueShape", "array");
        }
        if ("LONG_TEXT".equals(fieldType)) {
            fieldMap.put("control", "rich-text");
        }
        if ("DATE".equals(fieldType) || "DATETIME".equals(fieldType)) {
            fieldMap.put("control", "date");
        }
    }

    /** @deprecated 仅兼容旧路径；优先 {@link #buildEntityWriteFieldsFromAssignments} */
    public static List<Map<String, Object>> buildEntityWriteFields(
            List<Map<String, Object>> entityFilters, String purpose) {
        List<Map<String, Object>> fields = new ArrayList<>();
        if ("update".equals(purpose)) {
            fields.add(field("id", "编号", "input", true, true, null, null));
        }
        if ("delete".equals(purpose)) {
            return List.of(field("id", "编号", "input", true, false, null, null));
        }
        if (entityFilters == null) {
            return fields;
        }
        for (Map<String, Object> filter : entityFilters) {
            String fieldKey = String.valueOf(filter.get("fieldKey"));
            if ("id".equals(fieldKey)) {
                continue;
            }
            String control = String.valueOf(filter.getOrDefault("control", "input"));
            String label = String.valueOf(filter.getOrDefault("label", fieldKey));
            Object ref = filter.get("refTarget");
            String refKey = null;
            if (ref instanceof Map<?, ?> refMap) {
                Object key = refMap.get("queryContractKey");
                if (key == null) {
                    key = refMap.get("instanceKey");
                }
                if (key != null) {
                    refKey = String.valueOf(key);
                }
            }
            fields.add(field(fieldKey, label, control,
                    Boolean.TRUE.equals(filter.get("required")), false,
                    (String) filter.get("dictType"), refKey,
                    (String) filter.get("validationRules")));
        }
        return fields;
    }

    private static String resolveWriteControl(String fieldType) {
        if (fieldType == null) {
            return "input";
        }
        return switch (fieldType) {
            case "ENUM" -> "dict";
            case "BOOLEAN" -> "boolean";
            case "ENTITY_REF_MULTI" -> "ref-picker-multi";
            case "LONG_TEXT" -> "rich-text";
            case "DATE", "DATETIME" -> "date";
            default -> FieldTypeEnum.isEntityRef(fieldType) ? "ref-picker" : "input";
        };
    }
}
