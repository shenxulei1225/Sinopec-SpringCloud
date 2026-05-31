package cn.cheers.x.module.dynamicbusiness.controller.admin.computed.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 计算字段 更新请求 VO
 */
@Schema(description = "管理后台 - 计算字段更新 Request VO")
@Data
public class ComputedFieldUpdateReqVO {

    @Schema(description = "计算字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "计算字段ID不能为空")
    private Long id;

    @Schema(description = "字段名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "故障设备数")
    @NotBlank(message = "字段名称不能为空")
    @Size(max = 64, message = "字段名称长度不能超过64个字符")
    private String fieldName;

    @Schema(description = "字段编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "fault_device_count")
    @NotBlank(message = "字段编码不能为空")
    @Size(max = 64, message = "字段编码长度不能超过64个字符")
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "字段编码格式不正确，必须以小写字母开头，只能包含小写字母、数字和下划线")
    private String fieldCode;

    @Schema(description = "计算类型：AGGREGATE-聚合统计，FORMULA-公式计算", requiredMode = Schema.RequiredMode.REQUIRED, example = "AGGREGATE")
    @NotBlank(message = "计算类型不能为空")
    private String computeType;

    // ========== 聚合统计配置 ==========

    @Schema(description = "聚合函数：COUNT/SUM/AVG/MAX/MIN", example = "COUNT")
    private String aggregateFunction;

    @Schema(description = "统计目标业务类型编码", example = "equipment")
    private String targetBusinessType;

    @Schema(description = "统计目标 Model 编码", example = "device")
    private String targetModelCode;

    @Schema(description = "统计目标字段编码（SUM/AVG/MAX/MIN 时需要）", example = "hours")
    private String targetFieldCode;

    @Schema(description = "关联条件（JSON 格式）", example = "{\"relation_field\": \"department_id\", \"current_field\": \"id\"}")
    private Map<String, Object> relationCondition;

    @Schema(description = "筛选条件（JSON 格式）", example = "[{\"field\": \"status\", \"operator\": \"=\", \"value\": \"FAULT\"}]")
    private List<Map<String, Object>> filterCondition;

    // ========== 公式计算配置 ==========

    @Schema(description = "公式表达式", example = "[fault_count] / [total_count] * 100")
    private String formulaExpression;

    @Schema(description = "公式引用的字段列表", example = "[\"fault_count\", \"total_count\"]")
    private List<String> formulaFields;

    // ========== 结果配置 ==========

    @Schema(description = "结果类型：NUMBER-整数，DECIMAL-小数，PERCENTAGE-百分比", requiredMode = Schema.RequiredMode.REQUIRED, example = "NUMBER")
    @NotBlank(message = "结果类型不能为空")
    private String resultType;

    @Schema(description = "小数位数", example = "2")
    private Integer decimalPlaces;

    @Schema(description = "空值显示", example = "0")
    private String nullDisplay;

    // ========== 性能配置 ==========

    @Schema(description = "计算策略：REALTIME-实时计算，CACHED-缓存计算，PRECOMPUTED-预计算存储", example = "REALTIME")
    private String computeStrategy;

    @Schema(description = "缓存时间（分钟）", example = "5")
    private Integer cacheTtlMinutes;

    // ========== 其他 ==========

    @Schema(description = "字段说明", example = "统计故障状态的设备数量")
    @Size(max = 512, message = "字段说明长度不能超过512个字符")
    private String description;

    @Schema(description = "排序号", example = "0")
    private Integer sortOrder;
}
