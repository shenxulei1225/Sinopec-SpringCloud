package cn.cheers.x.inspection.task.service.execution.impl;

import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.service.execution.InspectionTaskStartExecutionService;
import cn.cheers.x.inspection.task.service.execution.openrun.PatrolOpenRunMaterializeService;
import cn.cheers.x.inspection.task.service.execution.openrun.PatrolOpenRunMaterializeService.FrozenOpenRun;
import cn.cheers.x.inspection.task.service.execution.openrun.PatrolOpenRunSnapshotSupport;
import cn.cheers.x.inspection.task.service.execution.scheduleboard.PatrolScheduleSlotExecutionWritebackService;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.dynamicbusiness.api.strategy.StrategyRuntimeApi;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyHandleRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 巡检「开始执行」：读待执行已冻结的动作参数快照，发出开跑事件；协议翻译由网关做。
 * <p>新建账在排期物化阶段已完成；本方法触发下发与标进行中。不负责 assemble、不读 host pack。
 * <p>禁止：开跑时再解析被检参数；在巡检模块做 protocol_mapping。
 */
@Service
@RequiredArgsConstructor
public class InspectionTaskStartExecutionServiceImpl implements InspectionTaskStartExecutionService {

    /** 巡检域执行记录型号（seed：exec_patrol_round） */
    public static final String PATROL_EXEC_MODEL_CODE = "exec_patrol_round";
    public static final String PATROL_RECORD_TYPE = "task_record_patrol";

    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final StrategyRuntimeApi strategyRuntimeApi;
    private final PatrolOpenRunMaterializeService openRunMaterializeService;
    private final PatrolScheduleSlotExecutionWritebackService scheduleSlotExecutionWritebackService;

    @Override
    public MissionStartRespDTO startExecution(Long taskId) {
        return startExecution(taskId, null);
    }

    @Override
    public MissionStartRespDTO startExecution(Long taskId, String scheduleSlotId) {
        PatrolTaskDraft task = patrolTaskEntityStore.require(taskId);
        String resolvedSlotId = scheduleSlotExecutionWritebackService.resolveScheduleSlotId(task, scheduleSlotId);
        if (!StringUtils.hasText(resolvedSlotId)) {
            throw ServiceExceptionUtil.invalidParamException("开跑必须能对应到一条已物化的计划点");
        }
        FrozenOpenRun frozen = openRunMaterializeService.requireFrozen(task, resolvedSlotId);
        ExecutionDeviceBinding binding = frozen.binding();
        requireKnownProtocol(binding);

        StrategyHandleRespDTO handled = publishStart(task, binding, frozen, resolvedSlotId);
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
            throw ServiceExceptionUtil.invalidParamException("开跑后没有关联的执行账");
        }
        scheduleSlotExecutionWritebackService.markStarted(
                resolvedSlotId, task.id(), handled.getExecutionRecordId(), task.facilityId());
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
            FrozenOpenRun frozen,
            String scheduleSlotId
    ) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        if (frozen.standardSnapshot() instanceof Map<?, ?> existing) {
            snapshot.putAll(copyMap(existing));
        }
        snapshot.put("source", PatrolOpenRunSnapshotSupport.SOURCE_START);
        snapshot.put("taskDefinitionId", task.id());
        snapshot.put("protocolVersion", binding.getProtocolCode());
        snapshot.put("dispatchActionCount", frozen.dispatchActions().size());
        snapshot.put("pendingMaterializedRecordId", frozen.executionRecordId());

        StrategyTriggerEventDTO event = new StrategyTriggerEventDTO();
        event.setEventType(StrategyTriggerEventDTO.EVENT_EXECUTION_START);
        event.setEntityTypeCode(PATROL_RECORD_TYPE);
        event.setTaskDefinitionId(task.id());
        event.setModelCode(PATROL_EXEC_MODEL_CODE);
        event.setExecutionRecordId(frozen.executionRecordId());
        event.setExecutionName(StringUtils.hasText(task.name())
                ? task.name() + "-执行"
                : "巡检执行-" + task.id());
        event.setStandardSnapshot(snapshot);
        event.setProtocolVersion(binding.getProtocolCode().trim());
        event.setLogicalDeviceId(binding.getLogicalDeviceId().trim());
        event.setDispatchActions(PatrolOpenRunSnapshotSupport.toActionMaps(frozen.dispatchActions()));
        event.setScheduleSlotId(scheduleSlotId.trim());

        CommonResult<StrategyHandleRespDTO> rpc = strategyRuntimeApi.handle(event);
        if (rpc == null || !rpc.isSuccess() || rpc.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    rpc != null && StringUtils.hasText(rpc.getMsg())
                            ? rpc.getMsg()
                            : "开跑后策略没有处理完");
        }
        if (!rpc.getData().isMatched()) {
            throw ServiceExceptionUtil.invalidParamException(
                    StringUtils.hasText(rpc.getData().getSkipReason())
                            ? rpc.getData().getSkipReason()
                            : "开跑没有命中策略");
        }
        return rpc.getData();
    }

    private static Map<String, Object> copyMap(Map<?, ?> source) {
        Map<String, Object> copy = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            if (entry.getKey() != null) {
                copy.put(String.valueOf(entry.getKey()), entry.getValue());
            }
        }
        return copy;
    }

    private static void requireKnownProtocol(ExecutionDeviceBinding binding) {
        if (!ProtocolCodes.isKnown(binding.getProtocolCode().trim())) {
            throw ServiceExceptionUtil.invalidParamException(
                    "绑定的协议版本须是机器人或无人机对接协议，不能用厂商名");
        }
    }
}
