package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * 把已算好的任务步骤图拆成：打包下发的动作、任务里给人看的准备/检查步骤。
 * <p>任务页只展「任务准备 → 检查步骤完成」。包内序号写在步骤上，上报按序号对回检查步骤。
 * <p>禁止：现读检查项方法重算整树；步骤上存参当权威；用点位对步。
 */
public final class TaskStepTreeAssembler {

    public static final String PREPARE_STEP_CODE = "prepare";
    public static final String SEQ_PREFIX = "seq-";

    private TaskStepTreeAssembler() {
    }

    public static String sequenceStepCode(int sequence) {
        return SEQ_PREFIX + sequence;
    }

    /**
     * @param nodes                 创建任务时已算好的步骤
     * @param packByInspectionItem  被检查设备上、按检查项取的参数包；准备动作没有检查项则空袋
     * @param resolveActionId       只有识别码时向动作库要主键
     */
    public static AssembledOpenRun assemble(
            List<TaskStepNode> nodes,
            Function<Long, HostSopParamPack> packByInspectionItem,
            Function<String, Optional<Long>> resolveActionId
    ) {
        if (nodes == null || nodes.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException(
                    "任务没有执行步骤图，请先在排期与资源处理完冲突或完成智能编排");
        }
        Map<String, TaskStepNode> byKey = indexByKey(nodes);
        List<TaskExecutionStartReqDTO.StepDraft> sessionSteps = new ArrayList<>();
        emitPrepareIfNeeded(nodes, byKey, sessionSteps);
        emitInspectionItems(nodes, sessionSteps);

        List<DispatchAction> dispatchActions = new ArrayList<>();
        List<StepBinding> bindings = new ArrayList<>();
        int sequence = 0;
        for (int i = 0; i < nodes.size(); i++) {
            TaskStepNode node = nodes.get(i);
            if (!node.isAction()) {
                continue;
            }
            Long itemId = ancestorInspectionItemId(node, byKey);
            String parentCode = itemId != null
                    ? inspectionItemStepCode(byKey, itemId, node.parentNodeKey())
                    : PREPARE_STEP_CODE;
            boolean dispatch = shouldDispatch(node);
            String stepCode = dispatch ? sequenceStepCode(sequence) : actionKeepCode(node, i);
            sessionSteps.add(toDraft(node, sessionSteps.size(), stepCode, parentCode, dispatch, sequence));
            if (!dispatch) {
                continue;
            }
            Long actionId = requireActionId(node, resolveActionId);
            HostSopParamPack pack = itemId == null || packByInspectionItem == null
                    ? HostSopParamPack.empty()
                    : defaultPack(packByInspectionItem.apply(itemId));
            Map<String, Object> params = pack.paramsFor(itemId, node.nodeKey(), node.refCode());
            dispatchActions.add(new DispatchAction(actionId, params));
            bindings.add(new StepBinding(stepCode, sequence, actionId, itemId));
            sequence++;
        }
        if (dispatchActions.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException(
                    "任务步骤图没有可下发的动作。人工确认和智能识别不会发给终端");
        }
        return new AssembledOpenRun(
                List.copyOf(dispatchActions),
                List.copyOf(sessionSteps),
                List.copyOf(bindings));
    }

    static boolean shouldDispatch(TaskStepNode node) {
        if (node == null || !node.isAction()) {
            return false;
        }
        return !isHumanOrIdentify(node.title()) && !isHumanOrIdentify(node.refCode());
    }

    private static boolean isHumanOrIdentify(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return text.contains("人工确认") || text.contains("智能识别");
    }

    private static void emitPrepareIfNeeded(
            List<TaskStepNode> nodes,
            Map<String, TaskStepNode> byKey,
            List<TaskExecutionStartReqDTO.StepDraft> sessionSteps
    ) {
        boolean needPrepare = false;
        for (TaskStepNode node : nodes) {
            if (node.isAction() && ancestorInspectionItemId(node, byKey) == null) {
                needPrepare = true;
                break;
            }
        }
        if (!needPrepare) {
            return;
        }
        TaskExecutionStartReqDTO.StepDraft prepare = new TaskExecutionStartReqDTO.StepDraft();
        prepare.setName("任务准备");
        prepare.setStepCode(PREPARE_STEP_CODE);
        prepare.setStepOrder(0);
        prepare.setStepTitle("任务准备");
        prepare.setStepType("prepare");
        prepare.setStepRequired(true);
        prepare.setSource(Map.of("displayGroup", "prepare"));
        sessionSteps.add(prepare);
    }

    private static void emitInspectionItems(
            List<TaskStepNode> nodes,
            List<TaskExecutionStartReqDTO.StepDraft> sessionSteps
    ) {
        Set<String> seen = new LinkedHashSet<>();
        for (TaskStepNode node : nodes) {
            if (!node.isInspectionItem() || seen.contains(node.nodeKey())) {
                continue;
            }
            seen.add(node.nodeKey());
            TaskExecutionStartReqDTO.StepDraft item = new TaskExecutionStartReqDTO.StepDraft();
            item.setName(displayName(node));
            item.setStepCode(node.nodeKey());
            item.setStepOrder(sessionSteps.size());
            item.setStepTitle(displayName(node));
            item.setStepType(TaskStepNode.HANG_INSPECTION_ITEM);
            item.setStepRequired(true);
            Map<String, Object> source = new LinkedHashMap<>();
            source.put("hangTypeCode", TaskStepNode.HANG_INSPECTION_ITEM);
            source.put("refId", node.refId());
            source.put("displayGroup", "inspection");
            item.setSource(source);
            sessionSteps.add(item);
        }
    }

    private static String inspectionItemStepCode(
            Map<String, TaskStepNode> byKey, Long itemId, String parentKey
    ) {
        if (parentKey != null && byKey.containsKey(parentKey) && byKey.get(parentKey).isInspectionItem()) {
            return parentKey;
        }
        for (TaskStepNode node : byKey.values()) {
            if (node.isInspectionItem() && itemId != null && itemId.equals(node.refId())) {
                return node.nodeKey();
            }
        }
        return PREPARE_STEP_CODE;
    }

    private static String actionKeepCode(TaskStepNode node, int index) {
        return node.nodeKey().isBlank() ? "keep-" + (index + 1) : node.nodeKey();
    }

    private static Long requireActionId(
            TaskStepNode node,
            Function<String, Optional<Long>> resolveActionId
    ) {
        if (node.refId() != null && node.refId() > 0) {
            return node.refId();
        }
        if (node.refCode() != null && resolveActionId != null) {
            Optional<Long> resolved = resolveActionId.apply(node.refCode());
            if (resolved != null && resolved.isPresent() && resolved.get() > 0) {
                return resolved.get();
            }
        }
        throw ServiceExceptionUtil.invalidParamException(
                "步骤「" + displayName(node) + "」对不上动作库");
    }

    private static Long ancestorInspectionItemId(TaskStepNode node, Map<String, TaskStepNode> byKey) {
        String parentKey = node.parentNodeKey();
        int guard = 0;
        while (parentKey != null && guard++ < byKey.size()) {
            TaskStepNode parent = byKey.get(parentKey);
            if (parent == null) {
                return null;
            }
            if (parent.isInspectionItem()) {
                return parent.refId();
            }
            parentKey = parent.parentNodeKey();
        }
        return null;
    }

    private static Map<String, TaskStepNode> indexByKey(List<TaskStepNode> nodes) {
        Map<String, TaskStepNode> byKey = new LinkedHashMap<>();
        for (TaskStepNode node : nodes) {
            if (!node.nodeKey().isBlank()) {
                byKey.put(node.nodeKey(), node);
            }
        }
        return byKey;
    }

    private static TaskExecutionStartReqDTO.StepDraft toDraft(
            TaskStepNode node,
            int order,
            String stepCode,
            String parentCode,
            boolean dispatched,
            int sequence
    ) {
        TaskExecutionStartReqDTO.StepDraft draft = new TaskExecutionStartReqDTO.StepDraft();
        String name = displayName(node);
        draft.setName(name);
        draft.setStepCode(stepCode);
        draft.setStepOrder(order);
        draft.setStepTitle(name);
        draft.setStepType(node.hangTypeCode());
        draft.setStepRequired(true);
        draft.setParentStepCode(parentCode);
        Map<String, Object> source = new LinkedHashMap<>();
        source.put("hangTypeCode", node.hangTypeCode());
        source.put("refCode", node.refCode());
        source.put("refId", node.refId());
        source.put("nodeKey", node.nodeKey());
        source.put("dispatched", dispatched);
        if (dispatched) {
            source.put("sequence", sequence);
        }
        draft.setSource(source);
        return draft;
    }

    private static String displayName(TaskStepNode node) {
        if (!node.title().isBlank()) {
            return node.title();
        }
        if (node.refCode() != null) {
            return node.refCode();
        }
        return node.refId() == null ? node.nodeKey() : String.valueOf(node.refId());
    }

    private static HostSopParamPack defaultPack(HostSopParamPack pack) {
        return pack == null ? HostSopParamPack.empty() : pack;
    }

    /**
     * @param dispatchActions 打进指令包的动作，顺序即包内 sequence
     * @param sessionSteps    任务页步骤：准备、检查项、其下动作（含人工确认/识别）
     * @param bindings        步骤 ↔ 包内序号，上报用
     */
    public record AssembledOpenRun(
            List<DispatchAction> dispatchActions,
            List<TaskExecutionStartReqDTO.StepDraft> sessionSteps,
            List<StepBinding> bindings
    ) {
    }

    public record StepBinding(
            String stepCode,
            int sequence,
            long actionId,
            Long inspectionItemId
    ) {
    }
}
