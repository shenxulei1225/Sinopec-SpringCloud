package cn.iocoder.yudao.module.emergency.controller.admin.organization.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 组织成员创建 Request VO")
@Data
public class OrganizationMemberCreateReqVO {

    @Schema(description = "组织ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "角色", requiredMode = Schema.RequiredMode.REQUIRED, example = "director")
    @NotNull(message = "角色不能为空")
    private String role;

    @Schema(description = "联系方式（JSON格式）", example = "{\"phone\":\"13800138000\",\"email\":\"test@example.com\"}")
    private Map<String, Object> contactInfo;

    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled;
}



