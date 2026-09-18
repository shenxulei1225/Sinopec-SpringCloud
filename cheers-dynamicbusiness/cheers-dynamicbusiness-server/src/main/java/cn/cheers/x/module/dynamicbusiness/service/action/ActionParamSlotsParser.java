package cn.cheers.x.module.dynamicbusiness.service.action;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 解析动作实体 {@code param_slots_json}。
 *
 * <p>权威外形是按执行手段拆开的 {@code {version, methods:[{executionMeans, fields:[{fieldCode}]}]} }。
 * 旧外形是顶层 {@code fields} 或字符串数组。列表接口必须两种都认，不能只读顶层 fields，
 * 否则「调整拍摄角度」这类只写在手段里的参数会在对接页被当成没有参数。</p>
 *
 * <p>不负责：填写对照、猜缺省手段、在读路径补参数。</p>
 */
public final class ActionParamSlotsParser {

    private static final ObjectMapper JSON = new ObjectMapper();

    private ActionParamSlotsParser() {
    }

    public record Snapshot(List<String> paramSlots, Map<String, List<String>> paramSlotsByMeans) {
    }

    public static Snapshot parse(Object raw) {
        Object value = coerce(raw);
        if (value instanceof Map<?, ?> root) {
            Object methods = root.get("methods");
            if (methods instanceof List<?> methodRows) {
                Map<String, List<String>> byMeans = new LinkedHashMap<>();
                Set<String> merged = new LinkedHashSet<>();
                for (Object rowRaw : methodRows) {
                    if (!(rowRaw instanceof Map<?, ?> row)) {
                        continue;
                    }
                    String means = firstText(row, "executionMeans", "execution_means");
                    List<String> fields = readFieldCodes(firstPresent(row, "fields", "params", "paramSlots", "param_slots"));
                    if (!means.isEmpty()) {
                        byMeans.put(means.toUpperCase(Locale.ROOT), List.copyOf(fields));
                    }
                    merged.addAll(fields);
                }
                merged.addAll(readFieldCodes(root.get("fields")));
                return new Snapshot(List.copyOf(merged), Map.copyOf(byMeans));
            }
        }
        return new Snapshot(readFieldCodes(value), Map.of());
    }

    private static Object coerce(Object raw) {
        if (raw instanceof String s) {
            String trimmed = s.trim();
            if (trimmed.isEmpty()) {
                return null;
            }
            if (trimmed.startsWith("[") || trimmed.startsWith("{")) {
                try {
                    return JSON.readValue(trimmed, Object.class);
                } catch (Exception ignored) {
                    return trimmed;
                }
            }
            return trimmed;
        }
        return raw;
    }

    private static List<String> readFieldCodes(Object raw) {
        Object value = coerce(raw);
        if (value == null) {
            return List.of();
        }
        if (value instanceof Map<?, ?> root) {
            Object fields = root.get("fields");
            if (fields != null) {
                return readFieldCodes(fields);
            }
            String single = firstFieldCode(root);
            return single != null ? List.of(single) : List.of();
        }
        if (value instanceof List<?> list) {
            List<String> out = new ArrayList<>();
            for (Object item : list) {
                if (item == null) {
                    continue;
                }
                if (item instanceof Map<?, ?> map) {
                    String code = firstFieldCode(map);
                    if (code != null) {
                        out.add(code);
                    }
                    continue;
                }
                String text = String.valueOf(item).trim();
                if (!text.isEmpty()) {
                    out.add(text);
                }
            }
            return List.copyOf(out);
        }
        if (value instanceof String s) {
            String text = s.trim();
            return text.isEmpty() ? List.of() : List.of(text);
        }
        return List.of(String.valueOf(value));
    }

    private static String firstFieldCode(Map<?, ?> map) {
        Object slot = firstPresent(map, "fieldCode", "field_code", "slotKey", "code");
        if (slot == null) {
            return null;
        }
        String text = String.valueOf(slot).trim();
        return text.isEmpty() ? null : text;
    }

    private static Object firstPresent(Map<?, ?> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private static String firstText(Map<?, ?> map, String... keys) {
        Object value = firstPresent(map, keys);
        return value == null ? "" : String.valueOf(value).trim();
    }
}
