package cn.cheers.x.alarm.service.rule.pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 时序数据点
 *
 * <p>用于模式检测算法的数据结构，包含时间戳和数值</p>
 *
 * @author 告警管理模块
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataPoint {

    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 数值
     */
    private BigDecimal value;

    /**
     * 便捷构造方法
     */
    public DataPoint(long epochMilli, double value) {
        this.timestamp = LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(epochMilli),
                java.time.ZoneId.systemDefault()
        );
        this.value = BigDecimal.valueOf(value);
    }

    /**
     * 便捷构造方法
     */
    public DataPoint(LocalDateTime timestamp, double value) {
        this.timestamp = timestamp;
        this.value = BigDecimal.valueOf(value);
    }

    /**
     * 获取 double 值
     */
    public double getDoubleValue() {
        return value != null ? value.doubleValue() : 0.0;
    }
}
