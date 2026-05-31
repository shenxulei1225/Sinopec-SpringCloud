package cn.cheers.x.module.dynamicbusiness.controller.admin.computed.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 计算字段 响应 VO
 */
@Schema(description = "管理后台 - 计算字段 Response VO")
@Data
public class ComputedFieldRespVO {

    @Schema(description = "计算字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "所属 Model ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long modelId;

    @Schema(description = "字段名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "故障设备数")
    private String fieldName;

    @Schema(description = "字段编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "fault_device_count")
    private String fieldCode;

    @Schema(description = "计算类型：AGGREGATE-聚合统计，FORMULA-公式计算", requiredMode = Schema.RequiredMode.REQUIRED, example = "AGGREGATE")
    private String computeType;

    @Schema(description = "计算类型名称", example = "聚合统计")
    private String computeTypeName;

    // ========== 聚合统计配置 ==========

    @Schema(description = "聚合函数：COUNT/SUM/AVG/MAX/MIN", example = "COUNT")
    private String aggregateFunction;

    @Schema(description = "聚合函数名称", example = "计数")
    private String aggregateFunctionName;

    @Schema(description = "统计目标业务类型编码", example = "equipment")
    private String targetBusinessType;

    @Schema(description = "统计目标业务类型名称", example = "设备管理")
    private String targetBusinessTypeName;

    @Schema(description = "统计目标 Model 编码", example = "device")
    private String targetModelCode;

    @Schema(description = "统计目标 Model 名称", example = "设备")
    private String targetModelName;

    @Schema(description = "统计目标字段编码", example = "hours")
    private String targetFieldCode;

    @Schema(description = "关联条件（JSON 格式）")
    private Map<String, Object> relationCondition;

    @Schema(description = "筛选条件（JSON 格式）")
    private List<Map<String, Object>> filterCondition;

    // ========== 公式计算配置 ==========

    @Schema(description = "公式表达式", example = "[fault_count] / [total_count] * 100")
    private String formulaExpression;

    @Schema(description = "公式引用的字段列表")
    private List<String> formulaFields;

    // ========== 结果配置 ==========

    @Schema(description = "结果类型：NUMBER-整数，DECIMAL-小数，PERCENTAGE-百分比", requiredMode = Schema.RequiredMode.REQUIRED, example = "NUMBER")
    private String resultType;

    @Schema(description = "结果类型名称", example = "整数")
    private String resultTypeName;

    @Schema(description = "小数位数", example = "2")
    private Integer decimalPlaces;

    @Schema(description = "空值显示", example = "0")
    private String nullDisplay;

    // ========== 性能配置 ==========

    @Schema(description = "计算策略：REALTIME-实时计算，CACHED-缓存计算，PRECOMPUTED-预计算存储", example = "REALTIME")
    private String computeStrategy;

    @Schema(description = "计算策略名称", example = "实时计算")
    private String computeStrategyName;

    @Schema(description = "缓存时间（分钟）", example = "5")
    private Integer cacheTtlMinutes;

    // ========== 其他 ==========

    @Schema(description = "字段说明", example = "统计故障状态的设备数量")
    private String description;

    @Schema(description = "排序号", example = "0")
    private Integer sortOrder;

    @Schema(description = "计算公式的可读描述", example = "统计 设备管理/设备 中 状态=故障 的记录数")
    private String formulaDescription;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
