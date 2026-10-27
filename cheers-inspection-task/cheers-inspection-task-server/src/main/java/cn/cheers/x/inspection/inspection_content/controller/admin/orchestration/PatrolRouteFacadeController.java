package cn.cheers.x.inspection.inspection_content.controller.admin.orchestration;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolInspectionConfigReadinessRespVO;
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
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("/orchestration/detect-conflicts")
    @Operation(
            operationId = "detectPatrolScheduleConflicts",
            summary = "冲突检测：没有重叠时带上计划时间点，有重叠时不带计划点")
    public CommonResult<ScheduleConflictReportDTO> detectScheduleConflicts(
            @Valid @RequestBody PatrolOrchestrationRunReqVO reqVO) {
        return success(patrolRouteFacadeService.detectScheduleConflicts(reqVO));
    }

    @PostMapping("/orchestration/validate-inspection-config")
    @Operation(
            operationId = "validatePatrolInspectionConfig",
            summary = "第 3 步·只查配置：检查项方法与执行设备是否齐全，不生成步骤图")
    public CommonResult<PatrolInspectionConfigReadinessRespVO> validateInspectionConfig(
            @Valid @RequestBody PatrolOrchestrationRunReqVO reqVO) {
        return success(patrolRouteFacadeService.validateInspectionConfig(reqVO));
    }

    @PostMapping("/orchestration/compose-execution-step-tree")
    @Operation(
            operationId = "composePatrolExecutionStepTree",
            summary = "第 3 步·只生成步骤图：不写试排、不试译")
    public CommonResult<ScheduleRunResponse> composeExecutionStepTree(
            @Valid @RequestBody PatrolOrchestrationRunReqVO reqVO) {
        return success(patrolRouteFacadeService.composeExecutionStepTree(reqVO));
    }

    @PostMapping("/orchestration/preview-instructions")
    @Operation(
            operationId = "previewPatrolPageInstructions",
            summary = "第 3 步·只生成指令：用页面上已有的步骤图试译一次，不写出计划点")
    public CommonResult<ScheduleRunResponse> previewPageInstructions(
            @Valid @RequestBody PatrolOrchestrationRunReqVO reqVO) {
        return success(patrolRouteFacadeService.previewPageInstructions(reqVO));
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

    @PostMapping("/orchestration/rewrite-missing-ledgers")
    @Operation(
            operationId = "rewriteMissingPatrolLedgers",
            summary = "已生成任务的待执行账不在了，按还没开跑的计划点把账写回去。已开跑的账不另写。不重新排期")
    public CommonResult<Boolean> rewriteMissingLedgers(@RequestParam("taskId") Long taskId) {
        patrolRouteFacadeService.rewriteMissingPendingLedgers(taskId);
        return success(true);
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
