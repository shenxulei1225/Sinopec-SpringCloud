package cn.cheers.x.module.platform.orchestration.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import cn.cheers.x.module.platform.orchestration.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 调用编排排程运行（与 {@code ScheduleRunController} 路径一致）。
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 排程编排运行")
public interface ScheduleRunApi {

    String PREFIX = "/platform/runtime/schedule";

    @PostMapping(PREFIX + "/run")
    @Operation(summary = "排程运行：按 orchestrationRef 执行阶段机")
    CommonResult<ScheduleRunResponse> run(@RequestBody ScheduleRunRequest request);
}
