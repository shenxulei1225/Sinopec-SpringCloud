package cn.cheers.x.module.dynamicbusiness.controller.admin.model;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelEntityAssociationRespVO;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelEntityRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 型号—实体多对多关联 API（跨类型挂靠；租户物理表 *_t{tenantId}）。
 */
@Tag(name = "管理后台 - 型号—实体关联 API")
@RestController
@RequestMapping("/dynamicbusiness/model-entity-relation")
@Validated
public class ModelEntityRelationController {

    @Resource
    private ModelEntityRelationService relationService;

    @PostMapping("/associate")
    @Operation(summary = "关联单个实体到单个型号（跨类型）")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<ModelEntityAssociationRespVO> associate(
            @RequestParam("modelId") @NotNull Long modelId,
            @RequestParam("entityId") @NotNull Long entityId,
            @RequestParam("modelEntityTypeCode") @NotBlank String modelEntityTypeCode,
            @RequestParam("entityTypeCode") @NotBlank String entityTypeCode) {
        return success(relationService.associate(modelId, entityId, modelEntityTypeCode, entityTypeCode));
    }

    @DeleteMapping("/disassociate")
    @Operation(summary = "取消单个实体与单个型号的关联")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<ModelEntityAssociationRespVO> disassociate(
            @RequestParam("modelId") @NotNull Long modelId,
            @RequestParam("entityId") @NotNull Long entityId,
            @RequestParam("modelEntityTypeCode") @NotBlank String modelEntityTypeCode,
            @RequestParam("entityTypeCode") @NotBlank String entityTypeCode) {
        return success(relationService.disassociate(modelId, entityId, modelEntityTypeCode, entityTypeCode));
    }

    @GetMapping("/exists")
    @Operation(summary = "检查型号—实体关联是否存在")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<Boolean> exists(
            @RequestParam("modelId") @NotNull Long modelId,
            @RequestParam("entityId") @NotNull Long entityId,
            @RequestParam("modelEntityTypeCode") @NotBlank String modelEntityTypeCode,
            @RequestParam("entityTypeCode") @NotBlank String entityTypeCode) {
        return success(relationService.existsRelation(modelId, entityId, modelEntityTypeCode, entityTypeCode));
    }

    @PostMapping("/model/{modelId}/entities")
    @Operation(summary = "批量关联多个实体到单个型号")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<ModelEntityAssociationRespVO> batchAssociate(
            @PathVariable("modelId") Long modelId,
            @RequestParam("modelEntityTypeCode") @NotBlank String modelEntityTypeCode,
            @RequestParam("entityTypeCode") @NotBlank String entityTypeCode,
            @RequestBody List<Long> entityIds) {
        return success(relationService.batchAssociateEntitiesToModel(
                modelId, entityIds, modelEntityTypeCode, entityTypeCode));
    }

    @DeleteMapping("/model/{modelId}/entities")
    @Operation(summary = "批量取消多个实体与单个型号的关联")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<ModelEntityAssociationRespVO> batchDisassociate(
            @PathVariable("modelId") Long modelId,
            @RequestParam("modelEntityTypeCode") @NotBlank String modelEntityTypeCode,
            @RequestParam("entityTypeCode") @NotBlank String entityTypeCode,
            @RequestBody List<Long> entityIds) {
        return success(relationService.batchDisassociateEntitiesFromModel(
                modelId, entityIds, modelEntityTypeCode, entityTypeCode));
    }

    @GetMapping("/model/{modelId}/entity-ids")
    @Operation(summary = "按型号列出挂靠实体 ID")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<Long>> listEntityIdsByModel(
            @PathVariable("modelId") Long modelId,
            @RequestParam("modelEntityTypeCode") @NotBlank String modelEntityTypeCode,
            @RequestParam("entityTypeCode") @NotBlank String entityTypeCode) {
        return success(relationService.listEntityIdsByModelId(modelId, modelEntityTypeCode, entityTypeCode));
    }

    @GetMapping("/entity/{entityId}/model-ids")
    @Operation(summary = "按实体列出挂靠型号 ID")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<Long>> listModelIdsByEntity(
            @PathVariable("entityId") Long entityId,
            @RequestParam("entityTypeCode") @NotBlank String entityTypeCode,
            @RequestParam(value = "modelEntityTypeCode", required = false) String modelEntityTypeCode) {
        return success(relationService.listModelIdsByEntityId(entityId, entityTypeCode, modelEntityTypeCode));
    }
}
