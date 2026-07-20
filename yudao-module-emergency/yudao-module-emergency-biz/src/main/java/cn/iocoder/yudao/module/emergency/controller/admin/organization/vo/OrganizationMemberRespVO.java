package cn.iocoder.yudao.module.emergency.controller.admin.organization.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "管理后台 - 组织成员 Response VO")
@Data
public class OrganizationMemberRespVO {

    @Schema(description = "成员ID", example = "1")
    private Long id;

    @Schema(description = "组织ID", example = "1")
    private Long orgId;

    @Schema(description = "用户ID", example = "100")
    private Long userId;

    @Schema(description = "角色", example = "director")
    private String role;

    @Schema(description = "联系方式（JSON格式）", example = "{\"phone\":\"13800138000\",\"email\":\"test@example.com\"}")
    private Map<String, Object> contactInfo;

    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}



