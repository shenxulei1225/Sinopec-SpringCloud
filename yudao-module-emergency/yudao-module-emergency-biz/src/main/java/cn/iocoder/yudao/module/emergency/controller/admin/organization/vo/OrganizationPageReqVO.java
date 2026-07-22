package cn.iocoder.yudao.module.emergency.controller.admin.organization.vo;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 应急组织分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OrganizationPageReqVO extends PageParam {

    @Schema(description = "组织名称", example = "应急领导小组")
    private String orgName;

    @Schema(description = "组织类型", example = "leadership_group")
    private String orgType;

    @Schema(description = "上级组织ID", example = "1")
    private Long parentId;

    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled;
}



