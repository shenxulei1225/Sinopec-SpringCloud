package cn.iocoder.yudao.module.emergency.controller.admin.organization.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 应急组织创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmergencyOrganizationCreateReqVO extends EmergencyOrganizationBaseVO {

    @Schema(description = "组织名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "应急管理部门")
    @NotBlank(message = "组织名称不能为空")
    private String name;

    @Schema(description = "组织编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "EMERGENCY_DEPT_001")
    @NotBlank(message = "组织编码不能为空")
    private String code;
}








