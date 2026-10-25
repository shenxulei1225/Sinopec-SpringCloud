package cn.cheers.x.module.dynamicbusiness.controller.admin.useraccount.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 新建用户。口令只在这次请求里出现，不写入用户目录的字段。
 */
@Data
public class UserAccountCreateReqVO {

    @Schema(description = "账号", example = "jinqiao")
    private String account;

    @Schema(description = "昵称", example = "王磊")
    private String nickname;

    @Schema(description = "手机")
    private String mobile;

    @Schema(description = "状态。1 启用，0 停用")
    private Integer status;

    @Schema(description = "口令。保存后不再回显")
    private String password;

    @Schema(description = "所属场站。场站上的人必填")
    private Long facilityId;

    @Schema(description = "可以进入的业务租户。平台上的人至少一座")
    private List<Long> visitTenantIds;

    @Schema(description = "要挂上的部门。组织架构页新建时传入。不传则只建用户")
    private Long departmentCategoryId;
}
