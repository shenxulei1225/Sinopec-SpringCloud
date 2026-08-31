package cn.cheers.x.device.protocolgateway.controller;

import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.service.DeviceProtocolCapabilityService;
import cn.cheers.x.device.protocolgateway.service.DeviceProtocolCapabilityService.CapabilityDispatchResult;
import cn.cheers.x.device.protocolgateway.service.DeviceProtocolCapabilityService.CapabilityPreview;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 协议能力联调入口。
 * <p>入参为本次动态执行意图（protocolCode + deviceId + templateId + waypoints），
 * 不是预制指令包，也不是巡检业务「开始执行」。
 */
@RestController
@RequestMapping("/device-protocol/capability")
@RequiredArgsConstructor
public class DeviceProtocolCapabilityController {

    private final DeviceProtocolCapabilityService capabilityService;

    /**
     * 只翻译预览，不发送。
     */
    @PostMapping("/preview")
    public CapabilityPreview preview(@RequestBody DeviceMissionPlan plan) {
        return capabilityService.preview(plan);
    }

    /**
     * 翻译后若设备在线则下发 500104。
     */
    @PostMapping("/dispatch")
    public CapabilityDispatchResult dispatch(@RequestBody DeviceMissionPlan plan) {
        return capabilityService.dispatch(plan);
    }
}
