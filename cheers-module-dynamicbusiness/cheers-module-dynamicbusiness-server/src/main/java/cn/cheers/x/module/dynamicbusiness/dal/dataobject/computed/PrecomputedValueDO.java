package cn.cheers.x.module.dynamicbusiness.dal.dataobject.computed;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 预计算值 DO
 * 
 * 用于存储 PRECOMPUTED 策略的计算字段结果。
 * 支持持久化存储、状态跟踪和重试机制。
 * 
 * @author yudao
 */
@TableName("dynamic_precomputed_value")
@KeySequence("dynamic_precomputed_value_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrecomputedValueDO extends TenantBaseDO {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Model ID
     */
    private Long modelId;

    /**
     * 实体 ID
     */
    private Long entityId;

    /**
     * 计算字段编码
     */
    private String fieldCode;

    /**
     * 计算结果（JSON 格式存储）
     */
    private String computedValue;

    /**
     * 值类型
     * 
     * NUMBER: 整数
     * DECIMAL: 小数
     * PERCENTAGE: 百分比
     * STRING: 字符串
     */
    private String valueType;

    /**
     * 计算状态
     * 
     * PENDING: 待计算
     * COMPUTING: 计算中
     * COMPLETED: 已完成
     * FAILED: 失败
     */
    private String computeStatus;

    /**
     * 最后计算时间
     */
    private LocalDateTime lastComputeTime;

    /**
     * 下次计算时间（用于定时重算）
     */
    private LocalDateTime nextComputeTime;

    /**
     * 计算耗时（毫秒）
     */
    private Long computeDurationMs;

    /**
     * 错误信息（失败时）
     */
    private String errorMessage;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 版本号（乐观锁）
     */
    @Version
    private Integer version;

    // ========== 计算状态常量 ==========
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_COMPUTING = "COMPUTING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";

    // ========== 值类型常量 ==========
    public static final String VALUE_TYPE_NUMBER = "NUMBER";
    public static final String VALUE_TYPE_DECIMAL = "DECIMAL";
    public static final String VALUE_TYPE_PERCENTAGE = "PERCENTAGE";
    public static final String VALUE_TYPE_STRING = "STRING";

    /**
     * 判断是否待计算
     */
    public boolean isPending() {
        return STATUS_PENDING.equals(this.computeStatus);
    }

    /**
     * 判断是否计算中
     */
    public boolean isComputing() {
        return STATUS_COMPUTING.equals(this.computeStatus);
    }

    /**
     * 判断是否已完成
     */
    public boolean isCompleted() {
        return STATUS_COMPLETED.equals(this.computeStatus);
    }

    /**
     * 判断是否失败
     */
    public boolean isFailed() {
        return STATUS_FAILED.equals(this.computeStatus);
    }

    /**
     * 判断是否可以重试
     * 
     * @param maxRetryCount 最大重试次数
     * @return 是否可以重试
     */
    public boolean canRetry(int maxRetryCount) {
        return isFailed() && (retryCount == null || retryCount < maxRetryCount);
    }
}
