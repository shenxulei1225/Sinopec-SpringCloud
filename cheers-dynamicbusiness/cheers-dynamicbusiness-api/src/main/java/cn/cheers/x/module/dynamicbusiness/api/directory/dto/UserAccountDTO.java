package cn.cheers.x.module.dynamicbusiness.api.directory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户目录的强类型只读数据。
 *
 * <p>权威来源是动态业务 {@code user_account} 实体。认证、BPM、CRM 不得读取系统用户表补值。</p>
 */
@Data
@Schema(description = "RPC 服务 - 用户目录数据")
public class UserAccountDTO {

    @Schema(description = "用户目录 ID")
    private Long id;

    @Schema(description = "账号")
    private String account;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机")
    private String mobile;

    @Schema(description = "状态。1 启用，0 停用")
    private Integer status;

    @Schema(description = "所属部门 ID")
    private Long departmentId;

    @Schema(description = "平台用户可进入的业务租户 ID")
    private List<Long> visitTenantIds;
}
