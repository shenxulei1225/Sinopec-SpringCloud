package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.task.ActionDurationParamSupport;
import cn.cheers.x.inspection.task.service.task.ActionParamSlotsSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 生成总任务步骤图并写回。第 3 步无冲突或智能编排时由门面调用，本服务不负责占窗。
 * <p>树上顺序：起点与头动作 → 巡检对象（下挂检查项中文名）→ 检查项下挂动作 → 终点与尾动作。头尾拆成动作库子动作，标题写中文名。
 * <p>写入时把检查方法槽、动作库该档槽、该设备检查参数包（到达点写成点位名称、拍照角、焦距等）和动作库 {@code action_duration} 写进动作节点；对象/检查项写合计分钟。槽在动作里有、设备没填值仍列出该参数，不写成无参数。开跑仍读宿主包，不把树上参数当调度权威。
 * <p>检查方法与任务页同一条读法：先读检查项 {@code step_tree_json}，按任务已选巡检方式取该档。
 * 旧 {@code action_tree_json} 只读历史，不编一棵树。
 * <p>禁止：saveRoute 写步骤图；草稿保存时生成；开跑时再算树；人工硬挂机器人头尾；把读空旧列说成没配方法。
 */
@Service
@RequiredArgsConstructor
public class TaskStepTreeGenerateService {

    static final String ITEM_TYPE = "inspection_item";
    static final String FIELD_STEP_TREE = "step_tree_json";
    static final String FIELD_ACTION_TREE = "action_tree_json";
    static final String HEAD_ROBOT = "act-robot-head";
    static final String TAIL_ROBOT = "act-robot-tail";
    static final String HEAD_UAV = "act-uav-head";
    static final String TAIL_UAV = "act-uav-tail";
    static final String LOCATION_REF = "location_ref";
    /** 字段库「选择位置」；到达指定位置动作用此槽，不是 location_ref。 */
    static final String SELECT_LOCATION = "F-e49f76bdca3e4e1393e8e2f1cc0e7867";
    static final String ACTION_TYPE = "action";

    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final PatrolTaskStepTreeReader catalog;
    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;

    public Map<String, Object> generate(Long taskId, List<Long> equipmentOrder) {
        return generate(taskId, equipmentOrder, null, null);
    }

    /**
     * 按勾选检查项生成步骤图并写回，返回刚写下的树。
     * 无人机起飞点、降落点来自任务创建的起点终点，不从设备检查配置读。
     * 机器人没有起飞降落，但仍要把这两端写进路线和返回充电。
     */
    public Map<String, Object> generate(Long taskId, List<Long> equipmentOrder, String startStopId, String endStopId) {
        PatrolTaskDraft draft = patrolTaskEntityStore.require(taskId);
        if (!StringUtils.hasText(draft.patrolExecutionMode())) {
            throw ServiceExceptionUtil.invalidParamException("还没有选择巡检方式");
        }
        List<TaskStepTreeGenerator.SelectedEquipment> selected = selectedOf(draft.inspectionContent());
        Map<Long, String> itemNames = itemNamesOf(draft.inspectionContent());
        Map<Long, List<TaskStepTreeGenerator.MethodAction>> methods = loadMethods(
                selected, draft.patrolExecutionMode(), itemNames);
        Map<String, Object> planned = plannedMap(draft.plannedRoute());
        String start = firstText(startStopId, plannedText(planned, "startStopId"));
        String end = firstText(endStopId, plannedText(planned, "endStopId"));
        if (!StringUtils.hasText(end) && StringUtils.hasText(start)
                && Boolean.TRUE.equals(planned.get("returnToStart"))) {
            end = start;
        }
        List<TaskStepTreeGenerator.MethodAction> head = List.of();
        List<TaskStepTreeGenerator.MethodAction> tail = List.of();
        String means = draft.patrolExecutionMode().trim();
        if (TaskStepTreeGenerator.MEANS_ROBOT.equals(means)
                || TaskStepTreeGenerator.MEANS_UAV.equals(means)
                || TaskStepTreeGenerator.MEANS_MANUAL.equals(means)) {
            if (!StringUtils.hasText(start) || !StringUtils.hasText(end)) {
                throw ServiceExceptionUtil.invalidParamException("请先在任务中设置起点和终点");
            }
        }
        if (TaskStepTreeGenerator.MEANS_ROBOT.equals(means)) {
            head = expandLibraryAction(HEAD_ROBOT, "机器人任务头", Map.of());
            tail = expandLibraryAction(TAIL_ROBOT, "机器人任务尾", locationParams(end));
        } else if (TaskStepTreeGenerator.MEANS_UAV.equals(means)) {
            head = expandLibraryAction(HEAD_UAV, "无人机任务头", locationParams(start));
            tail = expandLibraryAction(TAIL_UAV, "无人机任务尾", locationParams(end));
        }
        selected = withVisitStops(selected, means);
        Map<String, Object> tree = TaskStepTreeGenerator.generate(
                means, selected, equipmentOrder, methods, head, tail,
                start, anchorTitle("起点", start), end, anchorTitle("终点", end), itemNames);
        enrichReviewSnapshot(tree, selected, means);
        patrolTaskEntityStore.writeStepTree(taskId, tree);
        return tree;
    }

    private Map<Long, List<TaskStepTreeGenerator.MethodAction>> loadMethods(
            List<TaskStepTreeGenerator.SelectedEquipment> selected,
            String means,
            Map<Long, String> itemNames
    ) {
        Map<Long, List<TaskStepTreeGenerator.MethodAction>> methods = new LinkedHashMap<>();
        for (TaskStepTreeGenerator.SelectedEquipment equipment : selected) {
            for (Long itemId : equipment.itemIds()) {
                if (methods.containsKey(itemId)) {
                    continue;
                }
                EntityRespDTO item = requireItem(itemId);
                Map<String, Object> bag = bagOf(item);
                putItemNameIfAbsent(itemNames, itemId, bag);
                Object raw = methodSnapshotOf(bag);
                List<TaskStepTreeGenerator.MethodAction> actions = resolveLibraryActions(
                        InspectionMethodSnapshotParser.actionsForMeans(raw, means, objectMapper),
                        itemId,
                        itemNames);
                // 读到的该档步骤必须放进结果。漏放时后面一律当成没配方法。
                methods.put(itemId, actions);
            }
        }
        return methods;
    }

    private EntityRespDTO requireItem(Long itemId) {
        CommonResult<EntityRespDTO> result = entityRpcApi.getEntity(itemId, ITEM_TYPE);
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException("读不到检查项");
        }
        return result.getData();
    }

    /**
     * 检查项步骤树权威列；旧动作树列只在权威列空白时读历史。
     */
    private static Object methodSnapshotOf(Map<String, Object> bag) {
        Object stepTree = bag.get(FIELD_STEP_TREE);
        if (hasSnapshot(stepTree)) {
            return stepTree;
        }
        return bag.get(FIELD_ACTION_TREE);
    }

    private static boolean hasSnapshot(Object raw) {
        if (raw == null) {
            return false;
        }
        if (raw instanceof String text) {
            return StringUtils.hasText(text);
        }
        return true;
    }

    /**
     * 树上 actionId 是动作编码；写入步骤图前补动作库主键，对不上就暴露缺口。
     */
    private List<TaskStepTreeGenerator.MethodAction> resolveLibraryActions(
            List<TaskStepTreeGenerator.MethodAction> actions,
            Long itemId,
            Map<Long, String> itemNames
    ) {
        if (actions == null || actions.isEmpty()) {
            return List.of();
        }
        List<TaskStepTreeGenerator.MethodAction> resolved = new ArrayList<>();
        for (TaskStepTreeGenerator.MethodAction action : actions) {
            if (action.refId() != null) {
                resolved.add(action);
                continue;
            }
            Optional<Long> actionId = catalog.resolveActionId(action.refCode());
            if (actionId.isEmpty()) {
                String item = itemNames != null && StringUtils.hasText(itemNames.get(itemId))
                        ? "检查项「" + itemNames.get(itemId).trim() + "」"
                        : "检查项 #" + itemId;
                String step = StringUtils.hasText(action.title())
                        ? "步骤「" + action.title().trim() + "」"
                        : "步骤";
                throw ServiceExceptionUtil.invalidParamException(
                        item + "的" + step + "对不上动作库");
            }
            resolved.add(namedAction(
                    actionId.get(), action.refCode(), action.title(), action.params(),
                    action.sourceNodeKey(), action.declaredSlots()));
        }
        return resolved;
    }

    private TaskStepTreeGenerator.MethodAction requireLibraryAction(
            String code, String title, Map<String, Object> params
    ) {
        Optional<Long> actionId = catalog.resolveActionId(code);
        if (actionId.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("动作库没有本巡检方式的头尾动作");
        }
        return namedAction(actionId.get(), code, title, params, null, List.of());
    }

    /**
     * 复合头尾拆成库里的子动作；叶子用动作库名称。缺子动作或对不上库 → 报缺口。
     */
    private List<TaskStepTreeGenerator.MethodAction> expandLibraryAction(
            String code, String fallbackTitle, Map<String, Object> inheritParams
    ) {
        return expandAction(requireLibraryAction(code, fallbackTitle, inheritParams), inheritParams, new HashSet<>());
    }

    private List<TaskStepTreeGenerator.MethodAction> expandAction(
            TaskStepTreeGenerator.MethodAction action,
            Map<String, Object> inheritParams,
            Set<String> visiting
    ) {
        EntityRespDTO entity = loadAction(action.refId(), action.refCode());
        if (entity == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    "动作库没有「" + firstText(action.title(), action.refCode()) + "」");
        }
        Map<String, Object> bag = bagOf(entity);
        String code = firstText(textOf(bag.get("code")), action.refCode());
        if (code != null && !visiting.add(code)) {
            throw ServiceExceptionUtil.invalidParamException("动作库头尾动作存在循环引用");
        }
        String title = displayActionTitle(action.title(), bag, code);
        List<String> children = childCodesOf(bag.get("child_action_ids_json"));
        if (children.isEmpty()) {
            return List.of(new TaskStepTreeGenerator.MethodAction(
                    entity.getId(), code, title, paramsForLeaf(bag, inheritParams)));
        }
        List<TaskStepTreeGenerator.MethodAction> expanded = new ArrayList<>();
        for (String child : children) {
            expanded.addAll(expandAction(
                    requireLibraryAction(child, child, Map.of()), inheritParams, visiting));
        }
        return expanded;
    }

    private TaskStepTreeGenerator.MethodAction namedAction(
            Long actionId, String code, String title, Map<String, Object> params,
            String sourceNodeKey, List<String> declaredSlots
    ) {
        EntityRespDTO entity = loadAction(actionId, code);
        String resolved = displayActionTitle(title, entity == null ? Map.of() : bagOf(entity), code);
        return new TaskStepTreeGenerator.MethodAction(
                actionId, code, resolved, params, sourceNodeKey, declaredSlots);
    }

    private List<TaskStepTreeGenerator.SelectedEquipment> withVisitStops(
            List<TaskStepTreeGenerator.SelectedEquipment> selected,
            String means
    ) {
        List<TaskStepTreeGenerator.SelectedEquipment> visits = new ArrayList<>();
        for (TaskStepTreeGenerator.SelectedEquipment equipment : selected) {
            HostSopParamPack pack = catalog.loadHostPackOrEmpty(equipment.equipmentId());
            String stopId = stopIdOf(pack, equipment.itemIds(), means);
            visits.add(equipment.withVisit(stopId, objectTitle(equipment.equipmentName())));
        }
        return visits;
    }

    private static String stopIdOf(HostSopParamPack pack, List<Long> itemIds, String means) {
        if (pack == null) {
            return null;
        }
        if (itemIds != null) {
            for (Long itemId : itemIds) {
                String location = pack.firstLocationRef(itemId, means);
                if (StringUtils.hasText(location)) {
                    return location.trim();
                }
            }
        }
        return null;
    }

    /**
     * 核对计划要能看见到达点、拍照参数和耗时：把宿主包与动作库耗时写进已生成的节点。
     * 开跑仍读设备上的参数包，这里只做生成时快照，不另造一套耗时权威。
     */
    @SuppressWarnings("unchecked")
    private void enrichReviewSnapshot(
            Map<String, Object> tree,
            List<TaskStepTreeGenerator.SelectedEquipment> selected,
            String means
    ) {
        Object nodesRaw = tree.get("nodes");
        if (!(nodesRaw instanceof List<?> list) || list.isEmpty()) {
            return;
        }
        Map<String, Map<String, Object>> byKey = new LinkedHashMap<>();
        List<Map<String, Object>> nodes = new ArrayList<>();
        for (Object raw : list) {
            if (raw instanceof Map<?, ?> row) {
                Map<String, Object> node = (Map<String, Object>) row;
                nodes.add(node);
                Object key = node.get("nodeKey");
                if (key != null && StringUtils.hasText(String.valueOf(key))) {
                    byKey.put(String.valueOf(key).trim(), node);
                }
            }
        }
        Map<Long, HostSopParamPack> packs = new HashMap<>();
        for (TaskStepTreeGenerator.SelectedEquipment equipment : selected) {
            packs.put(equipment.equipmentId(), catalog.loadHostPackOrEmpty(equipment.equipmentId()));
        }
        Map<String, String> pointNames = new LinkedHashMap<>();
        putPointName(pointNames, plannedText(tree, "startStopId"));
        putPointName(pointNames, plannedText(tree, "endStopId"));
        Map<String, Integer> actionMinutes = new LinkedHashMap<>();
        for (Map<String, Object> node : nodes) {
            if (!TaskStepNode.HANG_ACTION.equals(textOf(node.get("hangTypeCode")))) {
                continue;
            }
            Map<String, Object> merged = new LinkedHashMap<>(paramsOf(node.get("params")));
            Long itemId = ancestorRefId(node, byKey, TaskStepNode.HANG_INSPECTION_ITEM);
            Long equipmentId = ancestorRefId(node, byKey, TaskStepNode.HANG_EQUIPMENT);
            EntityRespDTO action = loadAction(idOf(node.get("refId")), textOf(node.get("refCode")));
            if (action != null) {
                for (String slot : ActionParamSlotsSupport.fieldCodes(
                        bagOf(action).get("param_slots_json"), means, objectMapper)) {
                    merged.putIfAbsent(slot, "");
                }
            }
            if (itemId != null && equipmentId != null) {
                HostSopParamPack pack = packs.getOrDefault(equipmentId, HostSopParamPack.empty());
                merged.putAll(pack.paramsFor(
                        itemId, textOf(node.get("sourceNodeKey")), textOf(node.get("refCode")), means));
            }
            normalizeLocationParam(merged, LOCATION_REF, pointNames);
            normalizeLocationParam(merged, SELECT_LOCATION, pointNames);
            Optional<Integer> minutes = durationOfAction(idOf(node.get("refId")), textOf(node.get("refCode")), means);
            if (minutes.isPresent()) {
                merged.put(ActionDurationParamSupport.FIELD_CODE, minutes.get());
                actionMinutes.put(textOf(node.get("nodeKey")), minutes.get());
            }
            if (!merged.isEmpty()) {
                node.put("params", merged);
            }
        }
        writeDurationSums(nodes, byKey, actionMinutes);
        if (!pointNames.isEmpty()) {
            tree.put("pointNames", pointNames);
        }
    }

    /**
     * 到达位置可能是点位编码，也可能是 {id, code, name}。写成编码，并把中文名放进 pointNames。
     */
    private void normalizeLocationParam(
            Map<String, Object> params, String field, Map<String, String> pointNames
    ) {
        if (!params.containsKey(field)) {
            return;
        }
        Object raw = params.get(field);
        String code = locationCodeOf(raw);
        String name = locationNameOf(raw);
        if (StringUtils.hasText(code)) {
            if (StringUtils.hasText(name)) {
                pointNames.put(code, name);
            } else {
                putPointName(pointNames, code);
                name = pointNames.get(code);
            }
            params.put(field, StringUtils.hasText(name) ? name : code);
            return;
        }
        if (StringUtils.hasText(name)) {
            params.put(field, name);
        }
    }

    private static String locationCodeOf(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Map<?, ?> obj) {
            Object code = obj.get("code");
            if (code != null && StringUtils.hasText(String.valueOf(code))) {
                return String.valueOf(code).trim();
            }
            Object id = obj.get("id");
            if (id != null && StringUtils.hasText(String.valueOf(id))
                    && !"null".equalsIgnoreCase(String.valueOf(id))) {
                return String.valueOf(id).trim();
            }
            return null;
        }
        String text = String.valueOf(raw).trim();
        return text.isEmpty() || "null".equalsIgnoreCase(text) ? null : text;
    }

    private static String locationNameOf(Object raw) {
        if (!(raw instanceof Map<?, ?> obj)) {
            return null;
        }
        Object name = obj.get("name");
        return name == null || !StringUtils.hasText(String.valueOf(name))
                ? null : String.valueOf(name).trim();
    }

    private void putPointName(Map<String, String> names, String stopId) {
        if (!StringUtils.hasText(stopId) || names.containsKey(stopId.trim())) {
            return;
        }
        String name = pointName(stopId.trim());
        if (StringUtils.hasText(name)) {
            names.put(stopId.trim(), name);
        }
    }

    private Optional<Integer> durationOfAction(Long actionId, String actionCode, String means) {
        EntityRespDTO entity = loadAction(actionId, actionCode);
        if (entity == null) {
            return Optional.empty();
        }
        return ActionDurationParamSupport.minutesFromSlots(
                bagOf(entity).get("param_slots_json"), means, objectMapper);
    }

    private static Map<String, Object> paramsOf(Object raw) {
        if (raw instanceof Map<?, ?> map) {
            Map<String, Object> copy = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() != null) {
                    copy.put(String.valueOf(entry.getKey()), entry.getValue());
                }
            }
            return copy;
        }
        return new LinkedHashMap<>();
    }

    private static Long ancestorRefId(
            Map<String, Object> node,
            Map<String, Map<String, Object>> byKey,
            String hangType
    ) {
        String parentKey = textOf(node.get("parentNodeKey"));
        int guard = 0;
        while (StringUtils.hasText(parentKey) && guard++ < byKey.size()) {
            Map<String, Object> parent = byKey.get(parentKey);
            if (parent == null) {
                return null;
            }
            if (hangType.equals(textOf(parent.get("hangTypeCode")))) {
                return idOf(parent.get("refId"));
            }
            parentKey = textOf(parent.get("parentNodeKey"));
        }
        return null;
    }

    private static void writeDurationSums(
            List<Map<String, Object>> nodes,
            Map<String, Map<String, Object>> byKey,
            Map<String, Integer> actionMinutes
    ) {
        if (actionMinutes.isEmpty()) {
            return;
        }
        Map<String, Integer> sums = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : actionMinutes.entrySet()) {
            Map<String, Object> action = byKey.get(entry.getKey());
            if (action == null) {
                continue;
            }
            String parentKey = textOf(action.get("parentNodeKey"));
            int guard = 0;
            while (StringUtils.hasText(parentKey) && guard++ < byKey.size()) {
                sums.merge(parentKey, entry.getValue(), Integer::sum);
                Map<String, Object> parent = byKey.get(parentKey);
                parentKey = parent == null ? "" : textOf(parent.get("parentNodeKey"));
            }
        }
        for (Map<String, Object> node : nodes) {
            String key = textOf(node.get("nodeKey"));
            Integer minutes = sums.get(key);
            if (minutes == null) {
                continue;
            }
            Map<String, Object> params = paramsOf(node.get("params"));
            params.put(ActionDurationParamSupport.FIELD_CODE, minutes);
            node.put("params", params);
        }
    }

    private static Long idOf(Object raw) {
        if (raw instanceof Number number && number.longValue() > 0) {
            return number.longValue();
        }
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                long value = Long.parseLong(text.trim());
                return value > 0 ? value : null;
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static String objectTitle(String equipmentName) {
        if (StringUtils.hasText(equipmentName)) {
            return equipmentName.trim();
        }
        throw ServiceExceptionUtil.invalidParamException("巡检对象缺少名称");
    }

    private String anchorTitle(String kind, String stopId) {
        String point = pointName(stopId);
        if (StringUtils.hasText(point)) {
            return kind + " · " + point;
        }
        return StringUtils.hasText(stopId) ? kind + " · " + stopId.trim() : kind;
    }

    private String pointName(String stopId) {
        if (!StringUtils.hasText(stopId)) {
            return null;
        }
        String ref = stopId.trim();
        EntityRespDTO entity = optionalEntity(entityRpcApi.getEntityByCode(ref, "point"));
        if (entity == null) {
            Long id = idOf(ref);
            if (id != null) {
                entity = optionalEntity(entityRpcApi.getEntity(id, "point"));
            }
        }
        if (entity == null) {
            return null;
        }
        Object name = bagOf(entity).get("name");
        return name == null || !StringUtils.hasText(String.valueOf(name)) ? null : String.valueOf(name).trim();
    }

    private EntityRespDTO loadAction(Long actionId, String actionCode) {
        if (actionId != null && actionId > 0) {
            EntityRespDTO byId = optionalEntity(entityRpcApi.getEntity(actionId, ACTION_TYPE));
            if (byId != null) {
                return byId;
            }
        }
        if (!StringUtils.hasText(actionCode)) {
            return null;
        }
        return optionalEntity(entityRpcApi.getEntityByCode(actionCode.trim(), ACTION_TYPE));
    }

    private static EntityRespDTO optionalEntity(CommonResult<EntityRespDTO> result) {
        if (result == null || !result.isSuccess() || result.getData() == null) {
            return null;
        }
        return result.getData();
    }

    private List<String> childCodesOf(Object raw) {
        Object value = raw;
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                value = objectMapper.readValue(text, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (Exception ex) {
                return List.of();
            }
        }
        if (!(value instanceof List<?> list)) {
            return List.of();
        }
        List<String> codes = new ArrayList<>();
        for (Object row : list) {
            if (row != null && StringUtils.hasText(String.valueOf(row))) {
                codes.add(String.valueOf(row).trim());
            }
        }
        return codes;
    }

    private Map<String, Object> paramsForLeaf(Map<String, Object> bag, Map<String, Object> inheritParams) {
        if (inheritParams == null || inheritParams.isEmpty() || !hasLocationSlot(bag.get("param_slots_json"))) {
            return Map.of();
        }
        return inheritParams;
    }

    private boolean hasLocationSlot(Object raw) {
        Object value = raw;
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                value = objectMapper.readValue(text, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (Exception ex) {
                return false;
            }
        }
        if (!(value instanceof Map<?, ?> root)) {
            return false;
        }
        if (fieldsHaveLocation(root.get("fields"))) {
            return true;
        }
        Object methods = root.get("methods");
        if (!(methods instanceof List<?> list)) {
            return false;
        }
        for (Object row : list) {
            if (row instanceof Map<?, ?> method && fieldsHaveLocation(method.get("fields"))) {
                return true;
            }
        }
        return false;
    }

    private static boolean fieldsHaveLocation(Object raw) {
        if (!(raw instanceof List<?> fields)) {
            return false;
        }
        for (Object row : fields) {
            if (row instanceof Map<?, ?> field && LOCATION_REF.equals(textOf(field.get("fieldCode")))) {
                return true;
            }
        }
        return false;
    }

    private static String displayActionTitle(String preferred, Map<String, Object> bag, String code) {
        if (usableTitle(preferred, code)) {
            return preferred.trim();
        }
        String name = textOf(bag.get("name"));
        if (usableTitle(name, code)) {
            return name;
        }
        return firstText(preferred, code);
    }

    private static boolean usableTitle(String title, String code) {
        if (!StringUtils.hasText(title)) {
            return false;
        }
        String trimmed = title.trim();
        if (code != null && trimmed.equalsIgnoreCase(code.trim())) {
            return false;
        }
        return !trimmed.startsWith("act-") && !trimmed.startsWith("action-");
    }

    private static String textOf(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    private static Map<String, Object> locationParams(String stopId) {
        if (!StringUtils.hasText(stopId)) {
            return Map.of();
        }
        return Map.of(LOCATION_REF, stopId.trim());
    }

    private static String firstText(String preferred, String fallback) {
        if (StringUtils.hasText(preferred)) {
            return preferred.trim();
        }
        return StringUtils.hasText(fallback) ? fallback.trim() : null;
    }

    private Map<String, Object> plannedMap(Object plannedRoute) {
        if (plannedRoute instanceof Map<?, ?> map) {
            Map<String, Object> copied = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() != null) {
                    copied.put(String.valueOf(entry.getKey()), entry.getValue());
                }
            }
            return copied;
        }
        if (plannedRoute instanceof String json && StringUtils.hasText(json)) {
            try {
                return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (Exception ex) {
                throw ServiceExceptionUtil.invalidParamException("已保存路线读不出起点终点");
            }
        }
        return Map.of();
    }

    private static String plannedText(Map<String, Object> planned, String key) {
        Object raw = planned.get(key);
        return raw == null ? null : String.valueOf(raw);
    }

    private static List<TaskStepTreeGenerator.SelectedEquipment> selectedOf(InspectionContent content) {
        List<InspectionContent.ObjectContent> objects = allObjects(content);
        if (objects.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("任务草稿还没有勾选被巡检设备和检查项");
        }
        List<TaskStepTreeGenerator.SelectedEquipment> selected = new ArrayList<>();
        for (InspectionContent.ObjectContent object : objects) {
            List<Long> itemIds = new ArrayList<>();
            if (object.getItems() != null) {
                for (InspectionContent.ItemContent item : object.getItems()) {
                    if (item.getItemId() != null) {
                        itemIds.add(item.getItemId());
                    }
                }
            }
            selected.add(new TaskStepTreeGenerator.SelectedEquipment(
                    object.getObjectId(), itemIds, object.getObjectName()));
        }
        return selected;
    }

    /**
     * 检查项名称先取任务草稿里用户看到的名字，读实体时再补空白。
     * 模板组和自行添加的对象都要收，漏一组树上就没有检查项名。
     */
    private static Map<Long, String> itemNamesOf(InspectionContent content) {
        Map<Long, String> names = new LinkedHashMap<>();
        for (InspectionContent.ObjectContent object : allObjects(content)) {
            if (object.getItems() == null) {
                continue;
            }
            for (InspectionContent.ItemContent item : object.getItems()) {
                if (item.getItemId() == null || !StringUtils.hasText(item.getItemName())) {
                    continue;
                }
                names.put(item.getItemId(), item.getItemName().trim());
            }
        }
        return names;
    }

    private static List<InspectionContent.ObjectContent> allObjects(InspectionContent content) {
        List<InspectionContent.ObjectContent> objects = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        collectObjects(content == null ? null : content.getCustomObjects(), objects, seen);
        if (content != null && content.getGroups() != null) {
            for (InspectionContent.ObjectGroup group : content.getGroups()) {
                collectObjects(group == null ? null : group.getObjects(), objects, seen);
            }
        }
        return objects;
    }

    private static void collectObjects(
            List<InspectionContent.ObjectContent> source,
            List<InspectionContent.ObjectContent> sink,
            Set<Long> seen
    ) {
        if (source == null) {
            return;
        }
        for (InspectionContent.ObjectContent object : source) {
            if (object == null || object.getObjectId() == null || !seen.add(object.getObjectId())) {
                continue;
            }
            sink.add(object);
        }
    }

    private static void putItemNameIfAbsent(Map<Long, String> itemNames, Long itemId, Map<String, Object> bag) {
        if (itemNames.containsKey(itemId) && StringUtils.hasText(itemNames.get(itemId))) {
            return;
        }
        Object name = bag.get("name");
        if (name == null) {
            name = bag.get("item_name");
        }
        if (name == null || !StringUtils.hasText(String.valueOf(name))) {
            return;
        }
        itemNames.put(itemId, String.valueOf(name).trim());
    }

    private static Map<String, Object> bagOf(EntityRespDTO entity) {
        Map<String, Object> bag = new LinkedHashMap<>();
        if (entity.getBaseFields() != null) {
            bag.putAll(entity.getBaseFields());
        }
        if (entity.getCustomFields() != null) {
            bag.putAll(entity.getCustomFields());
        }
        return bag;
    }
}
