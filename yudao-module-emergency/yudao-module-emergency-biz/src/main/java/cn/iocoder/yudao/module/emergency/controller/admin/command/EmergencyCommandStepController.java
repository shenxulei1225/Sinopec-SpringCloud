package cn.iocoder.yudao.module.emergency.controller.admin.command;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.*;
import cn.iocoder.yudao.module.emergency.service.command.EmergencyCommandStepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 指令步骤
 */
@Tag(name = "管理后台 - 指令步骤")
@RestController
@RequestMapping("/emergency/command-step")
@Validated
public class EmergencyCommandStepController {

    @Resource
    private EmergencyCommandStepService commandStepService;

    @PostMapping("/create")
    @Operation(summary = "创建指令步骤")
    public CommonResult<Long> createCommandStep(@Valid @RequestBody CommandStepCreateReqVO createReqVO) {
        Long stepId = commandStepService.createCommandStep(createReqVO);
        return success(stepId);
    }

    @PutMapping("/update")
    @Operation(summary = "更新指令步骤")
    public CommonResult<Boolean> updateCommandStep(@Valid @RequestBody CommandStepUpdateReqVO updateReqVO) {
        commandStepService.updateCommandStep(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除指令步骤")
    @Parameter(name = "id", description = "步骤编号", required = true)
    public CommonResult<Boolean> deleteCommandStep(@RequestParam("id") Long id) {
        commandStepService.deleteCommandStep(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得指令步骤")
    @Parameter(name = "id", description = "步骤编号", required = true)
    public CommonResult<CommandStepRespVO> getCommandStep(@RequestParam("id") Long id) {
        CommandStepRespVO step = commandStepService.getCommandStep(id);
        return success(step);
    }

    @GetMapping("/list")
    @Operation(summary = "获得指令步骤列表")
    @Parameter(name = "commandId", description = "指令编号", required = true)
    public CommonResult<List<CommandStepRespVO>> getCommandStepList(@RequestParam("commandId") Long commandId) {
        List<CommandStepRespVO> steps = commandStepService.getCommandStepList(commandId);
        return success(steps);
    }

    @PutMapping("/start")
    @Operation(summary = "启动指令步骤")
    @Parameter(name = "id", description = "步骤编号", required = true)
    public CommonResult<Boolean> startCommandStep(@RequestParam("id") Long id) {
        commandStepService.startCommandStep(id);
        return success(true);
    }

    @PutMapping("/complete")
    @Operation(summary = "完成指令步骤")
    @Parameter(name = "id", description = "步骤编号", required = true)
    public CommonResult<Boolean> completeCommandStep(@RequestParam("id") Long id,
                                                      @Valid @RequestBody CommandStepCompleteReqVO completeReqVO) {
        commandStepService.completeCommandStep(id, completeReqVO);
        return success(true);
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消指令步骤")
    @Parameter(name = "id", description = "步骤编号", required = true)
    public CommonResult<Boolean> cancelCommandStep(@RequestParam("id") Long id,
                                                    @RequestParam(value = "reason", required = false) String reason) {
        commandStepService.cancelCommandStep(id, reason);
        return success(true);
    }
}

