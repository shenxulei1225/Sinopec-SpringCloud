package cn.cheers.x.system.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理员直接设新口令。要求这个人已经有口令摘要。
 * 不核对旧密码。不新建用户目录记录。
 */
@Schema(description = "管理后台 - 重置口令")
@Data
public class LoginCredentialReplaceReqVO {

    @Schema(description = "用户目录里这条记录的编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;
}
