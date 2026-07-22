package cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.abrole;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - A/B角创建请求")
@Data
public class ABRoleCreateReqVO extends ABRoleBaseVO {

    @Schema(description = "角色编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "ROLE001")
    @NotBlank(message = "角色编号不能为空")
    private String roleCode;

    @Schema(description = "角色名称", example = "应急指挥A/B角")
    private String roleName;

    @Schema(description = "A角用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "A角用户ID不能为空")
    private Long aUserId;

    @Schema(description = "B角用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "B角用户ID不能为空")
    private Long bUserId;
}