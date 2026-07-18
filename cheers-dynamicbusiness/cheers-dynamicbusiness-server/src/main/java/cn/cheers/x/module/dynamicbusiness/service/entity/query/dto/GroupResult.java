package cn.cheers.x.module.dynamicbusiness.service.entity.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分组聚合结果
 * 
 * 封装单个分组的聚合结果，包含分组键、聚合值和记录数
 * 
 * @author 系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupResult {

    /**
     * 分组键
     * 分组字段的值
     */
    private Object groupKey;

    /**
     * 分组键显示名称
     * 如果分组字段是选择类型，这里存储选项的显示名称
     */
    private String groupKeyLabel;

    /**
     * 聚合值
     * 根据聚合类型不同，可能是：
     * - COUNT: Long 类型的计数值
     * - SUM: BigDecimal 类型的求和值
     * - AVG: BigDecimal 类型的平均值
     * - MAX/MIN: 与字段类型相同的最大/最小值
     */
    private Object value;

    /**
     * 记录数
     * 该分组中的记录总数
     */
    private Long count;

    // ==================== 便捷构造方法 ====================

    /**
     * 创建分组结果
     *
     * @param groupKey 分组键
     * @param value 聚合值
     * @param count 记录数
     * @return 分组结果
     */
    public static GroupResult of(Object groupKey, Object value, Long count) {
        return GroupResult.builder()
                .groupKey(groupKey)
                .value(value)
                .count(count)
                .build();
    }

    /**
     * 创建带显示名称的分组结果
     *
     * @param groupKey 分组键
     * @param groupKeyLabel 分组键显示名称
     * @param value 聚合值
     * @param count 记录数
     * @return 分组结果
     */
    public static GroupResult of(Object groupKey, String groupKeyLabel, Object value, Long count) {
        return GroupResult.builder()
                .groupKey(groupKey)
                .groupKeyLabel(groupKeyLabel)
                .value(value)
                .count(count)
                .build();
    }
}
