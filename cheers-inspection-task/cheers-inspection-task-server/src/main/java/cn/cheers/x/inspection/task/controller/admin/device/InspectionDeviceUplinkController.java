package cn.cheers.x.inspection.task.controller.admin.device;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
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
 * 过渡 HTTP：接收网关采集样本。正式主路径仍是总线。
 * <p>只消费样本，不接收原始报文当主契约。
 */
@Tag(name = "RPC - 设备上行回写")
@RestController
@RequestMapping(ApiConstants.PREFIX + "/device-uplink")
@Validated
public class InspectionDeviceUplinkController {

    @Resource
    private InspectionDeviceUplinkService inspectionDeviceUplinkService;

    @PostMapping("/notify")
    @Operation(summary = "接收采集样本，经策略往执行账记过程（步骤完成过渡仍写）")
    public CommonResult<Boolean> notify(@RequestBody CollectionSample sample) {
        inspectionDeviceUplinkService.applyCollection(sample);
        return success(true);
    }
}
