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
     * 只按这次检查项对上条目，再按步骤节点键（或动作识别码）取参数袋。
     * <p>对不上或袋是空的 → 空袋，由对照填包暴露缺参。禁止拿别的检查项参数顶上。
     */
    public Map<String, Object> paramsFor(Long inspectionItemId, String nodeKey, String refCode) {
        if (inspectionItemId == null) {
            return Map.of();
        }
        for (Entry entry : entries) {
            if (inspectionItemId.equals(entry.subjectId())) {
                return entry.paramsAt(nodeKey, refCode);
            }
        }
        return Map.of();
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
        Long subjectId = idOf(row.get("subjectId"));
        Object paramsRaw = row.get("paramsByNode");
        if (!(paramsRaw instanceof Map<?, ?> paramsMap)) {
            return new Entry(subjectId, Map.of());
        }
        Map<String, Map<String, Object>> paramsByNode = new LinkedHashMap<>();
        for (Map.Entry<?, ?> item : paramsMap.entrySet()) {
            String key = item.getKey() == null ? "" : String.valueOf(item.getKey()).trim();
            if (key.isBlank() || !(item.getValue() instanceof Map<?, ?> bag)) {
                continue;
            }
            paramsByNode.put(key, new LinkedHashMap<>((Map<String, Object>) bag));
        }
        return new Entry(subjectId, paramsByNode);
    }

    private static Long idOf(Object raw) {
        if (raw instanceof Number number && number.longValue() > 0) {
            return number.longValue();
        }
        return null;
    }

    private record Entry(Long subjectId, Map<String, Map<String, Object>> paramsByNode) {
        Map<String, Object> paramsAt(String nodeKey, String refCode) {
            if (nodeKey != null && !nodeKey.isBlank() && paramsByNode.containsKey(nodeKey)) {
                return paramsByNode.get(nodeKey);
            }
            if (refCode != null && !refCode.isBlank() && paramsByNode.containsKey(refCode)) {
                return paramsByNode.get(refCode);
            }
            return Map.of();
        }
    }
}
