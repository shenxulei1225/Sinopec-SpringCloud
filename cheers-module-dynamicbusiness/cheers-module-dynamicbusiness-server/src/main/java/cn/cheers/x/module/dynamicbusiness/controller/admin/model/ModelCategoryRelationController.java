package cn.cheers.x.module.dynamicbusiness.controller.admin.model;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.BatchModelCategoryAssociationRespVO;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelCategoryRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 模型-分类关联 API Controller
 *
 * <p>提供 Model-Category 多对多关联的所有操作接口。</p>
 *
 * @author 基础服务模块
 */
@Tag(name = "管理后台 - 模型-分类关联 API")
@RestController
@RequestMapping("/dynamicbusiness/model-category-relation")
@Validated
public class ModelCategoryRelationController {

    @Resource
    private ModelCategoryRelationService relationService;

    // ==================== 单模型-单分类操作 ====================

    @PostMapping("/associate")
    @Operation(
        summary = "关联单个模型到单个分类",
        description = "适用场景：模型编辑页手动绑定一个分类。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。\n" +
                "说明：只建立模型-分类关系，不返回模型详情。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<Boolean> associate(
            @RequestParam("modelId") Long modelId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(relationService.associate(modelId, categoryId, entityTypeCode) != null);
    }

    @DeleteMapping("/disassociate")
    @Operation(
        summary = "解除单个模型与单个分类的关联",
        description = "适用场景：模型编辑页移除单个分类绑定。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<Boolean> disassociate(
            @RequestParam("modelId") Long modelId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(relationService.disassociate(modelId, categoryId, entityTypeCode) != null);
    }

    @GetMapping("/exists")
    @Operation(
        summary = "检查关联是否存在",
        description = "适用场景：前端提交前做幂等判断或按钮态控制。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<Boolean> existsRelation(
            @RequestParam("modelId") Long modelId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(relationService.existsRelation(modelId, categoryId, entityTypeCode));
    }

    // ==================== 单模型-多分类操作 ====================

    @PostMapping("/model/{modelId}/categories")
    @Operation(
        summary = "批量关联单个模型到多个分类",
        description = "适用场景：一次性给一个模型绑定多个分类。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<Boolean> batchAssociateModelToCategories(
            @PathVariable("modelId") Long modelId,
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestBody List<Long> categoryIds) {
        return success(relationService.batchAssociateModelToCategories(modelId, categoryIds, entityTypeCode) != null);
    }

    @DeleteMapping("/model/{modelId}/categories")
    @Operation(
        summary = "批量解除单个模型与多个分类的关联",
        description = "适用场景：一次性移除一个模型的多个分类绑定。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<Boolean> batchDisassociateModelFromCategories(
            @PathVariable("modelId") Long modelId,
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestBody List<Long> categoryIds) {
        return success(relationService.batchDisassociateModelFromCategories(modelId, categoryIds, entityTypeCode) != null);
    }

    @PutMapping("/model/{modelId}/categories")
    @Operation(
        summary = "更新模型关联的分类",
        description = "适用场景：模型编辑保存时用最新分类集合覆盖旧关系（差集同步）。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<Boolean> updateAssociation(
            @PathVariable("modelId") Long modelId,
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestBody List<Long> categoryIds) {
        return success(relationService.updateAssociation(modelId, categoryIds, entityTypeCode) != null);
    }

    // ==================== 多模型批量操作 ====================

    @PostMapping("/category/{categoryId}/models")
    @Operation(
        summary = "批量关联多个模型到单个分类",
        description = "适用场景：分类侧批量挂载多个模型。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。\n" +
                "前端实现建议：通常需要“批量关联”按钮或导入任务触发；不建议普通逐条交互调用。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<BatchModelCategoryAssociationRespVO> batchAssociateModelsToCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestBody List<Long> modelIds) {
        return success(relationService.batchAssociateModelsToCategory(modelIds, categoryId, entityTypeCode));
    }

    @DeleteMapping("/category/{categoryId}/models")
    @Operation(
        summary = "批量解除多个模型与单个分类关联",
        description = "适用场景：分类侧批量移除多个模型。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。\n" +
                "前端实现建议：通常需要“批量解除关联”按钮或导入任务触发；不建议普通逐条交互调用。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<BatchModelCategoryAssociationRespVO> batchDisassociateModelsFromCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestBody List<Long> modelIds) {
        return success(relationService.batchDisassociateModelsFromCategory(modelIds, categoryId, entityTypeCode));
    }

    @PostMapping("/batch/associate")
    @Operation(
        summary = "批量关联多个模型到多个分类",
        description = "适用场景：批量建立多模型-多分类关系。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。\n" +
                "前端实现建议：通常用于后台批处理、导入任务；常规页面交互一般不直接开放。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<BatchModelCategoryAssociationRespVO> batchAssociateModelsToCategories(
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("modelIds") List<Long> modelIds,
            @RequestBody List<Long> categoryIds) {
        return success(relationService.batchAssociateModelsToCategories(modelIds, categoryIds, entityTypeCode));
    }

    @PostMapping("/batch/disassociate")
    @Operation(
        summary = "批量解除多个模型与多个分类关联",
        description = "适用场景：批量拆除多模型-多分类关系。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。\n" +
                "前端实现建议：通常用于后台批处理、导入回滚或专用批量按钮；常规页面交互一般不直接开放。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<BatchModelCategoryAssociationRespVO> batchDisassociateModelsFromCategories(
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("modelIds") List<Long> modelIds,
            @RequestBody List<Long> categoryIds) {
        return success(relationService.batchDisassociateModelsFromCategories(modelIds, categoryIds, entityTypeCode));
    }

    @PutMapping("/batch/update")
    @Operation(
        summary = "批量更新多个模型与分类关联",
        description = "适用场景：批量以目标分类集合覆盖多个模型的现有关联。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。\n" +
                "前端实现建议：建议仅在管理端批量工具或导入流程中使用。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<BatchModelCategoryAssociationRespVO> batchUpdateAssociation(
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("modelIds") List<Long> modelIds,
            @RequestBody List<Long> categoryIds) {
        return success(relationService.batchUpdateAssociation(modelIds, categoryIds, entityTypeCode));
    }

    // ==================== 查询操作（列表） ====================

    @GetMapping("/model/{modelId}/category-ids")
    @Operation(
        summary = "获取模型关联的分类ID列表",
        description = "适用场景：模型详情页回显分类绑定。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。"
    )
    @Parameter(name = "modelId", description = "模型ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<List<Long>> getModelCategoryIds(@PathVariable("modelId") Long modelId,
                                                            @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(relationService.listCategoryIdsByModelId(modelId, entityTypeCode));
    }

    @GetMapping("/category/{categoryId}/model-ids")
    @Operation(
        summary = "获取分类关联的模型ID列表",
        description = "适用场景：分类侧获取当前分类直接关联的模型ID（不含子树）。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。\n" +
                "说明：仅返回模型ID；如需模型详情，请调用业务模型聚合接口。"
    )
    @Parameter(name = "categoryId", description = "分类ID", required = true, example = "64")
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<List<Long>> getCategoryModelIds(@PathVariable("categoryId") Long categoryId,
                                                            @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(relationService.listModelIdsByCategoryIdOnly(categoryId, entityTypeCode));
    }

    @GetMapping("/category/{categoryId}/model-ids-with-descendants")
    @Operation(
        summary = "获取分类（含子树）关联的模型ID列表",
        description = "适用场景：分类树点击后，需要包含子分类范围查询模型。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<List<Long>> listModelIdsByCategoryIdWithDescendants(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(relationService.listModelIdsByCategoryIdWithDescendants(categoryId, entityTypeCode));
    }

    @GetMapping("/categories/model-ids-only")
    @Operation(
        summary = "获取多分类（不含子树）关联的模型ID列表",
        description = "适用场景：多分类筛选，且每个分类仅取自身节点。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<List<Long>> listModelIdsByCategoryIdsOnly(
            @RequestParam("categoryIds") List<Long> categoryIds,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(relationService.listModelIdsByCategoryIdsOnly(categoryIds, entityTypeCode));
    }

    @GetMapping("/categories/model-ids-with-descendants")
    @Operation(
        summary = "获取多分类（含子树）关联的模型ID列表",
        description = "适用场景：多分类筛选，并扩展每个分类的子树范围。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<List<Long>> listModelIdsByCategoryIdsWithDescendants(
            @RequestParam("categoryIds") List<Long> categoryIds,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(relationService.listModelIdsByCategoryIdsWithDescendants(categoryIds, entityTypeCode));
    }

    // ==================== 查询操作（分页） ====================

    @GetMapping("/category/{categoryId}/page-model-ids-only")
    @Operation(
        summary = "分页获取单分类（不含子树）关联的模型ID",
        description = "适用场景：单分类模型列表分页展示。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<PageResult<Long>> pageModelIdsByCategoryIdOnly(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("pageSize") Integer pageSize) {
        return success(relationService.pageModelIdsByCategoryIdOnly(categoryId, entityTypeCode, pageNo, pageSize));
    }

    @GetMapping("/category/{categoryId}/page-model-ids-with-descendants")
    @Operation(
        summary = "分页获取单分类（含子树）关联的模型ID",
        description = "适用场景：单分类（含子树）模型列表分页展示。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<PageResult<Long>> pageModelIdsByCategoryIdWithDescendants(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("pageSize") Integer pageSize) {
        return success(relationService.pageModelIdsByCategoryIdWithDescendants(categoryId, entityTypeCode, pageNo, pageSize));
    }

    @GetMapping("/categories/page-model-ids-only")
    @Operation(
        summary = "分页获取多分类（不含子树）关联的模型ID",
        description = "适用场景：多分类组合条件下分页查询模型ID。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<PageResult<Long>> pageModelIdsByCategoryIdsOnly(
            @RequestParam("categoryIds") List<Long> categoryIds,
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("pageSize") Integer pageSize) {
        return success(relationService.pageModelIdsByCategoryIdsOnly(categoryIds, entityTypeCode, pageNo, pageSize));
    }

    @GetMapping("/categories/page-model-ids-with-descendants")
    @Operation(
        summary = "分页获取多分类（含子树）关联的模型ID",
        description = "适用场景：多分类（含子树）组合条件下分页查询模型ID。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<PageResult<Long>> pageModelIdsByCategoryIdsWithDescendants(
            @RequestParam("categoryIds") List<Long> categoryIds,
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("pageSize") Integer pageSize) {
        return success(relationService.pageModelIdsByCategoryIdsWithDescendants(categoryIds, entityTypeCode, pageNo, pageSize));
    }

    @GetMapping("/categories/page-model-ids-only-db")
    @Operation(
        summary = "DB前置分页获取多分类（不含子树）关联的模型ID",
        description = "适用场景：数据量较大时，使用DB前置分页减少内存分页开销。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<PageResult<Long>> pageModelIdsByCategoryIdsOnlyDb(
            @RequestParam("categoryIds") List<Long> categoryIds,
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("pageSize") Integer pageSize) {
        return success(relationService.pageModelIdsByCategoryIdsOnlyDb(categoryIds, entityTypeCode, pageNo, pageSize));
    }

    @GetMapping("/categories/page-model-ids-with-descendants-db")
    @Operation(
        summary = "DB前置分页获取多分类（含子树）关联的模型ID",
        description = "适用场景：多分类含子树且数据量较大，走DB前置分页。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<PageResult<Long>> pageModelIdsByCategoryIdsWithDescendantsDb(
            @RequestParam("categoryIds") List<Long> categoryIds,
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("pageSize") Integer pageSize) {
        return success(relationService.pageModelIdsByCategoryIdsWithDescendantsDb(categoryIds, entityTypeCode, pageNo, pageSize));
    }

    // ==================== 级联清理操作 ====================

    @DeleteMapping("/cleanup/model/{modelId}")
    @Operation(
        summary = "解除模型的所有分类关联",
        description = "适用场景：删除模型前或重建关系前做级联清理。\n" +
                "业务范围：全业务（不传 entityTypeCode）。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<Boolean> deleteAllByModelId(@PathVariable("modelId") Long modelId) {
        relationService.deleteAllByModelId(modelId);
        return success(true);
    }

    @DeleteMapping("/cleanup/models")
    @Operation(
        summary = "批量解除多个模型的所有分类关联",
        description = "适用场景：批量删除模型或批量重置关系前清理。\n" +
                "业务范围：全业务（不传 entityTypeCode）。\n" +
                "前端实现建议：高风险操作，建议仅在专用批量按钮并二次确认后触发，或由任务系统执行。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<Boolean> deleteAllByModelIds(@RequestParam("modelIds") List<Long> modelIds) {
        relationService.deleteAllByModelIds(modelIds);
        return success(true);
    }

    @DeleteMapping("/cleanup/category/{categoryId}")
    @Operation(
        summary = "解除分类的所有模型关联（全业务）",
        description = "适用场景：删除分类节点时的全量级联清理。\n" +
                "业务范围：全业务（不传 entityTypeCode）。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<Boolean> deleteAllByCategoryId(@PathVariable("categoryId") Long categoryId) {
        relationService.deleteAllByCategoryId(categoryId);
        return success(true);
    }

    @DeleteMapping("/cleanup/categories")
    @Operation(
        summary = "批量解除多个分类的所有模型关联（全业务）",
        description = "适用场景：批量删除分类树时的全量级联清理。\n" +
                "业务范围：全业务（不传 entityTypeCode）。\n" +
                "前端实现建议：高风险操作，建议仅在专用批量按钮并二次确认后触发，或由任务系统执行。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<Boolean> deleteAllByCategoryIds(@RequestParam("categoryIds") List<Long> categoryIds) {
        relationService.deleteAllByCategoryIds(categoryIds);
        return success(true);
    }

    @DeleteMapping("/cleanup/category/{categoryId}/in-business")
    @Operation(
        summary = "解除分类在指定业务下的所有模型关联",
        description = "适用场景：仅在某个业务域内重置分类与模型关系。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<Boolean> deleteAllByCategoryIdInBusiness(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        relationService.deleteAllByCategoryIdInBusiness(categoryId, entityTypeCode);
        return success(true);
    }

    @DeleteMapping("/cleanup/categories/in-business")
    @Operation(
        summary = "批量解除多个分类在指定业务下的所有模型关联",
        description = "适用场景：仅在某个业务域内批量重置分类与模型关系。\n" +
                "业务范围：指定业务（必须传 entityTypeCode）。\n" +
                "前端实现建议：建议通过批量按钮或导入任务触发，不建议普通逐条交互。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<Boolean> deleteAllByCategoryIdsInBusiness(
            @RequestParam("categoryIds") List<Long> categoryIds,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        relationService.deleteAllByCategoryIdsInBusiness(categoryIds, entityTypeCode);
        return success(true);
    }
}
