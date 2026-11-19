package cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 组织架构页左侧的一行：组织机构里的一级（集团、省公司、分公司、作业区）。
 * 场站不在这棵树上。
 */
@Schema(description = "组织树节点")
@Data
public class OrganizationTreeNodeRespVO {

    @Schema(description = "节点主键，类型加记录编号，例如 organization:86")
    private String key;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "节点类型，固定为 organization")
    private String kind;

    @Schema(description = "组织机构记录编号，用它查这个节点的部门")
    private Long entityId;

    @Schema(description = "组织类型（型号编号）。点这个组织时，职务清单按它筛选")
    private Long modelId;

    @Schema(description = "下级组织节点")
    private List<OrganizationTreeNodeRespVO> children;
}
