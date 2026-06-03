package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 从 System 模块 SaveReqVO 反射生成 CRUD 表单字段 schema。
 */
@Slf4j
final class SystemVoSchemaScanner {

    private SystemVoSchemaScanner() {
    }

    static List<Map<String, Object>> scanWriteFields(SystemCapabilityResourceDef def, String purpose) {
        if ("delete".equals(purpose)) {
            return List.of(InteractionSchemaBuilder.field("id", "编号", "input", true, false, null, null));
        }
        SystemVoSchemaRegistry.VoBinding binding = SystemVoSchemaRegistry.find(def.resourceCode()).orElse(null);
        if (binding == null) {
            return fallbackFromCatalogSpecs(def, purpose);
        }
        try {
            Class<?> voClass = Class.forName(binding.saveVoClassName());
            return scanVoClass(def, voClass, binding, purpose);
        } catch (ClassNotFoundException ex) {
            log.warn("[SystemVoSchemaScanner] VO 类不在 classpath，回退 catalog 推导: {}", binding.saveVoClassName());
            return fallbackFromCatalogSpecs(def, purpose);
        }
    }

    private static List<Map<String, Object>> scanVoClass(
            SystemCapabilityResourceDef def,
            Class<?> voClass,
            SystemVoSchemaRegistry.VoBinding binding,
            String purpose) {
        List<Map<String, Object>> fields = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        for (Field declared : voClass.getDeclaredFields()) {
            if (shouldSkipField(declared)) {
                continue;
            }
            String fieldKey = declared.getName();
            if (seen.contains(fieldKey)) {
                continue;
            }
            if ("update".equals(purpose) && binding.createOnlyFieldKeys().contains(fieldKey)) {
                continue;
            }
            if ("create".equals(purpose) && "id".equals(fieldKey)) {
                continue;
            }
            Map<String, Object> field = toFieldSchema(def, declared, purpose);
            if (field == null) {
                continue;
            }
            seen.add(fieldKey);
            fields.add(field);
        }
        if ("update".equals(purpose) && !seen.contains("id")) {
            fields.add(0, InteractionSchemaBuilder.field("id", "编号", "input", true, true, null, null));
        }
        return fields;
    }

    private static boolean shouldSkipField(Field field) {
        int mod = field.getModifiers();
        if (Modifier.isStatic(mod) || field.isSynthetic()) {
            return true;
        }
        return field.getAnnotation(JsonIgnore.class) != null;
    }

    private static Map<String, Object> toFieldSchema(SystemCapabilityResourceDef def, Field declared, String purpose) {
        String fieldKey = declared.getName();
        Schema schema = declared.getAnnotation(Schema.class);
        String label = schema != null && StrUtil.isNotBlank(schema.description())
                ? schema.description() : fieldKey;

        boolean required = isRequired(declared, schema, fieldKey, purpose);
        boolean readOnly = "update".equals(purpose) && "id".equals(fieldKey);
        String control = resolveControl(declared, fieldKey);
        String dictType = resolveDictType(declared, fieldKey);
        String refTarget = resolveRefTarget(def.resourceCode(), fieldKey, declared, control);

        Map<String, Object> field = InteractionSchemaBuilder.field(
                fieldKey, label, control, required, readOnly, dictType, refTarget);

        applySizeConstraints(field, declared);
        applyPatternConstraint(field, declared);
        applyMultiValueShape(field, declared, control);
        return field;
    }

    private static boolean isRequired(Field field, Schema schema, String fieldKey, String purpose) {
        if ("update".equals(purpose) && "id".equals(fieldKey)) {
            return true;
        }
        if ("create".equals(purpose) && "id".equals(fieldKey)) {
            return false;
        }
        if (field.getAnnotation(NotNull.class) != null
                || field.getAnnotation(NotBlank.class) != null
                || field.getAnnotation(NotEmpty.class) != null) {
            return true;
        }
        if (schema != null && schema.requiredMode() == Schema.RequiredMode.REQUIRED) {
            return true;
        }
        if ("create".equals(purpose) && "password".equals(fieldKey)) {
            return true;
        }
        return false;
    }

    private static String resolveControl(Field field, String fieldKey) {
        Class<?> type = field.getType();
        if (Boolean.class.equals(type) || boolean.class.equals(type)) {
            return "boolean";
        }
        if (Collection.class.isAssignableFrom(type) || type.isArray()) {
            return "ref-picker-multi";
        }
        InEnum inEnum = field.getAnnotation(InEnum.class);
        if (inEnum != null || "status".equals(fieldKey) || fieldKey.endsWith("Status")) {
            return "dict";
        }
        if (isRefField(fieldKey, type)) {
            return "ref-picker";
        }
        if (String.class.equals(type) && (fieldKey.contains("content") || fieldKey.contains("remark"))) {
            return "input";
        }
        return "input";
    }

    private static boolean isRefField(String fieldKey, Class<?> type) {
        if (!Long.class.equals(type) && !Integer.class.equals(type)) {
            return false;
        }
        return "parentId".equals(fieldKey)
                || fieldKey.endsWith("Id")
                || fieldKey.endsWith("UserId");
    }

    private static String resolveDictType(Field field, String fieldKey) {
        InEnum inEnum = field.getAnnotation(InEnum.class);
        if (inEnum != null && inEnum.value() == CommonStatusEnum.class) {
            return "common_status";
        }
        if ("status".equals(fieldKey) || fieldKey.endsWith("Status")) {
            return "common_status";
        }
        if ("readStatus".equals(fieldKey)) {
            return "infra_boolean_string";
        }
        if ("sendStatus".equals(fieldKey)) {
            return "system_mail_send_status";
        }
        return null;
    }

    private static String resolveRefTarget(
            String resourceCode, String fieldKey, Field field, String control) {
        if (!"ref-picker".equals(control) && !"ref-picker-multi".equals(control)) {
            return null;
        }
        if ("parentId".equals(fieldKey)) {
            return SystemRefTargetResolver.resolveParentRef(resourceCode);
        }
        return SystemRefTargetResolver.resolve(resourceCode, fieldKey);
    }

    private static void applySizeConstraints(Map<String, Object> field, Field declared) {
        Size size = declared.getAnnotation(Size.class);
        if (size != null) {
            if (size.max() < Integer.MAX_VALUE) {
                field.put("maxLength", size.max());
            }
            if (size.min() > 0) {
                field.put("minLength", size.min());
            }
            return;
        }
        Length length = declared.getAnnotation(Length.class);
        if (length != null) {
            if (length.max() < Integer.MAX_VALUE) {
                field.put("maxLength", length.max());
            }
            if (length.min() > 0) {
                field.put("minLength", length.min());
            }
        }
    }

    private static void applyPatternConstraint(Map<String, Object> field, Field declared) {
        Pattern pattern = declared.getAnnotation(Pattern.class);
        if (pattern != null && StrUtil.isNotBlank(pattern.regexp())) {
            field.put("pattern", pattern.regexp());
            return;
        }
        if (declared.getAnnotation(Email.class) != null) {
            field.put("pattern", "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
        }
    }

    private static void applyMultiValueShape(Map<String, Object> field, Field declared, String control) {
        if (!"ref-picker-multi".equals(control)) {
            return;
        }
        field.put("valueShape", "array");
        Class<?> elementType = resolveCollectionElementType(declared);
        if (Long.class.equals(elementType) || Integer.class.equals(elementType)) {
            field.put("control", "ref-picker-multi");
        }
    }

    private static Class<?> resolveCollectionElementType(Field field) {
        Type generic = field.getGenericType();
        if (generic instanceof ParameterizedType parameterized) {
            Type[] args = parameterized.getActualTypeArguments();
            if (args.length == 1 && args[0] instanceof Class<?> clazz) {
                return clazz;
            }
        }
        return Long.class;
    }

    /** VO 不可用时：仅用 catalog filters + displays 推导（不含硬编码 extras） */
    private static List<Map<String, Object>> fallbackFromCatalogSpecs(SystemCapabilityResourceDef def, String purpose) {
        List<Map<String, Object>> fields = new ArrayList<>();
        if ("update".equals(purpose)) {
            fields.add(InteractionSchemaBuilder.field("id", "编号", "input", true, true, null, null));
        }
        List<String> seen = new ArrayList<>();
        for (SystemCapabilityResourceDef.FilterSpec spec : def.filters()) {
            if (seen.contains(spec.fieldKey())) {
                continue;
            }
            seen.add(spec.fieldKey());
            fields.add(InteractionSchemaBuilder.field(
                    spec.fieldKey(), spec.label(), spec.control(),
                    false, false, spec.dictType(), spec.refTargetKey()));
        }
        for (SystemCapabilityResourceDef.DisplaySpec spec : def.displayFields()) {
            if (seen.contains(spec.fieldKey()) || "id".equals(spec.fieldKey())) {
                continue;
            }
            seen.add(spec.fieldKey());
            String control = "dict".equals(spec.renderAs()) ? "dict" : "input";
            fields.add(InteractionSchemaBuilder.field(
                    spec.fieldKey(), spec.label(), control, false, false,
                    "dict".equals(spec.renderAs()) ? "common_status" : null, null));
        }
        return fields;
    }
}
