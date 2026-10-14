package cn.cheers.x.inspection.task.controller.admin.task;

import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.inspection.task.controller.admin.vo.task.*;
import cn.cheers.x.inspection.task.service.execution.InspectionTaskStartExecutionService;
import cn.cheers.x.inspection.task.service.execution.openrun.BatteryHeadOpenRunGuard;
import cn.cheers.x.inspection.task.service.query.InspectionTaskQueryService;
import cn.cheers.x.inspection.task.service.task.InspectionTaskService;
import cn.cheers.x.inspection.task.service.task.GeneratedCheckInputReviewService;
import cn.cheers.x.inspection.task.service.task.PatrolTaskCreateProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 巡检任务 Controller。
 *
 * <p>任务管理接口：</p>
 * <ul>
 *   <li>分页查询 - /page</li>
 *   <li>详情查询 - /get-detail（包含子任务列表）</li>
 *   <li>创建/删除 - /create, /delete；修改稿与已发布更新走各自业务接口</li>
 *   <li>启用/停用 - 走 {@code /inspection/orchestration/orchestration/generate-task} 与 {@code abort}，本 Controller 不再暴露 /enable、/disable</li>
 *   <li>开始执行 - /{id}/start-execution（下发地面站；与排程 enable 无关）</li>
 * </ul>
 *
 * <p>排期管理在独立的 ScheduleController 中。</p>
 * <p>巡检内容模板管理在 CollectionController 中。</p>
 */
@Tag(name = "管理后台 - 巡检任务")
@RestController
@RequestMapping("/inspection-task/task")
@Validated
public class InspectionTaskController {

    @Resource
    private InspectionTaskService inspectionTaskService;

    @Resource
    private InspectionTaskQueryService inspectionTaskQueryService;

    @Resource
    private InspectionTaskStartExecutionService inspectionTaskStartExecutionService;

    @Resource
    private PatrolTaskCreateProcessService patrolTaskCreateProcessService;

    @Resource
    private GeneratedCheckInputReviewService generatedCheckInputReviewService;

    @Resource
    private BatteryHeadOpenRunGuard batteryHeadOpenRunGuard;

    // ==================== 查询 ====================

    @GetMapping("/page")
    @Operation(summary = "分页查询任务列表", description = "返回轻量级任务列表，包含子任务数量")
    public CommonResult<PageResult<InspectionTaskSimpleRespVO>> getTaskPage(@Valid InspectionTaskPageReqVO pageReqVO) {
        return success(inspectionTaskQueryService.getTaskPage(pageReqVO));
    }

    @GetMapping("/get-detail/{id}")
    @Operation(summary = "获取任务详情", description = "返回任务详情，包含子任务列表（轻量级）")
    public CommonResult<InspectionTaskRespVO> getTaskDetail(@PathVariable("id") Long id) {
        return success(inspectionTaskQueryService.getTaskDetail(id));
    }

    @GetMapping("/statistics")
    @Operation(summary = "任务统计", description = "总数/已启用/已停用/草稿计数，供首页任务信息卡片")
    public CommonResult<InspectionTaskStatisticsRespVO> getTaskStatistics() {
        return success(inspectionTaskQueryService.getTaskStatistics());
    }

    // ==================== 基础CRUD ====================

    @PostMapping("/create")
    @Operation(summary = "创建任务")
    public CommonResult<Long> createTask(@Valid @RequestBody InspectionTaskCreateReqVO reqVO) {
        return success(inspectionTaskService.createTask(reqVO));
    }

    @PutMapping("/{id}/unpublished-modification")
    @Operation(summary = "保存已发布任务的未发布修改", description = "只写下还没更新上去的修改，不改正在跑的任务字段")
    public CommonResult<Boolean> saveUnpublishedModification(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> snapshot) {
        inspectionTaskService.saveUnpublishedModification(id, snapshot);
        return success(true);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除任务")
    @Parameter(name = "id", required = true, description = "任务ID")
    public CommonResult<Boolean> deleteTask(@PathVariable("id") Long id) {
        inspectionTaskService.deleteTask(id);
        return success(true);
    }

    /**
     * 开始执行：向地面站下发指令包并启动。与「生成任务」无关。
     */
    @PostMapping("/{id}/start-execution")
    @Operation(summary = "开始执行", description = "读待执行快照并下发设备；不是生成任务（commitOrchestration）")
    @Parameter(name = "id", required = true, description = "任务ID")
    public CommonResult<MissionStartRespDTO> startExecution(
            @PathVariable("id") Long id,
            @RequestParam(value = "slotId", required = false) String slotId) {
        return success(inspectionTaskStartExecutionService.startExecution(id, slotId));
    }

    /**
     * 电量检查已经提示这次无法执行之后，用户选择取消这次还没下发的执行。
     */
    @PostMapping("/execution-record/{recordId}/cancel-unrunnable")
    @Operation(summary = "取消这次无法执行的待执行")
    public CommonResult<Boolean> cancelUnrunnable(@PathVariable("recordId") Long recordId) {
        batteryHeadOpenRunGuard.cancelUnrunnable(recordId);
        return success(true);
    }

    @PostMapping("/{id}/review-generated-inputs")
    @Operation(summary = "打开已生成任务时核对检查方法和参数", description = "只比较已勾检查项当前步骤和这些步骤声明的参数，与生成时的步骤图。未生成不提示。不收回设备占用")
    @Parameter(name = "id", required = true, description = "任务ID")
    public CommonResult<InspectionTaskGeneratedInputReviewRespVO> reviewGeneratedInputs(@PathVariable("id") Long id) {
        return success(generatedCheckInputReviewService.review(id));
    }

    @PostMapping("/{id}/refresh-durations")
    @Operation(summary = "打开任务现算时长", description = "更新检查项动作耗时分钟数，供排期加总。不据此作废向导")
    @Parameter(name = "id", required = true, description = "任务ID")
    public CommonResult<InspectionTaskDurationRefreshRespVO> refreshDurations(
            @PathVariable("id") Long id,
            @RequestBody(required = false) InspectionTaskDurationRefreshReqVO reqVO
    ) {
        return success(patrolTaskCreateProcessService.refreshDurations(id, reqVO));
    }

}
