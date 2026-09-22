package cn.cheers.x.inspection.task.service.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 从动作库参数清单读排程用的动作耗时。
 * <p>权威：{@code param_slots_json} 里 {@code action_duration} 的默认值，单位分钟。
 * <p>不负责：写巡检内容、算路径耗时、读宿主参数包。
 * <p>禁止：把 {@code dwell_duration}（停留秒）当成动作耗时；缺默认值时猜分钟数。
 */
public final class ActionDurationParamSupport {

    public static final String FIELD_CODE = "action_duration";

    private ActionDurationParamSupport() {
    }

    /**
     * 先读当前巡检方式那一档 methods，没有再读顶层 fields。
     *
     * @return 有非负默认值 → 分钟数；槽不存在或默认值为空 → empty
     */
    public static Optional<Integer> minutesFromSlots(Object raw, String means, ObjectMapper objectMapper) {
        Object value = unwrap(raw, objectMapper);
        if (!(value instanceof Map<?, ?> root)) {
            return Optional.empty();
        }
        Optional<Integer> fromMeans = minutesFromMethods(root, means);
        if (fromMeans.isPresent()) {
            return fromMeans;
        }
        return minutesFromFields(root.get("fields"));
    }

    private static Optional<Integer> minutesFromMethods(Map<?, ?> root, String means) {
        Object methodsRaw = root.get("methods");
        if (!(methodsRaw instanceof List<?> methods) || methods.isEmpty()) {
            return Optional.empty();
        }
        String wanted = means == null ? "" : means.trim().toUpperCase();
        if (wanted.isEmpty()) {
            return Optional.empty();
        }
        for (Object row : methods) {
            if (!(row instanceof Map<?, ?> method)) {
                continue;
            }
            String rowMeans = text(first(method, "methodKey", "executionMeans")).toUpperCase();
            if (!wanted.equals(rowMeans)) {
                continue;
            }
            return minutesFromFields(method.get("fields"));
        }
        return Optional.empty();
    }

    private static Optional<Integer> minutesFromFields(Object fieldsRaw) {
        if (!(fieldsRaw instanceof List<?> fields)) {
            return Optional.empty();
        }
        for (Object row : fields) {
            if (!(row instanceof Map<?, ?> field)) {
                continue;
            }
            if (!FIELD_CODE.equals(text(first(field, "fieldCode", "code")))) {
                continue;
            }
            return minutesOf(first(field, "defaultValue", "value"));
        }
        return Optional.empty();
    }

    static Optional<Integer> minutesOf(Object raw) {
        if (raw == null) {
            return Optional.empty();
        }
        if (raw instanceof Number number) {
            int minutes = number.intValue();
            return minutes < 0 ? Optional.empty() : Optional.of(minutes);
        }
        String text = String.valueOf(raw).trim();
        if (!StringUtils.hasText(text) || "null".equalsIgnoreCase(text)) {
            return Optional.empty();
        }
        try {
            int minutes = Integer.parseInt(text);
            return minutes < 0 ? Optional.empty() : Optional.of(minutes);
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
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
