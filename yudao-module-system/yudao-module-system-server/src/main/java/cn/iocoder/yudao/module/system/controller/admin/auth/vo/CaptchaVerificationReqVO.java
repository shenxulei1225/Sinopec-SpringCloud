package cn.iocoder.yudao.module.system.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 验证码 Request VO")
@Data
public class CaptchaVerificationReqVO {

    // ========== 算术验证码相关 ==========
    @Schema(description = "验证码校验串，格式为 captchaKey@captchaCode", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "4f1d8f66-67cf-4df4-b2b3-d74c4f3724f0@8")
    @NotEmpty(message = "验证码不能为空", groups = CodeEnableGroup.class)
    private String captchaVerification;

    /**
     * 开启验证码的 Group
     */
    public interface CodeEnableGroup {
    }
}
