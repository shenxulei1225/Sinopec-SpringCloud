package cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.member;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 组织成员分页查询请求")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OrganizationMemberPageReqVO extends PageParam {

    @Schema(description = "组织ID", example = "1")
    private Long orgId;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "角色", example = "director")
    private String role;

    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled;
}
