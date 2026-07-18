package cn.cheers.x.module.dynamicbusiness.controller.admin.category;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTypeCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTypeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTypeUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 分类类型管理 Controller
 */
@Tag(name = "管理后台 - 分类类型管理", description = "提供分类类型的创建、更新、删除、查询功能。分类类型定义了分类的维度体系，如区域、设备类型、项目阶段等")
@RestController
@RequestMapping("/dynamicbusiness/category-type")
@Validated
@RequiredArgsConstructor
public class CategoryTypeController {

    @Resource
    private final CategoryTypeService categoryTypeService;

    @PostMapping("/create")
    @Operation(
        summary = "创建分类类型",
        description = """
            创建一个新的分类类型，定义分类维度体系。
            - 分类类型编码全局唯一，用于标识分类维度
            - 例如：region（区域）、equipment_type（设备类型）、project_phase（项目阶段）
            - 创建后可以基于此类型创建多棵分类树
            """
    )
    @PreAuthorize("@ss.hasPermission('system:category-type:create')")
    public CommonResult<Long> createCategoryType(@Valid @RequestBody CategoryTypeCreateReqVO reqVO) {
        return success(categoryTypeService.createCategoryType(reqVO));
    }

    @PutMapping("/update")
    @Operation(
        summary = "更新分类类型",
        description = """
            更新分类类型的基本信息。
            - 只能更新名称、描述、状态等基本信息
            - 分类类型编码不可修改
            """
    )
    @PreAuthorize("@ss.hasPermission('system:category-type:update')")
    public CommonResult<Boolean> updateCategoryType(@Valid @RequestBody CategoryTypeUpdateReqVO reqVO) {
        categoryTypeService.updateCategoryType(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(
        summary = "删除分类类型",
        description = """
            删除指定的分类类型。
            - 删除前会检查是否有分类数据在使用此类型
            - 如果存在关联的分类数据，则禁止删除
            """
    )
    @Parameter(name = "id", description = "分类类型编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:category-type:delete')")
    public CommonResult<Boolean> deleteCategoryType(@RequestParam("id") Long id) {
        categoryTypeService.deleteCategoryType(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(
        summary = "获取分类类型详情",
        description = """
            根据分类类型ID获取详细信息，包含：
            - 基本信息（编码、名称、描述等）
            - 创建者和创建时间信息
            """
    )
    @Parameter(name = "id", description = "分类类型编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:category-type:query')")
    public CommonResult<CategoryTypeRespVO> getCategoryType(@RequestParam("id") Long id) {
        return success(categoryTypeService.getCategoryType(id));
    }

    @GetMapping("/list-enabled")
    @Operation(
        summary = "获取启用的分类类型列表",
        description = """
            获取所有状态为启用的分类类型列表。
            - 用于下拉选择器等场景
            - 按创建时间倒序排列
            """
    )
    @PreAuthorize("@ss.hasPermission('system:category-type:query')")
    public CommonResult<List<CategoryTypeRespVO>> getEnabledCategoryTypes() {
        return success(categoryTypeService.getEnabledCategoryTypes());
    }

    @GetMapping("/list-by-creator")
    @Operation(
        summary = "获取指定用户的分类类型列表",
        description = """
            根据创建者用户ID获取分类类型列表。
            - 用于权限控制和个人分类管理
            - 按创建时间倒序排列
            """
    )
    @Parameter(name = "creator", description = "创建者", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:category-type:query')")
    public CommonResult<List<CategoryTypeRespVO>> getCategoryTypesByCreator(@RequestParam("creator") String creator) {
        return success(categoryTypeService.getCategoryTypesByCreator(creator));
    }

    @GetMapping("/search")
    @Operation(
        summary = "搜索分类类型",
        description = """
            根据关键词搜索分类类型。
            - 关键词会匹配分类类型编码、名称、描述
            - 不进行分页，返回所有匹配的结果
            - 适用于搜索选择器等场景
            """
    )
    @Parameter(name = "keyword", description = "关键词（必填，支持模糊匹配分类类型编码、名称、描述）", required = true, example = "项目")
    @PreAuthorize("@ss.hasPermission('system:category-type:query')")
    public CommonResult<List<CategoryTypeRespVO>> searchCategoryTypes(@RequestParam("keyword") String keyword) {
        return success(categoryTypeService.searchCategoryTypes(keyword));
    }

    @GetMapping("/get-by-code")
    @Operation(
        summary = "根据编码获取分类类型",
        description = """
            根据分类类型编码获取详情。
            - 返回分类类型基础信息与 topLevelCategoryId
            """
    )
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", required = true, example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:category-type:query')")
    public CommonResult<CategoryTypeRespVO> getCategoryTypeByCode(@RequestParam("categoryTypeCode") String categoryTypeCode) {
        return success(categoryTypeService.getCategoryTypeByCode(categoryTypeCode));
    }

}