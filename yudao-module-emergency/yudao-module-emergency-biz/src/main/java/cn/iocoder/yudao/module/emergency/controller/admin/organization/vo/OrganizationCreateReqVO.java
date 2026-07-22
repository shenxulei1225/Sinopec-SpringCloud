package cn.iocoder.yudao.module.emergency.controller.admin.organization.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 应急组织创建 Request VO")
@Data
public class OrganizationCreateReqVO {

    @Schema(description = "组织编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "ORG001")
    @NotBlank(message = "组织编号不能为空")
    private String orgCode;

    @Schema(description = "组织名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "应急领导小组")
    @NotBlank(message = "组织名称不能为空")
    private String orgName;

    @Schema(description = "组织类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "leadership_group")
    @NotBlank(message = "组织类型不能为空")
    private String orgType;

    @Schema(description = "上级组织ID。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", example = "1")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long parentId;

    @Schema(description = "组织职责描述", example = "负责应急响应的指挥和决策")
    private String description;

    @Schema(description = "是否动态组织", example = "false")
    private Boolean isDynamic;

    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled;
}



