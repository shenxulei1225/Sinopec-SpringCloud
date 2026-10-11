package cn.cheers.x.inspection.task.api;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.api.dto.PatrolGeneratedTaskDownlinkDTO;
import cn.cheers.x.inspection.task.service.execution.InspectionDeviceUplinkService;
import cn.cheers.x.inspection.task.service.execution.monitor.PatrolGeneratedTaskLookup;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 接收网关投递的上报样本。
 * <p>负责：把样本交给巡检上报处理，由策略写入这次任务执行记录。协议监控下发前交出已生成任务的编号和动作清单。
 * <p>不负责：拆报文、判协议合格、在这里直接改执行账、翻译指令包。
 */
@RestController
@Validated
public class InspectionDeviceUplinkApiImpl implements InspectionDeviceUplinkApi {

    @Resource
    private InspectionDeviceUplinkService inspectionDeviceUplinkService;

    @Resource
    private PatrolGeneratedTaskLookup patrolGeneratedTaskLookup;

    @Override
    public CommonResult<Boolean> notify(CollectionSample sample) {
        inspectionDeviceUplinkService.applyCollection(sample);
        return success(Boolean.TRUE);
    }

    @Override
    public CommonResult<PatrolGeneratedTaskDownlinkDTO> generatedTask(String deviceId) {
        return success(patrolGeneratedTaskLookup.forDevice(deviceId));
    }
}
