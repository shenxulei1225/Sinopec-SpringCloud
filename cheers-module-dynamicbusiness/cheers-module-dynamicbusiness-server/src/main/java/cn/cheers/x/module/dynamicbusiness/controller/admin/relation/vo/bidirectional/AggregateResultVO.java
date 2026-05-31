package cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.bidirectional;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 聚合结果 VO
 * 
 * <p>用于展示按关联字段分组统计的结果。</p>
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 聚合结果 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregateResultVO {

    @Schema(description = "分组键（关联实体 ID）", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    private Long groupKey;

    @Schema(description = "分组键显示值（关联实体名称）", example = "2024年1月生产计划")
    private String groupKeyDisplay;

    @Schema(description = "聚合值", requiredMode = Schema.RequiredMode.REQUIRED, example = "8")
    private BigDecimal aggregateValue;

    @Schema(description = "聚合类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "COUNT")
    private String aggregateType;

    @Schema(description = "记录数量（用于 AVG 计算）", example = "8")
    private Long recordCount;

    @Schema(description = "关联实体的 Model 编码", example = "production_plan")
    private String relatedModelCode;

    @Schema(description = "关联实体的 Model 名称", example = "生产计划")
    private String relatedModelName;
}
