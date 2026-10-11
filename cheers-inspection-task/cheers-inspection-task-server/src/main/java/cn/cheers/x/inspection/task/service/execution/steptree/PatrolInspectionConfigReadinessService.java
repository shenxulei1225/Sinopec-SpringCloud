package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolDispatchProfile;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolInspectionConfigGapVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolInspectionConfigReadinessRespVO;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.model.task.InspectionContent.ItemContent;
import cn.cheers.x.inspection.task.model.task.InspectionContent.ObjectContent;
import cn.cheers.x.inspection.task.service.task.PatrolPlannedRouteSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.platform.orchestration.route.RoutePayloadKeys;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 第 3 步「只查配置、不生成步骤图」：检查执行设备、协议、路线端点、检查项方法快照是否齐全。
 */
@Service
@RequiredArgsConstructor
public class PatrolInspectionConfigReadinessService {

    private static final String ITEM_TYPE = "inspection_item";
    private static final String FIELD_STEP_TREE = "step_tree_json";
    private static final String FIELD_ACTION_TREE = "action_tree_json";

    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;

    public PatrolInspectionConfigReadinessRespVO evaluate(PatrolTaskDraft draft) {
        List<PatrolInspectionConfigGapVO> gaps = new ArrayList<>();
        if (draft == null) {
            gaps.add(gap("MISSING_TASK", "任务不存在", null));
            return build(gaps);
        }
        if (!StringUtils.hasText(draft.patrolExecutionMode())) {
            gaps.add(gap("MISSING_MEANS", "还没有选择巡检方式", null));
        }
        ExecutionDeviceBinding binding = draft.executionDeviceBinding();
        if (binding == null
                || binding.getEquipmentId() == null
                || !StringUtils.hasText(binding.getProtocolCode())
                || !StringUtils.hasText(binding.getLogicalDeviceId())) {
            gaps.add(gap("MISSING_EXECUTION_DEVICE", "请先选择执行设备并绑定协议", null));
        }
        String means = draft.patrolExecutionMode() == null ? null : draft.patrolExecutionMode().trim();
        String protocol = binding != null && StringUtils.hasText(binding.getProtocolCode())
                ? binding.getProtocolCode().trim()
                : null;
        if (TaskStepTreeGenerator.MEANS_UAV.equals(means)
                && protocol != null
                && !ProtocolDispatchProfile.supportsUavOpeningSteps(protocol)) {
            gaps.add(gap(
                    "UNSUPPORTED_PROTOCOL",
                    "当前对接协议还不能自动生成无人机开头步骤，请换设备或协议",
                    null));
        }
        if (needsRouteEndpoints(means)) {
            Map<String, Object> planned = PatrolPlannedRouteSupport.asPlannedMap(draft.plannedRoute());
            String start = textFrom(planned, RoutePayloadKeys.START_STOP_ID, draft.startStopId());
            String end = textFrom(planned, RoutePayloadKeys.END_STOP_ID, draft.endStopId());
            if (!StringUtils.hasText(end) && StringUtils.hasText(start)
                    && planned != null && Boolean.TRUE.equals(planned.get(RoutePayloadKeys.RETURN_TO_START))) {
                end = start;
            }
            if (!StringUtils.hasText(start) || !StringUtils.hasText(end)) {
                gaps.add(gap("MISSING_ROUTE_ENDPOINTS", "请先在任务中设置起点和终点", null));
            }
        }
        collectInspectionMethodGaps(draft.inspectionContent(), means, gaps);
        return build(gaps);
    }

    private void collectInspectionMethodGaps(
            InspectionContent content, String means, List<PatrolInspectionConfigGapVO> gaps) {
        if (content == null || !StringUtils.hasText(means)) {
            return;
        }
        List<ObjectContent> objects = allObjects(content);
        if (objects.isEmpty()) {
            gaps.add(gap("EMPTY_INSPECTION_CONTENT", "还没有勾选被巡检设备和检查项", null));
            return;
        }
        java.util.Set<Long> seenItems = new java.util.HashSet<>();
        for (ObjectContent object : objects) {
            if (object.getItems() == null) {
                continue;
            }
            for (ItemContent row : object.getItems()) {
                Long itemId = row == null ? null : row.getItemId();
                if (itemId == null || !seenItems.add(itemId)) {
                    continue;
                }
                EntityRespDTO item = loadItem(itemId);
                if (item == null) {
                    gaps.add(gap("ITEM_NOT_FOUND", "读不到检查项 #" + itemId, itemId));
                    continue;
                }
                Map<String, Object> bag = bagOf(item);
                Object raw = methodSnapshotOf(bag);
                List<TaskStepTreeGenerator.MethodAction> actions =
                        InspectionMethodSnapshotParser.actionsForMeans(raw, means, objectMapper);
                if (actions == null || actions.isEmpty()) {
                    String name = StringUtils.hasText(row.getItemName())
                            ? row.getItemName().trim()
                            : itemName(bag, itemId);
                    gaps.add(gap(
                            "EMPTY_INSPECTION_METHOD",
                            "检查项「" + name + "」还没有配置「" + means + "」检查方法",
                            itemId));
                }
            }
        }
    }

    private static List<ObjectContent> allObjects(InspectionContent content) {
        List<ObjectContent> objects = new ArrayList<>();
        java.util.Set<Long> seen = new java.util.HashSet<>();
        if (content.getGroups() != null) {
            for (InspectionContent.ObjectGroup group : content.getGroups()) {
                if (group == null || group.getObjects() == null) {
                    continue;
                }
                for (ObjectContent object : group.getObjects()) {
                    if (object == null || object.getObjectId() == null || !seen.add(object.getObjectId())) {
                        continue;
                    }
                    objects.add(object);
                }
            }
        }
        if (content.getCustomObjects() != null) {
            for (ObjectContent object : content.getCustomObjects()) {
                if (object == null || object.getObjectId() == null || !seen.add(object.getObjectId())) {
                    continue;
                }
                objects.add(object);
            }
        }
        return objects;
    }

    private static boolean needsRouteEndpoints(String means) {
        return TaskStepTreeGenerator.MEANS_ROBOT.equals(means)
                || TaskStepTreeGenerator.MEANS_UAV.equals(means)
                || TaskStepTreeGenerator.MEANS_MANUAL.equals(means);
    }

    private EntityRespDTO loadItem(Long itemId) {
        CommonResult<EntityRespDTO> result = entityRpcApi.getEntity(itemId, ITEM_TYPE);
        if (result == null || !result.isSuccess() || result.getData() == null) {
            return null;
        }
        return result.getData();
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

    private static Map<String, Object> bagOf(EntityRespDTO entity) {
        java.util.LinkedHashMap<String, Object> bag = new java.util.LinkedHashMap<>();
        if (entity.getBaseFields() != null) {
            bag.putAll(entity.getBaseFields());
        }
        if (entity.getCustomFields() != null) {
            bag.putAll(entity.getCustomFields());
        }
        return bag;
    }

    private static String itemName(Map<String, Object> bag, Long itemId) {
        Object name = bag.get("name");
        if (name != null && StringUtils.hasText(name.toString())) {
            return name.toString().trim();
        }
        return "#" + itemId;
    }

    private static String textFrom(Map<String, Object> planned, String key, String fallback) {
        if (planned != null && planned.get(key) != null && StringUtils.hasText(planned.get(key).toString())) {
            return planned.get(key).toString().trim();
        }
        return StringUtils.hasText(fallback) ? fallback.trim() : null;
    }

    private static PatrolInspectionConfigGapVO gap(String code, String message, Long itemId) {
        return PatrolInspectionConfigGapVO.builder().code(code).message(message).itemId(itemId).build();
    }

    private static PatrolInspectionConfigReadinessRespVO build(List<PatrolInspectionConfigGapVO> gaps) {
        return PatrolInspectionConfigReadinessRespVO.builder()
                .ready(gaps.isEmpty())
                .gaps(List.copyOf(gaps))
                .build();
    }
}
