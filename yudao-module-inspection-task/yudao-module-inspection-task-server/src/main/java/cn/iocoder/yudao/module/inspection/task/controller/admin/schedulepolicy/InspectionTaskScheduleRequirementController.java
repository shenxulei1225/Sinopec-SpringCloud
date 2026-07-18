package cn.iocoder.yudao.module.inspection.task.controller.admin.schedulepolicy;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy.*;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule.InspectionTaskScheduleRequirementDO.ScheduleTemplateConfig;
import cn.iocoder.yudao.module.inspection.task.service.schedule.InspectionTaskScheduleRequirementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 排期需求 Controller
 *
 * <p>排期需求是一个配置容器，包含多个模板组合。</p>
 */
@Tag(name = "管理后台 - 排期需求")
@RestController
@RequestMapping("/inspection-task/schedule-requirement")
@Validated
public class InspectionTaskScheduleRequirementController {

    @Resource
    private InspectionTaskScheduleRequirementService scheduleRequirementService;

    // ==================== 基础 CRUD ====================

    @PostMapping("/create")
    @Operation(summary = "创建排期需求")
    public CommonResult<Long> createScheduleRequirement(@Valid @RequestBody InspectionTaskScheduleRequirementCreateReqVO reqVO) {
        return success(scheduleRequirementService.createScheduleRequirement(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新排期需求")
    public CommonResult<Boolean> updateScheduleRequirement(@Valid @RequestBody InspectionTaskScheduleRequirementUpdateReqVO reqVO) {
        scheduleRequirementService.updateScheduleRequirement(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除排期需求")
    @Parameter(name = "id", required = true, description = "排期需求ID")
    public CommonResult<Boolean> deleteScheduleRequirement(@PathVariable("id") Long id) {
        scheduleRequirementService.deleteScheduleRequirement(id);
        return success(true);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "获取排期需求详情")
    @Parameter(name = "id", required = true, description = "排期需求ID")
    public CommonResult<InspectionTaskScheduleRequirementRespVO> getScheduleRequirement(@PathVariable("id") Long id) {
        return success(scheduleRequirementService.getScheduleRequirement(id));
    }

    @GetMapping("/get-by-task/{taskId}")
    @Operation(summary = "根据任务ID获取排期需求")
    @Parameter(name = "taskId", required = true, description = "任务ID")
    public CommonResult<InspectionTaskScheduleRequirementRespVO> getScheduleRequirementByTaskId(@PathVariable("taskId") Long taskId) {
        return success(scheduleRequirementService.getScheduleRequirementByTaskId(taskId));
    }

    @GetMapping("/list-by-task/{taskId}")
    @Operation(summary = "根据任务ID获取所有排期需求")
    @Parameter(name = "taskId", required = true, description = "任务ID")
    public CommonResult<List<InspectionTaskScheduleRequirementRespVO>> getScheduleRequirementsByTaskId(@PathVariable("taskId") Long taskId) {
        return success(scheduleRequirementService.getScheduleRequirementsByTaskId(taskId));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询排期需求")
    public CommonResult<PageResult<InspectionTaskScheduleRequirementRespVO>> getScheduleRequirementPage(@Valid InspectionTaskScheduleRequirementPageReqVO pageReqVO) {
        // TODO: 实现分页查询
        return success(new PageResult<>(List.of(), 0L));
    }

    // ==================== 模板组合操作 ====================

    @PostMapping("/add-template-config/{requirementId}")
    @Operation(summary = "添加模板组合到排期需求")
    @Parameter(name = "requirementId", required = true, description = "排期需求ID")
    public CommonResult<Boolean> addTemplateConfig(
            @PathVariable("requirementId") Long requirementId,
            @Valid @RequestBody ScheduleTemplateConfigVO templateConfig) {
        ScheduleTemplateConfig config = convertToDOConfig(templateConfig);
        scheduleRequirementService.addTemplateConfig(requirementId, config);
        return success(true);
    }

    @PutMapping("/update-template-config/{requirementId}/{templateId}")
    @Operation(summary = "更新模板组合配置")
    @Parameter(name = "requirementId", required = true, description = "排期需求ID")
    @Parameter(name = "templateId", required = true, description = "模板ID")
    public CommonResult<Boolean> updateTemplateConfig(
            @PathVariable("requirementId") Long requirementId,
            @PathVariable("templateId") Long templateId,
            @Valid @RequestBody ScheduleTemplateConfigVO templateConfig) {
        ScheduleTemplateConfig config = convertToDOConfig(templateConfig);
        scheduleRequirementService.updateTemplateConfig(requirementId, templateId, config);
        return success(true);
    }

    @DeleteMapping("/remove-template-config/{requirementId}/{templateId}")
    @Operation(summary = "从排期需求中移除模板组合")
    @Parameter(name = "requirementId", required = true, description = "排期需求ID")
    @Parameter(name = "templateId", required = true, description = "模板ID")
    public CommonResult<Boolean> removeTemplateConfig(
            @PathVariable("requirementId") Long requirementId,
            @PathVariable("templateId") Long templateId) {
        scheduleRequirementService.removeTemplateConfig(requirementId, templateId);
        return success(true);
    }

    @PutMapping("/restore-template-config/{requirementId}/{templateId}")
    @Operation(summary = "恢复模板配置", description = "移除 changedFields，使用 originalConfig")
    @Parameter(name = "requirementId", required = true, description = "排期需求ID")
    @Parameter(name = "templateId", required = true, description = "模板ID")
    public CommonResult<Boolean> restoreTemplateConfig(
            @PathVariable("requirementId") Long requirementId,
            @PathVariable("templateId") Long templateId) {
        scheduleRequirementService.restoreTemplateConfig(requirementId, templateId);
        return success(true);
    }

    @GetMapping("/enabled-template-configs")
    @Operation(summary = "获取所有启用的模板组合", description = "用于编排引擎")
    public CommonResult<List<ScheduleTemplateConfigVO>> getEnabledTemplateConfigs() {
        List<ScheduleTemplateConfig> configs = scheduleRequirementService.getEnabledTemplateConfigs();
        return success(configs.stream().map(this::convertToVO).toList());
    }

    // ==================== 私有方法 ====================

    private ScheduleTemplateConfig convertToDOConfig(ScheduleTemplateConfigVO vo) {
        ScheduleTemplateConfig config = new ScheduleTemplateConfig();
        config.setTemplateId(vo.getTemplateId());
        config.setTemplateName(vo.getTemplateName());
        config.setOriginalConfig(vo.getOriginalConfig());
        config.setChangedFields(vo.getChangedFields());
        config.setEnabled(vo.getEnabled());
        return config;
    }

    private ScheduleTemplateConfigVO convertToVO(ScheduleTemplateConfig config) {
        ScheduleTemplateConfigVO vo = new ScheduleTemplateConfigVO();
        vo.setTemplateId(config.getTemplateId());
        vo.setTemplateName(config.getTemplateName());
        vo.setOriginalConfig(config.getOriginalConfig());
        vo.setChangedFields(config.getChangedFields());
        vo.setEnabled(config.getEnabled());
        return vo;
    }
}
