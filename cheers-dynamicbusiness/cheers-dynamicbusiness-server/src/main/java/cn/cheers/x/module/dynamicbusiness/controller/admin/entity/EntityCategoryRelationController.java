package cn.cheers.x.module.dynamicbusiness.controller.admin.entity;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogEntryAccessService;
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

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 实体-分类关联 API Controller
 * 
 * <p>提供 Entity-Category 多对多关联的所有操作接口。</p>
 * <p>权限：把实体挂到分类、从分类取下，算改这条实体，要本页数据目录（registryCode）的实体修改授权；
 * 查看要实体查看授权。实体必须属于本页目录。分类树本身是公共的筛选视角，不按目录核对。</p>
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
    /** 实体是否属于本页数据目录。 */
    @Resource
    private CatalogEntryAccessService catalogEntryAccessService;

    // ==================== 单个操作（按 entityId + categoryId） ====================

    @PostMapping("/associate")
    @Operation(summary = "关联单个实体到单个分类")
    @PreAuthorize("@catalogAccess.allow(#registryCode, #entityTypeCode, 'update')")
    public CommonResult<EntityCategoryAssociationRespVO> associateByEntityIdAndCategoryId(
            @RequestParam(value = "registryCode", required = false) String registryCode,
            @RequestParam("entityId") Long entityId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        catalogEntryAccessService.assertRecordsInTarget(registryCode, entityTypeCode, List.of(entityId));
        return success(relationService.associate(entityId, categoryId, entityTypeCode));
    }

    @DeleteMapping("/disassociate")
    @Operation(summary = "取消单个实体与单个分类的关联")
    @PreAuthorize("@catalogAccess.allow(#registryCode, #entityTypeCode, 'update')")
    public CommonResult<EntityCategoryAssociationRespVO> disassociateByEntityIdAndCategoryId(
            @RequestParam(value = "registryCode", required = false) String registryCode,
            @RequestParam("entityId") Long entityId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        catalogEntryAccessService.assertRecordsInTarget(registryCode, entityTypeCode, List.of(entityId));
        return success(relationService.disassociate(entityId, categoryId, entityTypeCode));
    }

    @GetMapping("/exists")
    @Operation(summary = "检查实体与分类关联是否存在")
    @PreAuthorize("@catalogAccess.allow(#registryCode, #entityTypeCode, 'query')")
    public CommonResult<Boolean> existsByEntityIdAndCategoryId(
            @RequestParam(value = "registryCode", required = false) String registryCode,
            @RequestParam("entityId") Long entityId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        catalogEntryAccessService.assertRecordsInTarget(registryCode, entityTypeCode, List.of(entityId));
        return success(relationService.existsRelation(entityId, categoryId, entityTypeCode));
    }

    @GetMapping("/entity/{entityId}/category-ids")
    @Operation(summary = "获取实体关联的分类ID列表")
    @Parameter(name = "entityId", description = "实体ID", required = true, example = "1001")
    @PreAuthorize("@catalogAccess.allow(#registryCode, #entityTypeCode, 'query')")
    public CommonResult<List<Long>> listCategoryIdsByEntityId(@PathVariable("entityId") Long entityId,
                                                              @RequestParam(value = "registryCode", required = false) String registryCode,
                                                              @RequestParam("entityTypeCode") String entityTypeCode) {
        catalogEntryAccessService.assertRecordsInTarget(registryCode, entityTypeCode, List.of(entityId));
        return success(relationService.listCategoryIdsByEntityId(entityId, entityTypeCode));
    }

    // ==================== 单个操作（按 categoryId） ====================

    @GetMapping("/category/{categoryId}/entity-ids")
    @Operation(summary = "获取分类关联的实体ID列表")
    @Parameter(name = "categoryId", description = "分类ID", required = true, example = "64")
    @PreAuthorize("@catalogAccess.allow(#registryCode, #entityTypeCode, 'query')")
    public CommonResult<List<Long>> listEntityIdsByCategoryId(@PathVariable("categoryId") Long categoryId,
                                                              @RequestParam(value = "registryCode", required = false) String registryCode,
                                                              @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(catalogEntryAccessService.retainRecordsInTarget(registryCode, entityTypeCode,
                relationService.listEntityIdsByCategoryIdOnly(categoryId, entityTypeCode)));
    }


    // ==================== 批量操作（按 categoryId + entityIds） ====================

    @PostMapping("/category/{categoryId}/entities")
    @Operation(summary = "批量关联多个实体到单个分类")
    @PreAuthorize("@catalogAccess.allow(#registryCode, #entityTypeCode, 'update')")
    public CommonResult<BatchEntityCategoryAssociationRespVO> batchAssociateByCategoryIdAndEntityIds(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam(value = "registryCode", required = false) String registryCode,
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestBody List<Long> entityIds) {
        catalogEntryAccessService.assertRecordsInTarget(registryCode, entityTypeCode, entityIds);
        return success(relationService.batchAssociateEntitiesToCategory(entityIds, categoryId, entityTypeCode));
    }

    @DeleteMapping("/category/{categoryId}/entities")
    @Operation(summary = "批量取消多个实体与单个分类的关联")
    @PreAuthorize("@catalogAccess.allow(#registryCode, #entityTypeCode, 'update')")
    public CommonResult<BatchEntityCategoryAssociationRespVO> batchDisassociateByCategoryIdAndEntityIds(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam(value = "registryCode", required = false) String registryCode,
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestBody List<Long> entityIds) {
        catalogEntryAccessService.assertRecordsInTarget(registryCode, entityTypeCode, entityIds);
        return success(relationService.batchDisassociateEntitiesFromCategory(entityIds, categoryId, entityTypeCode));
    }

    // ==================== 批量操作（按 entityIds + categoryIds） ====================

    @PostMapping("/batch/associate")
    @Operation(summary = "批量关联多个实体到多个分类")
    @PreAuthorize("@catalogAccess.allow(#reqVO.registryCode, #reqVO.entityTypeCode, 'update')")
    public CommonResult<BatchEntityCategoryAssociationRespVO> batchAssociateByEntityIdsAndCategoryIds(
            @RequestBody @Valid BatchEntityCategoryAssociationReqVO reqVO) {
        catalogEntryAccessService.assertRecordsInTarget(reqVO.getRegistryCode(), reqVO.getEntityTypeCode(), reqVO.getEntityIds());
        return success(relationService.batchAssociateEntitiesToCategories(
                reqVO.getEntityIds(), reqVO.getCategoryIds(), reqVO.getEntityTypeCode()));
    }

    @PostMapping("/batch/disassociate")
    @Operation(summary = "批量取消多个实体与多个分类的关联")
    @PreAuthorize("@catalogAccess.allow(#reqVO.registryCode, #reqVO.entityTypeCode, 'update')")
    public CommonResult<BatchEntityCategoryAssociationRespVO> batchDisassociateByEntityIdsAndCategoryIds(
            @RequestBody @Valid BatchEntityCategoryAssociationReqVO reqVO) {
        catalogEntryAccessService.assertRecordsInTarget(reqVO.getRegistryCode(), reqVO.getEntityTypeCode(), reqVO.getEntityIds());
        return success(relationService.batchDisassociateEntitiesFromCategories(
                reqVO.getEntityIds(), reqVO.getCategoryIds(), reqVO.getEntityTypeCode()));
    }

    @PostMapping("/batch/replace")
    @Operation(summary = "批量替换多个实体的分类关联")
    @PreAuthorize("@catalogAccess.allow(#reqVO.registryCode, #reqVO.entityTypeCode, 'update')")
    public CommonResult<BatchEntityCategoryAssociationRespVO> batchUpdateByEntityIdsAndCategoryIds(
            @RequestBody @Valid BatchEntityCategoryAssociationReqVO reqVO) {
        catalogEntryAccessService.assertRecordsInTarget(reqVO.getRegistryCode(), reqVO.getEntityTypeCode(), reqVO.getEntityIds());
        return success(relationService.batchUpdateAssociation(
                reqVO.getEntityIds(), reqVO.getCategoryIds(), reqVO.getEntityTypeCode()));
    }

    
}
