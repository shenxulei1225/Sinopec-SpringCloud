package cn.cheers.x.module.dynamicbusiness.action.paramslots;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 动作库 {@code param_slots_json} 的共用读法：按巡检方式（及可选型号）取字段名单与默认值。
 * <p>任务冻结步骤、协议网关翻译前补默认、动作列表接口共用，禁止各模块复制 methodRow 逻辑。
 */
public final class ActionParamSlotsJsonCore {

    private static final String ACTION_DURATION = "action_duration";

    private ActionParamSlotsJsonCore() {
    }

    public static List<String> fieldCodes(
            Object raw, String means, Long equipmentModelId, ObjectMapper objectMapper) {
        Object value = unwrap(raw, objectMapper);
        if (!(value instanceof Map<?, ?> root)) {
            return List.of();
        }
        Map<?, ?> row = methodRow(root, means, equipmentModelId);
        if (row != null) {
            return fieldCodesFromFields(row.get("fields"));
        }
        return fieldCodesFromFields(root.get("fields"));
    }

    public static Map<String, Object> defaultValues(
            Object raw, String means, Long equipmentModelId, ObjectMapper objectMapper) {
        Object value = unwrap(raw, objectMapper);
        if (!(value instanceof Map<?, ?> root)) {
            return Map.of();
        }
        Map<?, ?> row = methodRow(root, means, equipmentModelId);
        if (row != null) {
            return defaultsFromFields(row.get("fields"));
        }
        return defaultsFromFields(root.get("fields"));
    }

    public static List<String> fieldCodesFromFields(Object fieldsRaw) {
        if (!(fieldsRaw instanceof List<?> fields)) {
            return List.of();
        }
        List<String> codes = new ArrayList<>();
        for (Object row : fields) {
            if (!(row instanceof Map<?, ?> field)) {
                continue;
            }
            String code = text(first(field, "fieldCode", "code"));
            if (!code.isBlank()) {
                codes.add(code);
            }
        }
        return codes;
    }

    public static boolean isFilled(Object raw) {
        if (raw == null) {
            return false;
        }
        if (raw instanceof String text) {
            return !text.isBlank();
        }
        return true;
    }

    public static Map<?, ?> methodRow(Map<?, ?> root, String means, Long equipmentModelId) {
        Object methodsRaw = root.get("methods");
        if (!(methodsRaw instanceof List<?> methods) || methods.isEmpty()) {
            return null;
        }
        String wanted = means == null ? "" : means.trim().toUpperCase();
        if (wanted.isEmpty()) {
            return null;
        }
        if (!paramsPerEquipmentModel(root, wanted)) {
            return unscopedParamRow(methods, wanted);
        }
        Map<?, ?> unscoped = null;
        for (Object row : methods) {
            if (!(row instanceof Map<?, ?> method)) {
                continue;
            }
            String rowMeans = text(first(method, "methodKey", "executionMeans", "execution_means")).toUpperCase();
            if (!wanted.equals(rowMeans)) {
                continue;
            }
            Long rowModel = modelIdOf(method);
            if (equipmentModelId != null && equipmentModelId.equals(rowModel)) {
                return method;
            }
            if (rowModel == null && unscoped == null) {
                unscoped = method;
            }
        }
        if (equipmentModelId == null) {
            if (unscoped != null && fieldCount(unscoped) > 0) {
                return unscoped;
            }
            return null;
        }
        return null;
    }

    /**
     * 这一巡检方式已经登记的适用型号：行上的型号，以及适用型号列表。
     * 生成步骤图时用来对上执行设备或挂载相机，不在这里挑一个型号。
     */
    public static List<Long> listedModelIds(Map<?, ?> root, String meansUpper) {
        Object methodsRaw = root.get("methods");
        if (!(methodsRaw instanceof List<?> methods)) {
            return List.of();
        }
        String wanted = meansUpper == null ? "" : meansUpper.trim().toUpperCase();
        List<Long> ids = new ArrayList<>();
        for (Object row : methods) {
            if (!(row instanceof Map<?, ?> method)) {
                continue;
            }
            String rowMeans = text(first(method, "methodKey", "executionMeans", "execution_means")).toUpperCase();
            if (!wanted.equals(rowMeans)) {
                continue;
            }
            Long rowModel = modelIdOf(method);
            if (rowModel != null && !ids.contains(rowModel)) {
                ids.add(rowModel);
            }
            Object listed = first(method, "applicableModelIds", "applicable_model_ids");
            if (listed instanceof List<?> list) {
                for (Object item : list) {
                    Long id = modelIdOf(Map.of("equipmentModelId", item));
                    if (id != null && !ids.contains(id)) {
                        ids.add(id);
                    }
                }
            }
        }
        return List.copyOf(ids);
    }

    public static boolean paramsPerEquipmentModel(Map<?, ?> root, String meansUpper) {
        Object methodsRaw = root.get("methods");
        if (!(methodsRaw instanceof List<?> methods)) {
            return false;
        }
        Boolean paramsPerModelFlag = null;
        Boolean sharedFlag = null;
        boolean split = false;
        for (Object row : methods) {
            if (!(row instanceof Map<?, ?> method)) {
                continue;
            }
            String rowMeans = text(first(method, "methodKey", "executionMeans", "execution_means")).toUpperCase();
            if (!meansUpper.equals(rowMeans)) {
                continue;
            }
            Object perModel = first(method, "paramsPerEquipmentModel");
            if (perModel instanceof Boolean flag) {
                paramsPerModelFlag = flag;
            }
            Object shared = first(method, "paramsSharedAcrossModels", "params_shared_across_models");
            if (shared instanceof Boolean flag) {
                sharedFlag = flag;
            }
            if (modelIdOf(method) != null) {
                split = true;
            }
        }
        if (paramsPerModelFlag != null) {
            return paramsPerModelFlag;
        }
        return split && !Boolean.TRUE.equals(sharedFlag);
    }

    public static Object unwrap(Object raw, ObjectMapper objectMapper) {
        if (raw == null || objectMapper == null) {
            return raw;
        }
        if (raw instanceof String text) {
            String trimmed = text.trim();
            if (trimmed.isEmpty()) {
                return null;
            }
            try {
                return objectMapper.readValue(trimmed, new TypeReference<Object>() {
                });
            } catch (Exception ex) {
                return null;
            }
        }
        return raw;
    }

    private static Map<String, Object> defaultsFromFields(Object fieldsRaw) {
        if (!(fieldsRaw instanceof List<?> fields)) {
            return Map.of();
        }
        Map<String, Object> defaults = new LinkedHashMap<>();
        for (Object row : fields) {
            if (!(row instanceof Map<?, ?> field)) {
                continue;
            }
            String code = text(first(field, "fieldCode", "code"));
            if (code.isBlank() || ACTION_DURATION.equals(code)) {
                continue;
            }
            String generator = text(first(field, "generator"));
            if ("UUID_V4".equalsIgnoreCase(generator)) {
                defaults.put(code, UUID.randomUUID().toString());
                continue;
            }
            Object value = first(field, "defaultValue", "value");
            if (value == null || (value instanceof String text && text.isBlank())) {
                continue;
            }
            defaults.put(code, value);
        }
        return defaults;
    }

    private static Map<?, ?> unscopedParamRow(List<?> methods, String meansUpper) {
        Map<?, ?> unscoped = null;
        Map<?, ?> firstModelRowWithFields = null;
        for (Object row : methods) {
            if (!(row instanceof Map<?, ?> method)) {
                continue;
            }
            String rowMeans = text(first(method, "methodKey", "executionMeans", "execution_means")).toUpperCase();
            if (!meansUpper.equals(rowMeans)) {
                continue;
            }
            Long rowModel = modelIdOf(method);
            if (rowModel != null) {
                if (firstModelRowWithFields == null && fieldCount(method) > 0) {
                    firstModelRowWithFields = method;
                }
                continue;
            }
            if (unscoped == null) {
                unscoped = method;
            }
        }
        if (unscoped != null && fieldCount(unscoped) > 0) {
            return unscoped;
        }
        return firstModelRowWithFields != null ? firstModelRowWithFields : unscoped;
    }

    private static int fieldCount(Map<?, ?> method) {
        Object fields = method.get("fields");
        return fields instanceof List<?> list ? list.size() : 0;
    }

    private static Long modelIdOf(Map<?, ?> method) {
        Object raw = first(method, "equipmentModelId", "equipment_model_id");
        if (raw instanceof Number number && number.longValue() > 0) {
            return number.longValue();
        }
        if (raw instanceof String text && !text.isBlank()) {
            try {
                long value = Long.parseLong(text.trim());
                return value > 0 ? value : null;
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static Object first(Map<?, ?> row, String... keys) {
        for (String key : keys) {
            if (row.containsKey(key) && row.get(key) != null) {
                return row.get(key);
            }
        }
        return null;
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }
}
