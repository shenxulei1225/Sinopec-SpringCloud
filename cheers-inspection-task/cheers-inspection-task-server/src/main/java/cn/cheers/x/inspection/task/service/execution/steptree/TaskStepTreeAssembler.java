package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * 把已算好的任务步骤图拆成：打包下发的动作、任务里给人看的准备/检查步骤。
 * <p>下发参数只读该步在任务步骤树上已经写下的那一份。每个要下发的动作单独成一条指令。
 * <p>动作是否下发只认生成任务时冻结的 actionDomain。设备动作统一进入协议网关；
 * 具体协议指令与打包方式由指令协议对接决定。非设备动作仍留在任务页。
 * <p>执行账上的 {@code stepOrder} 与步骤图节点 {@code order} 一致，不按写入先后编号；只读 {@code step_order} 排兄弟节点时与核对计划同一顺序。
 * <p>禁止：把一个动作的参数抄进另一个动作；按标题或指令编号合并；下发时再读设备参数包；用点位对步。
 */
public final class TaskStepTreeAssembler {

    public static final String PREPARE_STEP_CODE = "prepare";
    public static final String SEQ_PREFIX = "seq-";
    static final String DOMAIN_DEVICE = "DEVICE";

    private TaskStepTreeAssembler() {
    }

    public static String sequenceStepCode(int sequence) {
        return SEQ_PREFIX + sequence;
    }

    /**
     * @param nodes           创建任务时已算好的步骤；动作参数以步骤上已写下的为准
     * @param resolveActionId 只有识别码时向动作库要主键
     */
    public static AssembledOpenRun assemble(
            List<TaskStepNode> nodes,
            Function<String, Optional<Long>> resolveActionId
    ) {
        return assemble(nodes, resolveActionId, null);
    }

    /**
     * @param protocolVersion 保留调用契约。动作节点已经冻结当前协议角色，组装时不再按协议或标题二次判断。
     */
    public static AssembledOpenRun assemble(
            List<TaskStepNode> nodes,
            Function<String, Optional<Long>> resolveActionId,
            String protocolVersion
    ) {
        if (nodes == null || nodes.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException(
                    "任务没有执行步骤图，请先在排期与资源处理完冲突或完成智能编排");
        }
        Map<String, TaskStepNode> byKey = indexByKey(nodes);
        List<TaskExecutionStartReqDTO.StepDraft> sessionSteps = new ArrayList<>();
        emitPrepareIfNeeded(nodes, byKey, sessionSteps);
        emitVisitPoints(nodes, sessionSteps);
        emitInspectionItems(nodes, byKey, sessionSteps);

        List<DispatchAction> dispatchActions = new ArrayList<>();
        List<StepBinding> bindings = new ArrayList<>();
        int sequence = 0;
        for (int i = 0; i < nodes.size(); i++) {
            TaskStepNode node = nodes.get(i);
            if (!node.isAction()) {
                continue;
            }
            Long itemId = ancestorInspectionItemId(node, byKey);
            String visitPoint = itemId == null ? visitPointKey(node, byKey) : null;
            String parentCode = itemId != null
                    ? inspectionItemStepCode(byKey, itemId, node.parentNodeKey())
                    : visitPoint != null ? visitPoint : PREPARE_STEP_CODE;
            boolean dispatch = shouldDispatch(node);
            String stepCode = dispatch ? sequenceStepCode(sequence) : actionKeepCode(node, i);
            sessionSteps.add(toDraft(node, node.order(), stepCode, parentCode, dispatch, sequence));
            if (!dispatch) {
                continue;
            }
            Long actionId = requireActionId(node, resolveActionId);
            dispatchActions.add(new DispatchAction(actionId, copyParams(node.params()), node.refCode()));
            bindings.add(new StepBinding(stepCode, sequence, actionId, itemId));
            sequence++;
        }
        if (dispatchActions.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException(
                    "任务步骤图没有归属为设备下发或航线编排的动作");
        }
        sortSessionSteps(sessionSteps);
        return new AssembledOpenRun(
                List.copyOf(dispatchActions),
                List.copyOf(sessionSteps),
                List.copyOf(bindings));
    }

    /** 只拷贝这一步自己的参数。不从相邻动作补。 */
    private static Map<String, Object> copyParams(Map<String, Object> params) {
        return params == null || params.isEmpty() ? Map.of() : new LinkedHashMap<>(params);
    }

    static boolean shouldDispatch(TaskStepNode node) {
        if (node == null || !node.isAction()) {
            return false;
        }
        return DOMAIN_DEVICE.equals(node.actionDomain());
    }

    private static void emitPrepareIfNeeded(
            List<TaskStepNode> nodes,
            Map<String, TaskStepNode> byKey,
            List<TaskExecutionStartReqDTO.StepDraft> sessionSteps
    ) {
        boolean needPrepare = false;
        for (TaskStepNode node : nodes) {
            if (node.isAction()
                    && ancestorInspectionItemId(node, byKey) == null
                    && visitPointKey(node, byKey) == null) {
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

    /** 检查途经的点位。起点和终点仍走任务准备，不在这里再挂一层。 */
    private static void emitVisitPoints(
            List<TaskStepNode> nodes,
            List<TaskExecutionStartReqDTO.StepDraft> sessionSteps
    ) {
        Set<String> seen = new LinkedHashSet<>();
        for (TaskStepNode node : nodes) {
            if (!node.isStopGroup() || !TaskStepNode.HANG_POINT.equals(node.hangTypeCode())) {
                continue;
            }
            if (isTerminalPoint(node.nodeKey()) || seen.contains(node.nodeKey())) {
                continue;
            }
            seen.add(node.nodeKey());
            TaskExecutionStartReqDTO.StepDraft point = new TaskExecutionStartReqDTO.StepDraft();
            point.setName(displayName(node));
            point.setStepCode(node.nodeKey());
            point.setStepOrder(node.order());
            point.setStepTitle(displayName(node));
            point.setStepType(TaskStepNode.HANG_POINT);
            point.setStepRequired(true);
            Map<String, Object> source = new LinkedHashMap<>();
            source.put("hangTypeCode", TaskStepNode.HANG_POINT);
            source.put("refCode", node.refCode());
            source.put("displayGroup", "point");
            point.setSource(source);
            sessionSteps.add(point);
        }
    }

    private static boolean isTerminalPoint(String nodeKey) {
        return "stop-start".equals(nodeKey) || "stop-end".equals(nodeKey);
    }

    /** 动作直接挂在途经点上时，上报步骤的上级是这个点，不是任务准备。 */
    private static String visitPointKey(TaskStepNode node, Map<String, TaskStepNode> byKey) {
        String parentKey = node.parentNodeKey();
        int guard = 0;
        while (parentKey != null && guard++ < byKey.size()) {
            TaskStepNode parent = byKey.get(parentKey);
            if (parent == null) {
                return null;
            }
            if (TaskStepNode.HANG_POINT.equals(parent.hangTypeCode()) && !isTerminalPoint(parent.nodeKey())) {
                return parent.nodeKey();
            }
            if (parent.isInspectionItem()) {
                return null;
            }
            parentKey = parent.parentNodeKey();
        }
        return null;
    }

    /**
     * 检查项挂在所属途经点下面，和任务步骤图一致。
     * 没有途经点（整台设备都不经过点位）时不写上级，仍作为根。
     */
    private static void emitInspectionItems(
            List<TaskStepNode> nodes,
            Map<String, TaskStepNode> byKey,
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
            item.setStepOrder(node.order());
            item.setStepTitle(displayName(node));
            item.setStepType(TaskStepNode.HANG_INSPECTION_ITEM);
            item.setStepRequired(true);
            String pointKey = visitPointKey(node, byKey);
            if (pointKey != null) {
                item.setParentStepCode(pointKey);
            }
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

    private static void sortSessionSteps(List<TaskExecutionStartReqDTO.StepDraft> sessionSteps) {
        sessionSteps.sort(Comparator
                .comparingInt(TaskExecutionStartReqDTO.StepDraft::getStepOrder)
                .thenComparing(
                        TaskExecutionStartReqDTO.StepDraft::getStepCode,
                        Comparator.nullsLast(String::compareTo)));
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
        source.put("actionDomain", node.actionDomain());
        source.put("analysisCapabilityId", node.analysisCapabilityId());
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

    /**
     * @param dispatchActions 打进指令包的动作，顺序即包内 sequence
     * @param sessionSteps    任务页步骤：准备、途经点、其下检查项、检查项下的动作
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
