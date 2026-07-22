package cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.abrole;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - A/B角分页查询请求")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ABRolePageReqVO extends PageParam {

    @Schema(description = "角色编号", example = "ROLE001")
    private String roleCode;

    @Schema(description = "角色名称", example = "应急指挥A/B角")
    private String roleName;

    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled;

    @Schema(description = "部门ID", example = "1")
    private Long departmentId;

    @Schema(description = "A角用户ID", example = "1")
    private Long aUserId;

    @Schema(description = "B角用户ID", example = "2")
    private Long bUserId;

    @Schema(description = "当前激活角色（A/B）", example = "A")
    private String currentActive;
}
