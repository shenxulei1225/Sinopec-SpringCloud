package cn.iocoder.yudao.module.alarm.service.rule.pattern;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 趋势检测器
 *
 * <p>基于线性回归检测数据的持续上升或下降趋势</p>
 *
 * <p>算法说明：
 * <ul>
 *   <li>使用最小二乘法进行线性回归，计算斜率和 R² 值</li>
 *   <li>斜率表示趋势方向和强度</li>
 *   <li>R² 值表示线性拟合程度（趋势的可靠性）</li>
 *   <li>当斜率超过阈值且 R² 足够高时，判定存在趋势</li>
 * </ul>
 * </p>
 *
 * @author 告警管理模块
 */
@Slf4j
@Component
public class TrendDetector implements PatternDetector {

    public static final String PATTERN_TYPE = "TREND";

    /**
     * 默认斜率阈值（每个数据点的变化量）
     */
    private static final double DEFAULT_SLOPE_THRESHOLD = 0.1;

    /**
     * 默认 R² 阈值（线性拟合程度）
     */
    private static final double DEFAULT_R_SQUARED_THRESHOLD = 0.7;

    /**
     * 最小数据点数量
     */
    private static final int MIN_DATA_POINTS = 5;

    @Override
    public String getPatternType() {
        return PATTERN_TYPE;
    }

    @Override
    public String getDescription() {
        return "趋势检测器 - 基于线性回归检测持续上升或下降趋势";
    }

    @Override
    public Map<String, Object> getDefaultParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("slopeThreshold", DEFAULT_SLOPE_THRESHOLD);
        params.put("rSquaredThreshold", DEFAULT_R_SQUARED_THRESHOLD);
        params.put("direction", "ANY"); // UP, DOWN, ANY
        return params;
    }

    @Override
    public PatternDetectionResult detect(List<DataPoint> dataPoints, Map<String, Object> params) {
        if (dataPoints == null || dataPoints.size() < MIN_DATA_POINTS) {
            return PatternDetectionResult.notDetected(PATTERN_TYPE);
        }

        // 获取参数
        double slopeThreshold = getDoubleParam(params, "slopeThreshold", DEFAULT_SLOPE_THRESHOLD);
        double rSquaredThreshold = getDoubleParam(params, "rSquaredThreshold", DEFAULT_R_SQUARED_THRESHOLD);
        String direction = getStringParam(params, "direction", "ANY");

        int n = dataPoints.size();

        // 使用索引作为 x 值（0, 1, 2, ...）
        // 计算线性回归：y = slope * x + intercept
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            double x = i;
            double y = dataPoints.get(i).getDoubleValue();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double meanX = sumX / n;
        double meanY = sumY / n;

        // 计算斜率
        double numerator = sumXY - n * meanX * meanY;
        double denominator = sumX2 - n * meanX * meanX;
        
        if (Math.abs(denominator) < 1e-10) {
            return PatternDetectionResult.notDetected(PATTERN_TYPE);
        }

        double slope = numerator / denominator;
        double intercept = meanY - slope * meanX;

        // 计算 R² (决定系数)
        double ssTotal = 0, ssResidual = 0;
        for (int i = 0; i < n; i++) {
            double y = dataPoints.get(i).getDoubleValue();
            double yPredicted = slope * i + intercept;
            ssTotal += (y - meanY) * (y - meanY);
            ssResidual += (y - yPredicted) * (y - yPredicted);
        }

        double rSquared = ssTotal > 0 ? 1 - (ssResidual / ssTotal) : 0;

        // 判断是否存在趋势
        boolean hasTrend = false;
        String trendDirection = "";

        if (rSquared >= rSquaredThreshold && Math.abs(slope) >= slopeThreshold) {
            if (slope > 0 && ("UP".equals(direction) || "ANY".equals(direction))) {
                hasTrend = true;
                trendDirection = "上升";
            } else if (slope < 0 && ("DOWN".equals(direction) || "ANY".equals(direction))) {
                hasTrend = true;
                trendDirection = "下降";
            }
        }

        if (hasTrend) {
            Map<String, Object> details = new HashMap<>();
            details.put("slope", slope);
            details.put("intercept", intercept);
            details.put("rSquared", rSquared);
            details.put("slopeThreshold", slopeThreshold);
            details.put("rSquaredThreshold", rSquaredThreshold);
            details.put("direction", trendDirection);
            details.put("dataPointCount", n);
            details.put("startValue", dataPoints.get(0).getDoubleValue());
            details.put("endValue", dataPoints.get(n - 1).getDoubleValue());

            // 置信度：R² 值越高，置信度越高
            double confidence = rSquared;

            String description = String.format(
                    "检测到%s趋势：斜率 %.4f，R² = %.4f，从 %.4f 变化到 %.4f",
                    trendDirection, slope, rSquared,
                    dataPoints.get(0).getDoubleValue(),
                    dataPoints.get(n - 1).getDoubleValue()
            );

            log.debug("[TrendDetector] 检测到异常: {}", description);
            return PatternDetectionResult.detected(PATTERN_TYPE, description, confidence, details);
        }

        return PatternDetectionResult.notDetected(PATTERN_TYPE);
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

    /**
     * 获取字符串参数
     */
    private String getStringParam(Map<String, Object> params, String key, String defaultValue) {
        if (params == null || !params.containsKey(key)) {
            return defaultValue;
        }
        Object value = params.get(key);
        return value != null ? String.valueOf(value) : defaultValue;
    }
}
