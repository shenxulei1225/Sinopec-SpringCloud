package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskGeneratedInputReviewRespVO;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.execution.steptree.HostSopParamPack;
import cn.cheers.x.inspection.task.service.execution.steptree.InspectionMethodSnapshotParser;
import cn.cheers.x.inspection.task.service.execution.steptree.StepParamModelScope;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeGenerator;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 打开已生成任务时，核对已勾检查项的检查方法和本台参数是否还和生成时的步骤图一致。
 *
 * <p>管什么：已保存的勾选、这台设备当前的适用检查项关联、检查项当前这一档步骤、设备参数包、生成时写下的步骤图。
 * 选择位置、到达位置按点位编号对照；步骤上的名称只用于显示。
 * <p>不管什么：不改勾选，不收回设备占用，不改已写下的占窗。
 * 设备没填、生成时用动作库默认写下的槽，按同一规则和步骤图对照。设备已经填了的仍以设备上的值为准。
 * 现在要哪些参数读动作库当前这一档，不读检查项步骤上留下的名单。
 * 按型号分开的参数和生成步骤图用同一条规则：先对执行设备型号，对不上再看挂载里唯一一个适用型号。
 * 动作耗时只对照占窗：把检查项动作耗时和路径耗时的秒数相加，除以 60 向上取整到整分钟，和生成时占窗用的整分钟比。
 * 巡检内容里的合计现在按秒存。没写下这份秒数时，不记动作耗时这一条。没有动作耗时栏的步骤不占时间。
 * 还没生成的任务直接空结果，页面不提示。
 * 标准库新增、但这条任务没勾上的检查项不比较。
 */
@Service
@RequiredArgsConstructor
public class GeneratedCheckInputReviewService {

    static final String FIELD_STEP_TREE = "step_tree_json";
    static final String FIELD_ACTION_TREE = "action_tree_json";

    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final PatrolItemActionDurationCalculator itemActionDurationCalculator;
    private final GeneratedCheckInputReviewReadSupport readSupport;
    private final ObjectMapper objectMapper;

    public InspectionTaskGeneratedInputReviewRespVO review(Long taskId) {
        PatrolTaskDraft draft = patrolTaskEntityStore.require(taskId);
        return review(draft, readSupport.load(List.of(draft)));
    }

    /**
     * 列表核对只消费当前页已经批量装入的读快照，不再逐任务访问动态业务服务。
     */
    public InspectionTaskGeneratedInputReviewRespVO review(
            PatrolTaskDraft draft,
            GeneratedCheckInputReviewReadContext context) {
        InspectionTaskGeneratedInputReviewRespVO resp = new InspectionTaskGeneratedInputReviewRespVO();
        boolean generated = Boolean.TRUE.equals(draft.orchestrationCommitted());
        resp.setAlreadyGenerated(generated);
        if (!generated || draft.inspectionContent() == null || !StringUtils.hasText(draft.patrolExecutionMode())) {
            return resp;
        }
        Map<String, SnapshotItem> snapshot = snapshotItems(draft.stepTree());
        Map<String, String> pointNames = pointNamesOf(draft.stepTree());
        String means = draft.patrolExecutionMode().trim();
        Long executionEquipmentId = executionEquipmentId(draft, means);
        Long equipmentModelId = context.equipmentModelId(executionEquipmentId);
        List<Long> mountedModelIds = context.mountedModelIds(executionEquipmentId);
        List<InspectionTaskGeneratedInputReviewRespVO.Change> changes = new ArrayList<>();
        Map<Long, Set<Long>> applicableByEquipment = new LinkedHashMap<>();
        for (InspectionContent.ObjectContent object : objectsOf(draft.inspectionContent())) {
            if (object.getObjectId() == null || object.getItems() == null) {
                continue;
            }
            Set<Long> applicable = applicableByEquipment.computeIfAbsent(
                    object.getObjectId(), context::applicableItemIds);
            HostSopParamPack pack = context.hostPack(object.getObjectId());
            for (InspectionContent.ItemContent item : object.getItems()) {
                if (item.getItemId() == null) {
                    continue;
                }
                if (!applicable.contains(item.getItemId())) {
                    appendChanges(changes, object, item, GeneratedCheckInputDiff.noLongerApplicable());
                    continue;
                }
                SnapshotItem saved = snapshot.get(pairKey(object.getObjectId(), item.getItemId()));
                if (saved == null) {
                    continue;
                }
                List<GeneratedCheckInputDiff.StepView> live = liveSteps(
                        item.getItemId(), means, pack, pointNames, equipmentModelId, mountedModelIds, context);
                appendChanges(changes, object, item, GeneratedCheckInputDiff.diffItem(live, saved.steps()));
            }
        }
        appendDurationChange(changes, draft, equipmentModelId, context);
        resp.setChanges(changes);
        return resp;
    }

    /**
     * 动作耗时变了会改变每一段占窗的长度。只记差异，不把新秒数写回任务，也不改已经占下的窗口。
     * 生成时的合计只认巡检内容里写下的那一份秒数。没写就不记这一条。写成 0 就按 0 比。
     * 现在的合计算不出来（某一栏动作耗时空着）时不记这条，避免把缺口说成没变。
     */
    private void appendDurationChange(
            List<InspectionTaskGeneratedInputReviewRespVO.Change> changes,
            PatrolTaskDraft draft,
            Long equipmentModelId,
            GeneratedCheckInputReviewReadContext context
    ) {
        BigDecimal storedAction = PatrolItemActionDurationSupport.minutesOf(draft.inspectionContent());
        BigDecimal liveAction = itemActionDurationCalculator
                .computeLiveSeconds(
                        draft.inspectionContent(), draft.patrolExecutionMode(), equipmentModelId, context)
                .orElse(null);
        if (liveAction == null || storedAction == null) {
            return;
        }
        Integer travel = PatrolPlannedRouteSupport.resolveTravelMinutesOnly(
                draft.plannedRoute(), draft.patrolExecutionMode());
        int travelSeconds = travel == null ? 0 : Math.max(0, travel);
        int liveTotal = PatrolTaskDurationSupport.occupancyMinutes(
                liveAction.add(BigDecimal.valueOf(travelSeconds))
                        .divide(BigDecimal.valueOf(60), 4, java.math.RoundingMode.HALF_UP));
        int baseline = PatrolTaskDurationSupport.occupancyMinutes(
                storedAction.add(BigDecimal.valueOf(travelSeconds))
                        .divide(BigDecimal.valueOf(60), 4, java.math.RoundingMode.HALF_UP));
        if (liveTotal == baseline) {
            return;
        }
        InspectionTaskGeneratedInputReviewRespVO.Change row =
                new InspectionTaskGeneratedInputReviewRespVO.Change();
        row.setAspect("动作耗时");
        row.setDetail("生成时占窗按 " + baseline + " 分钟，现在检查项动作耗时加上路径耗时是 " + liveTotal + " 分钟");
        row.setDurationInput(true);
        changes.add(row);
    }

    private static void appendChanges(
            List<InspectionTaskGeneratedInputReviewRespVO.Change> changes,
            InspectionContent.ObjectContent object,
            InspectionContent.ItemContent item,
            List<GeneratedCheckInputDiff.Change> found
    ) {
        for (GeneratedCheckInputDiff.Change change : found) {
            InspectionTaskGeneratedInputReviewRespVO.Change row =
                    new InspectionTaskGeneratedInputReviewRespVO.Change();
            row.setEquipmentId(object.getObjectId());
            row.setEquipmentName(object.getObjectName());
            row.setInspectionItemId(item.getItemId());
            row.setInspectionItemName(item.getItemName());
            row.setAspect(change.aspect());
            row.setDetail(change.detail());
            row.setRouteInput(change.routeInput());
            row.setDurationInput(change.durationInput());
            row.setSlots(new ArrayList<>(change.slots()));
            changes.add(row);
        }
    }

    /**
     * 机器人和无人机对照已选执行设备的型号。其它巡检方式没有这台执行设备，型号保持空。
     * 读不到设备或型号就保持空，不换另一台设备。
     */
    private Long executionEquipmentId(PatrolTaskDraft draft, String means) {
        if (draft.executionDeviceBinding() == null
                || (!TaskStepTreeGenerator.MEANS_UAV.equals(means)
                && !TaskStepTreeGenerator.MEANS_ROBOT.equals(means))) {
            return null;
        }
        Long equipmentId = draft.executionDeviceBinding().getEquipmentId();
        if (equipmentId == null || equipmentId <= 0) {
            return null;
        }
        return equipmentId;
    }

    private List<GeneratedCheckInputDiff.StepView> liveSteps(
            Long itemId,
            String means,
            HostSopParamPack pack,
            Map<String, String> pointNames,
            Long equipmentModelId,
            List<Long> mountedModelIds,
            GeneratedCheckInputReviewReadContext context
    ) {
        EntityRespDTO entity = context.item(itemId);
        if (entity == null) {
            return List.of();
        }
        Map<String, Object> bag = bagOf(entity);
        Object raw = bag.get(FIELD_STEP_TREE);
        if (raw == null || (raw instanceof String text && !StringUtils.hasText(text))) {
            raw = bag.get(FIELD_ACTION_TREE);
        }
        List<TaskStepTreeGenerator.MethodAction> actions =
                InspectionMethodSnapshotParser.actionsForMeans(raw, means, objectMapper);
        List<GeneratedCheckInputDiff.StepView> steps = new ArrayList<>();
        for (TaskStepTreeGenerator.MethodAction action : actions) {
            String stepKey = StringUtils.hasText(action.sourceNodeKey())
                    ? action.sourceNodeKey().trim()
                    : text(action.refCode());
            String actionRef = StringUtils.hasText(action.refCode()) ? action.refCode().trim() : "";
            EntityRespDTO actionEntity = context.action(action.refCode());
            // 现在要哪些参数只认动作库。检查项步骤上抄下来的名单不参与对比。
            // 分型号参数先对执行设备，再对挂载里唯一适用型号。没对上就保持空名单，不拿另一型号。
            Object slotJson = actionEntity == null ? null : bagOf(actionEntity).get("param_slots_json");
            Long paramModelId = StepParamModelScope.resolve(
                    slotJson, means, equipmentModelId, mountedModelIds, objectMapper);
            Map<String, Object> params = new LinkedHashMap<>(
                    pack.paramsFor(itemId, action.sourceNodeKey(), action.refCode(), means, paramModelId));
            List<String> slots = List.of();
            if (slotJson != null) {
                slots = ActionParamSlotsSupport.fieldCodes(slotJson, means, paramModelId, objectMapper);
                ActionParamSlotsSupport.fillBlankFromDefaults(params, ActionParamSlotsSupport.defaultValues(
                        slotJson, means, paramModelId, objectMapper));
                Long durationModelId = paramModelId != null ? paramModelId : equipmentModelId;
                // 动作耗时不在设备参数包里。写入和对照都用这一型号的默认值换成的整数秒。
                ActionDurationParamSupport.readSlot(slotJson, means, durationModelId, objectMapper)
                        .ifPresent(duration -> params.put(ActionDurationParamSupport.FIELD_CODE, duration.seconds()));
            }
            steps.add(new GeneratedCheckInputDiff.StepView(
                    stepKey,
                    actionRef,
                    slots,
                    GeneratedCheckInputDiff.valuesOf(slots, params, pointNames)));
        }
        return steps;
    }

    @SuppressWarnings("unchecked")
    private Map<String, SnapshotItem> snapshotItems(Object stepTree) {
        Map<String, String> pointNames = pointNamesOf(stepTree);
        Map<String, SnapshotItem> items = new LinkedHashMap<>();
        if (!(stepTree instanceof Map<?, ?> tree)) {
            return items;
        }
        Object nodesRaw = tree.get("nodes");
        if (!(nodesRaw instanceof List<?> nodes)) {
            return items;
        }
        Map<String, Map<String, Object>> byKey = new LinkedHashMap<>();
        for (Object raw : nodes) {
            if (raw instanceof Map<?, ?> row) {
                Map<String, Object> node = (Map<String, Object>) row;
                String key = text(node.get("nodeKey"));
                if (StringUtils.hasText(key)) {
                    byKey.put(key, node);
                }
            }
        }
        for (Map<String, Object> node : byKey.values()) {
            if (!"inspection_item".equals(text(node.get("hangTypeCode")))) {
                continue;
            }
            Long itemId = longOf(node.get("refId"));
            Long equipmentId = ancestorRefId(node, byKey, "equipment");
            if (itemId == null || equipmentId == null) {
                continue;
            }
            List<GeneratedCheckInputDiff.StepView> steps = new ArrayList<>();
            for (Map<String, Object> action : childActions(text(node.get("nodeKey")), byKey)) {
                String stepKey = text(action.get("sourceNodeKey"));
                if (!StringUtils.hasText(stepKey)) {
                    stepKey = text(action.get("refCode"));
                }
                List<String> declared = stringList(action.get("declaredSlots"));
                steps.add(new GeneratedCheckInputDiff.StepView(
                        stepKey,
                        text(action.get("refCode")),
                        declared,
                        GeneratedCheckInputDiff.valuesOf(declared, mapOf(action.get("params")), pointNames)));
            }
            items.put(pairKey(equipmentId, itemId), new SnapshotItem(steps));
        }
        return items;
    }

    private static List<Map<String, Object>> childActions(String parentKey, Map<String, Map<String, Object>> byKey) {
        List<Map<String, Object>> actions = new ArrayList<>();
        if (!StringUtils.hasText(parentKey)) {
            return actions;
        }
        for (Map<String, Object> node : byKey.values()) {
            if (!"action".equals(text(node.get("hangTypeCode")))) {
                continue;
            }
            if (parentKey.equals(text(node.get("parentNodeKey")))) {
                actions.add(node);
            }
        }
        actions.sort((left, right) -> Integer.compare(intOf(left.get("order")), intOf(right.get("order"))));
        return actions;
    }

    private static Long ancestorRefId(Map<String, Object> node, Map<String, Map<String, Object>> byKey, String hangType) {
        String parentKey = text(node.get("parentNodeKey"));
        int guard = 0;
        while (StringUtils.hasText(parentKey) && guard++ < 20) {
            Map<String, Object> parent = byKey.get(parentKey);
            if (parent == null) {
                return null;
            }
            if (hangType.equals(text(parent.get("hangTypeCode")))) {
                return longOf(parent.get("refId"));
            }
            parentKey = text(parent.get("parentNodeKey"));
        }
        return null;
    }

    private static List<InspectionContent.ObjectContent> objectsOf(InspectionContent content) {
        List<InspectionContent.ObjectContent> objects = new ArrayList<>();
        if (content.getGroups() != null) {
            for (InspectionContent.ObjectGroup group : content.getGroups()) {
                if (group.getObjects() != null) {
                    objects.addAll(group.getObjects());
                }
            }
        }
        if (content.getCustomObjects() != null) {
            objects.addAll(content.getCustomObjects());
        }
        return objects;
    }

    private static String pairKey(Long equipmentId, Long itemId) {
        return equipmentId + ":" + itemId;
    }

    private record SnapshotItem(List<GeneratedCheckInputDiff.StepView> steps) {
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

    @SuppressWarnings("unchecked")
    private static Map<String, String> pointNamesOf(Object stepTree) {
        if (!(stepTree instanceof Map<?, ?> tree)) {
            return Map.of();
        }
        Object raw = tree.get("pointNames");
        if (!(raw instanceof Map<?, ?> names)) {
            return Map.of();
        }
        Map<String, String> pointNames = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : names.entrySet()) {
            String key = text(entry.getKey());
            String name = text(entry.getValue());
            if (StringUtils.hasText(key) && StringUtils.hasText(name)) {
                pointNames.put(key, name);
            }
        }
        return pointNames;
    }

    private static Map<String, Object> mapOf(Object raw) {
        if (raw instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    private static List<String> stringList(Object raw) {
        if (!(raw instanceof List<?> list)) {
            return List.of();
        }
        List<String> values = new ArrayList<>();
        for (Object row : list) {
            String text = text(row);
            if (StringUtils.hasText(text)) {
                values.add(text);
            }
        }
        return values;
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    private static Long longOf(Object raw) {
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

    private static int intOf(Object raw) {
        if (raw instanceof Number number) {
            return number.intValue();
        }
        return 0;
    }
}
