package cn.iocoder.yudao.module.emergency.api.process;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emergency.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 流程服务任务回调（由 Flowable 服务任务 HTTP/委派调用）。
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 应急流程服务任务")
public interface EmergencyProcessTaskApi {

    String PREFIX = ApiConstants.PREFIX + "/process/service-task";

    @PostMapping(PREFIX + "/start-response")
    @Operation(summary = "服务任务：启动响应 → 能力编排")
    CommonResult<Boolean> startResponse(@Valid @RequestBody StartResponseServiceTaskReq req);

    @Data
    class StartResponseServiceTaskReq {
        @NotNull
        private Long eventId;
        /** 流程变量透传；台账尚未可见时兜底，优先仍读研判 newLevel */
        private String responseLevel;
    }
}
