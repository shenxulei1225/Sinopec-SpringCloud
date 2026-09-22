package cn.cheers.x.inspection.inspection_content.controller.admin.orchestration;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolOrchestrationRunReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolRouteRunReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolSlotWritebackReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolTaskPauseReqVO;
import cn.cheers.x.inspection.inspection_content.service.orchestration.PatrolRouteFacadeService;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleConflictReportDTO;
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
 * 巡检门面 HTTP：路径规划、冲突检测、第 3 步无冲突写试排、智能编排解冲突、生成任务。
 */
@Tag(name = "管理后台 - 巡检路线与排期")
@RestController
@RequestMapping("/inspection/orchestration")
@Validated
public class PatrolRouteFacadeController {

    @Resource
    private PatrolRouteFacadeService patrolRouteFacadeService;

    @PostMapping("/route/preview")
    @Operation(
            operationId = "previewPatrolRoute",
            summary = "路径规划·预览路线：算路 dryRun，不写 plannedRoute")
    public CommonResult<ScheduleRunResponse> previewRoute(@Valid @RequestBody PatrolRouteRunReqVO reqVO) {
        return success(patrolRouteFacadeService.previewRoute(reqVO));
    }

    @PostMapping("/route/save")
    @Operation(operationId = "savePatrolRoute", summary = "路径规划·保存路线：算路并写入 plannedRoute")
    public CommonResult<ScheduleRunResponse> saveRoute(@Valid @RequestBody PatrolRouteRunReqVO reqVO) {
        return success(patrolRouteFacadeService.saveRoute(reqVO));
    }

    @PostMapping("/orchestration/detect-conflicts")
    @Operation(
            operationId = "detectPatrolScheduleConflicts",
            summary = "冲突检测：查指定设备已有占窗是否重叠，只出报告")
    public CommonResult<ScheduleConflictReportDTO> detectScheduleConflicts(
            @Valid @RequestBody PatrolOrchestrationRunReqVO reqVO) {
        return success(patrolRouteFacadeService.detectScheduleConflicts(reqVO));
    }

    @PostMapping("/orchestration/prepare-confirm")
    @Operation(
            operationId = "preparePatrolConfirmPreview",
            summary = "第 3 步无冲突写试排：按模板时间点写试排快照并生成步骤图")
    public CommonResult<ScheduleRunResponse> prepareConfirmPreview(
            @Valid @RequestBody PatrolOrchestrationRunReqVO reqVO) {
        return success(patrolRouteFacadeService.prepareConfirmPreview(reqVO));
    }

    @PostMapping("/orchestration/preview")
    @Operation(
            operationId = "previewPatrolOrchestration",
            summary = "智能编排·试排：冲突解决 + 调用步骤图生成，写 orchestrationPreview 草稿")
    public CommonResult<ScheduleRunResponse> previewOrchestration(@Valid @RequestBody PatrolOrchestrationRunReqVO reqVO) {
        return success(patrolRouteFacadeService.previewOrchestration(reqVO));
    }

    @PostMapping("/orchestration/generate-task")
    @Operation(
            operationId = "generatePatrolTask",
            summary = "生成任务：试排快照落库占窗定稿，写每计划点待执行，登记到点自动开跑（向导底栏与列表启用第二步）")
    public CommonResult<ScheduleRunResponse> generateTask(@Valid @RequestBody PatrolOrchestrationRunReqVO reqVO) {
        return success(patrolRouteFacadeService.commitOrchestration(reqVO));
    }

    @PostMapping("/orchestration/abort")
    @Operation(
            operationId = "stopPatrolAutoStart",
            summary = "任务列表·停用：关到点自动开跑并释放未执行占窗")
    public CommonResult<Boolean> abortOrchestration(@Valid @RequestBody PatrolTaskPauseReqVO reqVO) {
        patrolRouteFacadeService.abortOrchestration(reqVO);
        return success(true);
    }

    @PostMapping("/slot/writeback")
    @Operation(operationId = "writebackPatrolScheduleSlot", summary = "计划点状态回写")
    public CommonResult<Boolean> writebackSlot(@Valid @RequestBody PatrolSlotWritebackReqVO reqVO) {
        patrolRouteFacadeService.writebackSlot(reqVO);
        return success(true);
    }
}
