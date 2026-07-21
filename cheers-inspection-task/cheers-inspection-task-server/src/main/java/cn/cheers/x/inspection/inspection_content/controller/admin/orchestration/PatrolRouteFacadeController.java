package cn.cheers.x.inspection.inspection_content.controller.admin.orchestration;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolRouteRunReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolScheduleEnableReqVO;
import cn.cheers.x.inspection.inspection_content.service.orchestration.PatrolRouteFacadeService;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 巡检组路线编排门面：预览 / 确认 / 启用均只触发 schedule/run。
 */
@Tag(name = "管理后台 - 巡检组路线编排")
@RestController
@RequestMapping("/inspection/orchestration")
@Validated
public class PatrolRouteFacadeController {

    @Resource
    private PatrolRouteFacadeService patrolRouteFacadeService;

    @PostMapping("/route/preview")
    @Operation(summary = "预览组路线：expand + route，dryRun")
    public CommonResult<ScheduleRunResponse> previewRoute(@Valid @RequestBody PatrolRouteRunReqVO reqVO) {
        return success(patrolRouteFacadeService.previewRoute(reqVO));
    }

    @PostMapping("/route/confirm")
    @Operation(summary = "确认组路线：expand + route + confirm")
    public CommonResult<ScheduleRunResponse> confirmRoute(@Valid @RequestBody PatrolRouteRunReqVO reqVO) {
        return success(patrolRouteFacadeService.confirmRoute(reqVO));
    }

    @PostMapping("/schedule/enable")
    @Operation(summary = "启用排程：从已确认任务快照 expand + solve + persist")
    public CommonResult<ScheduleRunResponse> enableSchedule(@Valid @RequestBody PatrolScheduleEnableReqVO reqVO) {
        return success(patrolRouteFacadeService.enableSchedule(reqVO));
    }
}
