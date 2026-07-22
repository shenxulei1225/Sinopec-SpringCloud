package cn.cheers.x.module.dynamicbusiness.controller.admin.category;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryUserBindReqVO;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryUserRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 分类与系统用户关联")
@RestController
@RequestMapping("/dynamicbusiness/category-user")
@Validated
public class CategoryUserRelationController {

    @Resource
    private CategoryUserRelationService categoryUserRelationService;

    @PostMapping("/bind")
    @Operation(summary = "绑定系统用户到分类节点")
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<Boolean> bind(@Valid @RequestBody CategoryUserBindReqVO reqVO) {
        categoryUserRelationService.bindUsers(reqVO.getCategoryId(), reqVO.getUserIds());
        return success(true);
    }

    @PostMapping("/unbind")
    @Operation(summary = "从分类节点解绑系统用户")
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<Boolean> unbind(@Valid @RequestBody CategoryUserBindReqVO reqVO) {
        categoryUserRelationService.unbindUsers(reqVO.getCategoryId(), reqVO.getUserIds());
        return success(true);
    }

    @GetMapping("/list-user-ids")
    @Operation(summary = "查询分类节点下的系统用户 id 列表")
    @Parameter(name = "categoryId", description = "分类编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<Long>> listUserIds(@RequestParam("categoryId") Long categoryId) {
        return success(categoryUserRelationService.listUserIds(categoryId));
    }
}
