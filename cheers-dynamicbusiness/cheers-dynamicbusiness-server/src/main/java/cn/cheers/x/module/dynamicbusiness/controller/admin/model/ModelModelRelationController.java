package cn.cheers.x.module.dynamicbusiness.controller.admin.model;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelModelAssociationRespVO;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelModelRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 模型—模型关联。
 *
 * <p>同类型和异类型都在同一张表，用来源类型、目标类型区分。本接口不写实体，不改上级。</p>
 */
@Tag(name = "管理后台 - 模型—模型关联 API")
@RestController
@RequestMapping("/dynamicbusiness/model-model-relation")
@Validated
public class ModelModelRelationController {

    @Resource
    private ModelModelRelationService relationService;

    @PostMapping("/associate")
    @Operation(summary = "挂上一条模型—模型关联")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<ModelModelAssociationRespVO> associate(
            @RequestParam("sourceModelId") @NotNull Long sourceModelId,
            @RequestParam("targetModelId") @NotNull Long targetModelId,
            @RequestParam("sourceEntityTypeCode") @NotBlank String sourceEntityTypeCode,
            @RequestParam("targetEntityTypeCode") @NotBlank String targetEntityTypeCode) {
        return success(relationService.associate(
                sourceModelId, targetModelId, sourceEntityTypeCode, targetEntityTypeCode));
    }

    @DeleteMapping("/disassociate")
    @Operation(summary = "卸下一条模型—模型关联")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<ModelModelAssociationRespVO> disassociate(
            @RequestParam("sourceModelId") @NotNull Long sourceModelId,
            @RequestParam("targetModelId") @NotNull Long targetModelId,
            @RequestParam("sourceEntityTypeCode") @NotBlank String sourceEntityTypeCode,
            @RequestParam("targetEntityTypeCode") @NotBlank String targetEntityTypeCode) {
        return success(relationService.disassociate(
                sourceModelId, targetModelId, sourceEntityTypeCode, targetEntityTypeCode));
    }

    @GetMapping("/target-ids")
    @Operation(summary = "按来源型号列出目标型号")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<Long>> listTargetIds(
            @RequestParam("sourceModelId") @NotNull Long sourceModelId,
            @RequestParam("sourceEntityTypeCode") @NotBlank String sourceEntityTypeCode,
            @RequestParam("targetEntityTypeCode") @NotBlank String targetEntityTypeCode) {
        return success(relationService.listTargetModelIds(
                sourceModelId, sourceEntityTypeCode, targetEntityTypeCode));
    }

    @GetMapping("/source-ids")
    @Operation(summary = "按目标型号反查来源型号")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<Long>> listSourceIds(
            @RequestParam("targetModelId") @NotNull Long targetModelId,
            @RequestParam("targetEntityTypeCode") @NotBlank String targetEntityTypeCode,
            @RequestParam("sourceEntityTypeCode") @NotBlank String sourceEntityTypeCode) {
        return success(relationService.listSourceModelIds(
                targetModelId, targetEntityTypeCode, sourceEntityTypeCode));
    }
}
