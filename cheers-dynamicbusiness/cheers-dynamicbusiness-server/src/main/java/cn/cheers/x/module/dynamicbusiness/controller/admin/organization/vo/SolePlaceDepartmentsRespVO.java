package cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 场站目录里只有一条启用记录时，组织架构页不显示组织列，直接用这一条下列部门。
 * 有多条或一条都没有时接口返回空，页面仍显示组织列。
 */
@Data
public class SolePlaceDepartmentsRespVO {

    @Schema(description = "这一条场站目录记录的编号")
    private Long facilityId;

    @Schema(description = "这一条场站目录记录的名称")
    private String facilityName;

    @Schema(description = "挂在这一条上的部门")
    private List<OrganizationDepartmentRespVO> departments;
}
