package cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.bidirectional;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 聚合查询请求 VO
 * 
 * <p>用于按关联字段分组统计。</p>
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 聚合查询 Request VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregateQueryReqVO {

    @Schema(description = "Model 编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "production_task")
    @NotBlank(message = "Model 编码不能为空")
    private String modelCode;

    @Schema(description = "聚合类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "COUNT")
    @NotBlank(message = "聚合类型不能为空")
    private String aggregateType;

    @Schema(description = "分组字段编码（关联字段）", requiredMode = Schema.RequiredMode.REQUIRED, example = "plan_id")
    @NotBlank(message = "分组字段编码不能为空")
    private String groupByFieldCode;

    @Schema(description = "聚合目标字段编码（SUM/AVG 时需要）", example = "amount")
    private String targetFieldCode;

    @Schema(description = "筛选条件", example = "[{\"fieldCode\": \"status\", \"operator\": \"EQ\", \"value\": \"1\"}]")
    private List<Map<String, Object>> filterConditions;

    @Schema(description = "是否只统计非空值", example = "true")
    private Boolean excludeNull;

    @Schema(description = "结果数量限制", example = "100")
    private Integer limit;

    /**
     * 聚合类型枚举
     */
    public enum AggregateType {
        COUNT,  // 计数
        SUM,    // 求和
        AVG,    // 平均值
        MAX,    // 最大值
        MIN     // 最小值
    }
}
