package cn.cheers.x.module.dynamicbusiness.service.entity.query.dto;

import cn.cheers.x.module.dynamicbusiness.service.entity.query.enums.AggregateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 聚合结果
 *
 * 封装聚合查询的结果，支持单值结果和分组结果
 *
 * @author 系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregateResult {

    /**
     * 聚合类型
     */
    private AggregateType aggregateType;

    /**
     * 聚合字段编码
     */
    private String fieldCode;

    /**
     * 聚合值（非分组时）
     * 根据聚合类型不同，可能是：
     * - COUNT: Long 类型的计数值
     * - SUM: BigDecimal 类型的求和值
     * - AVG: BigDecimal 类型的平均值
     * - MAX/MIN: 与字段类型相同的最大/最小值
     */
    private Object value;

    /**
     * 总记录数
     * 参与聚合的记录总数
     */
    private Long totalCount;

    /**
     * 分组结果（分组时）
     * 如果指定了分组字段，则返回分组结果列表
     */
    @Builder.Default
    private List<GroupResult> groups = new ArrayList<>();

    /**
     * 分组字段编码
     */
    private String groupByFieldCode;

    // ==================== 便捷方法 ====================

    /**
     * 判断是否为分组聚合结果
     *
     * @return 是否为分组聚合结果
     */
    public boolean isGrouped() {
        return groupByFieldCode != null && !groupByFieldCode.isEmpty();
    }

    /**
     * 获取聚合值（Long 类型）
     * 适用于 COUNT 聚合
     *
     * @return Long 类型的聚合值
     */
    public Long getValueAsLong() {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());
    }

    /**
     * 获取聚合值（BigDecimal 类型）
     * 适用于 SUM、AVG 聚合
     *
     * @return BigDecimal 类型的聚合值
     */
    public BigDecimal getValueAsBigDecimal() {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        return new BigDecimal(value.toString());
    }

    /**
     * 获取聚合值（Double 类型）
     *
     * @return Double 类型的聚合值
     */
    public Double getValueAsDouble() {
        if (value == null) {
            return null;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(value.toString());
    }

    // ==================== 静态构建方法 ====================

    /**
     * 创建单值聚合结果
     *
     * @param aggregateType 聚合类型
     * @param fieldCode 聚合字段编码
     * @param value 聚合值
     * @param totalCount 总记录数
     * @return 聚合结果
     */
    public static AggregateResult single(AggregateType aggregateType, String fieldCode, 
                                          Object value, Long totalCount) {
        return AggregateResult.builder()
                .aggregateType(aggregateType)
                .fieldCode(fieldCode)
                .value(value)
                .totalCount(totalCount)
                .build();
    }

    /**
     * 创建分组聚合结果
     *
     * @param aggregateType 聚合类型
     * @param fieldCode 聚合字段编码
     * @param groupByFieldCode 分组字段编码
     * @param groups 分组结果列表
     * @param totalCount 总记录数
     * @return 聚合结果
     */
    public static AggregateResult grouped(AggregateType aggregateType, String fieldCode,
                                           String groupByFieldCode, List<GroupResult> groups,
                                           Long totalCount) {
        return AggregateResult.builder()
                .aggregateType(aggregateType)
                .fieldCode(fieldCode)
                .groupByFieldCode(groupByFieldCode)
                .groups(groups)
                .totalCount(totalCount)
                .build();
    }

    /**
     * 创建计数结果
     *
     * @param count 计数值
     * @return 聚合结果
     */
    public static AggregateResult count(Long count) {
        return AggregateResult.builder()
                .aggregateType(AggregateType.COUNT)
                .value(count)
                .totalCount(count)
                .build();
    }
}
