package cn.cheers.x.maintenance.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 调用编排排程运行（进程名与 orchestration application.yaml 一致）
 */
@FeignClient(name = "platformorchestration-server")
public interface ScheduleRunApi {

    @PostMapping("/platform/runtime/schedule/run")
    CommonResult<ScheduleRunResponse> run(@RequestBody ScheduleRunRequest request);
}
