package cn.cheers.x.inspection.task.api;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.api.dto.CommandEventStatusReqDTO;
import cn.cheers.x.inspection.task.api.dto.PatrolGeneratedTaskDownlinkDTO;
import cn.cheers.x.inspection.task.api.dto.PatrolGeneratedTaskOptionDTO;
import cn.cheers.x.inspection.task.api.dto.ReplayPoseReqDTO;
import cn.cheers.x.inspection.task.api.dto.TaskExchangeTranscriptReqDTO;
import cn.cheers.x.inspection.task.api.dto.TaskExchangeTranscriptRespDTO;
import cn.cheers.x.inspection.task.service.execution.ExecutionEventLedger;
import cn.cheers.x.inspection.task.service.execution.InspectionDeviceUplinkService;
import cn.cheers.x.inspection.task.service.execution.monitor.PatrolGeneratedTaskLookup;
import cn.cheers.x.inspection.task.service.execution.monitor.TaskExchangeTranscriptService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 接收网关投递的上报样本。
 * <p>负责：把样本交给巡检上报处理，由策略写入这次任务执行记录。协议监控下发前交出已生成任务的编号和动作清单。
 * <p>不负责：拆报文、判协议合格、在这里挑选记进哪一条任务、翻译指令包。
 */
@RestController
@Validated
public class InspectionDeviceUplinkApiImpl implements InspectionDeviceUplinkApi {

    @Resource
    private InspectionDeviceUplinkService inspectionDeviceUplinkService;

    @Resource
    private PatrolGeneratedTaskLookup patrolGeneratedTaskLookup;

    @Resource
    private TaskExchangeTranscriptService taskExchangeTranscriptService;

    @Resource
    private ExecutionEventLedger executionEventLedger;

    @Override
    public CommonResult<Boolean> notify(CollectionSample sample) {
        inspectionDeviceUplinkService.applyCollection(sample);
        return success(Boolean.TRUE);
    }

    @Override
    public CommonResult<PatrolGeneratedTaskDownlinkDTO> generatedTask(String deviceId) {
        return success(patrolGeneratedTaskLookup.forDevice(deviceId));
    }

    @Override
    public CommonResult<List<PatrolGeneratedTaskOptionDTO>> generatedTasks(String deviceId) {
        return success(patrolGeneratedTaskLookup.listForDevice(deviceId));
    }

    @Override
    public CommonResult<PatrolGeneratedTaskDownlinkDTO> generatedTaskRecord(String deviceId, long executionRecordId) {
        return success(patrolGeneratedTaskLookup.forRecord(deviceId, executionRecordId));
    }

    @Override
    public CommonResult<Boolean> markCommandReceived(CommandEventStatusReqDTO req) {
        if (req == null) {
            executionEventLedger.markReceived(null, null);
            return success(Boolean.FALSE);
        }
        executionEventLedger.markReceived(req.getExecutionRecordId(), req.getMsgId());
        return success(Boolean.TRUE);
    }

    @Override
    public CommonResult<TaskExchangeTranscriptRespDTO> recordExchange(TaskExchangeTranscriptReqDTO req) {
        return success(taskExchangeTranscriptService.record(req));
    }

    @Override
    public CommonResult<Boolean> recordReplayPose(ReplayPoseReqDTO req) {
        return success(taskExchangeTranscriptService.recordPose(req));
    }
}
