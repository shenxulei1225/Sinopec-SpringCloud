package cn.cheers.x.inspection.inspection_content.controller.admin.orchestration;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolRouteRunReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolScheduleEnableReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolSlotWritebackReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolTaskPauseReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolTaskResumeReqVO;
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
 * 巡检组路线编排门面：预览 / 确认 / 排期预占 / 启用验窗 / 让路 / 恢复 / 回写。
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
    @Operation(summary = "确认组路线：expand + route + confirm（需 taskId 绑定台账）")
    public CommonResult<ScheduleRunResponse> confirmRoute(@Valid @RequestBody PatrolRouteRunReqVO reqVO) {
        return success(patrolRouteFacadeService.confirmRoute(reqVO));
    }

    @PostMapping("/schedule/reserve")
    @Operation(summary = "排期预占：solve + persist，任务=已排未启用")
    public CommonResult<ScheduleRunResponse> reserveSchedule(@Valid @RequestBody PatrolScheduleEnableReqVO reqVO) {
        return success(patrolRouteFacadeService.reserveSchedule(reqVO));
    }

    @PostMapping("/schedule/enable")
    @Operation(summary = "启用开跑：验占窗通过后任务已启用（波次 3 起须先 reserve）")
    public CommonResult<ScheduleRunResponse> enableSchedule(@Valid @RequestBody PatrolScheduleEnableReqVO reqVO) {
        return success(patrolRouteFacadeService.enableSchedule(reqVO));
    }

    @PostMapping("/task/hold-pause")
    @Operation(summary = "挂起式暂停：不释放占用")
    public CommonResult<Boolean> holdPause(@Valid @RequestBody PatrolTaskPauseReqVO reqVO) {
        patrolRouteFacadeService.holdPause(reqVO);
        return success(true);
    }

    @PostMapping("/task/yield-pause")
    @Operation(summary = "让路式暂停：释放未执行占用")
    public CommonResult<Boolean> yieldPause(@Valid @RequestBody PatrolTaskPauseReqVO reqVO) {
        patrolRouteFacadeService.yieldPause(reqVO);
        return success(true);
    }

    @PostMapping("/task/abort")
    @Operation(summary = "中止：释放未执行占用")
    public CommonResult<Boolean> abort(@Valid @RequestBody PatrolTaskPauseReqVO reqVO) {
        patrolRouteFacadeService.abort(reqVO);
        return success(true);
    }

    @PostMapping("/task/resume")
    @Operation(summary = "恢复：orch.patrol.replan_v1 重排剩余停靠点")
    public CommonResult<ScheduleRunResponse> resume(@Valid @RequestBody PatrolTaskResumeReqVO reqVO) {
        return success(patrolRouteFacadeService.resume(reqVO));
    }

    @PostMapping("/slot/writeback")
    @Operation(summary = "计划点状态回写")
    public CommonResult<Boolean> writebackSlot(@Valid @RequestBody PatrolSlotWritebackReqVO reqVO) {
        patrolRouteFacadeService.writebackSlot(reqVO);
        return success(true);
    }
}
