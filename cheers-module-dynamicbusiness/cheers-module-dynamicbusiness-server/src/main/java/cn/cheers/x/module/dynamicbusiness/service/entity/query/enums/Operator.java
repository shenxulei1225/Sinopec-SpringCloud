package cn.cheers.x.module.dynamicbusiness.service.entity.query.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 查询操作符枚举
 * 
 * 定义扩展字段查询支持的所有操作符类型
 * 
 * @author 系统
 */
@Getter
@AllArgsConstructor
public enum Operator {

    /**
     * 等于
     */
    EQ("eq", "等于", "="),

    /**
     * 不等于
     */
    NE("ne", "不等于", "!="),

    /**
     * 大于
     */
    GT("gt", "大于", ">"),

    /**
     * 大于等于
     */
    GE("ge", "大于等于", ">="),

    /**
     * 小于
     */
    LT("lt", "小于", "<"),

    /**
     * 小于等于
     */
    LE("le", "小于等于", "<="),

    /**
     * 区间查询（包含边界）
     */
    BETWEEN("between", "区间", "BETWEEN"),

    /**
     * 模糊匹配
     */
    LIKE("like", "模糊匹配", "LIKE"),

    /**
     * 包含（IN 查询）
     */
    IN("in", "包含", "IN"),

    /**
     * 不包含（NOT IN 查询）
     */
    NOT_IN("not_in", "不包含", "NOT IN"),

    /**
     * 为空
     */
    IS_NULL("is_null", "为空", "IS NULL"),

    /**
     * 不为空
     */
    IS_NOT_NULL("is_not_null", "不为空", "IS NOT NULL");

    /**
     * 操作符编码
     */
    private final String code;

    /**
     * 操作符名称
     */
    private final String name;

    /**
     * SQL 操作符
     */
    private final String sqlOperator;

    /**
     * 根据编码获取操作符
     * 
     * @param code 操作符编码
     * @return 操作符枚举，如果未找到返回 null
     */
    public static Operator getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (Operator operator : values()) {
            if (operator.getCode().equals(code)) {
                return operator;
            }
        }
        return null;
    }

    /**
     * 判断是否为范围操作符
     * 范围操作符需要使用索引表查询
     * 
     * @return 是否为范围操作符
     */
    public boolean isRangeOperator() {
        return this == GT || this == GE || this == LT || this == LE || this == BETWEEN;
    }

    /**
     * 判断是否为等值操作符
     * 等值操作符可以使用 JSONB 直接查询
     * 
     * @return 是否为等值操作符
     */
    public boolean isEqualityOperator() {
        return this == EQ || this == IN;
    }

    /**
     * 判断是否需要值参数
     * IS_NULL 和 IS_NOT_NULL 不需要值参数
     * 
     * @return 是否需要值参数
     */
    public boolean requiresValue() {
        return this != IS_NULL && this != IS_NOT_NULL;
    }

    /**
     * 判断是否需要第二个值参数
     * 只有 BETWEEN 需要第二个值
     * 
     * @return 是否需要第二个值参数
     */
    public boolean requiresSecondValue() {
        return this == BETWEEN;
    }
}
