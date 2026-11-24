package cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 点中组织节点后中间一列的一个部门。
 * 场站编号为空：部门直接挂在这个组织节点上。
 * 场站编号有值：部门在「所属组织」是这个节点的场站里；前端用 facilityName 做分组行，部门行只写短名。
 */
@Data
public class OrganizationDepartmentRespVO {

    @Schema(description = "部门记录编号")
    private Long id;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "上级部门编号。没有上级时为空或 0")
    private Long parentId;

    @Schema(description = "部门所在场站的记录编号。直接挂在组织节点上的部门为空")
    private Long facilityId;

    @Schema(description = "部门所在场站的名称。直接挂在组织节点上的部门为空")
    private String facilityName;
}
