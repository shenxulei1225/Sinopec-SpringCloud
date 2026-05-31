package cn.cheers.x.module.dynamicbusiness.controller.admin.category;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.*;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.CREATE;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.UPDATE;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.DELETE;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 分类管理 Controller
 * 
 * Category（分类）用于对 Model 和 Entity 进行归类与筛选，是一个树形结构。
 * Category 只用于归类与筛选，**不影响字段规则**（字段规则由 Model 决定）。
 * 一个 Model 可以关联多个 Category，一个 Category 下可以有多个 Model。
 * 
 * @author yudao
 */
@Tag(name = "管理后台 - 分类管理", description = "提供分类的创建、更新、删除、查询、移动、排序等功能。分类用于对模型和实体进行归类与筛选，不影响字段规则")
@RestController
@RequestMapping("/dynamicbusiness/category")
@Validated
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @PostMapping("/create")
    @Operation(
        summary = "创建分类",
        description = """
            创建一个新的分类节点。
            - 分类支持树形结构，可以指定父分类ID
            - 分类名称在同一业务类型下必须唯一
            - 分类只用于归类与筛选，不影响字段规则
            """
    )
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:category:create')")
    public CommonResult<Long> createCategory(@Valid @RequestBody CategoryCreateReqVO reqVO) {
        return success(categoryService.createCategory(reqVO));
    }

    @PutMapping("/update")
    @Operation(
        summary = "更新分类",
        description = """
            更新分类的基本信息（名称、描述、状态等）。
            - 分类名称在同一业务类型下必须唯一
            - 更新不会影响已关联的模型和实体
            """
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<Boolean> updateCategory(@Valid @RequestBody CategoryUpdateReqVO reqVO) {
        categoryService.updateCategory(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(
        summary = "删除分类",
        description = """
            删除分类节点。
            - 如果分类下有子分类，需要设置 cascade=true 才能级联删除
            - 删除前会检查是否有关联的模型，如果存在关联则禁止删除
            - 删除分类不会影响已关联的模型和实体，只是解除关联关系
            """
    )
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('system:category:delete')")
    public CommonResult<Boolean> deleteCategory(@Valid @RequestBody CategoryDeleteReqVO reqVO) {
        categoryService.deleteCategory(reqVO);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(
        summary = "获取分类详情",
        description = """
            根据分类ID获取详细信息，包含：
            - 基本信息（名称、描述、状态等）
            - 父分类ID和路径信息
            """
    )
    @Parameter(name = "id", description = "分类编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<CategoryRespVO> getCategory(@RequestParam("id") Long id) {
        return success(categoryService.getCategoryVO(id));
    }

    @GetMapping("/tree")
    @Operation(
        summary = "获取分类树",
        description = """
            获取完整的分类树结构。
            - 返回列表结构，包含所有子分类
            - 支持按状态过滤（只返回启用或禁用的分类）
            - 适用于下拉选择、树形展示等场景
            """
    )
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", required = true, example = "equipment")
    @Parameter(name = "status", description = "状态（可选，0-禁用，1-启用）", example = "1")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<CategoryTreeRespVO>> getCategoryTree(@RequestParam("categoryTypeCode") String categoryTypeCode,
                                                                  @RequestParam(value = "status", required = false) Integer status) {
        return success(categoryService.getCategoryTreeByType(categoryTypeCode, status));
    }

    @GetMapping("/tree-with-models")
    @Operation(
        summary = "获取分类树（含模型）",
        description = """
            获取完整分类树，并在每个分类节点附带 models 列表。
            - 用于 Pattern B 左侧树“分类 + 模型”展示
            - 返回结构前端可直接渲染，无需二次组装
            """
    )
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", required = true, example = "equipment")
    @Parameter(name = "status", description = "状态（可选，0-禁用，1-启用）", example = "1")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<CategoryTreeWithModelsRespVO>> getCategoryTreeWithModels(
            @RequestParam("categoryTypeCode") String categoryTypeCode,
            @RequestParam(value = "status", required = false) Integer status) {
        return success(categoryService.getCategoryTreeWithModels(categoryTypeCode, status));
    }

    @GetMapping("/get-subtree")
    @Operation(
        summary = "获取指定节点的子树",
        description = """
            获取以指定分类为根节点的子树结构（包含该节点及其所有后代）。
            - 支持按状态过滤（只返回启用或禁用的分类）
            - 适用于只展示某个分类分支的场景
            """
    )
    @Parameter(name = "id", description = "分类编号（子树根节点）", required = true, example = "1")
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", required = true, example = "equipment")
    @Parameter(name = "status", description = "状态（可选，0-禁用，1-启用）", example = "1")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<CategoryTreeRespVO> getCategorySubtreeWithRoot(@RequestParam("id") Long id,
                                                               @RequestParam("categoryTypeCode") String categoryTypeCode,
                                                               @RequestParam(value = "status", required = false) Integer status) {
        return success(categoryService.getCategorySubtreeWithRoot(id, categoryTypeCode, status));
    }

    @GetMapping("/list-children")
    @Operation(
        summary = "获取子分类列表（扁平）",
        description = """
            获取指定分类节点的子分类列表（扁平结构）。
            - recursive=false：只返回直接子分类
            - recursive=true：返回所有后代分类（递归，扁平列表，不包含根节点自身）
            - 支持按状态过滤
            """
    )
    @Parameter(name = "id", description = "分类编号（父分类ID）", required = true, example = "1")
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", required = true, example = "equipment")
    @Parameter(name = "status", description = "状态（可选，0-禁用，1-启用）", example = "1")
    @Parameter(name = "recursive", description = "是否递归获取所有子分类（默认false）", example = "false")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<CategoryRespVO>> getChildren(@RequestParam("id") Long id,
                                                          @RequestParam("categoryTypeCode") String categoryTypeCode,
                                                          @RequestParam(value = "status", required = false) Integer status,
                                                          @RequestParam(value = "recursive", defaultValue = "false") Boolean recursive) {
        if (Boolean.TRUE.equals(recursive)) {
            return success(categoryService.listByParentRecursive(categoryTypeCode, id, status));
        } else {
            return success(categoryService.listByParent(categoryTypeCode, id, status));
        }
    }
    
    @GetMapping("/get-path")
    @Operation(
        summary = "获取分类路径",
        description = """
            获取从根分类到指定分类的完整路径。
            - 返回路径数组，从根分类到当前分类
            - 适用于面包屑导航等场景
            """
    )
    @Parameter(name = "id", description = "分类编号", required = true, example = "1")
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", required = true, example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<CategoryRespVO>> getPath(@RequestParam("id") Long id,
                                                      @RequestParam("categoryTypeCode") String categoryTypeCode) {
        return success(categoryService.getPath(id, categoryTypeCode));
    }



    @PutMapping("/move")
    @Operation(
        summary = "移动分类",
        description = """
            将分类移动到新的父分类下。
            - 可以改变分类在树中的位置
            - 移动后会自动更新排序和路径信息
            """
    )
    @Parameter(name = "id", description = "分类编号", required = true, example = "1")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<Boolean> moveCategory(@RequestParam("id") Long id,
                                              @Valid @RequestBody CategoryMoveReqVO body) {
        categoryService.moveCategory(id, body.getTargetParentId(), body.getCategoryTypeCode());
        return success(true);
    }

    @PutMapping("/sort")
    @Operation(
        summary = "排序分类",
        description = """
            对分类进行排序。
            - 对指定父分类下的子分类进行排序
            - 通过传入分类ID数组的顺序来指定排序
            - 会验证所有分类是否属于同一个父分类
            """
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<Boolean> sortCategories(@Valid @RequestBody CategorySortReqVO body) {
        categoryService.sortCategories(body.getParentId(), body.getCategoryIds(), body.getCategoryTypeCode());
        return success(true);
    }

    @PostMapping("/drag")
    @Operation(
        summary = "拖拽分类（排序/改层级）",
        description = """
            支持 BEFORE/AFTER/INNER 三种拖拽落点：
            - BEFORE/AFTER：与目标节点同父级内重排
            - INNER：拖入目标节点内部，成为其子节点
            仅影响分类的 parentId/sort/treePath，不影响模型/实体关联关系。
            """
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<Boolean> dragCategory(@Valid @RequestBody CategoryDragReqVO body) {
        categoryService.dragCategory(body);
        return success(true);
    }

    @GetMapping("/search")
    @Operation(
        summary = "搜索分类",
        description = """
            根据关键词搜索分类。
            - 关键词会匹配分类名称（模糊搜索）
            - 返回所有匹配的分类，不进行分页
            - 适用于分类选择器等场景
            """
    )
    @Parameter(name = "keyword", description = "关键词（必填，模糊匹配分类名称）", required = true, example = "设备")
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", required = true, example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<CategoryRespVO>> searchCategories(@RequestParam("keyword") String keyword,
                                                               @RequestParam("categoryTypeCode") String categoryTypeCode) {
        return success(categoryService.searchCategoryList(keyword, categoryTypeCode));
    }

    @GetMapping("/search-tree")
    @Operation(
        summary = "搜索分类（树形返回）",
        description = """
            根据关键词搜索分类，并以树形结构返回结果。
            - 关键词会匹配分类名称（模糊搜索）
            - 返回树包含：命中节点 + 命中节点祖先链 + 命中节点的全部子孙节点（整棵子树）
            - 适用于树组件搜索后直接展示结果（无需前端重新拼树）
            """
    )
    @Parameter(name = "keyword", description = "关键词（必填，模糊匹配分类名称）", required = true, example = "设备")
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", required = true, example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<CategoryTreeRespVO>> searchCategoryTree(@RequestParam("keyword") String keyword,
                                                                     @RequestParam("categoryTypeCode") String categoryTypeCode) {
        return success(categoryService.searchCategoryTree(keyword, categoryTypeCode));
    }

    @PutMapping("/enable")
    @Operation(
        summary = "启用分类",
        description = """
            启用指定的分类。
            - 启用后分类可以正常使用
            - 禁用的分类在查询时会被过滤（如果指定了status参数）
            """
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<Boolean> enable(@Valid @RequestBody CategoryEnableDisableReqVO reqVO) {
        categoryService.updateStatus(reqVO.getId(), 1, reqVO.getCategoryTypeCode());
        return success(true);
    }

    @PutMapping("/disable")
    @Operation(
        summary = "禁用分类",
        description = """
            禁用指定的分类。
            - 禁用后分类在查询时会被过滤（如果指定了status参数）
            - 已关联的模型和实体不受影响
            """
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<Boolean> disable(@Valid @RequestBody CategoryEnableDisableReqVO reqVO) {
        categoryService.updateStatus(reqVO.getId(), 0, reqVO.getCategoryTypeCode());
        return success(true);
    }

    @PostMapping("/batch-create")
    @Operation(
        summary = "批量创建分类",
        description = """
            批量创建多个分类。
            - 支持一次创建多个分类节点
            - 返回创建成功的分类列表
            """
    )
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:category:create')")
    public CommonResult<List<CategoryRespVO>> batchCreateCategory(@Valid @RequestBody CategoryBatchCreateReqVO reqVO) {
        return success(categoryService.batchCreateCategory(reqVO.getCategories()));
    }

    @PutMapping("/batch-update")
    @Operation(
        summary = "批量更新分类",
        description = """
            批量更新多个分类的信息。
            - 支持一次更新多个分类
            - 返回更新后的分类列表
            """
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<List<CategoryRespVO>> batchUpdateCategory(@Valid @RequestBody CategoryBatchUpdateReqVO reqVO) {
        return success(categoryService.batchUpdateCategory(reqVO.getCategories()));
    }

    @DeleteMapping("/batch-delete")
    @Operation(
        summary = "批量删除分类",
        description = """
            批量删除多个分类。
            - 支持一次删除多个分类
            - 如果设置了级联删除，会同时删除子分类
            - 返回删除结果，包含成功和失败的信息
            """
    )
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('system:category:delete')")
    public CommonResult<CategoryBatchDeleteRespVO> batchDeleteCategory(@Valid @RequestBody CategoryBatchDeleteReqVO reqVO) {
        return success(categoryService.batchDeleteCategory(reqVO.getIds(), false, reqVO.getCategoryTypeCode()));
    }
}