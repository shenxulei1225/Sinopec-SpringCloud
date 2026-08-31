package cn.cheers.x.device.protocolgateway.api;

import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.enums.ApiConstants;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 协议网关正式下行：翻译执行意图并下发指令包 + 启动执行。
 * <p>不管巡检任务编排；入参须已带 protocolCode 与逻辑设备标识。
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 设备协议任务下行")
public interface DeviceProtocolMissionApi {

    String PREFIX = ApiConstants.PREFIX + "/mission";

    /**
     * 按对接协议编码翻译 → 下发 500104 → 下发 500201。
     * <p>设备当前无连接或写出失败时返回 success=false，不假装成功。
     */
    @PostMapping(PREFIX + "/dispatch-and-startup")
    @Operation(summary = "翻译并下发指令包后启动执行")
    CommonResult<MissionStartRespDTO> dispatchAndStartup(@Valid @RequestBody DeviceMissionPlan plan);
}
