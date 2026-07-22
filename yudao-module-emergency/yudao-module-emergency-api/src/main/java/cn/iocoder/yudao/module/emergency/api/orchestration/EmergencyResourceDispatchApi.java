package cn.iocoder.yudao.module.emergency.api.orchestration;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyResourceDispatchExpandRespDTO;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyResourceDispatchReqDTO;
import cn.iocoder.yudao.module.emergency.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 编排回调：资源调度阶段能力。
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 应急资源调度编排回调")
public interface EmergencyResourceDispatchApi {

    String PREFIX = ApiConstants.PREFIX + "/orchestration/resource-dispatch";

    @PostMapping(PREFIX + "/validate")
    @Operation(summary = "校验资源调度载荷")
    CommonResult<Boolean> validate(@Valid @RequestBody EmergencyResourceDispatchReqDTO req);

    @PostMapping(PREFIX + "/expand")
    @Operation(summary = "展开：校验资源可派发并准备调度草稿元数据")
    CommonResult<EmergencyResourceDispatchExpandRespDTO> expand(@Valid @RequestBody EmergencyResourceDispatchReqDTO req);

    @PostMapping(PREFIX + "/solve")
    @Operation(summary = "占窗求解：无窗则显式 skip；有窗但未接入排程则失败")
    CommonResult<EmergencyResourceDispatchExpandRespDTO> solve(
            @Valid @RequestBody EmergencyResourceDispatchExpandRespDTO expand);

    @PostMapping(PREFIX + "/persist")
    @Operation(summary = "落库：派发资源并写过程时间线")
    CommonResult<EmergencyResourceDispatchExpandRespDTO> persist(
            @Valid @RequestBody EmergencyResourceDispatchExpandRespDTO expandResult);
}
