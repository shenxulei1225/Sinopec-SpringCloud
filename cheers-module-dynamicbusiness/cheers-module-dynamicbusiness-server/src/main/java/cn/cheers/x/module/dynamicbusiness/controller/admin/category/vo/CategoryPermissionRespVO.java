package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类级权限响应 VO
 */
@Schema(description = "管理后台 - 分类级权限 Response VO")
@Data
public class CategoryPermissionRespVO {

    @Schema(description = "权限ID", example = "1")
    private Long id;

    @Schema(description = "角色ID", example = "1")
    private Long roleId;

    @Schema(description = "角色名称", example = "管理员")
    private String roleName;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "分类名称", example = "设备分类")
    private String categoryName;

    @Schema(description = "是否可查看", example = "true")
    private Boolean canView;

    @Schema(description = "是否可管理", example = "false")
    private Boolean canManage;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
