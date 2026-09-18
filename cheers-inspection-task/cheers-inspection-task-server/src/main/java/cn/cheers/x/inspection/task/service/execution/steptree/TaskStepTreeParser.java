package cn.cheers.x.inspection.task.service.execution.steptree;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 读总任务上创建时已算好的步骤图 {@code step_tree_json}。
 * <p>只认步骤节点快照（version + nodes）。空数组、旧动作树数组、非法 JSON → 空树，不猜历史形态。
 * <p>禁止：把检查项方法树当成任务步骤图。
 */
public final class TaskStepTreeParser {

    private TaskStepTreeParser() {
    }

    /**
     * 解析步骤图。缺列、空对象、对不上形态 → 空列表（缺口由开跑报出来）。
     */
    public static List<TaskStepNode> parse(Object raw, ObjectMapper objectMapper) {
        Object value = unwrap(raw, objectMapper);
        if (!(value instanceof Map<?, ?> obj)) {
            return List.of();
        }
        Object nodesRaw = obj.get("nodes");
        if (!(nodesRaw instanceof List<?> list)) {
            return List.of();
        }
        List<TaskStepNode> nodes = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            TaskStepNode node = parseNode(list.get(i), i);
            if (node != null) {
                nodes.add(node);
            }
        }
        nodes.sort(Comparator.comparingInt(TaskStepNode::order));
        List<TaskStepNode> ordered = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            TaskStepNode node = nodes.get(i);
            ordered.add(new TaskStepNode(
                    node.nodeKey(),
                    i + 1,
                    node.parentNodeKey(),
                    node.hangTypeCode(),
                    node.refCode(),
                    node.refId(),
                    node.title(),
                    node.params()
            ));
        }
        return List.copyOf(ordered);
    }

    private static Object unwrap(Object raw, ObjectMapper objectMapper) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof String text) {
            String trimmed = text.trim();
            if (trimmed.isEmpty() || "[]".equals(trimmed) || "{}".equals(trimmed)) {
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
    private static TaskStepNode parseNode(Object raw, int index) {
        if (!(raw instanceof Map<?, ?> row)) {
            return null;
        }
        String hangTypeCode = textOf(row.get("hangTypeCode"));
        String refCode = textOf(row.get("refCode"));
        Long refId = idOf(row.get("refId"));
        if (hangTypeCode.isBlank() || (refCode.isBlank() && refId == null)) {
            return null;
        }
        int order = numberOf(row.get("order"), index + 1);
        String nodeKey = textOf(row.get("nodeKey"));
        if (nodeKey.isBlank()) {
            nodeKey = "n" + (index + 1);
        }
        return new TaskStepNode(
                nodeKey,
                order,
                textOf(row.get("parentNodeKey")),
                hangTypeCode,
                refCode.isBlank() ? null : refCode,
                refId,
                textOf(row.get("title")),
                paramsOf(row.get("params"))
        );
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> paramsOf(Object raw) {
        if (raw instanceof Map<?, ?> map) {
            return new LinkedHashMap<>((Map<String, Object>) map);
        }
        return Map.of();
    }

    private static String textOf(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
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

    private static int numberOf(Object raw, int fallback) {
        if (raw instanceof Number number) {
            return number.intValue();
        }
        return fallback;
    }
}
