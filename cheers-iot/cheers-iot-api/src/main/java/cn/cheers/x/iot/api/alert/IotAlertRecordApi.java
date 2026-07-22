package cn.cheers.x.iot.api.alert;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.iot.api.alert.dto.IotAlertRecordRespDTO;
import cn.cheers.x.iot.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 跨模块读告警记录（应急告警转事件等）。
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - IoT 告警记录")
public interface IotAlertRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/alert-record";

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "获得告警记录")
    @Parameter(name = "id", description = "告警记录编号", required = true)
    CommonResult<IotAlertRecordRespDTO> getAlertRecord(@RequestParam("id") Long id);
}
