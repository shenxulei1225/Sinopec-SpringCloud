package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 智能编排（previewOrchestration）前，把检查项该档手段的步骤串成总任务步骤图。
 * <p>用户看树的顺序：起点 → 巡检对象（下挂检查项）→ 检查项下挂动作 → 终点。头尾是已拆开的库内子动作。
 * <p>草稿保存不算树。树上不写被巡检设备实参；头尾不挂检查项。
 * <p>禁止：saveRoute 写树；从固定表拼树；开跑时再生成；人工硬挂机器人/无人机头尾；用「到达点」冒充巡检对象。
 */
public final class TaskStepTreeGenerator {

    public static final String MEANS_MANUAL = "MANUAL";
    public static final String MEANS_UAV = "UAV";
    public static final String MEANS_ROBOT = "ROBOT";
    public static final String MEANS_CAMERA = "FIXED_CAMERA";

    private TaskStepTreeGenerator() {
    }

    /**
     * @param means              巡检方式
     * @param selected           勾选顺序：被巡检设备 + 该项下勾选的检查项
     * @param equipmentOrder     路径停靠对应的设备顺序；摄像机或未规划时用勾选顺序
     * @param methodActions      检查项在该手段档上的动作
     * @param headActions        机器人/无人机头动作；人工/摄像机必须空
     * @param tailActions        机器人/无人机尾动作；人工/摄像机必须空
     */
    public static Map<String, Object> generate(
            String means,
            List<SelectedEquipment> selected,
            List<Long> equipmentOrder,
            Map<Long, List<MethodAction>> methodActions,
            List<MethodAction> headActions,
            List<MethodAction> tailActions
    ) {
        return generate(means, selected, equipmentOrder, methodActions, headActions, tailActions, Map.of());
    }

    /**
     * 缺本档方法的检查项跳过，不把已有方法的项整棵打回。
     * 全部勾选项都缺这一档，才报出哪台设备、哪一项、缺哪一档。
     * 禁止再用一句「检查项没有配置本巡检方式的检查方法」糊过去。
     */
    public static Map<String, Object> generate(
            String means,
            List<SelectedEquipment> selected,
            List<Long> equipmentOrder,
            Map<Long, List<MethodAction>> methodActions,
            List<MethodAction> headActions,
            List<MethodAction> tailActions,
            Map<Long, String> itemNames
    ) {
        return generate(
                means, selected, equipmentOrder, methodActions, headActions, tailActions,
                null, "起点", null, "终点", itemNames);
    }

    /**
     * 智能编排前生成步骤图，并把任务创建选定的起终点写到树根。
     * 无人机起飞/降落、机器人返回充电的点位从这两端来，不从设备检查配置猜。
     */
    public static Map<String, Object> generate(
            String means,
            List<SelectedEquipment> selected,
            List<Long> equipmentOrder,
            Map<Long, List<MethodAction>> methodActions,
            List<MethodAction> headActions,
            List<MethodAction> tailActions,
            String startStopId,
            String endStopId
    ) {
        return generate(
                means, selected, equipmentOrder, methodActions, headActions, tailActions,
                startStopId, endStopId, Map.of());
    }

    public static Map<String, Object> generate(
            String means,
            List<SelectedEquipment> selected,
            List<Long> equipmentOrder,
            Map<Long, List<MethodAction>> methodActions,
            List<MethodAction> headActions,
            List<MethodAction> tailActions,
            String startStopId,
            String endStopId,
            Map<Long, String> itemNames
    ) {
        return generate(
                means, selected, equipmentOrder, methodActions, headActions, tailActions,
                startStopId, "起点", endStopId, "终点", itemNames);
    }

    public static Map<String, Object> generate(
            String means,
            List<SelectedEquipment> selected,
            List<Long> equipmentOrder,
            Map<Long, List<MethodAction>> methodActions,
            List<MethodAction> headActions,
            List<MethodAction> tailActions,
            String startStopId,
            String startTitle,
            String endStopId,
            String endTitle,
            Map<Long, String> itemNames
    ) {
        if (selected == null || selected.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("任务草稿还没有勾选被巡检设备和检查项");
        }
        String mode = requireMeans(means);
        if ((MEANS_MANUAL.equals(mode) || MEANS_CAMERA.equals(mode))
                && ((headActions != null && !headActions.isEmpty())
                || (tailActions != null && !tailActions.isEmpty()))) {
            throw ServiceExceptionUtil.invalidParamException("人工和摄像机不挂机器人或无人机的头尾动作");
        }
        List<Long> order = resolveEquipmentOrder(selected, equipmentOrder);
        List<Map<String, Object>> nodes = new ArrayList<>();
        List<String> missingMethods = new ArrayList<>();
        int seq = 1;
        int emittedItems = 0;
        seq = emitAnchor(nodes, seq, "stop-start", startStopId, startTitle, defaultList(headActions), "head");
        for (Long equipmentId : order) {
            SelectedEquipment equipment = findSelected(selected, equipmentId);
            if (equipment.itemIds().isEmpty()) {
                throw ServiceExceptionUtil.invalidParamException("被巡检设备没有勾选检查项");
            }
            boolean anyReady = false;
            for (Long itemId : equipment.itemIds()) {
                List<MethodAction> actions = methodActions == null ? null : methodActions.get(itemId);
                if (actions != null && !actions.isEmpty()) {
                    anyReady = true;
                    continue;
                }
                missingMethods.add(missingMethodMessage(equipment, itemId, itemNames, mode));
            }
            if (!anyReady) {
                continue;
            }
            String objectKey = "stop-" + equipmentId;
            nodes.add(visitGroupNode(objectKey, seq++, equipment));
            for (Long itemId : equipment.itemIds()) {
                List<MethodAction> actions = methodActions == null ? null : methodActions.get(itemId);
                if (actions == null || actions.isEmpty()) {
                    continue;
                }
                String itemKey = "item-" + itemId + "-" + equipmentId;
                nodes.add(node(itemKey, seq++, objectKey, TaskStepNode.HANG_INSPECTION_ITEM,
                        null, itemId, itemTitle(itemId, itemNames)));
                seq = emitActions(nodes, seq, itemKey, actions, "act-" + itemId);
                emittedItems++;
            }
        }
        if (emittedItems == 0) {
            throw ServiceExceptionUtil.invalidParamException(
                    missingMethods.isEmpty()
                            ? "检查项还没有配置本巡检方式的检查方法"
                            : String.join("；", missingMethods));
        }
        seq = emitAnchor(nodes, seq, "stop-end", endStopId, endTitle, defaultList(tailActions), "tail");
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("version", 1);
        tree.put("nodes", List.copyOf(nodes));
        if (startStopId != null && !startStopId.isBlank()) {
            tree.put("startStopId", startStopId.trim());
        }
        if (endStopId != null && !endStopId.isBlank()) {
            tree.put("endStopId", endStopId.trim());
        }
        return tree;
    }

    private static String requireMeans(String means) {
        if (means == null || means.isBlank()) {
            throw ServiceExceptionUtil.invalidParamException("还没有选择巡检方式");
        }
        String mode = means.trim();
        if (!MEANS_MANUAL.equals(mode) && !MEANS_UAV.equals(mode)
                && !MEANS_ROBOT.equals(mode) && !MEANS_CAMERA.equals(mode)) {
            throw ServiceExceptionUtil.invalidParamException("巡检方式无法识别");
        }
        return mode;
    }

    private static List<Long> resolveEquipmentOrder(
            List<SelectedEquipment> selected, List<Long> equipmentOrder
    ) {
        if (equipmentOrder != null && !equipmentOrder.isEmpty()) {
            return List.copyOf(equipmentOrder);
        }
        List<Long> order = new ArrayList<>();
        for (SelectedEquipment equipment : selected) {
            order.add(equipment.equipmentId());
        }
        return order;
    }

    private static SelectedEquipment findSelected(List<SelectedEquipment> selected, Long equipmentId) {
        for (SelectedEquipment equipment : selected) {
            if (Objects.equals(equipment.equipmentId(), equipmentId)) {
                return equipment;
            }
        }
        throw ServiceExceptionUtil.invalidParamException("路径上的设备不在本次勾选里");
    }

    private static int emitAnchor(
            List<Map<String, Object>> nodes,
            int seq,
            String key,
            String stopId,
            String title,
            List<MethodAction> actions,
            String actionPrefix
    ) {
        if ((stopId == null || stopId.isBlank()) && actions.isEmpty()) {
            return seq;
        }
        nodes.add(anchorNode(key, seq++, stopId, title));
        return emitActions(nodes, seq, key, actions, actionPrefix);
    }

    private static Map<String, Object> anchorNode(String key, int order, String stopId, String title) {
        if (stopId != null && !stopId.isBlank()) {
            return node(key, order, null, TaskStepNode.HANG_POINT, stopId.trim(), null,
                    title == null || title.isBlank() ? stopId.trim() : title);
        }
        return node(key, order, null, TaskStepNode.HANG_POINT, key, null,
                title == null || title.isBlank() ? key : title);
    }

    /**
     * 巡检对象成组：挂设备，标题写对象中文名。点位只出现在起点/终点，不顶替对象。
     */
    private static Map<String, Object> visitGroupNode(String key, int order, SelectedEquipment equipment) {
        return node(key, order, null, TaskStepNode.HANG_EQUIPMENT, null, equipment.equipmentId(),
                objectTitle(equipment));
    }

    private static String objectTitle(SelectedEquipment equipment) {
        if (equipment.equipmentName() != null && !equipment.equipmentName().isBlank()) {
            return equipment.equipmentName().trim();
        }
        throw ServiceExceptionUtil.invalidParamException("巡检对象缺少名称");
    }

    private static String itemTitle(Long itemId, Map<Long, String> itemNames) {
        if (itemNames != null && itemId != null) {
            String name = itemNames.get(itemId);
            if (name != null && !name.isBlank()) {
                return name.trim();
            }
        }
        throw ServiceExceptionUtil.invalidParamException(
                itemId == null ? "检查项缺少名称" : "检查项 #" + itemId + " 缺少名称");
    }

    private static int emitActions(
            List<Map<String, Object>> nodes,
            int seq,
            String parentKey,
            List<MethodAction> actions,
            String keyPrefix
    ) {
        int next = seq;
        for (int i = 0; i < actions.size(); i++) {
            MethodAction action = actions.get(i);
            if (action.refId() == null && (action.refCode() == null || action.refCode().isBlank())) {
                throw ServiceExceptionUtil.invalidParamException("检查方法步骤对不上动作库");
            }
            String key = keyPrefix + "-" + (i + 1);
            Map<String, Object> seeded = new LinkedHashMap<>(action.params());
            for (String slot : action.declaredSlots()) {
                seeded.putIfAbsent(slot, "");
            }
            Map<String, Object> row = node(key, next++, parentKey, TaskStepNode.HANG_ACTION,
                    action.refCode(), action.refId(), action.title(), seeded);
            if (action.sourceNodeKey() != null) {
                row.put("sourceNodeKey", action.sourceNodeKey());
            }
            nodes.add(row);
        }
        return next;
    }

    private static Map<String, Object> node(
            String nodeKey, int order, String parentKey,
            String hangType, String refCode, Long refId, String title
    ) {
        return node(nodeKey, order, parentKey, hangType, refCode, refId, title, Map.of());
    }

    private static Map<String, Object> node(
            String nodeKey, int order, String parentKey,
            String hangType, String refCode, Long refId, String title,
            Map<String, Object> params
    ) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("nodeKey", nodeKey);
        row.put("order", order);
        if (parentKey != null) {
            row.put("parentNodeKey", parentKey);
        }
        row.put("hangTypeCode", hangType);
        if (refCode != null && !refCode.isBlank()) {
            row.put("refCode", refCode);
        }
        if (refId != null) {
            row.put("refId", refId);
        }
        row.put("title", title == null ? "" : title);
        if (params != null && !params.isEmpty()) {
            row.put("params", new LinkedHashMap<>(params));
        }
        return row;
    }

    private static List<MethodAction> defaultList(List<MethodAction> actions) {
        return actions == null ? List.of() : actions;
    }

    private static String meansLabel(String means) {
        if (MEANS_MANUAL.equals(means)) {
            return "人工";
        }
        if (MEANS_UAV.equals(means)) {
            return "无人机";
        }
        if (MEANS_ROBOT.equals(means)) {
            return "机器人";
        }
        if (MEANS_CAMERA.equals(means)) {
            return "固定摄像机";
        }
        return "当前巡检方式";
    }

    /**
     * 缺方法必须点出设备、检查项、巡检方式，用户才能去标准检查库补这一档。
     */
    private static String missingMethodMessage(
            SelectedEquipment equipment,
            Long itemId,
            Map<Long, String> itemNames,
            String means
    ) {
        String device = equipment.equipmentName() == null || equipment.equipmentName().isBlank()
                ? "设备 #" + equipment.equipmentId()
                : "「" + equipment.equipmentName().trim() + "」";
        String itemName = itemNames == null ? null : itemNames.get(itemId);
        String item = itemName == null || itemName.isBlank()
                ? "检查项 #" + itemId
                : "检查项「" + itemName.trim() + "」";
        return device + "的" + item + "还没有配置「" + meansLabel(means) + "」检查方法";
    }

    public record SelectedEquipment(
            Long equipmentId, List<Long> itemIds, String equipmentName, String stopId, String stopTitle
    ) {
        public SelectedEquipment(Long equipmentId, List<Long> itemIds) {
            this(equipmentId, itemIds, null, null, null);
        }

        public SelectedEquipment(Long equipmentId, List<Long> itemIds, String equipmentName) {
            this(equipmentId, itemIds, equipmentName, null, null);
        }

        public SelectedEquipment {
            if (equipmentId == null) {
                throw ServiceExceptionUtil.invalidParamException("被巡检设备缺少编号");
            }
            itemIds = itemIds == null ? List.of() : List.copyOf(itemIds);
            equipmentName = equipmentName == null || equipmentName.isBlank() ? null : equipmentName.trim();
            stopId = stopId == null || stopId.isBlank() ? null : stopId.trim();
            stopTitle = stopTitle == null || stopTitle.isBlank() ? equipmentName : stopTitle.trim();
        }

        public SelectedEquipment withVisit(String nextStopId, String nextTitle) {
            return new SelectedEquipment(equipmentId, itemIds, equipmentName, nextStopId, nextTitle);
        }
    }

    public record MethodAction(
            Long refId, String refCode, String title, Map<String, Object> params,
            String sourceNodeKey, List<String> declaredSlots
    ) {
        public MethodAction(Long refId, String refCode, String title) {
            this(refId, refCode, title, Map.of(), null, List.of());
        }

        public MethodAction(Long refId, String refCode, String title, Map<String, Object> params) {
            this(refId, refCode, title, params, null, List.of());
        }

        public MethodAction(Long refId, String refCode, String title, Map<String, Object> params, String sourceNodeKey) {
            this(refId, refCode, title, params, sourceNodeKey, List.of());
        }

        public MethodAction {
            params = params == null ? Map.of() : Map.copyOf(params);
            sourceNodeKey = sourceNodeKey == null || sourceNodeKey.isBlank() ? null : sourceNodeKey.trim();
            declaredSlots = declaredSlots == null ? List.of() : List.copyOf(declaredSlots);
        }
    }
}
