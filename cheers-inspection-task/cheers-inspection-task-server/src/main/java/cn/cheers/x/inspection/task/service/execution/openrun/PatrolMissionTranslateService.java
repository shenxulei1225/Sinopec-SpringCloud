package cn.cheers.x.inspection.task.service.execution.openrun;

import cn.cheers.x.device.protocolgateway.api.DeviceProtocolMissionApi;
import cn.cheers.x.device.protocolgateway.api.dto.MissionTranslateCommitRespDTO;
import cn.cheers.x.device.protocolgateway.api.dto.MissionTranslatePreviewRespDTO;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.service.execution.steptree.PatrolTaskStepTreeReader;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepNode;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeAssembler;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeParser;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskRecordIngestAllocator;
import cn.cheers.x.inspection.task.service.task.TaskPatrolRecordingSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 任务创建第 3 步「生成步骤图后、进核对前」的试译，以及「生成任务」时的正式翻译。
 * <p>试译不落盘，失败应留在第 3 步；生成任务 commit 时航线与下行报文写入待执行快照。开跑只下发，不再 translate。
 */
@Service
@RequiredArgsConstructor
public class PatrolMissionTranslateService {

    private final DeviceProtocolMissionApi deviceProtocolMissionApi;
    private final PatrolTaskStepTreeReader taskStepTreeCatalog;
    private final PatrolTaskRecordIngestAllocator patrolTaskRecordIngestAllocator;
    private final ObjectMapper objectMapper;

    /**
     * 第 3 步生成步骤图后、进入核对前调用：对照与航线/KMZ 有问题则当场报错，不得进核对。
     * 东古/机器人等需要平台收流的协议须传入 {@code scheduleSlotIdForIngest}，试译前分配收流 URL。
     */
    public void assertPreviewTranslate(PatrolTaskDraft draft, Object executionStepTree) {
        assertPreviewTranslate(draft, executionStepTree, List.<String>of());
    }

    /**
     * @param scheduleSlotIdForIngest 本页第一个计划点 id；需要收流时用于 {@link PatrolTaskRecordIngestAllocator#allocate}
     */
    public void assertPreviewTranslate(
            PatrolTaskDraft draft, Object executionStepTree, String scheduleSlotIdForIngest) {
        if (scheduleSlotIdForIngest == null || scheduleSlotIdForIngest.isBlank()) {
            assertPreviewTranslate(draft, executionStepTree, List.of());
            return;
        }
        assertPreviewTranslate(draft, executionStepTree, List.of(scheduleSlotIdForIngest.trim()));
    }

    /**
     * 各计划点的指令相同，试译只做一次。
     * 需要平台收流且页面上已有计划点时，用第一个计划点分配收流地址。
     * 还没有计划点时仍试译这一份指令，不另编一份。
     */
    public void assertPreviewTranslate(
            PatrolTaskDraft draft, Object executionStepTree, List<String> scheduleSlotIdsForIngest) {
        PatrolTaskDraft withTree = withStepTree(draft, executionStepTree);
        ExecutionDeviceBinding binding = withTree.executionDeviceBinding();
        boolean needsIngest = binding != null
                && TaskPatrolRecordingSupport.usesServerIngest(binding.getProtocolCode());
        List<String> slotIds = scheduleSlotIdsForIngest == null
                ? List.of()
                : scheduleSlotIdsForIngest.stream().filter(StringUtils::hasText).map(String::trim).distinct().toList();
        if (!needsIngest || slotIds.isEmpty()) {
            previewOnce(withTree, null);
            return;
        }
        String mediaStreamUrl = resolvePreviewMediaStreamUrl(withTree, slotIds.get(0));
        previewOnce(withTree, mediaStreamUrl);
    }

    private void previewOnce(PatrolTaskDraft withTree, String mediaStreamUrl) {
        DeviceMissionPlan plan = toMissionPlan(withTree, previewStorageKey(withTree.id()), mediaStreamUrl);
        CommonResult<MissionTranslatePreviewRespDTO> rpc = deviceProtocolMissionApi.previewTranslate(plan);
        if (rpc == null || !rpc.isSuccess() || rpc.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    formatPreviewTranslateFailure(rpc != null ? rpc.getMsg() : null));
        }
        MissionTranslatePreviewRespDTO preview = rpc.getData();
        if (!preview.ok()) {
            throw ServiceExceptionUtil.invalidParamException(
                    StringUtils.hasText(preview.failureReason())
                            ? preview.failureReason()
                            : "指令或航线试译未通过，请完善检查参数与协议对照");
        }
    }

    /**
     * 生成任务物化待执行前：正式翻译并落盘航线（若适用）。
     */
    public MissionTranslateCommitRespDTO commitForMaterialize(PatrolTaskDraft task) {
        return commitForMaterialize(task, materializeStorageKey(task.id()), null);
    }

    /**
     * 一次正式翻译。东古、机器人传入本批第一个计划点的收流地址，网关写入开始录像。
     * 其余执行点不另译，由物化把报文里的地址换成该点自己的地址。
     *
     * @param mediaStreamUrl 非空时写入 {@link DeviceMissionPlan#mediaStreamUrl()}
     */
    public MissionTranslateCommitRespDTO commitForMaterialize(
            PatrolTaskDraft task, String storageKey, String mediaStreamUrl) {
        DeviceMissionPlan plan = toMissionPlan(task, storageKey, mediaStreamUrl);
        CommonResult<MissionTranslateCommitRespDTO> rpc = deviceProtocolMissionApi.commitTranslate(plan);
        if (rpc == null || !rpc.isSuccess() || rpc.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    rpc != null && StringUtils.hasText(rpc.getMsg()) ? rpc.getMsg() : "指令正式翻译失败");
        }
        if (rpc.getData().pieces() == null || rpc.getData().pieces().isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("指令正式翻译未产生可下发报文");
        }
        return rpc.getData();
    }

    private DeviceMissionPlan toMissionPlan(PatrolTaskDraft draft, String storageKey) {
        return toMissionPlan(draft, storageKey, null);
    }

    private DeviceMissionPlan toMissionPlan(
            PatrolTaskDraft draft, String storageKey, String mediaStreamUrl) {
        ExecutionDeviceBinding binding = requireBinding(draft);
        TaskStepTreeAssembler.AssembledOpenRun assembled = assembleFromDraft(draft);
        List<DispatchAction> actions = assembled.dispatchActions();
        if (actions == null || actions.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("步骤图没有可下发的动作，不能试译或生成指令");
        }
        String stream = mediaStreamUrl == null || mediaStreamUrl.isBlank() ? null : mediaStreamUrl.trim();
        return new DeviceMissionPlan(
                binding.getProtocolCode().trim(),
                binding.getLogicalDeviceId().trim(),
                storageKey,
                storageKey,
                actions,
                binding.getEquipmentId(),
                null,
                stream);
    }

    private static ExecutionDeviceBinding requireBinding(PatrolTaskDraft draft) {
        ExecutionDeviceBinding binding = draft.executionDeviceBinding();
        if (binding == null
                || !StringUtils.hasText(binding.getProtocolCode())
                || !StringUtils.hasText(binding.getLogicalDeviceId())) {
            throw ServiceExceptionUtil.invalidParamException("任务未绑定完整执行设备与协议，不能试译指令");
        }
        return binding;
    }

    public static PatrolTaskDraft withStepTree(PatrolTaskDraft draft, Object executionStepTree) {
        return new PatrolTaskDraft(
                draft.id(),
                draft.name(),
                draft.domain(),
                draft.facilityId(),
                draft.patrolExecutionMode(),
                draft.inspectionContent(),
                draft.executionDeviceBinding(),
                draft.resourcePolicy(),
                executionStepTree,
                draft.plannedRoute(),
                draft.manageStatus(),
                draft.createUnlockedStep(),
                draft.startStopId(),
                draft.endStopId(),
                draft.runtimeJobId(),
                draft.orchestrationCommitted(),
                draft.scheduleConfig(),
                draft.schedulePolicyId(),
                draft.orchestrationPreviewSlots(),
                draft.orchestrationPreviewPlainSummary(),
                draft.updateHint(),
                draft.taskPreparation());
    }

    private static String formatPreviewTranslateFailure(String rpcMsg) {
        String msg = StringUtils.hasText(rpcMsg) ? rpcMsg.trim() : "指令试译失败";
        if (msg.contains("preview-translate") && msg.contains("不存在")) {
            return msg
                    + "。请确认 Nacos 中 cheers-device-protocol-gateway-server 已注册、8095 网关进程在运行，"
                    + "且 inspection-task Feign 未指向错误实例（非 Spring 映射继承问题）";
        }
        return msg;
    }

    private String resolvePreviewMediaStreamUrl(PatrolTaskDraft draft, String scheduleSlotIdForIngest) {
        ExecutionDeviceBinding binding = draft.executionDeviceBinding();
        if (binding == null || !StringUtils.hasText(binding.getProtocolCode())) {
            return null;
        }
        if (!TaskPatrolRecordingSupport.usesServerIngest(binding.getProtocolCode())) {
            return null;
        }
        if (!StringUtils.hasText(scheduleSlotIdForIngest)) {
            return null;
        }
        return patrolTaskRecordIngestAllocator.allocate(draft.id(), scheduleSlotIdForIngest.trim());
    }

    private static String previewStorageKey(Long taskId) {
        return "patrol-preview-" + taskId;
    }

    static String materializeStorageKey(Long taskId) {
        return "patrol-task-" + taskId;
    }

    private TaskStepTreeAssembler.AssembledOpenRun assembleFromDraft(PatrolTaskDraft task) {
        List<TaskStepNode> nodes = TaskStepTreeParser.parse(task.stepTree(), objectMapper);
        if (nodes.isEmpty()) {
            nodes = taskStepTreeCatalog.loadPatrolTaskStepTree(task.id());
        }
        String protocol = task.executionDeviceBinding() == null
                ? null
                : task.executionDeviceBinding().getProtocolCode();
        return TaskStepTreeAssembler.assemble(nodes, taskStepTreeCatalog::resolveActionId, protocol);
    }
}
