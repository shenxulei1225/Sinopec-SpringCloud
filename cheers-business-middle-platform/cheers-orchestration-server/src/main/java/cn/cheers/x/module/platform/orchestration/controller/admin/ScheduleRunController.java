package cn.cheers.x.module.platform.orchestration.controller.admin;

import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import cn.cheers.x.module.platform.orchestration.service.ScheduleOrchestrationService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 平台编排")
@RestController
@RequestMapping("/platform/runtime/schedule")
public class ScheduleRunController {

    @Resource
    private ScheduleOrchestrationService scheduleOrchestrationService;

    @PostMapping("/run")
    @Operation(summary = "排程运行：validate → expand → solve → persist")
    public CommonResult<ScheduleRunResponse> run(@Valid @RequestBody ScheduleRunRequest request) {
        return success(scheduleOrchestrationService.runSchedule(request, null));
    }
}
