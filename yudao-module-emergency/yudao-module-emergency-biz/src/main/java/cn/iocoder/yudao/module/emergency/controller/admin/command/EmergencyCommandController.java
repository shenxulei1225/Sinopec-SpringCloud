package cn.iocoder.yudao.module.emergency.controller.admin.command;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.*;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandDO;
import cn.iocoder.yudao.module.emergency.service.command.EmergencyCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 应急指令
 */
@Tag(name = "管理后台 - 应急指令")
@RestController
@RequestMapping("/emergency/command")
@Validated
public class EmergencyCommandController {

    @Resource
    private EmergencyCommandService commandService;

    @PostMapping("/create")
    @Operation(summary = "创建指令")
    public CommonResult<Long> createCommand(@Valid @RequestBody EmergencyCommandCreateReqVO createReqVO) {
        Long commandId = commandService.createCommand(createReqVO);
        return success(commandId);
    }

    @PutMapping("/update")
    @Operation(summary = "更新指令")
    public CommonResult<Boolean> updateCommand(@Valid @RequestBody EmergencyCommandUpdateReqVO updateReqVO) {
        commandService.updateCommand(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除指令")
    @Parameter(name = "id", description = "指令编号", required = true)
    public CommonResult<Boolean> deleteCommand(@RequestParam("id") Long id) {
        commandService.deleteCommand(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得指令")
    @Parameter(name = "id", description = "指令编号", required = true)
    public CommonResult<EmergencyCommandRespVO> getCommand(@RequestParam("id") Long id) {
        EmergencyCommandDO command = commandService.getCommand(id);
        // 这里应该使用MapStruct进行转换，暂时直接返回DO
        // TODO: 添加MapStruct映射
        return success(null);
    }

    @GetMapping("/page")
    @Operation(summary = "获得指令分页")
    public CommonResult<PageResult<EmergencyCommandRespVO>> getCommandPage(@Valid EmergencyCommandPageReqVO pageReqVO) {
        PageResult<EmergencyCommandDO> pageResult = commandService.getCommandPage(pageReqVO);
        // 这里应该使用MapStruct进行转换，暂时返回null
        // TODO: 添加MapStruct映射
        return success(null);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新指令状态")
    public CommonResult<Boolean> updateCommandStatus(@RequestParam("id") Long id, @RequestParam("status") String status) {
        commandService.updateCommandStatus(id, status);
        return success(true);
    }

    @PutMapping("/terminate")
    @Operation(summary = "终止指令")
    public CommonResult<Boolean> terminateCommand(@RequestParam("id") Long id, @RequestParam("reason") String reason) {
        commandService.terminateCommand(id, reason);
        return success(true);
    }
}
