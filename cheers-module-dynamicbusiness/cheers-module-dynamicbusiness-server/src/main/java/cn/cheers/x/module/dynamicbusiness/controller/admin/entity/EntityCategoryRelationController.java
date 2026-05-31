package cn.cheers.x.module.dynamicbusiness.controller.admin.entity;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.BatchEntityCategoryAssociationReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.BatchEntityCategoryAssociationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCategoryAssociationRespVO;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 实体-分类关联 API Controller
 * 
 * <p>提供 Entity-Category 多对多关联的所有操作接口。</p>
 * 
 * @author 基础服务模块
 */
@Tag(name = "管理后台 - 实体-分类关联 API")
@RestController
@RequestMapping("/dynamicbusiness/entity-category-relation")
@Validated
public class EntityCategoryRelationController {

    @Resource
    private EntityCategoryRelationService relationService;

    // ==================== 单个操作（按 entityId + categoryId） ====================

    @PostMapping("/associate")
    @Operation(summary = "关联单个实体到单个分类")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<EntityCategoryAssociationRespVO> associateByEntityIdAndCategoryId(
            @RequestParam("entityId") Long entityId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("businessTypeCode") String businessTypeCode) {
        return success(relationService.associate(entityId, categoryId, businessTypeCode));
    }

    @DeleteMapping("/disassociate")
    @Operation(summary = "取消单个实体与单个分类的关联")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<EntityCategoryAssociationRespVO> disassociateByEntityIdAndCategoryId(
            @RequestParam("entityId") Long entityId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("businessTypeCode") String businessTypeCode) {
        return success(relationService.disassociate(entityId, categoryId, businessTypeCode));
    }

    @GetMapping("/exists")
    @Operation(summary = "检查实体与分类关联是否存在")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<Boolean> existsByEntityIdAndCategoryId(
            @RequestParam("entityId") Long entityId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("businessTypeCode") String businessTypeCode) {
        return success(relationService.existsRelation(entityId, categoryId, businessTypeCode));
    }

    @GetMapping("/entity/{entityId}/category-ids")
    @Operation(summary = "获取实体关联的分类ID列表")
    @Parameter(name = "entityId", description = "实体ID", required = true, example = "1001")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<Long>> listCategoryIdsByEntityId(@PathVariable("entityId") Long entityId,
                                                              @RequestParam("businessTypeCode") String businessTypeCode) {
        return success(relationService.listCategoryIdsByEntityId(entityId, businessTypeCode));
    }

    // ==================== 单个操作（按 categoryId） ====================

    @GetMapping("/category/{categoryId}/entity-ids")
    @Operation(summary = "获取分类关联的实体ID列表")
    @Parameter(name = "categoryId", description = "分类ID", required = true, example = "64")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<Long>> listEntityIdsByCategoryId(@PathVariable("categoryId") Long categoryId,
                                                              @RequestParam("businessTypeCode") String businessTypeCode) {
        return success(relationService.listEntityIdsByCategoryIdOnly(categoryId, businessTypeCode));
    }


    // ==================== 批量操作（按 categoryId + entityIds） ====================

    @PostMapping("/category/{categoryId}/entities")
    @Operation(summary = "批量关联多个实体到单个分类")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<BatchEntityCategoryAssociationRespVO> batchAssociateByCategoryIdAndEntityIds(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam("businessTypeCode") String businessTypeCode,
            @RequestBody List<Long> entityIds) {
        return success(relationService.batchAssociateEntitiesToCategory(entityIds, categoryId, businessTypeCode));
    }

    @DeleteMapping("/category/{categoryId}/entities")
    @Operation(summary = "批量取消多个实体与单个分类的关联")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<BatchEntityCategoryAssociationRespVO> batchDisassociateByCategoryIdAndEntityIds(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam("businessTypeCode") String businessTypeCode,
            @RequestBody List<Long> entityIds) {
        return success(relationService.batchDisassociateEntitiesFromCategory(entityIds, categoryId, businessTypeCode));
    }

    // ==================== 批量操作（按 entityIds + categoryIds） ====================

    @PostMapping("/batch/associate")
    @Operation(summary = "批量关联多个实体到多个分类")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<BatchEntityCategoryAssociationRespVO> batchAssociateByEntityIdsAndCategoryIds(
            @RequestBody @Valid BatchEntityCategoryAssociationReqVO reqVO) {
        return success(relationService.batchAssociateEntitiesToCategories(
                reqVO.getEntityIds(), reqVO.getCategoryIds(), reqVO.getBusinessTypeCode()));
    }

    @PostMapping("/batch/disassociate")
    @Operation(summary = "批量取消多个实体与多个分类的关联")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<BatchEntityCategoryAssociationRespVO> batchDisassociateByEntityIdsAndCategoryIds(
            @RequestBody @Valid BatchEntityCategoryAssociationReqVO reqVO) {
        return success(relationService.batchDisassociateEntitiesFromCategories(
                reqVO.getEntityIds(), reqVO.getCategoryIds(), reqVO.getBusinessTypeCode()));
    }

    @PostMapping("/batch/replace")
    @Operation(summary = "批量替换多个实体的分类关联")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<BatchEntityCategoryAssociationRespVO> batchUpdateByEntityIdsAndCategoryIds(
            @RequestBody @Valid BatchEntityCategoryAssociationReqVO reqVO) {
        return success(relationService.batchUpdateAssociation(
                reqVO.getEntityIds(), reqVO.getCategoryIds(), reqVO.getBusinessTypeCode()));
    }

    
}
