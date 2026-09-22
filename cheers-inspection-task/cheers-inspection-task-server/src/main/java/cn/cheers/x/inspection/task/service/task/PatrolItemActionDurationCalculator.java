package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.execution.steptree.InspectionMethodSnapshotParser;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeGenerator;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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
 * 第 1 步计算方法：按已勾选检查项读检查方法，再从动作库加总检查项动作耗时。
 * <p>打开任务也调本方法，不要求用户点到第 2 步。
 * <p>权威：动作库 {@code action_duration} 默认值（分钟）。同一检查项挂在多台设备上要加多次。
 * <p>不负责：路径耗时、冲突检测、写已保存路线、读宿主参数包覆盖。
 * <p>禁止：缺默认值时猜分钟数；把停留时长秒当成动作耗时。
 */
@Component
@RequiredArgsConstructor
public class PatrolItemActionDurationCalculator {

    static final String ITEM_TYPE = "inspection_item";
    static final String ACTION_TYPE = "action";
    static final String FIELD_STEP_TREE = "step_tree_json";
    static final String FIELD_ACTION_TREE = "action_tree_json";
    static final String FIELD_PARAM_SLOTS = "param_slots_json";
    static final String FIELD_CHILD_IDS = "child_action_ids_json";
    static final String FIELD_COMPOSITE = "is_composite";
    static final String FIELD_CODE = "code";

    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;

    public Optional<Integer> computeLiveMinutes(InspectionContent content, String patrolExecutionMode) {
        if (content == null || !StringUtils.hasText(patrolExecutionMode)) {
            return Optional.empty();
        }
        List<Long> itemIds = selectedItemIds(content);
        if (itemIds.isEmpty()) {
            return Optional.empty();
        }
        Map<Long, Optional<Integer>> perItem = new HashMap<>();
        int total = 0;
        for (Long itemId : itemIds) {
            Optional<Integer> minutes = perItem.computeIfAbsent(
                    itemId, id -> minutesOfItem(id, patrolExecutionMode.trim()));
            if (minutes.isEmpty()) {
                return Optional.empty();
            }
            total += minutes.get();
        }
        return Optional.of(total);
    }

    private Optional<Integer> minutesOfItem(Long itemId, String means) {
        EntityRespDTO item = optionalEntity(entityRpcApi.getEntity(itemId, ITEM_TYPE));
        if (item == null) {
            return Optional.empty();
        }
        List<TaskStepTreeGenerator.MethodAction> actions = InspectionMethodSnapshotParser.actionsForMeans(
                methodSnapshotOf(bagOf(item)), means, objectMapper);
        if (actions.isEmpty()) {
            return Optional.empty();
        }
        int total = 0;
        for (TaskStepTreeGenerator.MethodAction action : actions) {
            Optional<Integer> minutes = minutesOfAction(action.refId(), action.refCode(), means, new HashSet<>());
            if (minutes.isEmpty()) {
                return Optional.empty();
            }
            total += minutes.get();
        }
        return Optional.of(total);
    }

    private Optional<Integer> minutesOfAction(Long actionId, String actionCode, String means, Set<String> visiting) {
        EntityRespDTO entity = loadAction(actionId, actionCode);
        if (entity == null) {
            return Optional.empty();
        }
        Map<String, Object> bag = bagOf(entity);
        String code = firstText(bag.get(FIELD_CODE), actionCode, entity.getId());
        if (code != null && !visiting.add(code)) {
            return Optional.empty();
        }
        Optional<Integer> own = ActionDurationParamSupport.minutesFromSlots(
                bag.get(FIELD_PARAM_SLOTS), means, objectMapper);
        if (own.isPresent()) {
            return own;
        }
        if (!isComposite(bag.get(FIELD_COMPOSITE))) {
            return Optional.empty();
        }
        List<String> children = childCodesOf(bag.get(FIELD_CHILD_IDS));
        if (children.isEmpty()) {
            return Optional.empty();
        }
        int total = 0;
        for (String child : children) {
            Optional<Integer> minutes = minutesOfAction(null, child, means, visiting);
            if (minutes.isEmpty()) {
                return Optional.empty();
            }
            total += minutes.get();
        }
        return Optional.of(total);
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

    private List<String> childCodesOf(Object raw) {
        Object value = raw;
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                value = objectMapper.readValue(text, new TypeReference<Object>() {
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

    private static boolean isComposite(Object raw) {
        if (raw instanceof Boolean flag) {
            return flag;
        }
        if (raw instanceof Number number) {
            return number.intValue() == 1;
        }
        return raw != null && "true".equalsIgnoreCase(String.valueOf(raw).trim());
    }

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

    private static List<Long> selectedItemIds(InspectionContent content) {
        List<Long> ids = new ArrayList<>();
        collectFromObjects(content.getCustomObjects(), ids);
        if (content.getGroups() != null) {
            for (InspectionContent.ObjectGroup group : content.getGroups()) {
                collectFromObjects(group.getObjects(), ids);
            }
        }
        return ids;
    }

    private static void collectFromObjects(List<InspectionContent.ObjectContent> objects, List<Long> ids) {
        if (objects == null) {
            return;
        }
        for (InspectionContent.ObjectContent object : objects) {
            if (object == null || object.getItems() == null) {
                continue;
            }
            for (InspectionContent.ItemContent item : object.getItems()) {
                if (item != null && item.getItemId() != null) {
                    ids.add(item.getItemId());
                }
            }
        }
    }

    private static EntityRespDTO optionalEntity(CommonResult<EntityRespDTO> result) {
        if (result == null || !result.isSuccess() || result.getData() == null) {
            return null;
        }
        return result.getData();
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

    private static String firstText(Object preferred, String fallback, Long id) {
        if (preferred != null && StringUtils.hasText(String.valueOf(preferred))) {
            return String.valueOf(preferred).trim();
        }
        if (StringUtils.hasText(fallback)) {
            return fallback.trim();
        }
        return id == null ? null : String.valueOf(id);
    }
}
