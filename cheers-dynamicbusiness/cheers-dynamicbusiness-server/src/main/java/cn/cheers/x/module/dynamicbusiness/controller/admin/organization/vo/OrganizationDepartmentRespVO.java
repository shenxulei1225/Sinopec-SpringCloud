package cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 挂在某一家公司或分公司上的部门。
 */
@Data
public class OrganizationDepartmentRespVO {

    @Schema(description = "部门记录编号")
    private Long id;

    @Schema(description = "部门名称")
    private String name;
}
