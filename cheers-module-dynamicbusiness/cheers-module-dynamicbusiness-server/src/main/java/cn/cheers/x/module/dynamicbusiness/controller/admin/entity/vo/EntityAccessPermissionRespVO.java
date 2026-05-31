package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实体访问权限响应 VO
 */
@Schema(description = "管理后台 - 实体访问权限 Response VO")
@Data
public class EntityAccessPermissionRespVO {

    @Schema(description = "权限ID", example = "1")
    private Long id;

    @Schema(description = "角色ID", example = "1")
    private Long roleId;

    @Schema(description = "角色名称", example = "管理员")
    private String roleName;

    @Schema(description = "实体ID", example = "1")
    private Long entityId;

    @Schema(description = "实体名称", example = "设备A")
    private String entityName;

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    private String businessTypeCode;

    @Schema(description = "是否可查看", example = "true")
    private Boolean canView;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
