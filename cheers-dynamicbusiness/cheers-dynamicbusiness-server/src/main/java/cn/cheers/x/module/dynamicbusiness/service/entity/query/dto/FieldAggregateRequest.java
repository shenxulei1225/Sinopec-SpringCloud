package cn.cheers.x.module.dynamicbusiness.service.entity.query.dto;

import cn.cheers.x.module.dynamicbusiness.service.entity.query.enums.AggregateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 字段聚合请求
 *
 * 封装扩展字段统计聚合的请求参数，支持计数、求和、平均值、最大值、最小值等聚合操作
 *
 * @author 系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldAggregateRequest {

    /**
     * 模型ID（必填）
     * 指定要聚合的 Model
     */
    @NotNull(message = "模型ID不能为空")
    private Long modelId;

    /**
     * 查询条件列表（可选）
     * 用于在聚合前过滤数据
     * 最多支持 10 个条件
     */
    @Valid
    @Size(max = 10, message = "查询条件最多支持10个")
    @Builder.Default
    private List<FieldCondition> conditions = new ArrayList<>();

    /**
     * 聚合类型（必填）
     */
    @NotNull(message = "聚合类型不能为空")
    private AggregateType aggregateType;

    /**
     * 聚合字段编码
     * - 对于 COUNT：可选，不指定则统计总数
     * - 对于 SUM、AVG、MAX、MIN：必填
     */
    private String fieldCode;

    /**
     * 分组字段编码（可选）
     * 如果指定，则按此字段分组后再聚合
     */
    private String groupByFieldCode;

    // ==================== 便捷方法 ====================

    /**
     * 添加查询条件
     *
     * @param condition 查询条件
     * @return 当前请求对象（支持链式调用）
     */
    public FieldAggregateRequest addCondition(FieldCondition condition) {
        if (this.conditions == null) {
            this.conditions = new ArrayList<>();
        }
        this.conditions.add(condition);
        return this;
    }

    /**
     * 判断是否有查询条件
     *
     * @return 是否有查询条件
     */
    public boolean hasConditions() {
        return conditions != null && !conditions.isEmpty();
    }

    /**
     * 判断是否有分组
     *
     * @return 是否有分组
     */
    public boolean hasGroupBy() {
        return groupByFieldCode != null && !groupByFieldCode.isEmpty();
    }

    // ==================== 静态构建方法 ====================

    /**
     * 创建计数聚合请求
     *
     * @param modelId 模型ID
     * @return 聚合请求
     */
    public static FieldAggregateRequest count(Long modelId) {
        return FieldAggregateRequest.builder()
                .modelId(modelId)
                .aggregateType(AggregateType.COUNT)
                .build();
    }

    /**
     * 创建求和聚合请求
     *
     * @param modelId 模型ID
     * @param fieldCode 聚合字段编码
     * @return 聚合请求
     */
    public static FieldAggregateRequest sum(Long modelId, String fieldCode) {
        return FieldAggregateRequest.builder()
                .modelId(modelId)
                .aggregateType(AggregateType.SUM)
                .fieldCode(fieldCode)
                .build();
    }

    /**
     * 创建平均值聚合请求
     *
     * @param modelId 模型ID
     * @param fieldCode 聚合字段编码
     * @return 聚合请求
     */
    public static FieldAggregateRequest avg(Long modelId, String fieldCode) {
        return FieldAggregateRequest.builder()
                .modelId(modelId)
                .aggregateType(AggregateType.AVG)
                .fieldCode(fieldCode)
                .build();
    }

    /**
     * 创建最大值聚合请求
     *
     * @param modelId 模型ID
     * @param fieldCode 聚合字段编码
     * @return 聚合请求
     */
    public static FieldAggregateRequest max(Long modelId, String fieldCode) {
        return FieldAggregateRequest.builder()
                .modelId(modelId)
                .aggregateType(AggregateType.MAX)
                .fieldCode(fieldCode)
                .build();
    }

    /**
     * 创建最小值聚合请求
     *
     * @param modelId 模型ID
     * @param fieldCode 聚合字段编码
     * @return 聚合请求
     */
    public static FieldAggregateRequest min(Long modelId, String fieldCode) {
        return FieldAggregateRequest.builder()
                .modelId(modelId)
                .aggregateType(AggregateType.MIN)
                .fieldCode(fieldCode)
                .build();
    }

    /**
     * 设置分组字段
     *
     * @param groupByFieldCode 分组字段编码
     * @return 当前请求对象（支持链式调用）
     */
    public FieldAggregateRequest groupBy(String groupByFieldCode) {
        this.groupByFieldCode = groupByFieldCode;
        return this;
    }
}
