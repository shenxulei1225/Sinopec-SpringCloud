package cn.iocoder.yudao.module.emergency.controller.admin.organization.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 应急组织 Response VO")
@Data
public class OrganizationRespVO {

    @Schema(description = "组织ID", example = "1")
    private Long id;

    @Schema(description = "组织编号", example = "ORG001")
    private String orgCode;

    @Schema(description = "组织名称", example = "应急领导小组")
    private String orgName;

    @Schema(description = "组织类型", example = "leadership_group")
    private String orgType;

    @Schema(description = "上级组织ID", example = "1")
    private Long parentId;

    @Schema(description = "组织职责描述", example = "负责应急响应的指挥和决策")
    private String description;

    @Schema(description = "是否动态组织", example = "false")
    private Boolean isDynamic;

    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}



