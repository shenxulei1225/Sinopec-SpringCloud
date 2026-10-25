package cn.cheers.x.module.dynamicbusiness.controller.admin.useraccount.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserAccountResetPasswordReqVO {

    @Schema(description = "用户目录里这条记录的编号")
    private Long id;

    @Schema(description = "新口令")
    private String password;
}
