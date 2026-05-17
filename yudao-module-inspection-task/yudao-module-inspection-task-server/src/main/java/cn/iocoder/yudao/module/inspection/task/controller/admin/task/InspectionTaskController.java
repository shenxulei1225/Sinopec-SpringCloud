package cn.iocoder.yudao.module.inspection.task.controller.admin.task;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.task.*;
import cn.iocoder.yudao.module.inspection.task.service.query.InspectionTaskQueryService;
import cn.iocoder.yudao.module.inspection.task.service.task.InspectionTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 巡检任务 Controller。
 *
 * <p>任务管理接口：</p>
 * <ul>
 *   <li>分页查询 - /page</li>
 *   <li>详情查询 - /get-detail（包含子任务列表）</li>
 *   <li>创建/更新/删除 - /create, /update, /delete</li>
 *   <li>启用/禁用 - /enable, /disable</li>
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

    @PutMapping("/enable/{id}")
    @Operation(summary = "启用任务")
    @Parameter(name = "id", required = true, description = "任务ID")
    public CommonResult<Boolean> enableTask(@PathVariable("id") Long id) {
        inspectionTaskService.enableTask(id);
        return success(true);
    }

    @PutMapping("/disable/{id}")
    @Operation(summary = "禁用任务")
    @Parameter(name = "id", required = true, description = "任务ID")
    public CommonResult<Boolean> disableTask(@PathVariable("id") Long id) {
        inspectionTaskService.disableTask(id);
        return success(true);
    }
}
