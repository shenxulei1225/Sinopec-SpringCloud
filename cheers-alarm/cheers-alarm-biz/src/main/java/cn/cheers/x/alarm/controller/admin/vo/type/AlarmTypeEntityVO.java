package cn.cheers.x.alarm.controller.admin.vo.type;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 告警类型实体 VO
 * 
 * <p>对应三层模型的 Entity 层（具体告警项）</p>
 */
@Schema(description = "管理后台 - 告警类型实体 VO")
@Data
public class AlarmTypeEntityVO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "实体编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "WATER_LEVEL_HIGH")
    private String code;

    @Schema(description = "实体名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "水位超标")
    private String name;

    @Schema(description = "所属模型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long modelId;

    @Schema(description = "所属分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）", example = "0")
    private Integer status;

    @Schema(description = "完整路径", example = "环境告警 > 水位告警 > 水位超标")
    private String fullPath;

}
