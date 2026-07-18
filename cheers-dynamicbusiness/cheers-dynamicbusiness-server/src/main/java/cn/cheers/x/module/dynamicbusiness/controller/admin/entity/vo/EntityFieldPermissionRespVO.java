package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字段级权限响应 VO
 */
@Schema(description = "管理后台 - 字段级权限 Response VO")
@Data
public class EntityFieldPermissionRespVO {

    @Schema(description = "权限ID", example = "1")
    private Long id;

    @Schema(description = "角色ID", example = "1")
    private Long roleId;

    @Schema(description = "角色名称", example = "管理员")
    private String roleName;

    @Schema(description = "模型ID", example = "1")
    private Long modelId;

    @Schema(description = "模型名称", example = "设备模型")
    private String modelName;

    @Schema(description = "字段ID", example = "1")
    private Long fieldId;

    @Schema(description = "字段名称", example = "设备名称")
    private String fieldName;

    @Schema(description = "是否可查看", example = "true")
    private Boolean canView;

    @Schema(description = "是否可编辑", example = "false")
    private Boolean canEdit;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
