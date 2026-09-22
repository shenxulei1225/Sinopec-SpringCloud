package cn.cheers.x.inspection.task.service.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 读动作库 {@code param_slots_json} 里当前巡检方式声明了哪些参数槽。
 * <p>核对计划要能看见「到达位置 / 偏航角」这类槽：槽在动作库该档上，值仍只从设备检查参数包取。
 * <p>不负责：填宿主包实参、算动作耗时、猜别的手段的槽。
 */
public final class ActionParamSlotsSupport {

    private ActionParamSlotsSupport() {
    }

    /**
     * 先读当前巡检方式那一档 methods，没有再读顶层 fields。
     *
     * @return 该档 fieldCode 列表；没有这一档或没有槽 → 空列表，不猜别档
     */
    public static List<String> fieldCodes(Object raw, String means, ObjectMapper objectMapper) {
        Object value = unwrap(raw, objectMapper);
        if (!(value instanceof Map<?, ?> root)) {
            return List.of();
        }
        List<String> fromMeans = fieldCodesFromMethods(root, means);
        if (!fromMeans.isEmpty()) {
            return fromMeans;
        }
        return fieldCodesFromFields(root.get("fields"));
    }

    private static List<String> fieldCodesFromMethods(Map<?, ?> root, String means) {
        Object methodsRaw = root.get("methods");
        if (!(methodsRaw instanceof List<?> methods) || methods.isEmpty()) {
            return List.of();
        }
        String wanted = means == null ? "" : means.trim().toUpperCase();
        if (wanted.isEmpty()) {
            return List.of();
        }
        for (Object row : methods) {
            if (!(row instanceof Map<?, ?> method)) {
                continue;
            }
            String rowMeans = text(first(method, "methodKey", "executionMeans")).toUpperCase();
            if (!wanted.equals(rowMeans)) {
                continue;
            }
            return fieldCodesFromFields(method.get("fields"));
        }
        return List.of();
    }

    private static List<String> fieldCodesFromFields(Object fieldsRaw) {
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

    private static Object unwrap(Object raw, ObjectMapper objectMapper) {
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
