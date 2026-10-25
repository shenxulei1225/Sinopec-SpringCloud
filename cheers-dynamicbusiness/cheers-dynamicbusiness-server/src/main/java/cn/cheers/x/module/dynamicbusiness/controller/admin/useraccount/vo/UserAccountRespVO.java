package cn.cheers.x.module.dynamicbusiness.controller.admin.useraccount.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserAccountRespVO {

    @Schema(description = "用户目录里这条记录的编号")
    private Long id;

    @Schema(description = "账号")
    private String account;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机")
    private String mobile;

    @Schema(description = "状态。1 启用，0 停用")
    private Integer status;

    @Schema(description = "当前挂着的部门分类编号")
    private Long departmentCategoryId;

    @Schema(description = "当前挂着的部门名称")
    private String departmentName;
}
