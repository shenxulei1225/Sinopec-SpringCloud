package cn.iocoder.yudao.module.emergency.controller.admin.task;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emergency.controller.admin.task.vo.TaskCompleteReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.task.vo.TaskTerminateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.task.vo.TaskListByEventReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.task.vo.TaskListByEventRespVO;
import cn.iocoder.yudao.module.emergency.service.task.EmergencyTaskService;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/emergency/tasks")
@Tag(name = "管理后台 - 应急任务")
public class EmergencyTaskController {

    @Resource
    private EmergencyTaskService taskService;
    @Resource
    private EmergencyTaskMapper taskMapper;

    @GetMapping
    @Operation(summary = "获取任务列表", description = "根据响应ID获取关联的任务列表")
    @Parameter(name = "responseId", description = "响应ID", required = true)
    public CommonResult<java.util.Map<String, java.util.List<cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO>>> list(@RequestParam("responseId") Long responseId) {
        return success(java.util.Collections.singletonMap("list", taskMapper.selectList("response_id", responseId)));
    }

    @GetMapping("/by-event")
    @Operation(summary = "根据事件ID查询所有任务", description = "根据事件ID查询所有关联任务（包括预警阶段和响应阶段的任务），支持按阶段分组显示和阶段过滤")
    public CommonResult<TaskListByEventRespVO> getTasksByEventId(@Valid TaskListByEventReqVO reqVO) {
        TaskListByEventRespVO result = taskService.getTasksByEventId(
                reqVO.getEventId(),
                reqVO.getStage(),
                reqVO.getGroupByStage() != null ? reqVO.getGroupByStage() : true
        );
        return success(result);
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "启动任务", description = "将待启动状态的任务标记为进行中")
    @Parameter(name = "id", description = "任务ID", required = true)
    public CommonResult<Boolean> start(@PathVariable("id") Long id) {
        taskService.startTask(id);
        return success(true);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "完成任务", description = "将进行中的任务标记为已完成")
    @Parameter(name = "id", description = "任务ID", required = true)
    public CommonResult<Boolean> complete(@PathVariable("id") Long id,
                                          @Valid @RequestBody TaskCompleteReqVO reqVO) {
        taskService.completeTask(id, reqVO);
        return success(true);
    }

    @PostMapping("/{id}/terminate")
    @Operation(summary = "终止任务", description = "终止任务，标记为已终止状态")
    @Parameter(name = "id", description = "任务ID", required = true)
    public CommonResult<Boolean> terminate(@PathVariable("id") Long id,
                                           @Valid @RequestBody TaskTerminateReqVO reqVO) {
        taskService.terminateTask(id, reqVO.getReason());
        return success(true);
    }

    @GetMapping("/{id}/sub-tasks")
    @Operation(summary = "获取子任务列表", description = "根据父任务ID查询所有子任务")
    @Parameter(name = "id", description = "父任务ID", required = true)
    public CommonResult<java.util.List<cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO>> getSubTasks(@PathVariable("id") Long id) {
        return success(taskService.getSubTasks(id));
    }
}

