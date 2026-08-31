package cn.cheers.x.inspection.task.controller.admin.device;

import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.enums.ApiConstants;
import cn.cheers.x.inspection.task.service.execution.InspectionDeviceUplinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 设备上行回调入口（供协议网关 HTTP 推送）。
 * <p>路径与网关 {@code uplink-notify-url} 对齐；不走业务 admin 鉴权场景时由网关直连 RPC 前缀。
 */
@Tag(name = "RPC - 设备上行回写")
@RestController
@RequestMapping(ApiConstants.PREFIX + "/device-uplink")
@Validated
public class InspectionDeviceUplinkController {

    @Resource
    private InspectionDeviceUplinkService inspectionDeviceUplinkService;

    @PostMapping("/notify")
    @Operation(summary = "接收协议网关上行事件并回写任务设备运行态")
    public CommonResult<Boolean> notify(@RequestBody DeviceUplinkEventDTO event) {
        inspectionDeviceUplinkService.applyUplink(event);
        return success(true);
    }
}
