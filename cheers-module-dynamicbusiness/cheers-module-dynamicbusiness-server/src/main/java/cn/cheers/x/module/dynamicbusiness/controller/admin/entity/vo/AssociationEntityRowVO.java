package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 某个 REF 视角下的一行关联目标实体摘要。
 */
@Schema(description = "关联目标行")
@Data
public class AssociationEntityRowVO {

    @Schema(description = "目标实体 ID")
    private Long entityId;

    @Schema(description = "目标业务类型编码")
    private String businessTypeCode;

    @Schema(description = "目标实体名称")
    private String name;

    @Schema(description = "在指定 categoryTypeCode 下解析到的分类节点 ID；扁平行或未解析时为 null")
    private Long categoryId;

    @Schema(description = "分类展示名；扁平行或未解析时为 null")
    private String categoryName;
}
