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
public class EmergencyOrganizationPageReqVO extends PageParam {

    @Schema(description = "组织名称", example = "应急管理部门")
    private String name;

    @Schema(description = "组织编码", example = "EMERGENCY_DEPT_001")
    private String code;

    @Schema(description = "组织类型", example = "GOVERNMENT")
    private String type;

    @Schema(description = "上级组织ID", example = "1")
    private Long parentId;

    @Schema(description = "状态", example = "ENABLE")
    private String status;
}








