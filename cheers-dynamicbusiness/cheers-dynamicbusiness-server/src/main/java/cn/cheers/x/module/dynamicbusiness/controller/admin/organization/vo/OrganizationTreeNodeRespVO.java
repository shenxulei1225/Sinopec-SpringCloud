package cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 组织架构页左侧的一行。
 * region 来自运营区域分类。station 是按已有区域关联挂上的场站，不写回分类。
 */
@Schema(description = "组织树节点")
@Data
public class OrganizationTreeNodeRespVO {

    @Schema(description = "分类节点编号")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "region 运营区域，station 场站")
    private String kind;

    @Schema(description = "这条分类绑定的实体编号。场站用它查部门。")
    private Long entityId;

    @Schema(description = "子节点。场站挂在对应的区域节点下。")
    private List<OrganizationTreeNodeRespVO> children;
}
