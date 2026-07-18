package cn.iocoder.yudao.module.inspection.task.controller.admin.task;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.template.InspectionTaskTemplateCreateReqVO;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.template.InspectionTaskTemplateSaveFromTaskReqVO;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.template.InspectionTaskTemplateUpdateReqVO;
import cn.iocoder.yudao.module.inspection.task.service.task.InspectionTaskTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 巡检任务模板")
@RestController
@RequestMapping("/inspection-task/template")
@Validated
public class InspectionTaskTemplateController {

    @Resource
    private InspectionTaskTemplateService inspectionTaskTemplateService;

    @PostMapping("/create")
    @Operation(summary = "创建任务模板")
    public CommonResult<Long> createTemplate(@Valid @RequestBody InspectionTaskTemplateCreateReqVO reqVO) {
        return success(inspectionTaskTemplateService.createTemplate(reqVO));
    }

    @PostMapping("/save-from-task")
    @Operation(summary = "将已有任务另存为模板")
    public CommonResult<Long> saveTemplateFromTask(@Valid @RequestBody InspectionTaskTemplateSaveFromTaskReqVO reqVO) {
        return success(inspectionTaskTemplateService.saveTemplateFromTask(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新任务模板")
    public CommonResult<Boolean> updateTemplate(@Valid @RequestBody InspectionTaskTemplateUpdateReqVO reqVO) {
        inspectionTaskTemplateService.updateTemplate(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除任务模板")
    @Parameter(name = "id", required = true)
    public CommonResult<Boolean> deleteTemplate(@PathVariable("id") Long id) {
        inspectionTaskTemplateService.deleteTemplate(id);
        return success(true);
    }
}
