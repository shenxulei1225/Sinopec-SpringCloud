package cn.cheers.x.module.dynamicbusiness.service.entity.query.dto;

import cn.cheers.x.module.dynamicbusiness.service.entity.query.enums.Operator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;

/**
 * 字段查询条件
 *
 * 封装单个字段的查询条件，包含字段编码、操作符和值
 *
 * @author 系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldCondition {

    /**
     * 字段编码（必填）
     * 对应 FieldDefinition 中的 code
     */
    @NotBlank(message = "字段编码不能为空")
    private String fieldCode;

    /**
     * 操作符（必填）
     */
    @NotNull(message = "操作符不能为空")
    private Operator operator;

    /**
     * 查询值
     * - 对于 EQ、NE、GT、GE、LT、LE、LIKE：单个值
     * - 对于 IN、NOT_IN：值列表（Collection）
     * - 对于 BETWEEN：第一个边界值
     * - 对于 IS_NULL、IS_NOT_NULL：不需要值
     */
    private Object value;

    /**
     * 第二个查询值
     * 仅用于 BETWEEN 操作符，表示区间的第二个边界值
     */
    private Object value2;

    // ==================== 便捷构造方法 ====================

    /**
     * 创建等值查询条件
     *
     * @param fieldCode 字段编码
     * @param value 查询值
     * @return 字段条件
     */
    public static FieldCondition eq(String fieldCode, Object value) {
        return FieldCondition.builder()
                .fieldCode(fieldCode)
                .operator(Operator.EQ)
                .value(value)
                .build();
    }

    /**
     * 创建不等于查询条件
     *
     * @param fieldCode 字段编码
     * @param value 查询值
     * @return 字段条件
     */
    public static FieldCondition ne(String fieldCode, Object value) {
        return FieldCondition.builder()
                .fieldCode(fieldCode)
                .operator(Operator.NE)
                .value(value)
                .build();
    }

    /**
     * 创建大于查询条件
     *
     * @param fieldCode 字段编码
     * @param value 查询值
     * @return 字段条件
     */
    public static FieldCondition gt(String fieldCode, Object value) {
        return FieldCondition.builder()
                .fieldCode(fieldCode)
                .operator(Operator.GT)
                .value(value)
                .build();
    }

    /**
     * 创建大于等于查询条件
     *
     * @param fieldCode 字段编码
     * @param value 查询值
     * @return 字段条件
     */
    public static FieldCondition ge(String fieldCode, Object value) {
        return FieldCondition.builder()
                .fieldCode(fieldCode)
                .operator(Operator.GE)
                .value(value)
                .build();
    }

    /**
     * 创建小于查询条件
     *
     * @param fieldCode 字段编码
     * @param value 查询值
     * @return 字段条件
     */
    public static FieldCondition lt(String fieldCode, Object value) {
        return FieldCondition.builder()
                .fieldCode(fieldCode)
                .operator(Operator.LT)
                .value(value)
                .build();
    }

    /**
     * 创建小于等于查询条件
     *
     * @param fieldCode 字段编码
     * @param value 查询值
     * @return 字段条件
     */
    public static FieldCondition le(String fieldCode, Object value) {
        return FieldCondition.builder()
                .fieldCode(fieldCode)
                .operator(Operator.LE)
                .value(value)
                .build();
    }

    /**
     * 创建区间查询条件
     *
     * @param fieldCode 字段编码
     * @param value1 区间起始值（包含）
     * @param value2 区间结束值（包含）
     * @return 字段条件
     */
    public static FieldCondition between(String fieldCode, Object value1, Object value2) {
        return FieldCondition.builder()
                .fieldCode(fieldCode)
                .operator(Operator.BETWEEN)
                .value(value1)
                .value2(value2)
                .build();
    }

    /**
     * 创建模糊查询条件
     *
     * @param fieldCode 字段编码
     * @param value 查询值（支持 % 通配符）
     * @return 字段条件
     */
    public static FieldCondition like(String fieldCode, String value) {
        return FieldCondition.builder()
                .fieldCode(fieldCode)
                .operator(Operator.LIKE)
                .value(value)
                .build();
    }

    /**
     * 创建包含查询条件
     *
     * @param fieldCode 字段编码
     * @param values 值列表
     * @return 字段条件
     */
    public static FieldCondition in(String fieldCode, Collection<?> values) {
        return FieldCondition.builder()
                .fieldCode(fieldCode)
                .operator(Operator.IN)
                .value(values)
                .build();
    }

    /**
     * 创建不包含查询条件
     *
     * @param fieldCode 字段编码
     * @param values 值列表
     * @return 字段条件
     */
    public static FieldCondition notIn(String fieldCode, Collection<?> values) {
        return FieldCondition.builder()
                .fieldCode(fieldCode)
                .operator(Operator.NOT_IN)
                .value(values)
                .build();
    }

    /**
     * 创建为空查询条件
     *
     * @param fieldCode 字段编码
     * @return 字段条件
     */
    public static FieldCondition isNull(String fieldCode) {
        return FieldCondition.builder()
                .fieldCode(fieldCode)
                .operator(Operator.IS_NULL)
                .build();
    }

    /**
     * 创建不为空查询条件
     *
     * @param fieldCode 字段编码
     * @return 字段条件
     */
    public static FieldCondition isNotNull(String fieldCode) {
        return FieldCondition.builder()
                .fieldCode(fieldCode)
                .operator(Operator.IS_NOT_NULL)
                .build();
    }
}
