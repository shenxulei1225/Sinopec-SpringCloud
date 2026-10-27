package cn.cheers.x.inspection.task.service.execution.openrun;

import cn.cheers.x.device.protocolgateway.api.DjiCloudAircraftStatusApi;
import cn.cheers.x.device.protocolgateway.api.GroundStationLiveStatusApi;
import cn.cheers.x.device.protocolgateway.api.dto.AircraftFlightStatusDTO;
import cn.cheers.x.device.protocolgateway.api.dto.GroundStationLiveStatusDTO;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.api.dto.TaskExchangeTranscriptReqDTO;
import cn.cheers.x.inspection.task.service.execution.impl.InspectionTaskStartExecutionServiceImpl;
import cn.cheers.x.inspection.task.service.execution.openrun.PatrolOpenRunMaterializeService.FrozenOpenRun;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 开跑前做电量检查。不能下发时把比较结果记在这次执行上，并返回给页面的那一句。
 * <p>用户选取消这次执行时，把仍待执行的账记成已取消。先不执行则账保持待执行。
 * <p>不负责下发指令。东古的电量百分比按最长飞行时间匀速换成秒数。这一台空着时读型号默认值。设备上有耗电曲线时仍按曲线取。
 */
@Service
public class BatteryHeadOpenRunGuard {

    private static final String QUALIFY = "BATTERY";
    private static final String KIND = "电量检查";
    private static final String CANCELLED = "cancelled";

    private final GroundStationLiveStatusApi groundStationLiveStatusApi;
    private final DjiCloudAircraftStatusApi aircraftStatusApi;
    private final TaskExecutionSessionApi taskExecutionSessionApi;
    private final EntityRpcApi entityRpcApi;
    private final EquipmentFlightBudgetQuery equipmentFlightBudgetQuery;

    public BatteryHeadOpenRunGuard(
            GroundStationLiveStatusApi groundStationLiveStatusApi,
            DjiCloudAircraftStatusApi aircraftStatusApi,
            TaskExecutionSessionApi taskExecutionSessionApi,
            EntityRpcApi entityRpcApi,
            EquipmentFlightBudgetQuery equipmentFlightBudgetQuery) {
        this.groundStationLiveStatusApi = groundStationLiveStatusApi;
        this.aircraftStatusApi = aircraftStatusApi;
        this.taskExecutionSessionApi = taskExecutionSessionApi;
        this.entityRpcApi = entityRpcApi;
        this.equipmentFlightBudgetQuery = equipmentFlightBudgetQuery;
    }

    /**
     * 不能下发时返回提示。可以下发，或这棵步骤图没有电量检查，返回 null。
     */
    public String blockIfCannotRun(PatrolTaskDraft task, FrozenOpenRun frozen) {
        Reported reported = reported(task);
        String prompt = BatteryHeadGate.blockPrompt(
                task.stepTree(), reported.remainFlySeconds(), reported.electricity(), reported.fromFullCharge());
        if (prompt == null) {
            return null;
        }
        remember(frozen.executionRecordId(), prompt, reported.rawPayloadJson());
        return prompt;
    }

    /**
     * 用户选择取消这次还没下发的执行。已经不是待执行则拒绝。
     */
    public void cancelUnrunnable(Long executionRecordId) {
        if (executionRecordId == null || executionRecordId <= 0) {
            throw ServiceExceptionUtil.invalidParamException("要先选定这次执行");
        }
        CommonResult<EntityRespDTO> loaded = entityRpcApi.getEntity(
                executionRecordId, InspectionTaskStartExecutionServiceImpl.PATROL_RECORD_TYPE);
        if (loaded == null || !loaded.isSuccess() || loaded.getData() == null
                || loaded.getData().getCustomFields() == null) {
            throw ServiceExceptionUtil.invalidParamException("这次执行不存在");
        }
        String status = text(loaded.getData().getCustomFields().get("execution_status"));
        if (!"pending".equalsIgnoreCase(status)) {
            throw ServiceExceptionUtil.invalidParamException("这次执行已经不是待执行，不能再取消");
        }
        TaskExecutionWritebackReqDTO writeback = new TaskExecutionWritebackReqDTO();
        writeback.setExecutionRecordId(executionRecordId);
        writeback.setEntityTypeCode(InspectionTaskStartExecutionServiceImpl.PATROL_RECORD_TYPE);
        writeback.setExecutionStatus(CANCELLED);
        CommonResult<Boolean> written = taskExecutionSessionApi.writeback(writeback);
        if (written == null || !written.isSuccess()) {
            throw ServiceExceptionUtil.invalidParamException(
                    written != null && StringUtils.hasText(written.getMsg()) ? written.getMsg() : "取消这次执行失败");
        }
        remember(executionRecordId, "用户选择取消这次执行。指令没有下发。", null);
    }

    private Reported reported(PatrolTaskDraft task) {
        String protocol = task.executionDeviceBinding() == null
                ? ""
                : text(task.executionDeviceBinding().getProtocolCode());
        String deviceId = task.executionDeviceBinding() == null
                ? ""
                : text(task.executionDeviceBinding().getLogicalDeviceId());
        if (!StringUtils.hasText(deviceId)) {
            return new Reported(null, null, ProtocolCodes.ROBOT_WS.equals(protocol) || ProtocolCodes.UAV_WS.equals(protocol), null);
        }
        if (ProtocolCodes.isDjiCloud(protocol)) {
            CommonResult<AircraftFlightStatusDTO> rpc = aircraftStatusApi.status(deviceId);
            if (rpc == null || !rpc.isSuccess() || rpc.getData() == null || StringUtils.hasText(rpc.getData().gap())) {
                return new Reported(null, null, false, null);
            }
            return new Reported(rpc.getData().remainFlightTimeSeconds(), null, false, rpc.getData().rawPayloadJson());
        }
        if (ProtocolCodes.ROBOT_WS.equals(protocol) || ProtocolCodes.UAV_WS.equals(protocol)) {
            CommonResult<GroundStationLiveStatusDTO> rpc = groundStationLiveStatusApi.latest(deviceId);
            String electricity = rpc == null || !rpc.isSuccess() || rpc.getData() == null
                    ? null
                    : rpc.getData().electricity();
            EquipmentFlightBudgetQuery.Snapshot budget =
                    equipmentFlightBudgetQuery.load(task.executionDeviceBinding().getEquipmentId());
            return new Reported(
                    DongguBatteryRemain.seconds(electricity, budget.fullBatterySeconds(), budget.dischargeCurve()),
                    electricity,
                    true,
                    null);
        }
        return new Reported(null, null, false, null);
    }

    private void remember(Long executionRecordId, String summary, String rawPayloadJson) {
        TaskExecutionAppendProcessReqDTO append = new TaskExecutionAppendProcessReqDTO();
        append.setExecutionRecordId(executionRecordId);
        append.setEntityTypeCode(InspectionTaskStartExecutionServiceImpl.PATROL_RECORD_TYPE);
        append.setReceivedAtEpochMs(System.currentTimeMillis());
        append.setMessageKind(KIND);
        append.setProtocolQualify(QUALIFY);
        append.setSummary(summary);
        if (rawPayloadJson != null && !rawPayloadJson.isBlank()) {
            append.setDirection(TaskExchangeTranscriptReqDTO.INBOUND);
            append.setPayloadJson(rawPayloadJson);
        }
        taskExecutionSessionApi.appendProcess(append);
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    private record Reported(Integer remainFlySeconds, String electricity, boolean fromFullCharge, String rawPayloadJson) {
    }

}
