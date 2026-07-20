package cn.iocoder.yudao.module.emergency.controller.admin.command;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.*;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandTemplateDO;
import cn.iocoder.yudao.module.emergency.service.command.EmergencyCommandTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 应急指令模板")
@RestController
@RequestMapping("/emergency/command-template")
@Validated
public class EmergencyCommandTemplateController {

    @Resource
    private EmergencyCommandTemplateService commandTemplateService;

    @PostMapping("/create")
    @Operation(summary = "创建指令模板")
    public CommonResult<Long> createCommandTemplate(@Valid @RequestBody EmergencyCommandTemplateCreateReqVO createReqVO) {
        // 添加调试日志
        if (createReqVO.getApplicableScenarios() != null) {
            System.out.println("Controller接收到适用场景: " + createReqVO.getApplicableScenarios());
        } else {
            System.out.println("Controller接收到适用场景: null");
        }

        Long templateId = commandTemplateService.createCommandTemplate(createReqVO);
        return success(templateId);
    }

    @PutMapping("/update")
    @Operation(summary = "更新指令模板")
    public CommonResult<Boolean> updateCommandTemplate(@Valid @RequestBody EmergencyCommandTemplateUpdateReqVO updateReqVO) {
        // 添加调试日志
        if (updateReqVO.getApplicableScenarios() != null) {
            System.out.println("Controller更新接收到适用场景: " + updateReqVO.getApplicableScenarios());
        } else {
            System.out.println("Controller更新接收到适用场景: null");
        }

        commandTemplateService.updateCommandTemplate(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除指令模板")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteCommandTemplate(@RequestParam("id") Long id) {
        commandTemplateService.deleteCommandTemplate(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得指令模板")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<EmergencyCommandTemplateDO> getCommandTemplate(@RequestParam("id") Long id) {
        EmergencyCommandTemplateDO commandTemplate = commandTemplateService.getCommandTemplate(id);
        return success(commandTemplate);
    }

    @GetMapping("/page")
    @Operation(summary = "获得指令模板分页")
    public CommonResult<PageResult<EmergencyCommandTemplateDO>> getCommandTemplatePage(@Valid EmergencyCommandTemplatePageReqVO pageVO) {
        PageResult<EmergencyCommandTemplateDO> pageResult = commandTemplateService.getCommandTemplatePage(pageVO);
        return success(pageResult);
    }

    @GetMapping("/list-by-category")
    @Operation(summary = "获得指定分类的指令模板列表")
    @Parameter(name = "category", description = "分类")
    @Parameter(name = "stage", description = "阶段")
    public CommonResult<List<EmergencyCommandTemplateDO>> getCommandTemplateListByCategory(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "stage", required = false) String stage) {
        List<EmergencyCommandTemplateDO> list = commandTemplateService.getCommandTemplateListByCategory(category, stage);
        return success(list);
    }

    @PutMapping("/enable")
    @Operation(summary = "启用/禁用指令模板")
    @Parameter(name = "id", description = "编号", required = true)
    @Parameter(name = "enabled", description = "是否启用", required = true)
    public CommonResult<Boolean> enableCommandTemplate(@RequestParam("id") Long id, @RequestParam("enabled") Boolean enabled) {
        commandTemplateService.enableCommandTemplate(id, enabled);
        return success(true);
    }
}
