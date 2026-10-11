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
 * 无人机可以有多行，每行再带 {@code equipmentModelId}。列表按手段合并字段，不丢后写的型号。
 * 旧外形是顶层 {@code fields} 或字符串数组。列表接口必须两种都认，不能只读顶层 fields，
 * 否则「调整拍摄角度」这类只写在手段里的参数会在对接页被当成没有参数。</p>
 *
 * <p>不负责：填写对照、猜缺省手段、在读路径补参数。</p>
 */
public final class ActionParamSlotsParser {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final Set<String> SOURCE_KINDS = Set.of(
            "USER",
            "INTERACTION",
            "EXECUTOR_MOUNT",
            "SYSTEM",
            "REFERENCE_RESOURCE");

    private ActionParamSlotsParser() {
    }

    /**
     * 动作需要的来源字段已经由平台定义分组；协议对照只能从这些组里选，不允许页面重新给字段分类。
     */
    public record SourceField(String fieldCode, String sourceKind, Object defaultValue, String generator) {
    }

    public record Snapshot(
            List<String> paramSlots,
            Map<String, List<String>> paramSlotsByMeans,
            List<SourceField> paramSources,
            Map<String, List<SourceField>> paramSourcesByMeans
    ) {
    }

    public static Snapshot parse(Object raw) {
        Object value = coerce(raw);
        if (value instanceof Map<?, ?> root) {
            Object methods = root.get("methods");
            if (methods instanceof List<?> methodRows) {
                Map<String, List<String>> byMeans = new LinkedHashMap<>();
                Map<String, List<SourceField>> sourcesByMeans = new LinkedHashMap<>();
                Set<String> merged = new LinkedHashSet<>();
                Map<String, SourceField> mergedSources = new LinkedHashMap<>();
                for (Object rowRaw : methodRows) {
                    if (!(rowRaw instanceof Map<?, ?> row)) {
                        continue;
                    }
                    String means = firstText(row, "executionMeans", "execution_means");
                    Object fieldsRaw = firstPresent(row, "fields", "params", "paramSlots", "param_slots");
                    List<String> fields = readFieldCodes(fieldsRaw);
                    List<SourceField> sources = readSourceFields(fieldsRaw);
                    if (!means.isEmpty()) {
                        String key = means.toUpperCase(Locale.ROOT);
                        byMeans.put(key, unionCodes(byMeans.get(key), fields));
                        sourcesByMeans.put(key, unionSources(sourcesByMeans.get(key), sources));
                    }
                    merged.addAll(fields);
                    for (SourceField source : sources) {
                        mergedSources.putIfAbsent(source.fieldCode(), source);
                    }
                }
                Object rootFields = root.get("fields");
                merged.addAll(readFieldCodes(rootFields));
                for (SourceField source : readSourceFields(rootFields)) {
                    mergedSources.putIfAbsent(source.fieldCode(), source);
                }
                return new Snapshot(
                        List.copyOf(merged),
                        Map.copyOf(byMeans),
                        List.copyOf(mergedSources.values()),
                        Map.copyOf(sourcesByMeans));
            }
        }
        List<SourceField> sources = readSourceFields(value);
        return new Snapshot(readFieldCodes(value), Map.of(), sources, Map.of());
    }

    private static List<String> unionCodes(List<String> left, List<String> right) {
        Set<String> merged = new LinkedHashSet<>();
        if (left != null) {
            merged.addAll(left);
        }
        if (right != null) {
            merged.addAll(right);
        }
        return List.copyOf(merged);
    }

    private static List<SourceField> unionSources(List<SourceField> left, List<SourceField> right) {
        Map<String, SourceField> merged = new LinkedHashMap<>();
        if (left != null) {
            for (SourceField source : left) {
                merged.putIfAbsent(source.fieldCode(), source);
            }
        }
        if (right != null) {
            for (SourceField source : right) {
                merged.putIfAbsent(source.fieldCode(), source);
            }
        }
        return List.copyOf(merged.values());
    }

    private static List<SourceField> readSourceFields(Object raw) {
        Object value = coerce(raw);
        if (value instanceof Map<?, ?> root && root.get("fields") != null) {
            return readSourceFields(root.get("fields"));
        }
        if (!(value instanceof List<?> list)) {
            List<String> fields = readFieldCodes(value);
            return fields.stream().map(code -> new SourceField(code, "USER", null, "")).toList();
        }
        List<SourceField> out = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Map<?, ?> row) {
                String code = firstFieldCode(row);
                if (code == null) {
                    continue;
                }
                String sourceKind = normalizeSourceKind(firstText(row, "valueSource", "value_source"));
                Object defaultValue = firstPresent(row, "defaultValue", "default_value", "value");
                String generator = firstText(row, "generator").toUpperCase(Locale.ROOT);
                out.add(new SourceField(code, sourceKind, defaultValue, generator));
                continue;
            }
            if (item != null && !String.valueOf(item).trim().isEmpty()) {
                out.add(new SourceField(String.valueOf(item).trim(), "USER", null, ""));
            }
        }
        return List.copyOf(out);
    }

    private static String normalizeSourceKind(String raw) {
        String sourceKind = raw.toUpperCase(Locale.ROOT);
        return SOURCE_KINDS.contains(sourceKind) ? sourceKind : "USER";
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
