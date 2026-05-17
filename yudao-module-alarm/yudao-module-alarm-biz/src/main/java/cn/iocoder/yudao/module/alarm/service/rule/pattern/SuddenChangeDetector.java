package cn.iocoder.yudao.module.alarm.service.rule.pattern;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 突变检测器
 *
 * <p>基于 3σ 原则检测数据突变，识别异常跳变</p>
 *
 * <p>算法说明：
 * <ul>
 *   <li>计算数据的均值（μ）和标准差（σ）</li>
 *   <li>当某个数据点偏离均值超过 n 倍标准差时，判定为突变</li>
 *   <li>默认使用 3σ 原则（99.7% 置信区间）</li>
 * </ul>
 * </p>
 *
 * @author 告警管理模块
 */
@Slf4j
@Component
public class SuddenChangeDetector implements PatternDetector {

    public static final String PATTERN_TYPE = "SUDDEN_CHANGE";

    /**
     * 默认标准差倍数（3σ 原则）
     */
    private static final double DEFAULT_SIGMA_MULTIPLIER = 3.0;

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
        return "突变检测器 - 基于 3σ 原则检测数据异常跳变";
    }

    @Override
    public Map<String, Object> getDefaultParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("sigmaMultiplier", DEFAULT_SIGMA_MULTIPLIER);
        return params;
    }

    @Override
    public PatternDetectionResult detect(List<DataPoint> dataPoints, Map<String, Object> params) {
        if (dataPoints == null || dataPoints.size() < MIN_DATA_POINTS) {
            return PatternDetectionResult.notDetected(PATTERN_TYPE);
        }

        // 获取参数
        double sigmaMultiplier = getDoubleParam(params, "sigmaMultiplier", DEFAULT_SIGMA_MULTIPLIER);

        int n = dataPoints.size();
        
        // 使用除最新值外的历史数据计算均值和标准差
        // 这样可以判断最新值是否相对于历史数据发生突变
        int historySize = n - 1;
        if (historySize < MIN_DATA_POINTS - 1) {
            return PatternDetectionResult.notDetected(PATTERN_TYPE);
        }

        // 计算历史数据的均值
        double sum = 0;
        for (int i = 0; i < historySize; i++) {
            sum += dataPoints.get(i).getDoubleValue();
        }
        double mean = sum / historySize;

        // 计算历史数据的标准差
        double sumSquaredDiff = 0;
        for (int i = 0; i < historySize; i++) {
            double diff = dataPoints.get(i).getDoubleValue() - mean;
            sumSquaredDiff += diff * diff;
        }
        double stdDev = Math.sqrt(sumSquaredDiff / historySize);

        // 如果标准差为 0（所有历史值相同），使用绝对差值判断
        // 当历史值完全相同时，任何不同的值都应该被视为突变
        DataPoint latestPoint = dataPoints.get(n - 1);
        double latestValue = latestPoint.getDoubleValue();

        if (stdDev < 1e-10) {
            // 历史值完全相同，检查最新值是否不同
            double diff = Math.abs(latestValue - mean);
            if (diff > 1e-10) {
                // 最新值与历史值不同，视为突变
                Map<String, Object> details = new HashMap<>();
                details.put("latestValue", latestValue);
                details.put("mean", mean);
                details.put("stdDev", 0.0);
                details.put("absoluteDiff", diff);
                details.put("sigmaMultiplier", sigmaMultiplier);
                details.put("dataPointCount", n);
                details.put("historySize", historySize);

                String direction = latestValue > mean ? "上升" : "下降";
                String description = String.format(
                        "检测到数据突变（%s）：当前值 %.4f，历史均值 %.4f（历史值完全相同）",
                        direction, latestValue, mean
                );

                log.debug("[SuddenChangeDetector] 检测到异常: {}", description);
                return PatternDetectionResult.detected(PATTERN_TYPE, description, 1.0, details);
            }
            return PatternDetectionResult.notDetected(PATTERN_TYPE);
        }

        // 计算阈值
        double upperThreshold = mean + sigmaMultiplier * stdDev;
        double lowerThreshold = mean - sigmaMultiplier * stdDev;

        // 计算 Z-score
        double zScore = Math.abs(latestValue - mean) / stdDev;

        if (latestValue > upperThreshold || latestValue < lowerThreshold) {
            Map<String, Object> details = new HashMap<>();
            details.put("latestValue", latestValue);
            details.put("mean", mean);
            details.put("stdDev", stdDev);
            details.put("zScore", zScore);
            details.put("sigmaMultiplier", sigmaMultiplier);
            details.put("upperThreshold", upperThreshold);
            details.put("lowerThreshold", lowerThreshold);
            details.put("dataPointCount", dataPoints.size());

            // 置信度：Z-score 越大，置信度越高
            double confidence = Math.min(1.0, zScore / (sigmaMultiplier * 2));

            String direction = latestValue > upperThreshold ? "上升" : "下降";
            String description = String.format(
                    "检测到数据突变（%s）：当前值 %.4f，均值 %.4f，偏离 %.2f 个标准差",
                    direction, latestValue, mean, zScore
            );

            log.debug("[SuddenChangeDetector] 检测到异常: {}", description);
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
}
