package cn.cheers.x.inspection.task.controller.admin.task;

import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.inspection.task.controller.admin.vo.task.*;
import cn.cheers.x.inspection.task.service.execution.InspectionTaskStartExecutionService;
import cn.cheers.x.inspection.task.service.query.InspectionTaskQueryService;
import cn.cheers.x.inspection.task.service.task.InspectionTaskService;
import cn.cheers.x.inspection.task.service.task.PatrolTaskCreateProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 巡检任务 Controller。
 *
 * <p>任务管理接口：</p>
 * <ul>
 *   <li>分页查询 - /page</li>
 *   <li>详情查询 - /get-detail（包含子任务列表）</li>
 *   <li>创建/更新/删除 - /create, /update, /delete</li>
 *   <li>启用/禁用 - /enable, /disable</li>
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
    private cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeGenerateService taskStepTreeGenerateService;

    @Resource
    private PatrolTaskCreateProcessService patrolTaskCreateProcessService;

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

    @PutMapping("/update")
    @Operation(summary = "更新任务", description = "包含排期策略变更")
    public CommonResult<Boolean> updateTask(@Valid @RequestBody InspectionTaskUpdateReqVO reqVO) {
        inspectionTaskService.updateTask(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除任务")
    @Parameter(name = "id", required = true, description = "任务ID")
    public CommonResult<Boolean> deleteTask(@PathVariable("id") Long id) {
        inspectionTaskService.deleteTask(id);
        return success(true);
    }

    // ==================== 状态变更 ====================

    /**
     * 历史接口，待删除：只翻 enabled 标志、不占/不放排程窗，属绕过编排权威的旁路。
     * 启停请走 /inspection/orchestration/schedule/reserve+enable 与 /inspection/orchestration/task/abort。
     * 前端已不再调用；确认无外部调用方后删除。
     */
    @Deprecated
    @PutMapping("/enable/{id}")
    @Operation(summary = "启用任务（已废弃：请走编排 reserve+enable）")
    @Parameter(name = "id", required = true, description = "任务ID")
    public CommonResult<Boolean> enableTask(@PathVariable("id") Long id) {
        inspectionTaskService.enableTask(id);
        return success(true);
    }

    /**
     * 历史接口，待删除：不释放排程占窗。停用请走 /inspection/orchestration/task/abort。
     */
    @Deprecated
    @PutMapping("/disable/{id}")
    @Operation(summary = "禁用任务（已废弃：请走编排 abort）")
    @Parameter(name = "id", required = true, description = "任务ID")
    public CommonResult<Boolean> disableTask(@PathVariable("id") Long id) {
        inspectionTaskService.disableTask(id);
        return success(true);
    }

    /**
     * 开始执行：向地面站下发指令包并启动。与排程「启用」无关。
     */
    @PostMapping("/{id}/start-execution")
    @Operation(summary = "开始执行", description = "读任务执行设备绑定与已确认路线，调协议网关下发；不是排程 enable")
    @Parameter(name = "id", required = true, description = "任务ID")
    public CommonResult<MissionStartRespDTO> startExecution(@PathVariable("id") Long id) {
        return success(inspectionTaskStartExecutionService.startExecution(id));
    }

    @GetMapping("/{id}/create-progress")
    @Operation(summary = "建任务做到哪一步", description = "已放行步骤可以回头看和改；还没放到的不能跳过")
    @Parameter(name = "id", required = true, description = "任务ID")
    public CommonResult<InspectionTaskCreateProgressRespVO> getCreateProgress(@PathVariable("id") Long id) {
        return success(patrolTaskCreateProcessService.getProgress(id));
    }

    @PostMapping("/{id}/create-advance")
    @Operation(summary = "建任务放行下一步", description = "只能一步一步往前；本步该齐的数据必须已经写在总任务上")
    @Parameter(name = "id", required = true, description = "任务ID")
    public CommonResult<InspectionTaskCreateProgressRespVO> advanceCreate(
            @PathVariable("id") Long id,
            @Valid @RequestBody InspectionTaskCreateAdvanceReqVO reqVO
    ) {
        return success(patrolTaskCreateProcessService.advance(id, reqVO.getToStep()));
    }

    @PostMapping("/{id}/create-invalidate")
    @Operation(summary = "建任务后面步骤作废", description = "改了对象或检查项后收回后面进度，并作废已保存路线")
    @Parameter(name = "id", required = true, description = "任务ID")
    public CommonResult<InspectionTaskCreateProgressRespVO> invalidateCreate(
            @PathVariable("id") Long id,
            @Valid @RequestBody InspectionTaskCreateInvalidateReqVO reqVO
    ) {
        return success(patrolTaskCreateProcessService.invalidate(id, reqVO.getKeepThroughStep()));
    }

    @PostMapping("/{id}/generate-step-tree")
    @Operation(summary = "生成任务步骤图", description = "点路径规划或摄像机按勾选顺序时写入总任务步骤图")
    @Parameter(name = "id", required = true, description = "任务ID")
    public CommonResult<Boolean> generateStepTree(
            @PathVariable("id") Long id,
            @RequestBody(required = false) GenerateStepTreeReqVO reqVO
    ) {
        taskStepTreeGenerateService.generate(
                id,
                reqVO == null ? null : reqVO.getEquipmentOrder(),
                reqVO == null ? null : reqVO.getStartStopId(),
                reqVO == null ? null : reqVO.getEndStopId());
        return success(true);
    }
}
