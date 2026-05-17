package cn.iocoder.yudao.module.system.controller.admin.category;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryBatchCreateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryBatchDeleteReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryBatchDeleteRespVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryBatchUpdateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryDeleteReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryDragReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryEnableDisableReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryMoveReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryRespVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategorySortReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryTreeRespVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryCreateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryUpdateReqVO;
import cn.iocoder.yudao.module.system.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.CREATE;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.DELETE;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.UPDATE;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 分类管理")
@RestController
@RequestMapping("/system/category")
@Validated
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @PostMapping("/create")
    @Operation(summary = "创建分类")
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:category:create')")
    public CommonResult<Long> createCategory(@Valid @RequestBody CategoryCreateReqVO reqVO) {
        return success(categoryService.createCategory(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新分类")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<Boolean> updateCategory(@Valid @RequestBody CategoryUpdateReqVO reqVO) {
        categoryService.updateCategory(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除分类")
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('system:category:delete')")
    public CommonResult<Boolean> deleteCategory(@Valid @RequestBody CategoryDeleteReqVO reqVO) {
        categoryService.deleteCategory(reqVO);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取分类详情")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<CategoryRespVO> getCategory(@RequestParam("id") Long id) {
        return success(categoryService.getCategoryVO(id));
    }

    @GetMapping("/tree")
    @Operation(summary = "获取分类树")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<CategoryTreeRespVO>> getCategoryTree(@RequestParam("categoryTypeCode") String categoryTypeCode,
                                                                  @RequestParam(value = "status", required = false) Integer status) {
        return success(categoryService.getCategoryTreeByType(categoryTypeCode, status));
    }

    @GetMapping("/get-subtree")
    @Operation(summary = "获取指定节点的子树")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<CategoryTreeRespVO> getCategorySubtreeWithRoot(@RequestParam("id") Long id,
                                                                       @RequestParam("categoryTypeCode") String categoryTypeCode,
                                                                       @RequestParam(value = "status", required = false) Integer status) {
        return success(categoryService.getCategorySubtreeWithRoot(id, categoryTypeCode, status));
    }

    @GetMapping("/list-children")
    @Operation(summary = "获取子分类列表")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<CategoryRespVO>> getChildren(@RequestParam("id") Long id,
                                                          @RequestParam("categoryTypeCode") String categoryTypeCode,
                                                          @RequestParam(value = "status", required = false) Integer status,
                                                          @RequestParam(value = "recursive", defaultValue = "false") Boolean recursive) {
        return success(Boolean.TRUE.equals(recursive)
                ? categoryService.listByParentRecursive(categoryTypeCode, id, status)
                : categoryService.listByParent(categoryTypeCode, id, status));
    }

    @GetMapping("/get-path")
    @Operation(summary = "获取分类路径")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<CategoryRespVO>> getPath(@RequestParam("id") Long id,
                                                      @RequestParam("categoryTypeCode") String categoryTypeCode) {
        return success(categoryService.getPath(id, categoryTypeCode));
    }

    @PutMapping("/move")
    @Operation(summary = "移动分类")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<Boolean> moveCategory(@RequestParam("id") Long id,
                                              @Valid @RequestBody CategoryMoveReqVO body) {
        categoryService.moveCategory(id, body.getTargetParentId(), body.getCategoryTypeCode());
        return success(true);
    }

    @PutMapping("/sort")
    @Operation(summary = "排序分类")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<Boolean> sortCategories(@Valid @RequestBody CategorySortReqVO body) {
        categoryService.sortCategories(body.getParentId(), body.getCategoryIds(), body.getCategoryTypeCode());
        return success(true);
    }

    @PostMapping("/drag")
    @Operation(summary = "拖拽分类")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<Boolean> dragCategory(@Valid @RequestBody CategoryDragReqVO body) {
        categoryService.dragCategory(body);
        return success(true);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索分类")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<CategoryRespVO>> searchCategories(@RequestParam("keyword") String keyword,
                                                               @RequestParam("categoryTypeCode") String categoryTypeCode) {
        return success(categoryService.searchCategoryList(keyword, categoryTypeCode));
    }

    @GetMapping("/search-tree")
    @Operation(summary = "搜索分类树")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<CategoryTreeRespVO>> searchCategoryTree(@RequestParam("keyword") String keyword,
                                                                     @RequestParam("categoryTypeCode") String categoryTypeCode) {
        return success(categoryService.searchCategoryTree(keyword, categoryTypeCode));
    }

    @PutMapping("/enable")
    @Operation(summary = "启用分类")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<Boolean> enable(@Valid @RequestBody CategoryEnableDisableReqVO reqVO) {
        categoryService.updateStatus(reqVO.getId(), 1, reqVO.getCategoryTypeCode());
        return success(true);
    }

    @PutMapping("/disable")
    @Operation(summary = "禁用分类")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<Boolean> disable(@Valid @RequestBody CategoryEnableDisableReqVO reqVO) {
        categoryService.updateStatus(reqVO.getId(), 0, reqVO.getCategoryTypeCode());
        return success(true);
    }

    @PostMapping("/batch-create")
    @Operation(summary = "批量创建分类")
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:category:create')")
    public CommonResult<List<CategoryRespVO>> batchCreateCategory(@Valid @RequestBody CategoryBatchCreateReqVO reqVO) {
        return success(categoryService.batchCreateCategory(reqVO.getCategories()));
    }

    @PutMapping("/batch-update")
    @Operation(summary = "批量更新分类")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<List<CategoryRespVO>> batchUpdateCategory(@Valid @RequestBody CategoryBatchUpdateReqVO reqVO) {
        return success(categoryService.batchUpdateCategory(reqVO.getCategories()));
    }

    @DeleteMapping("/batch-delete")
    @Operation(summary = "批量删除分类")
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('system:category:delete')")
    public CommonResult<CategoryBatchDeleteRespVO> batchDeleteCategory(@Valid @RequestBody CategoryBatchDeleteReqVO reqVO) {
        return success(categoryService.batchDeleteCategory(reqVO.getIds(), Boolean.TRUE.equals(reqVO.getCascade()), reqVO.getCategoryTypeCode()));
    }
}
