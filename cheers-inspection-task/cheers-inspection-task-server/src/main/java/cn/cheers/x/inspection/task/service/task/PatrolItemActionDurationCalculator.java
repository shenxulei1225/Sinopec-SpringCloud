package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.execution.steptree.ActionGroupMemberParser;
import cn.cheers.x.inspection.task.service.execution.steptree.InspectionMethodSnapshotParser;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeGenerator;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 按已勾选检查项读检查方法，再从动作库加总检查项动作耗时。
 * <p>第 1 步还没选执行设备。动作已经按型号分开时，这一步不算耗时。
 * 排期选中执行设备后，只加这一型号在动作上的默认秒数。
 * <p>权威：动作库 {@code action_duration} 默认值，单位秒。同一检查项挂在多台设备上要加多次。
 * <p>不负责：路径耗时、冲突检测、写已保存路线、读宿主参数包覆盖。
 * <p>禁止：缺默认值时猜秒数；把不足 1 秒截成 0；把停留时长当成动作耗时。
 * 这一档没有动作耗时栏的步骤不占时间，按 0 计入，不得因此放弃整项。栏在但默认值为空，整项仍不算。
 */
@Component
@RequiredArgsConstructor
public class PatrolItemActionDurationCalculator {

    static final String ITEM_TYPE = "inspection_item";
    static final String ACTION_TYPE = "action";
    static final String FIELD_STEP_TREE = "step_tree_json";
    static final String FIELD_ACTION_TREE = "action_tree_json";
    static final String FIELD_PARAM_SLOTS = "param_slots_json";
    static final String FIELD_CODE = "code";

    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;
    private final ActionGroupMemberParser actionGroupMemberParser;

    public Optional<BigDecimal> computeLiveSeconds(InspectionContent content, String patrolExecutionMode) {
        return computeLiveSeconds(content, patrolExecutionMode, null);
    }

    /**
     * 按已选执行设备的型号加总。设备还没选时，型号传空，按型号分开的动作不算秒数。
     */
    public Optional<BigDecimal> computeLiveSeconds(
            InspectionContent content, String patrolExecutionMode, Long equipmentModelId) {
        if (content == null || !StringUtils.hasText(patrolExecutionMode)) {
            return Optional.empty();
        }
        List<Long> itemIds = selectedItemIds(content);
        if (itemIds.isEmpty()) {
            return Optional.empty();
        }
        Map<Long, Optional<BigDecimal>> perItem = new HashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        String means = patrolExecutionMode.trim();
        for (Long itemId : itemIds) {
            Optional<BigDecimal> minutes = perItem.computeIfAbsent(
                    itemId, id -> minutesOfItem(id, means, equipmentModelId));
            if (minutes.isEmpty()) {
                return Optional.empty();
            }
            total = total.add(minutes.get());
        }
        return Optional.of(ActionDurationParamSupport.plainMinutes(total));
    }

    /**
     * 排期已经选了执行设备时用这个。读不到这台设备的型号时不算耗时。
     */
    public Optional<BigDecimal> computeLiveSecondsForEquipment(
            InspectionContent content, String patrolExecutionMode, Long executionEquipmentId) {
        if (executionEquipmentId == null || executionEquipmentId <= 0) {
            return computeLiveSeconds(content, patrolExecutionMode, null);
        }
        Long modelId = modelIdOfEquipment(executionEquipmentId);
        if (modelId == null) {
            return Optional.empty();
        }
        return computeLiveSeconds(content, patrolExecutionMode, modelId);
    }

    private Long modelIdOfEquipment(Long equipmentId) {
        EntityRespDTO equipment = optionalEntity(entityRpcApi.getEntity(equipmentId, "equipment"));
        if (equipment == null) {
            return null;
        }
        Object raw = bagOf(equipment).get("modelId");
        if (raw == null) {
            raw = bagOf(equipment).get("model_id");
        }
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

    private Optional<BigDecimal> minutesOfItem(Long itemId, String means, Long equipmentModelId) {
        EntityRespDTO item = optionalEntity(entityRpcApi.getEntity(itemId, ITEM_TYPE));
        if (item == null) {
            return Optional.empty();
        }
        List<TaskStepTreeGenerator.MethodAction> actions = InspectionMethodSnapshotParser.actionsForMeans(
                methodSnapshotOf(bagOf(item)), means, objectMapper);
        if (actions.isEmpty()) {
            return Optional.empty();
        }
        BigDecimal total = BigDecimal.ZERO;
        for (TaskStepTreeGenerator.MethodAction action : actions) {
            Optional<BigDecimal> minutes = minutesOfAction(action.refCode(), means, equipmentModelId, new HashSet<>());
            if (minutes.isEmpty()) {
                return Optional.empty();
            }
            total = total.add(minutes.get());
        }
        return Optional.of(ActionDurationParamSupport.plainMinutes(total));
    }

    private Optional<BigDecimal> minutesOfAction(
            String actionCode, String means, Long equipmentModelId, Set<String> visiting) {
        EntityRespDTO entity = loadAction(actionCode);
        if (entity == null) {
            return Optional.empty();
        }
        Map<String, Object> bag = bagOf(entity);
        String code = firstText(bag.get(FIELD_CODE), actionCode);
        Set<String> branch = new HashSet<>(visiting);
        if (code != null && !branch.add(code)) {
            return Optional.empty();
        }
        List<ActionGroupMemberParser.Member> members = actionGroupMemberParser.parse(
                bag.get(FIELD_ACTION_TREE));
        if (!members.isEmpty()) {
            BigDecimal total = BigDecimal.ZERO;
            for (ActionGroupMemberParser.Member member : members) {
                Optional<BigDecimal> minutes = minutesOfAction(
                        member.actionCode(), means, equipmentModelId, branch);
                if (minutes.isEmpty()) {
                    return Optional.empty();
                }
                total = total.add(minutes.get());
            }
            return Optional.of(ActionDurationParamSupport.plainMinutes(total));
        }
        Optional<BigDecimal> own = ActionDurationParamSupport.secondsFromSlots(
                bag.get(FIELD_PARAM_SLOTS), means, equipmentModelId, objectMapper);
        if (own.isPresent()) {
            return own;
        }
        // 到达指定位置只有点位、没有动作耗时栏：不占检查项时间。栏在但没填默认值仍整项不算。
        if (ActionDurationParamSupport.declaresActionDuration(
                bag.get(FIELD_PARAM_SLOTS), means, equipmentModelId, objectMapper)) {
            return Optional.empty();
        }
        return Optional.of(BigDecimal.ZERO);
    }

    /** 巡检读动作只认业务编码。返回和打开这条动作相同的档案，含参数清单。 */
    private EntityRespDTO loadAction(String actionCode) {
        if (!StringUtils.hasText(actionCode)) {
            return null;
        }
        return optionalEntity(entityRpcApi.getEntityByCode(actionCode.trim(), ACTION_TYPE));
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

    private static String firstText(Object preferred, String fallback) {
        if (preferred != null && StringUtils.hasText(String.valueOf(preferred))) {
            return String.valueOf(preferred).trim();
        }
        return StringUtils.hasText(fallback) ? fallback.trim() : null;
    }
}
