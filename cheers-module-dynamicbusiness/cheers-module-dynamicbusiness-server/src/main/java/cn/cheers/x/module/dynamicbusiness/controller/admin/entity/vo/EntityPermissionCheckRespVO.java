package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * 实体权限检查结果 VO
 */
@Schema(description = "管理后台 - 实体权限检查结果 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityPermissionCheckRespVO {

    @Schema(description = "是否可访问", example = "true")
    private Boolean canAccess;

    @Schema(description = "是否可创建", example = "true")
    private Boolean canCreate;

    @Schema(description = "是否可更新", example = "true")
    private Boolean canUpdate;

    @Schema(description = "是否可删除", example = "true")
    private Boolean canDelete;

    @Schema(description = "可查看的字段ID列表")
    private Set<Long> viewableFieldIds;

    @Schema(description = "可编辑的字段ID列表")
    private Set<Long> editableFieldIds;

    @Schema(description = "可访问的分类ID列表")
    private Set<Long> accessibleCategoryIds;

    @Schema(description = "可管理的分类ID列表")
    private Set<Long> manageableCategoryIds;
}
