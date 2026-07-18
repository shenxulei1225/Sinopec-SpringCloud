package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 关联的实体数据
 *
 * 用于在分组展示中提供实体的基本信息。
 */
@Schema(description = "关联的实体数据")
@Data
public class AssociatedEntityData {

    @Schema(description = "实体ID", example = "101")
    private Long id;

    @Schema(description = "实体编码", example = "pump_001")
    private String code;

    @Schema(description = "实体名称", example = "水泵1")
    private String name;

    @Schema(description = "实体分类（用于分组依据）", example = "pump")
    private String category;
}