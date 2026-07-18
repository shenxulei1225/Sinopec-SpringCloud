package cn.cheers.x.alarm.service.rule.pattern;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 连续相同值检测器
 *
 * <p>检测数据是否连续出现相同值，可能表示传感器故障或数据卡死</p>
 *
 * <p>算法说明：
 * <ul>
 *   <li>统计连续相同值的次数</li>
 *   <li>当连续相同值次数达到阈值时触发告警</li>
 *   <li>支持配置容差范围（tolerance），在容差范围内视为相同值</li>
 * </ul>
 * </p>
 *
 * @author 告警管理模块
 */
@Slf4j
@Component
public class ConsecutiveSameDetector implements PatternDetector {

    public static final String PATTERN_TYPE = "CONSECUTIVE_SAME";

    /**
     * 默认连续相同值阈值
     */
    private static final int DEFAULT_COUNT_THRESHOLD = 5;

    /**
     * 默认容差（用于浮点数比较）
     */
    private static final double DEFAULT_TOLERANCE = 0.001;

    @Override
    public String getPatternType() {
        return PATTERN_TYPE;
    }

    @Override
    public String getDescription() {
        return "连续相同值检测器 - 检测传感器数据卡死或故障";
    }

    @Override
    public Map<String, Object> getDefaultParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("count", DEFAULT_COUNT_THRESHOLD);
        params.put("tolerance", DEFAULT_TOLERANCE);
        return params;
    }

    @Override
    public PatternDetectionResult detect(List<DataPoint> dataPoints, Map<String, Object> params) {
        if (dataPoints == null || dataPoints.size() < 2) {
            return PatternDetectionResult.notDetected(PATTERN_TYPE);
        }

        // 获取参数
        int countThreshold = getIntParam(params, "count", DEFAULT_COUNT_THRESHOLD);
        double tolerance = getDoubleParam(params, "tolerance", DEFAULT_TOLERANCE);

        // 检测连续相同值
        int maxConsecutiveCount = 1;
        int currentConsecutiveCount = 1;
        BigDecimal lastValue = dataPoints.get(0).getValue();
        BigDecimal consecutiveValue = lastValue;

        for (int i = 1; i < dataPoints.size(); i++) {
            BigDecimal currentValue = dataPoints.get(i).getValue();
            
            if (isValueEqual(lastValue, currentValue, tolerance)) {
                currentConsecutiveCount++;
                if (currentConsecutiveCount > maxConsecutiveCount) {
                    maxConsecutiveCount = currentConsecutiveCount;
                    consecutiveValue = currentValue;
                }
            } else {
                currentConsecutiveCount = 1;
            }
            lastValue = currentValue;
        }

        // 判断是否达到阈值
        if (maxConsecutiveCount >= countThreshold) {
            Map<String, Object> details = new HashMap<>();
            details.put("consecutiveCount", maxConsecutiveCount);
            details.put("consecutiveValue", consecutiveValue);
            details.put("threshold", countThreshold);
            details.put("tolerance", tolerance);
            details.put("dataPointCount", dataPoints.size());

            // 置信度：连续次数越多，置信度越高
            double confidence = Math.min(1.0, (double) maxConsecutiveCount / (countThreshold * 2));

            String description = String.format(
                    "检测到连续 %d 次相同值（%.4f），超过阈值 %d 次",
                    maxConsecutiveCount, consecutiveValue.doubleValue(), countThreshold
            );

            log.debug("[ConsecutiveSameDetector] 检测到异常: {}", description);
            return PatternDetectionResult.detected(PATTERN_TYPE, description, confidence, details);
        }

        return PatternDetectionResult.notDetected(PATTERN_TYPE);
    }

    /**
     * 判断两个值是否相等（考虑容差）
     */
    private boolean isValueEqual(BigDecimal v1, BigDecimal v2, double tolerance) {
        if (v1 == null || v2 == null) {
            return v1 == v2;
        }
        return v1.subtract(v2).abs().doubleValue() <= tolerance;
    }

    /**
     * 获取整数参数
     */
    private int getIntParam(Map<String, Object> params, String key, int defaultValue) {
        if (params == null || !params.containsKey(key)) {
            return defaultValue;
        }
        Object value = params.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取浮点数参数
     */
    private double getDoubleParam(Map<String, Object> params, String key, double defaultValue) {
        if (params == null || !params.containsKey(key)) {
            return defaultValue;
        }
        Object value = params.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
