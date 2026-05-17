package cn.iocoder.yudao.module.alarm.controller.admin.vo.type;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 告警类型模型 VO
 * 
 * <p>对应三层模型的 Model 层（告警子类）</p>
 */
@Schema(description = "管理后台 - 告警类型模型 VO")
@Data
public class AlarmTypeModelVO {

    @Schema(description = "模型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "模型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "ALARM_WATER_LEVEL")
    private String code;

    @Schema(description = "模型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "水位告警")
    private String name;

    @Schema(description = "所属分类ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long categoryId;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）", example = "0")
    private Integer status;

    @Schema(description = "子实体列表")
    private List<AlarmTypeEntityVO> children;

}
