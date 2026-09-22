package cn.cheers.x.inspection.task.service.execution.steptree;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备上的「设备 × 检查项」实参包。
 * <p>开跑只取已写入的 {@code paramsByNode}，不猜默认点位/角度。
 * <p>禁止：按旧 {@code targetType=sop} 再补一遍；缺条目假装有值。
 */
public final class HostSopParamPack {

    private final List<Entry> entries;

    private HostSopParamPack(List<Entry> entries) {
        this.entries = List.copyOf(entries);
    }

    public static HostSopParamPack empty() {
        return new HostSopParamPack(List.of());
    }

    public static HostSopParamPack parse(Object raw, ObjectMapper objectMapper) {
        Object value = unwrap(raw, objectMapper);
        if (!(value instanceof Map<?, ?> obj)) {
            return empty();
        }
        Object entriesRaw = obj.get("entries");
        if (!(entriesRaw instanceof List<?> list)) {
            return empty();
        }
        List<Entry> entries = new ArrayList<>();
        for (Object item : list) {
            Entry entry = parseEntry(item);
            if (entry != null) {
                entries.add(entry);
            }
        }
        return new HostSopParamPack(entries);
    }

    /**
     * 该检查项、当前巡检方式参数袋里第一次出现的到达位置。没有 → null，不猜别的项或别的方式。
     */
    public String firstLocationRef(Long inspectionItemId) {
        return firstLocationRef(inspectionItemId, null);
    }

    public String firstLocationRef(Long inspectionItemId, String means) {
        if (inspectionItemId == null) {
            return null;
        }
        for (Entry entry : entries) {
            if (!inspectionItemId.equals(entry.subjectId()) || !meansMatches(entry.dimensionValue(), means)) {
                continue;
            }
            String location = entry.firstLocationRef();
            if (location != null) {
                return location;
            }
        }
        return null;
    }

    /**
     * 这台设备参数包里第一次出现的到达位置。没有 → null。
     */
    public String firstLocationRef() {
        for (Entry entry : entries) {
            String location = entry.firstLocationRef();
            if (location != null) {
                return location;
            }
        }
        return null;
    }

    /**
     * 只按这次检查项对上条目，再按步骤节点键（或动作识别码）取参数袋。
     * <p>对不上或袋是空的 → 空袋，由对照填包暴露缺参。禁止拿别的检查项参数顶上。
     */
    public Map<String, Object> paramsFor(Long inspectionItemId, String nodeKey, String refCode) {
        return paramsFor(inspectionItemId, nodeKey, refCode, null);
    }

    /**
     * 带巡检方式：只读该方式条目。无人机袋不能顶机器人的到达点或拍照角。
     */
    public Map<String, Object> paramsFor(Long inspectionItemId, String nodeKey, String refCode, String means) {
        if (inspectionItemId == null) {
            return Map.of();
        }
        Map<String, Object> firstMatch = null;
        for (Entry entry : entries) {
            if (!inspectionItemId.equals(entry.subjectId()) || !meansMatches(entry.dimensionValue(), means)) {
                continue;
            }
            Map<String, Object> bag = entry.paramsAt(nodeKey, refCode);
            if (!bag.isEmpty()) {
                return bag;
            }
            if (firstMatch == null) {
                firstMatch = bag;
            }
        }
        return firstMatch == null ? Map.of() : firstMatch;
    }

    private static boolean meansMatches(String entryMeans, String wanted) {
        if (wanted == null || wanted.isBlank() || entryMeans == null || entryMeans.isBlank()) {
            return true;
        }
        return wanted.trim().equalsIgnoreCase(entryMeans.trim());
    }

    private static Object unwrap(Object raw, ObjectMapper objectMapper) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof String text) {
            String trimmed = text.trim();
            if (trimmed.isEmpty() || "{}".equals(trimmed)) {
                return null;
            }
            try {
                return objectMapper.readValue(trimmed, new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception ex) {
                return null;
            }
        }
        return raw;
    }

    @SuppressWarnings("unchecked")
    private static Entry parseEntry(Object raw) {
        if (!(raw instanceof Map<?, ?> row)) {
            return null;
        }
        Long subjectId = idOf(first(row.get("subjectId"), row.get("targetId")));
        String dimensionValue = textOf(row.get("dimensionValue"));
        Object paramsRaw = row.get("paramsByNode");
        if (!(paramsRaw instanceof Map<?, ?> paramsMap)) {
            return new Entry(subjectId, dimensionValue, Map.of());
        }
        Map<String, Map<String, Object>> paramsByNode = new LinkedHashMap<>();
        for (Map.Entry<?, ?> item : paramsMap.entrySet()) {
            String key = item.getKey() == null ? "" : String.valueOf(item.getKey()).trim();
            if (key.isBlank() || !(item.getValue() instanceof Map<?, ?> bag)) {
                continue;
            }
            paramsByNode.put(key, new LinkedHashMap<>((Map<String, Object>) bag));
        }
        return new Entry(subjectId, dimensionValue, paramsByNode);
    }

    private static String textOf(Object raw) {
        if (raw == null) {
            return null;
        }
        String text = String.valueOf(raw).trim();
        return text.isEmpty() ? null : text;
    }

    private static Object first(Object preferred, Object fallback) {
        return preferred != null ? preferred : fallback;
    }

    private static Long idOf(Object raw) {
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

    private record Entry(Long subjectId, String dimensionValue, Map<String, Map<String, Object>> paramsByNode) {
        Map<String, Object> paramsAt(String nodeKey, String refCode) {
            if (nodeKey != null && !nodeKey.isBlank() && paramsByNode.containsKey(nodeKey)) {
                return paramsByNode.get(nodeKey);
            }
            if (refCode != null && !refCode.isBlank() && paramsByNode.containsKey(refCode)) {
                return paramsByNode.get(refCode);
            }
            return Map.of();
        }

        private static final String SELECT_LOCATION = "F-e49f76bdca3e4e1393e8e2f1cc0e7867";

        String firstLocationRef() {
            for (Map<String, Object> bag : paramsByNode.values()) {
                String location = locationText(bag.get("location_ref"));
                if (location == null) {
                    location = locationText(bag.get(SELECT_LOCATION));
                }
                if (location != null) {
                    return location;
                }
            }
            return null;
        }

        private static String locationText(Object raw) {
            if (raw == null) {
                return null;
            }
            if (raw instanceof Map<?, ?> obj) {
                Object code = obj.get("code");
                if (code != null && !String.valueOf(code).isBlank()) {
                    return String.valueOf(code).trim();
                }
                Object id = obj.get("id");
                if (id != null && !String.valueOf(id).isBlank() && !"null".equalsIgnoreCase(String.valueOf(id))) {
                    return String.valueOf(id).trim();
                }
                return null;
            }
            String text = String.valueOf(raw).trim();
            return text.isEmpty() || "null".equalsIgnoreCase(text) ? null : text;
        }
    }
}
