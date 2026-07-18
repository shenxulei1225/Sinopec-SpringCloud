package cn.cheers.x.facility.management.controller.admin.vo.facility;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 设施树节点 VO
 */
@Schema(description = "管理后台 - 设施树节点 VO")
@Data
public class FacilityTreeNodeVO {

    @Schema(description = "节点ID")
    private Long id;

    @Schema(description = "父节点ID")
    private Long parentId;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "标题ID")
    private Long titleId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "子节点")
    private List<FacilityTreeNodeVO> children;
}
