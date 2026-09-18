package cn.cheers.x.inspection.task.service.execution.impl;

import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.service.execution.InspectionTaskStartExecutionService;
import cn.cheers.x.inspection.task.service.execution.steptree.EntityRpcTaskStepTreeCatalog;
import cn.cheers.x.inspection.task.service.execution.steptree.InspectedHostPackIndex;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepNode;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeAssembler;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.StrategyRuntimeApi;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyHandleRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 巡检「开始执行」：组好步骤和设备清单，发出「人点了开始」。
 * <p>新建账、发给设备、标进行中由条件策略按顺序做。本方法不自己写账、不下发。
 * <p>不负责：排期生成待执行、协议监测展示、现读检查项方法重算。
 * <p>禁止：改旧任务上的设备运行态当第二本账；用点位对步；用执行设备参数包冒充被检设备。
 */
@Service
@RequiredArgsConstructor
public class InspectionTaskStartExecutionServiceImpl implements InspectionTaskStartExecutionService {

    /** 巡检域执行记录型号（seed：exec_patrol_round） */
    public static final String PATROL_EXEC_MODEL_CODE = "exec_patrol_round";
    public static final String PATROL_RECORD_TYPE = "task_record_patrol";

    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final StrategyRuntimeApi strategyRuntimeApi;
    private final EntityRpcTaskStepTreeCatalog taskStepTreeCatalog;

    @Override
    public MissionStartRespDTO startExecution(Long taskId) {
        PatrolTaskDraft task = patrolTaskEntityStore.require(taskId);
        ExecutionDeviceBinding binding = requireCompleteBinding(task);
        requireKnownProtocol(binding);
        List<TaskStepNode> nodes = taskStepTreeCatalog.requireStepTree(task.id());
        InspectedHostPackIndex packs = InspectedHostPackIndex.from(
                task.inspectionContent(), taskStepTreeCatalog::loadHostPack);
        TaskStepTreeAssembler.AssembledOpenRun assembled = TaskStepTreeAssembler.assemble(
                nodes, packs::packForItem, taskStepTreeCatalog::resolveActionId);
        StrategyHandleRespDTO handled = publishStart(task, binding, assembled);
        if (Boolean.FALSE.equals(handled.getDispatchSuccess())) {
            return new MissionStartRespDTO(
                    false,
                    Boolean.TRUE.equals(handled.getDispatchOnline()),
                    Boolean.TRUE.equals(handled.getDispatchCommandSent()),
                    Boolean.TRUE.equals(handled.getDispatchStartupSent()),
                    handled.getDispatchFailureReason(),
                    handled.getDispatchCommandWireJson());
        }
        if (handled.getExecutionRecordId() == null) {
            throw ServiceExceptionUtil.invalidParamException("人点了开始后没有新建这次执行的账");
        }
        return new MissionStartRespDTO(
                true,
                handled.getDispatchOnline() == null || handled.getDispatchOnline(),
                handled.getDispatchCommandSent() == null || handled.getDispatchCommandSent(),
                handled.getDispatchStartupSent() == null || handled.getDispatchStartupSent(),
                null,
                handled.getDispatchCommandWireJson());
    }

    private StrategyHandleRespDTO publishStart(
            PatrolTaskDraft task,
            ExecutionDeviceBinding binding,
            TaskStepTreeAssembler.AssembledOpenRun assembled
    ) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("source", "inspection-start-execution");
        snapshot.put("taskDefinitionId", task.id());
        snapshot.put("protocolVersion", binding.getProtocolCode());
        snapshot.put("dispatchActionCount", assembled.dispatchActions().size());
        snapshot.put("stepBindings", assembled.bindings());

        StrategyTriggerEventDTO event = new StrategyTriggerEventDTO();
        event.setEventType(StrategyTriggerEventDTO.EVENT_EXECUTION_START);
        event.setEntityTypeCode(PATROL_RECORD_TYPE);
        event.setTaskDefinitionId(task.id());
        event.setModelCode(PATROL_EXEC_MODEL_CODE);
        event.setExecutionName(StringUtils.hasText(task.name())
                ? task.name() + "-执行"
                : "巡检执行-" + task.id());
        event.setStandardSnapshot(snapshot);
        event.setSteps(toMaps(assembled.sessionSteps()));
        event.setProtocolVersion(binding.getProtocolCode().trim());
        event.setLogicalDeviceId(binding.getLogicalDeviceId().trim());
        event.setDispatchActions(toActionMaps(assembled.dispatchActions()));

        CommonResult<StrategyHandleRespDTO> rpc = strategyRuntimeApi.handle(event);
        if (rpc == null || !rpc.isSuccess() || rpc.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    rpc != null && StringUtils.hasText(rpc.getMsg())
                            ? rpc.getMsg()
                            : "人点了开始后策略没有处理完");
        }
        if (!rpc.getData().isMatched()) {
            throw ServiceExceptionUtil.invalidParamException(
                    StringUtils.hasText(rpc.getData().getSkipReason())
                            ? rpc.getData().getSkipReason()
                            : "人点了开始没有命中策略");
        }
        return rpc.getData();
    }

    private static List<Map<String, Object>> toMaps(List<TaskExecutionStartReqDTO.StepDraft> steps) {
        List<Map<String, Object>> maps = new ArrayList<>();
        for (TaskExecutionStartReqDTO.StepDraft step : steps) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", step.getName());
            item.put("stepCode", step.getStepCode());
            item.put("stepOrder", step.getStepOrder());
            item.put("stepTitle", step.getStepTitle());
            item.put("stepType", step.getStepType());
            item.put("stepRequired", step.getStepRequired());
            item.put("parentStepCode", step.getParentStepCode());
            item.put("source", step.getSource());
            maps.add(item);
        }
        return maps;
    }

    private static List<Map<String, Object>> toActionMaps(List<DispatchAction> actions) {
        List<Map<String, Object>> maps = new ArrayList<>();
        for (DispatchAction action : actions) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("actionId", action.actionId());
            item.put("params", action.params());
            maps.add(item);
        }
        return maps;
    }

    private static ExecutionDeviceBinding requireCompleteBinding(PatrolTaskDraft task) {
        ExecutionDeviceBinding binding = task.executionDeviceBinding();
        if (binding == null
                || binding.getEquipmentId() == null
                || !StringUtils.hasText(binding.getProtocolCode())
                || !StringUtils.hasText(binding.getLogicalDeviceId())) {
            throw ServiceExceptionUtil.invalidParamException(
                    "任务未绑定执行设备（须含 equipmentId、protocolCode、logicalDeviceId）");
        }
        return binding;
    }

    private static void requireKnownProtocol(ExecutionDeviceBinding binding) {
        if (!ProtocolCodes.isKnown(binding.getProtocolCode().trim())) {
            throw ServiceExceptionUtil.invalidParamException(
                    "绑定的协议版本须是机器人或无人机对接协议，不能用厂商名");
        }
    }

}
