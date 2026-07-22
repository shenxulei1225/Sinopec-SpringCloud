package cn.iocoder.yudao.module.emergency.api.orchestration;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyStartResponseExpandRespDTO;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyStartResponseReqDTO;
import cn.iocoder.yudao.module.emergency.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 编排回调：启动响应当作适配器阶段能力暴露给中台。
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 应急启动响应编排回调")
public interface EmergencyStartResponseApi {

    String PREFIX = ApiConstants.PREFIX + "/orchestration/start-response";

    @PostMapping(PREFIX + "/validate")
    @Operation(summary = "校验启动响应载荷")
    CommonResult<Boolean> validate(@Valid @RequestBody EmergencyStartResponseReqDTO req);

    @PostMapping(PREFIX + "/expand")
    @Operation(summary = "展开：写响应并克隆预案任务")
    CommonResult<EmergencyStartResponseExpandRespDTO> expand(@Valid @RequestBody EmergencyStartResponseReqDTO req);

    @PostMapping(PREFIX + "/persist")
    @Operation(summary = "落库收尾：过程时间线等（expand 已写主台账时可为幂等确认）")
    CommonResult<Boolean> persist(@Valid @RequestBody EmergencyStartResponseExpandRespDTO expandResult);
}
