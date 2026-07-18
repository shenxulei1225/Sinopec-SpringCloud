package cn.cheers.x.module.dynamicbusiness.dal.dataobject.computed;

import cn.cheers.x.framework.mybatis.core.type.JsonbJsonTypeHandler;
import cn.cheers.x.framework.mybatis.core.type.JsonbMapTypeHandler;
import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 计算字段 DO
 * 
 * 业务含义：通过公式或聚合计算得出的虚拟字段。
 * 支持两种计算类型：
 * - AGGREGATE: 聚合统计（COUNT、SUM、AVG、MAX、MIN）
 * - FORMULA: 公式计算（+、-、*、/、%）
 * 
 * @author yudao
 */
@TableName(value = "dynamic_computed_field", autoResultMap = true)
@KeySequence("dynamic_computed_field_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComputedFieldDO extends TenantBaseDO {

    /**
     * 计算字段ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属 Model ID
     */
    private Long modelId;

    /**
     * 字段名称
     * 
     * 如"故障设备数"、"总工时"、"完成率"
     */
    private String fieldName;

    /**
     * 字段编码
     * 
     * 如"fault_device_count"、"total_hours"、"completion_rate"
     */
    private String fieldCode;

    // ========== 计算类型 ==========

    /**
     * 计算类型
     * 
     * AGGREGATE: 聚合统计
     * FORMULA: 公式计算
     */
    private String computeType;

    // ========== 聚合统计配置（compute_type = AGGREGATE 时使用）==========

    /**
     * 聚合函数
     * 
     * COUNT / SUM / AVG / MAX / MIN
     */
    private String aggregateFunction;

    /**
     * 统计目标业务类型编码
     */
    private String targetEntityType;

    /**
     * 统计目标 Model 编码
     */
    private String targetModelCode;

    /**
     * 统计目标字段编码
     * 
     * SUM / AVG / MAX / MIN 时需要指定
     */
    private String targetFieldCode;

    /**
     * 关联条件
     * 
     * JSON 格式，定义如何关联到当前 Entity
     * 例如：{"relation_field": "department_id", "current_field": "id"}
     */
    @TableField(typeHandler = JsonbMapTypeHandler.class)
    private Map<String, Object> relationCondition;

    /**
     * 筛选条件
     * 
     * JSON 格式，定义额外的筛选条件
     * 例如：[{"field": "status", "operator": "=", "value": "FAULT"}]
     */
    @TableField(typeHandler = JsonbJsonTypeHandler.class)
    private List<Map<String, Object>> filterCondition;

    // ========== 公式计算配置（compute_type = FORMULA 时使用）==========

    /**
     * 公式表达式
     * 
     * 例如："[fault_count] / [total_count] * 100"
     * 使用 [字段编码] 引用其他字段
     */
    private String formulaExpression;

    /**
     * 公式引用的字段列表
     * 
     * JSON 格式，记录公式中引用的所有字段编码
     */
    @TableField(typeHandler = JsonbJsonTypeHandler.class)
    private List<String> formulaFields;

    // ========== 结果配置 ==========

    /**
     * 结果类型
     * 
     * NUMBER: 整数
     * DECIMAL: 小数
     * PERCENTAGE: 百分比
     */
    private String resultType;

    /**
     * 小数位数
     */
    private Integer decimalPlaces;

    /**
     * 空值显示
     * 
     * 当计算结果为空时显示的值
     */
    private String nullDisplay;

    // ========== 性能配置 ==========

    /**
     * 计算策略
     * 
     * REALTIME: 实时计算（每次查询时计算）
     * CACHED: 缓存计算（首次计算后缓存，TTL 过期后重新计算）
     * PRECOMPUTED: 预计算存储（数据变化时触发异步重算）
     */
    private String computeStrategy;

    /**
     * 缓存时间（分钟）
     * 
     * CACHED 策略时使用
     */
    private Integer cacheTtlMinutes;

    // ========== 其他 ==========

    /**
     * 字段说明
     */
    private String description;

    /**
     * 排序号
     */
    private Integer sortOrder;

    // ========== 计算类型常量 ==========
    public static final String COMPUTE_TYPE_AGGREGATE = "AGGREGATE";
    public static final String COMPUTE_TYPE_FORMULA = "FORMULA";

    // ========== 聚合函数常量 ==========
    public static final String AGGREGATE_COUNT = "COUNT";
    public static final String AGGREGATE_SUM = "SUM";
    public static final String AGGREGATE_AVG = "AVG";
    public static final String AGGREGATE_MAX = "MAX";
    public static final String AGGREGATE_MIN = "MIN";

    // ========== 结果类型常量 ==========
    public static final String RESULT_TYPE_NUMBER = "NUMBER";
    public static final String RESULT_TYPE_DECIMAL = "DECIMAL";
    public static final String RESULT_TYPE_PERCENTAGE = "PERCENTAGE";

    // ========== 计算策略常量 ==========
    public static final String STRATEGY_REALTIME = "REALTIME";
    public static final String STRATEGY_CACHED = "CACHED";
    public static final String STRATEGY_PRECOMPUTED = "PRECOMPUTED";

    /**
     * 判断是否为聚合统计类型
     */
    public boolean isAggregate() {
        return COMPUTE_TYPE_AGGREGATE.equals(this.computeType);
    }

    /**
     * 判断是否为公式计算类型
     */
    public boolean isFormula() {
        return COMPUTE_TYPE_FORMULA.equals(this.computeType);
    }

    /**
     * 判断是否为实时计算策略
     */
    public boolean isRealtime() {
        return STRATEGY_REALTIME.equals(this.computeStrategy);
    }

    /**
     * 判断是否为缓存计算策略
     */
    public boolean isCached() {
        return STRATEGY_CACHED.equals(this.computeStrategy);
    }

    /**
     * 判断是否为预计算策略
     */
    public boolean isPrecomputed() {
        return STRATEGY_PRECOMPUTED.equals(this.computeStrategy);
    }
}
