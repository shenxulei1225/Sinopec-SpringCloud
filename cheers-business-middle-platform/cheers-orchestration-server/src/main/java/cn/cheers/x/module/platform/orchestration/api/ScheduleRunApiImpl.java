package cn.cheers.x.module.platform.orchestration.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import cn.cheers.x.module.platform.orchestration.service.ScheduleOrchestrationService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class ScheduleRunApiImpl implements ScheduleRunApi {

    @Resource
    private ScheduleOrchestrationService scheduleOrchestrationService;

    @Override
    public CommonResult<ScheduleRunResponse> run(ScheduleRunRequest request) {
        return success(scheduleOrchestrationService.runSchedule(request, null));
    }
}
