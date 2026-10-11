package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 智能编排（previewOrchestration）前，把检查项该档手段的步骤串成总任务步骤图。
 * <p>用户看树的顺序：起点 → 点位（其下一次到达，再挂这个点上的检查项和动作）→ 终点。
 * 没有点位的检查项仍按设备串。头尾是已拆开的库内子动作。
 * <p>草稿保存不算树。树上不写被巡检设备实参；头尾不挂检查项。同一点只放一次带位置参数的到达动作。
 * <p>禁止：路径预览写树；从固定表拼树；开跑时再生成；人工硬挂机器人/无人机头尾；按指令编号把拍照并成一条。
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
                null, "起点", null, "终点", itemNames, Map.of(), Set.of());
    }

    /**
     * 智能编排前生成步骤图，并把任务创建选定的起终点写到树根。
     * 起点和终点写在树根上，给算路和标题用。返回充电、降落点不从这两端猜。
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
                startStopId, "起点", endStopId, "终点", itemNames, Map.of(), Set.of());
    }

    /**
     * @param itemStops     键是「设备编号:检查项编号」，值是该检查项要去的点位编号或业务编码。空则仍按设备串检查项
     * @param locationSlots 动作参数里表示要去的点的槽。带这些槽的动作是到达，同一点只留第一次
     */
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
            Map<Long, String> itemNames,
            Map<String, String> itemStops,
            Set<String> locationSlots
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
        List<ItemPlacement> placements = new ArrayList<>();
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
                    placements.add(new ItemPlacement(
                            equipment, itemId, stopOf(itemStops, equipmentId, itemId), actions));
                    continue;
                }
                missingMethods.add(missingMethodMessage(equipment, itemId, itemNames, mode));
            }
            if (!anyReady) {
                continue;
            }
        }
        emittedItems = placements.size();
        if (hasStop(placements)) {
            seq = emitByPoint(nodes, seq, placements, itemNames, locationSlots);
        } else {
            seq = emitByEquipment(nodes, seq, placements, itemNames);
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

    static String itemStopKey(long equipmentId, long itemId) {
        return equipmentId + ":" + itemId;
    }

    private static String stopOf(Map<String, String> itemStops, Long equipmentId, Long itemId) {
        if (itemStops == null || equipmentId == null || itemId == null) {
            return null;
        }
        String stop = itemStops.get(itemStopKey(equipmentId, itemId));
        return stop == null || stop.isBlank() ? null : stop.trim();
    }

    private static boolean hasStop(List<ItemPlacement> placements) {
        for (ItemPlacement placement : placements) {
            if (placement.stopId() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 沿路径第一次见到的点位排在前面。同一个点上的检查项挂在一起，到达动作只留第一次。
     */
    private static int emitByPoint(
            List<Map<String, Object>> nodes,
            int seq,
            List<ItemPlacement> placements,
            Map<Long, String> itemNames,
            Set<String> locationSlots
    ) {
        Map<String, List<ItemPlacement>> byStop = new LinkedHashMap<>();
        List<ItemPlacement> withoutStop = new ArrayList<>();
        for (ItemPlacement placement : placements) {
            if (placement.stopId() == null) {
                withoutStop.add(placement);
            } else {
                byStop.computeIfAbsent(placement.stopId(), key -> new ArrayList<>()).add(placement);
            }
        }
        for (Map.Entry<String, List<ItemPlacement>> entry : byStop.entrySet()) {
            String pointKey = pointKey(entry.getKey());
            nodes.add(node(pointKey, seq++, null, TaskStepNode.HANG_POINT,
                    entry.getKey(), null, entry.getKey()));
            seq = emitPointGroup(nodes, seq, pointKey, entry.getValue(), itemNames, locationSlots);
        }
        return emitByEquipment(nodes, seq, withoutStop, itemNames);
    }

    private static int emitPointGroup(
            List<Map<String, Object>> nodes,
            int seq,
            String pointKey,
            List<ItemPlacement> group,
            Map<Long, String> itemNames,
            Set<String> locationSlots
    ) {
        MethodAction move = null;
        Long moveEquipmentId = null;
        Long moveItemId = null;
        int moveIndex = 0;
        List<ItemRest> rests = new ArrayList<>();
        for (ItemPlacement placement : group) {
            List<MethodAction> rest = new ArrayList<>();
            List<Integer> indexes = new ArrayList<>();
            List<MethodAction> actions = placement.actions();
            for (int i = 0; i < actions.size(); i++) {
                MethodAction action = actions.get(i);
                if (isLocationAction(action, locationSlots)) {
                    if (move == null) {
                        move = action;
                        moveEquipmentId = placement.equipment().equipmentId();
                        moveItemId = placement.itemId();
                        moveIndex = i + 1;
                    }
                    continue;
                }
                rest.add(action);
                indexes.add(i + 1);
            }
            rests.add(new ItemRest(placement, rest, indexes));
        }
        if (move != null) {
            seq = emitOneAction(nodes, seq, pointKey, move, actionKeyPrefix(moveItemId, moveEquipmentId), moveIndex,
                    moveEquipmentId, moveItemId);
        }
        for (ItemRest rest : rests) {
            SelectedEquipment equipment = rest.placement().equipment();
            String itemKey = "item-" + rest.placement().itemId() + "-" + equipment.equipmentId();
            Map<String, Object> item = node(itemKey, seq++, pointKey, TaskStepNode.HANG_INSPECTION_ITEM,
                    null, rest.placement().itemId(), itemTitle(rest.placement().itemId(), itemNames));
            item.put("equipmentId", equipment.equipmentId());
            nodes.add(item);
            seq = emitIndexedActions(nodes, seq, itemKey, rest.actions(), rest.indexes(),
                    actionKeyPrefix(rest.placement().itemId(), equipment.equipmentId()),
                    equipment.equipmentId(), rest.placement().itemId());
        }
        return seq;
    }

    private static int emitByEquipment(
            List<Map<String, Object>> nodes,
            int seq,
            List<ItemPlacement> placements,
            Map<Long, String> itemNames
    ) {
        Long currentEquipment = null;
        String objectKey = null;
        for (ItemPlacement placement : placements) {
            SelectedEquipment equipment = placement.equipment();
            if (!Objects.equals(currentEquipment, equipment.equipmentId())) {
                currentEquipment = equipment.equipmentId();
                objectKey = "stop-" + equipment.equipmentId();
                nodes.add(visitGroupNode(objectKey, seq++, equipment));
            }
            String itemKey = "item-" + placement.itemId() + "-" + equipment.equipmentId();
            Map<String, Object> item = node(itemKey, seq++, objectKey, TaskStepNode.HANG_INSPECTION_ITEM,
                    null, placement.itemId(), itemTitle(placement.itemId(), itemNames));
            item.put("equipmentId", equipment.equipmentId());
            nodes.add(item);
            List<Integer> indexes = new ArrayList<>();
            for (int i = 0; i < placement.actions().size(); i++) {
                indexes.add(i + 1);
            }
            seq = emitIndexedActions(nodes, seq, itemKey, placement.actions(), indexes,
                    actionKeyPrefix(placement.itemId(), equipment.equipmentId()),
                    equipment.equipmentId(), placement.itemId());
        }
        return seq;
    }

    /**
     * 同一检查项挂在多台设备上时，步骤编号带上设备编号。
     * 只写检查项编号会在更新任务时撞成同一个步骤编号。
     */
    private static String actionKeyPrefix(Long itemId, Long equipmentId) {
        if (equipmentId == null) {
            return "act-" + itemId;
        }
        return "act-" + itemId + "-" + equipmentId;
    }

    private static String pointKey(String stopId) {
        String safe = stopId.replaceAll("[^A-Za-z0-9._-]", "-");
        return "point-" + safe;
    }

    private static boolean isLocationAction(MethodAction action, Set<String> locationSlots) {
        if (locationSlots == null || locationSlots.isEmpty() || action.declaredSlots() == null) {
            return false;
        }
        for (String slot : action.declaredSlots()) {
            if (locationSlots.contains(slot)) {
                return true;
            }
        }
        return false;
    }

    private static int emitOneAction(
            List<Map<String, Object>> nodes,
            int seq,
            String parentKey,
            MethodAction action,
            String keyPrefix,
            int index,
            Long equipmentId,
            Long hostItemId
    ) {
        if (action.refId() == null && (action.refCode() == null || action.refCode().isBlank())) {
            throw ServiceExceptionUtil.invalidParamException("检查方法步骤对不上动作库");
        }
        String key = keyPrefix + "-" + index;
        Map<String, Object> seeded = new LinkedHashMap<>(action.params());
        for (String slot : action.declaredSlots()) {
            seeded.putIfAbsent(slot, "");
        }
        Map<String, Object> row = node(key, seq++, parentKey, TaskStepNode.HANG_ACTION,
                action.refCode(), action.refId(), action.title(), seeded);
        if (action.sourceNodeKey() != null) {
            row.put("sourceNodeKey", action.sourceNodeKey());
        }
        if (!action.declaredSlots().isEmpty()) {
            row.put("declaredSlots", List.copyOf(action.declaredSlots()));
        }
        writeActionOwnership(row, action);
        if (equipmentId != null) {
            row.put("equipmentId", equipmentId);
        }
        if (hostItemId != null) {
            row.put("hostItemId", hostItemId);
        }
        nodes.add(row);
        return seq;
    }

    private static int emitIndexedActions(
            List<Map<String, Object>> nodes,
            int seq,
            String parentKey,
            List<MethodAction> actions,
            List<Integer> indexes,
            String keyPrefix,
            Long equipmentId,
            Long hostItemId
    ) {
        for (int i = 0; i < actions.size(); i++) {
            seq = emitOneAction(nodes, seq, parentKey, actions.get(i), keyPrefix, indexes.get(i),
                    equipmentId, hostItemId);
        }
        return seq;
    }

    private record ItemPlacement(
            SelectedEquipment equipment, Long itemId, String stopId, List<MethodAction> actions
    ) {
    }

    private record ItemRest(ItemPlacement placement, List<MethodAction> actions, List<Integer> indexes) {
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
            if (!action.declaredSlots().isEmpty()) {
                row.put("declaredSlots", List.copyOf(action.declaredSlots()));
            }
            writeActionOwnership(row, action);
            nodes.add(row);
        }
        return next;
    }

    /**
     * 动作归属在生成任务时冻结。执行、会话和分析分流只读这份快照，不按标题重新判断。
     */
    private static void writeActionOwnership(Map<String, Object> row, MethodAction action) {
        if (action.actionDomain() != null) {
            row.put("actionDomain", action.actionDomain());
        }
        if (action.analysisCapabilityId() != null) {
            row.put("analysisCapabilityId", action.analysisCapabilityId());
        }
        row.put("required", action.required());
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
            String sourceNodeKey, List<String> declaredSlots,
            String actionDomain, Long analysisCapabilityId,
            boolean required
    ) {
        public MethodAction(Long refId, String refCode, String title) {
            this(refId, refCode, title, Map.of(), null, List.of(), null, null, true);
        }

        public MethodAction(Long refId, String refCode, String title, Map<String, Object> params) {
            this(refId, refCode, title, params, null, List.of(), null, null, true);
        }

        public MethodAction(Long refId, String refCode, String title, Map<String, Object> params, String sourceNodeKey) {
            this(refId, refCode, title, params, sourceNodeKey, List.of(), null, null, true);
        }

        public MethodAction(
                Long refId, String refCode, String title, Map<String, Object> params,
                String sourceNodeKey, List<String> declaredSlots
        ) {
            this(refId, refCode, title, params, sourceNodeKey, declaredSlots, null, null, true);
        }

        public MethodAction(
                Long refId, String refCode, String title, Map<String, Object> params,
                String sourceNodeKey, List<String> declaredSlots,
                String actionDomain, Long analysisCapabilityId
        ) {
            this(refId, refCode, title, params, sourceNodeKey, declaredSlots,
                    actionDomain, analysisCapabilityId, true);
        }

        public MethodAction {
            params = params == null ? Map.of() : Map.copyOf(params);
            sourceNodeKey = sourceNodeKey == null || sourceNodeKey.isBlank() ? null : sourceNodeKey.trim();
            declaredSlots = declaredSlots == null ? List.of() : List.copyOf(declaredSlots);
            actionDomain = actionDomain == null || actionDomain.isBlank() ? null : actionDomain.trim();
        }
    }
}
