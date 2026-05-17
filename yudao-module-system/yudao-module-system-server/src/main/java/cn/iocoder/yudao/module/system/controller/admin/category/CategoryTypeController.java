package cn.iocoder.yudao.module.system.controller.admin.category;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryTypeCreateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryTypeRespVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryTypeUpdateReqVO;
import cn.iocoder.yudao.module.system.service.category.CategoryTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 分类类型管理")
@RestController
@RequestMapping("/system/category-type")
@Validated
@RequiredArgsConstructor
public class CategoryTypeController {

    @Resource
    private final CategoryTypeService categoryTypeService;

    @PostMapping("/create")
    @Operation(summary = "创建分类类型")
    @PreAuthorize("@ss.hasPermission('system:category-type:create')")
    public CommonResult<Long> createCategoryType(@Valid @RequestBody CategoryTypeCreateReqVO reqVO) {
        return success(categoryTypeService.createCategoryType(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新分类类型")
    @PreAuthorize("@ss.hasPermission('system:category-type:update')")
    public CommonResult<Boolean> updateCategoryType(@Valid @RequestBody CategoryTypeUpdateReqVO reqVO) {
        categoryTypeService.updateCategoryType(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除分类类型")
    @PreAuthorize("@ss.hasPermission('system:category-type:delete')")
    public CommonResult<Boolean> deleteCategoryType(@RequestParam("id") Long id) {
        categoryTypeService.deleteCategoryType(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取分类类型详情")
    @PreAuthorize("@ss.hasPermission('system:category-type:query')")
    public CommonResult<CategoryTypeRespVO> getCategoryType(@RequestParam("id") Long id) {
        return success(categoryTypeService.getCategoryType(id));
    }

    @GetMapping("/list-enabled")
    @Operation(summary = "获取启用的分类类型列表")
    @PreAuthorize("@ss.hasPermission('system:category-type:query')")
    public CommonResult<List<CategoryTypeRespVO>> getEnabledCategoryTypes() {
        return success(categoryTypeService.getEnabledCategoryTypes());
    }

    @GetMapping("/list-by-creator")
    @Operation(summary = "获取指定创建者的分类类型列表")
    @PreAuthorize("@ss.hasPermission('system:category-type:query')")
    public CommonResult<List<CategoryTypeRespVO>> getCategoryTypesByCreator(@RequestParam("creator") String creator) {
        return success(categoryTypeService.getCategoryTypesByCreator(creator));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索分类类型")
    @PreAuthorize("@ss.hasPermission('system:category-type:query')")
    public CommonResult<List<CategoryTypeRespVO>> searchCategoryTypes(@RequestParam("keyword") String keyword) {
        return success(categoryTypeService.searchCategoryTypes(keyword));
    }

    @GetMapping("/get-by-code")
    @Operation(summary = "根据编码获取分类类型")
    @Parameter(name = "categoryTypeCode", required = true)
    @PreAuthorize("@ss.hasPermission('system:category-type:query')")
    public CommonResult<CategoryTypeRespVO> getCategoryTypeByCode(@RequestParam("categoryTypeCode") String categoryTypeCode) {
        return success(categoryTypeService.getCategoryTypeByCode(categoryTypeCode));
    }
}
