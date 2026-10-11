package cn.cheers.x.device.protocolgateway.controller;

import cn.cheers.x.device.protocolgateway.api.DeviceProtocolMissionApi;
import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.device.protocolgateway.api.dto.MissionTranslateCommitRespDTO;
import cn.cheers.x.device.protocolgateway.api.dto.MissionTranslatePreviewRespDTO;
import cn.cheers.x.device.protocolgateway.api.dto.MissionTranslatedDispatchReqDTO;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.service.DeviceProtocolMissionService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.error;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 协议网关正式下行 RPC 实现。
 * <p>与联调用 {@code /device-protocol/capability} 分开；本入口供巡检等业务 Feign 调用。
 */
@RestController
@Validated
@RequiredArgsConstructor
public class DeviceProtocolMissionController implements DeviceProtocolMissionApi {

    private final DeviceProtocolMissionService missionService;

    @Override
    public CommonResult<MissionStartRespDTO> dispatchAndStartup(DeviceMissionPlan plan) {
        return success(missionService.dispatchAndStartup(plan));
    }

    @Override
    public CommonResult<MissionTranslatePreviewRespDTO> previewTranslate(DeviceMissionPlan plan) {
        return success(missionService.previewTranslate(plan));
    }

    @Override
    public CommonResult<MissionTranslateCommitRespDTO> commitTranslate(DeviceMissionPlan plan) {
        try {
            return success(missionService.commitTranslate(plan));
        } catch (IllegalArgumentException ex) {
            return error(400, ex.getMessage());
        }
    }

    @Override
    public CommonResult<MissionStartRespDTO> dispatchTranslated(MissionTranslatedDispatchReqDTO req) {
        return success(missionService.dispatchTranslated(req));
    }
}
