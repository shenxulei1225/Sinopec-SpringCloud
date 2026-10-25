package cn.cheers.x.system.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新建记录保存成功后，认证按这个编号写下口令摘要。
 * 不接收昵称、手机、部门。
 */
@Schema(description = "管理后台 - 保存口令")
@Data
public class LoginCredentialSaveReqVO {

    @Schema(description = "用户目录里这条记录的编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;
}
