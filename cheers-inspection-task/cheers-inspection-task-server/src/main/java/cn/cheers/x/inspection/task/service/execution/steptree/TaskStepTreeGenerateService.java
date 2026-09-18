package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 点路径规划（或摄像机按勾选顺序）时生成总任务步骤图并写回。
 * <p>检查方法与任务页同一条读法：先读检查项 {@code step_tree_json}，按任务已选巡检方式取该档。
 * 旧 {@code action_tree_json} 只读历史，不编一棵树。
 * <p>禁止：草稿保存时生成；开跑时再算树；人工硬挂机器人头尾；把读空旧列说成没配方法。
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

    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final EntityRpcTaskStepTreeCatalog catalog;
    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;

    public void generate(Long taskId, List<Long> equipmentOrder) {
        generate(taskId, equipmentOrder, null, null);
    }

    /**
     * 按勾选检查项生成步骤图。
     * 无人机起飞点、降落点来自任务创建的起点终点，不从设备检查配置读。
     * 机器人没有起飞降落，但仍要把这两端写进路线和返回充电。
     */
    public void generate(Long taskId, List<Long> equipmentOrder, String startStopId, String endStopId) {
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
            head = List.of(requireLibraryAction(HEAD_ROBOT, "机器人任务头", Map.of()));
            tail = List.of(requireLibraryAction(TAIL_ROBOT, "机器人任务尾", locationParams(end)));
        } else if (TaskStepTreeGenerator.MEANS_UAV.equals(means)) {
            head = List.of(requireLibraryAction(HEAD_UAV, "无人机任务头", locationParams(start)));
            tail = List.of(requireLibraryAction(TAIL_UAV, "无人机任务尾", locationParams(end)));
        }
        Map<String, Object> tree = TaskStepTreeGenerator.generate(
                means, selected, equipmentOrder, methods, head, tail, start, end, itemNames);
        patrolTaskEntityStore.writeStepTree(taskId, tree);
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
            resolved.add(new TaskStepTreeGenerator.MethodAction(
                    actionId.get(), action.refCode(), action.title(), action.params()));
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
        return new TaskStepTreeGenerator.MethodAction(actionId.get(), code, title, params);
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
        if (content == null || content.getCustomObjects() == null || content.getCustomObjects().isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("任务草稿还没有勾选被巡检设备和检查项");
        }
        List<TaskStepTreeGenerator.SelectedEquipment> selected = new ArrayList<>();
        for (InspectionContent.ObjectContent object : content.getCustomObjects()) {
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
     */
    private static Map<Long, String> itemNamesOf(InspectionContent content) {
        Map<Long, String> names = new LinkedHashMap<>();
        if (content == null || content.getCustomObjects() == null) {
            return names;
        }
        for (InspectionContent.ObjectContent object : content.getCustomObjects()) {
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

    private static void putItemNameIfAbsent(Map<Long, String> itemNames, Long itemId, Map<String, Object> bag) {
        if (itemNames.containsKey(itemId)) {
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
