package cn.iocoder.yudao.module.alarm.controller.admin.vo.type;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 告警类型分类 VO
 * 
 * <p>对应三层模型的 Category 层（告警大类）</p>
 */
@Schema(description = "管理后台 - 告警类型分类 VO")
@Data
public class AlarmTypeCategoryVO {

    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "分类编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "ALARM_ENVIRONMENT")
    private String code;

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "环境告警")
    private String name;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）", example = "0")
    private Integer status;

    @Schema(description = "子模型列表")
    private List<AlarmTypeModelVO> children;

}
