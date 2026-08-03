package cn.cheers.x.module.dynamicbusiness.controller.admin.category;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryCategoryAssociationRespVO;
import cn.cheers.x.module.dynamicbusiness.service.category.relation.CategoryCategoryRelationService;
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
 * 分类—分类跨种类关联 API（同种类层级请用 parent_id；租户物理表 *_t{tenantId}）。
 */
@Tag(name = "管理后台 - 分类—分类关联 API")
@RestController
@RequestMapping("/dynamicbusiness/category-category-relation")
@Validated
public class CategoryCategoryRelationController {

    @Resource
    private CategoryCategoryRelationService relationService;

    @PostMapping("/associate")
    @Operation(summary = "关联单个成员分类到单个宿主分类（跨种类）")
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<CategoryCategoryAssociationRespVO> associate(
            @RequestParam("hostCategoryId") @NotNull Long hostCategoryId,
            @RequestParam("memberCategoryId") @NotNull Long memberCategoryId,
            @RequestParam("hostCategoryTypeCode") @NotBlank String hostCategoryTypeCode,
            @RequestParam("memberCategoryTypeCode") @NotBlank String memberCategoryTypeCode) {
        return success(relationService.associate(
                hostCategoryId, memberCategoryId, hostCategoryTypeCode, memberCategoryTypeCode));
    }

    @DeleteMapping("/disassociate")
    @Operation(summary = "取消成员与宿主的关联（默认含成员子孙上挂靠的边）")
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<CategoryCategoryAssociationRespVO> disassociate(
            @RequestParam("hostCategoryId") @NotNull Long hostCategoryId,
            @RequestParam("memberCategoryId") @NotNull Long memberCategoryId,
            @RequestParam("hostCategoryTypeCode") @NotBlank String hostCategoryTypeCode,
            @RequestParam("memberCategoryTypeCode") @NotBlank String memberCategoryTypeCode,
            @RequestParam(value = "includeDescendantMembers", defaultValue = "true")
            boolean includeDescendantMembers) {
        return success(relationService.disassociate(
                hostCategoryId, memberCategoryId, hostCategoryTypeCode, memberCategoryTypeCode,
                includeDescendantMembers));
    }

    @GetMapping("/exists")
    @Operation(summary = "检查分类—分类关联是否存在")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<Boolean> exists(
            @RequestParam("hostCategoryId") @NotNull Long hostCategoryId,
            @RequestParam("memberCategoryId") @NotNull Long memberCategoryId,
            @RequestParam("hostCategoryTypeCode") @NotBlank String hostCategoryTypeCode,
            @RequestParam("memberCategoryTypeCode") @NotBlank String memberCategoryTypeCode) {
        return success(relationService.existsRelation(
                hostCategoryId, memberCategoryId, hostCategoryTypeCode, memberCategoryTypeCode));
    }

    @PostMapping("/host/{hostCategoryId}/members")
    @Operation(summary = "批量关联多个成员分类到单个宿主")
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<CategoryCategoryAssociationRespVO> batchAssociate(
            @PathVariable("hostCategoryId") Long hostCategoryId,
            @RequestParam("hostCategoryTypeCode") @NotBlank String hostCategoryTypeCode,
            @RequestParam("memberCategoryTypeCode") @NotBlank String memberCategoryTypeCode,
            @RequestBody List<Long> memberCategoryIds) {
        return success(relationService.batchAssociateMembersToHost(
                hostCategoryId, memberCategoryIds, hostCategoryTypeCode, memberCategoryTypeCode));
    }

    @DeleteMapping("/host/{hostCategoryId}/members")
    @Operation(summary = "批量取消多个成员分类与单个宿主的关联")
    @PreAuthorize("@ss.hasPermission('system:category:update')")
    public CommonResult<CategoryCategoryAssociationRespVO> batchDisassociate(
            @PathVariable("hostCategoryId") Long hostCategoryId,
            @RequestParam("hostCategoryTypeCode") @NotBlank String hostCategoryTypeCode,
            @RequestParam("memberCategoryTypeCode") @NotBlank String memberCategoryTypeCode,
            @RequestBody List<Long> memberCategoryIds) {
        return success(relationService.batchDisassociateMembersFromHost(
                hostCategoryId, memberCategoryIds, hostCategoryTypeCode, memberCategoryTypeCode));
    }

    @GetMapping("/host/{hostCategoryId}/member-ids")
    @Operation(summary = "按宿主列出挂靠成员分类 ID（默认含子孙宿主上的挂靠汇总）")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<Long>> listMemberIdsByHost(
            @PathVariable("hostCategoryId") Long hostCategoryId,
            @RequestParam("hostCategoryTypeCode") @NotBlank String hostCategoryTypeCode,
            @RequestParam("memberCategoryTypeCode") @NotBlank String memberCategoryTypeCode,
            @RequestParam(value = "includeDescendantHosts", defaultValue = "true")
            boolean includeDescendantHosts) {
        return success(relationService.listMemberCategoryIdsByHost(
                hostCategoryId, hostCategoryTypeCode, memberCategoryTypeCode, includeDescendantHosts));
    }

    @GetMapping("/member/{memberCategoryId}/host-ids")
    @Operation(summary = "按成员列出挂靠宿主分类 ID")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<Long>> listHostIdsByMember(
            @PathVariable("memberCategoryId") Long memberCategoryId,
            @RequestParam("memberCategoryTypeCode") @NotBlank String memberCategoryTypeCode,
            @RequestParam(value = "hostCategoryTypeCode", required = false) String hostCategoryTypeCode) {
        return success(relationService.listHostCategoryIdsByMember(
                memberCategoryId, memberCategoryTypeCode, hostCategoryTypeCode));
    }
}
