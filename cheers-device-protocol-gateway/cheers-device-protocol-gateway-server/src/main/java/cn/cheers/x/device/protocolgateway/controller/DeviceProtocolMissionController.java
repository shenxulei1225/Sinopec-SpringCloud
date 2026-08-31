package cn.cheers.x.device.protocolgateway.controller;

import cn.cheers.x.device.protocolgateway.api.DeviceProtocolMissionApi;
import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.service.DeviceProtocolMissionService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

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
}
