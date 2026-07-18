package cn.cheers.x.module.dynamicbusiness.controller.admin.model;

import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelFieldGroupAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.*;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 模型字段分组关联 Controller
 * 
 * 职责：管理字段与分组的关联关系
 * 
 * 设计说明：
 * - 分组信息存储在 Model.fieldGroupsConfig 中（JSON格式），与字段库完全解耦
 * - 字段库是全局通用的，不存储任何与 model 相关的内容
 * - 字段与分组的关联通过 ModelFieldAssignment.fieldGroupId 实现
 * - 分组信息由 ModelFieldGroupService 管理（只管理分组本身，不涉及字段）
 * - 字段关联由此 Controller 管理（只管理字段与分组的关联关系）
 * 
 * @author yudao
 */
@Tag(name = "管理后台 - 模型字段分组关联", description = "提供字段与分组的关联管理功能")
@RestController
@RequestMapping("/dynamicbusiness/model-field-group-assignment")
@Validated
public class ModelFieldGroupAssignmentController {

    @Resource
    private ModelFieldGroupAssignmentService modelFieldGroupAssignmentService;

    @PostMapping("/assign")
    @Operation(
        summary = "将字段分配到分组",
        description = "将模型中的字段分配到指定的分组中"
    )
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:model-field-group-assignment:create')")
    public CommonResult<Boolean> assignFieldToGroup(
            @Parameter(description = "模型ID", required = true) @RequestParam Long modelId,
            @Parameter(description = "字段ID", required = true) @RequestParam Long fieldId,
            @Parameter(description = "分组ID", required = true) @RequestParam Long groupId) {
        modelFieldGroupAssignmentService.assignFieldToGroup(modelId, fieldId, groupId);
        return success(true);
    }

    @DeleteMapping("/unassign")
    @Operation(
        summary = "从分组移除字段",
        description = "将字段从分组中移除（取消分组关联）"
    )
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('system:model-field-group-assignment:delete')")
    public CommonResult<Boolean> unassignFieldFromGroup(
            @Parameter(description = "模型ID", required = true) @RequestParam Long modelId,
            @Parameter(description = "字段ID", required = true) @RequestParam Long fieldId) {
        modelFieldGroupAssignmentService.unassignFieldFromGroup(modelId, fieldId);
        return success(true);
    }

    @GetMapping("/fields")
    @Operation(
        summary = "获取分组下的字段列表",
        description = "查询指定分组下的所有字段"
    )
    @PreAuthorize("@ss.hasPermission('system:model-field-group-assignment:query')")
    public CommonResult<List<ModelFieldAssignmentRespVO>> getFieldsByGroup(
            @Parameter(description = "模型ID", required = true) @RequestParam Long modelId,
            @Parameter(description = "分组ID", required = true) @RequestParam Long groupId) {
        List<ModelFieldAssignmentRespVO> fields = modelFieldGroupAssignmentService.getFieldsByGroup(modelId, groupId);
        return success(fields);
    }

    @PostMapping("/reorder")
    @Operation(
        summary = "重排分组内字段顺序",
        description = "根据给定的字段ID顺序，重排指定分组下的字段顺序"
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model-field-group-assignment:update')")
    public CommonResult<Boolean> reorderGroupFields(
            @Parameter(description = "模型ID", required = true) @RequestParam Long modelId,
            @Parameter(description = "分组ID", required = true) @RequestParam Long groupId,
            @RequestBody List<Long> fieldIds) {
        modelFieldGroupAssignmentService.reorderGroupFields(modelId, groupId, fieldIds);
        return success(true);
    }
}
