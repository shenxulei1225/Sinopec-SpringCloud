package cn.cheers.x.inspection.task.service.execution.steptree;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 读检查项上按巡检方式分的检查方法。
 * <p>与任务页同一条读法：正式包是 { methods: [{ methodKey, stepTree }] }；
 * 历史包仍可读 { executionMeans, actionTree }。
 * <p>树上的 actionId 是动作库编码，不是数字主键。
 * <p>禁止：只认已退役的旧列旧形状；缺档时猜别的手段；把整棵任务步骤图当成检查方法。
 */
public final class InspectionMethodSnapshotParser {

    private InspectionMethodSnapshotParser() {
    }

    /**
     * 取出该巡检方式一档的动作步骤。没有这一档或树上没有可执行步 → 空列表，不猜别档。
     */
    public static List<TaskStepTreeGenerator.MethodAction> actionsForMeans(
            Object raw, String means, ObjectMapper objectMapper
    ) {
        Object value = unwrap(raw, objectMapper);
        if (!(value instanceof Map<?, ?> root)) {
            return List.of();
        }
        Object methodsRaw = root.get("methods");
        if (!(methodsRaw instanceof List<?> methods)) {
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
            return parseTree(first(method, "stepTree", "actionTree"));
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private static List<TaskStepTreeGenerator.MethodAction> parseTree(Object raw) {
        if (!(raw instanceof List<?> nodes)) {
            return List.of();
        }
        List<TaskStepTreeGenerator.MethodAction> actions = new ArrayList<>();
        for (Object node : nodes) {
            if (!(node instanceof Map<?, ?> row)) {
                continue;
            }
            collectActions((Map<String, Object>) row, actions);
        }
        return actions;
    }

    @SuppressWarnings("unchecked")
    private static void collectActions(Map<String, Object> row, List<TaskStepTreeGenerator.MethodAction> actions) {
        if (isExecutableStep(row)) {
            Object actionRef = first(row, "refId", "actionId", "id");
            Long refId = idOf(actionRef);
            String refCode = text(first(row, "refCode", "actionCode", "code"));
            if (refCode.isBlank() && actionRef instanceof String && refId == null) {
                refCode = text(actionRef);
            }
            String title = text(first(row, "stepTitle", "title", "name", "label"));
            String sourceNodeKey = text(first(row, "nodeKey", "key"));
            if (refId != null || !refCode.isBlank()) {
                actions.add(new TaskStepTreeGenerator.MethodAction(
                        refId, refCode.isBlank() ? null : refCode, title, Map.of(),
                        sourceNodeKey.isBlank() ? null : sourceNodeKey, slotsOf(row.get("paramSlots"))));
            }
        }
        Object children = first(row, "children", "nodes");
        if (children instanceof List<?> list) {
            for (Object child : list) {
                if (child instanceof Map<?, ?> childRow) {
                    collectActions((Map<String, Object>) childRow, actions);
                }
            }
        }
    }

    private static boolean isExecutableStep(Map<String, Object> row) {
        String nodeType = text(row.get("nodeType")).toUpperCase();
        return nodeType.isBlank() || "STEP".equals(nodeType);
    }

    private static Object unwrap(Object raw, ObjectMapper objectMapper) {
        if (raw == null) {
            return null;
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

    /**
     * 检查方法步骤上声明的参数槽。到达位置可能是 location_ref，也可能是选择位置字段编码。
     */
    private static List<String> slotsOf(Object raw) {
        if (!(raw instanceof List<?> list)) {
            return List.of();
        }
        List<String> slots = new ArrayList<>();
        for (Object row : list) {
            String slot = text(row);
            if (!slot.isBlank()) {
                slots.add(slot);
            }
        }
        return slots;
    }
}
