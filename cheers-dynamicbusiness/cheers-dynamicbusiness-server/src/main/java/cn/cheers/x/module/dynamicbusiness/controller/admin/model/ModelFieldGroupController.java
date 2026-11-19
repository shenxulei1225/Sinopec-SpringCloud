package cn.cheers.x.module.dynamicbusiness.controller.admin.model;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldGroupCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldGroupRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldGroupUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelFieldGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 模型字段分组 Controller
 *
 * 型号设计器里给字段分组、排顺序，属于平台配置。只有字段配置器读这些分组，实体表单不读。
 * 权限与字段分配（ModelFieldAssignmentController）同一套菜单权限，不按目录授权核对。
 */
@Tag(name = "管理后台 - 模型字段分组")
@RestController
@RequestMapping("/dynamicbusiness/model-field-group")
public class ModelFieldGroupController {

    @Resource
    private ModelFieldGroupService modelFieldGroupService;

    @PostMapping("/create")
    @Operation(summary = "创建模型字段分组")
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:update')")
    public CommonResult<Long> createModelFieldGroup(@Valid @RequestBody ModelFieldGroupCreateReqVO reqVO) {
        Long id = modelFieldGroupService.createModelFieldGroup(reqVO);
        return success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新模型字段分组")
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:update')")
    public CommonResult<Boolean> updateModelFieldGroup(@Valid @RequestBody ModelFieldGroupUpdateReqVO reqVO) {
        modelFieldGroupService.updateModelFieldGroup(reqVO.getModelId(), reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除模型字段分组")
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:update')")
    public CommonResult<Boolean> deleteModelFieldGroup(@RequestParam Long modelId, @RequestParam Long groupId) {
        modelFieldGroupService.deleteModelFieldGroup(modelId, groupId);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取模型字段分组详情")
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:query')")
    public CommonResult<ModelFieldGroupRespVO> getModelFieldGroup(@RequestParam Long modelId, @RequestParam Long groupId) {
        ModelFieldGroupRespVO respVO = modelFieldGroupService.getModelFieldGroup(modelId, groupId);
        return success(respVO);
    }

    @GetMapping("/list")
    @Operation(summary = "根据模型ID获取字段分组列表")
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:query')")
    public CommonResult<List<ModelFieldGroupRespVO>> listModelFieldGroups(@RequestParam Long modelId) {
        List<ModelFieldGroupRespVO> list = modelFieldGroupService.listModelFieldGroupsByModelId(modelId);
        return success(list);
    }
}
