package cn.cheers.x.module.dynamicbusiness.service.entity.query.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 聚合类型枚举
 *
 * 定义扩展字段统计聚合支持的所有聚合类型
 *
 * @author 系统
 */
@Getter
@AllArgsConstructor
public enum AggregateType {

    /**
     * 计数
     */
    COUNT("count", "计数", "COUNT"),

    /**
     * 求和
     */
    SUM("sum", "求和", "SUM"),

    /**
     * 平均值
     */
    AVG("avg", "平均值", "AVG"),

    /**
     * 最大值
     */
    MAX("max", "最大值", "MAX"),

    /**
     * 最小值
     */
    MIN("min", "最小值", "MIN");

    /**
     * 聚合类型编码
     */
    private final String code;

    /**
     * 聚合类型名称
     */
    private final String name;

    /**
     * SQL 函数名
     */
    private final String sqlFunction;

    /**
     * 根据编码获取聚合类型
     *
     * @param code 聚合类型编码
     * @return 聚合类型枚举，如果未找到返回 null
     */
    public static AggregateType getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (AggregateType type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为数值聚合类型
     * SUM、AVG 只能用于数值类型字段
     *
     * @return 是否为数值聚合类型
     */
    public boolean isNumericOnly() {
        return this == SUM || this == AVG;
    }

    /**
     * 判断是否需要指定聚合字段
     * COUNT 可以不指定字段（统计总数）
     *
     * @return 是否需要指定聚合字段
     */
    public boolean requiresField() {
        return this != COUNT;
    }
}
