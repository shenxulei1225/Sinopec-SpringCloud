package cn.cheers.x.alarm.service.rule.pattern;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 周期性波动检测器
 *
 * <p>检测数据是否存在周期性波动模式</p>
 *
 * <p>算法说明：
 * <ul>
 *   <li>使用自相关函数（ACF）检测周期性</li>
 *   <li>计算不同滞后期的自相关系数</li>
 *   <li>当某个滞后期的自相关系数超过阈值时，判定存在周期性</li>
 *   <li>简化实现：检测数据的峰谷交替模式</li>
 * </ul>
 * </p>
 *
 * @author 告警管理模块
 */
@Slf4j
@Component
public class PeriodicFluctuationDetector implements PatternDetector {

    public static final String PATTERN_TYPE = "PERIODIC_FLUCTUATION";

    /**
     * 默认最小周期数
     */
    private static final int DEFAULT_MIN_PERIODS = 2;

    /**
     * 默认波动幅度阈值（相对于均值的百分比）
     */
    private static final double DEFAULT_AMPLITUDE_THRESHOLD = 0.1;

    /**
     * 最小数据点数量
     */
    private static final int MIN_DATA_POINTS = 6;

    @Override
    public String getPatternType() {
        return PATTERN_TYPE;
    }

    @Override
    public String getDescription() {
        return "周期性波动检测器 - 检测数据的周期性波动模式";
    }

    @Override
    public Map<String, Object> getDefaultParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("minPeriods", DEFAULT_MIN_PERIODS);
        params.put("amplitudeThreshold", DEFAULT_AMPLITUDE_THRESHOLD);
        return params;
    }

    @Override
    public PatternDetectionResult detect(List<DataPoint> dataPoints, Map<String, Object> params) {
        if (dataPoints == null || dataPoints.size() < MIN_DATA_POINTS) {
            return PatternDetectionResult.notDetected(PATTERN_TYPE);
        }

        // 获取参数
        int minPeriods = getIntParam(params, "minPeriods", DEFAULT_MIN_PERIODS);
        double amplitudeThreshold = getDoubleParam(params, "amplitudeThreshold", DEFAULT_AMPLITUDE_THRESHOLD);

        // 计算均值
        double sum = 0;
        for (DataPoint dp : dataPoints) {
            sum += dp.getDoubleValue();
        }
        double mean = sum / dataPoints.size();

        // 如果均值接近 0，使用绝对阈值
        double effectiveThreshold = Math.abs(mean) > 1e-10 
                ? Math.abs(mean * amplitudeThreshold) 
                : amplitudeThreshold;

        // 检测峰谷点
        List<Integer> peakIndices = new ArrayList<>();
        List<Integer> valleyIndices = new ArrayList<>();

        for (int i = 1; i < dataPoints.size() - 1; i++) {
            double prev = dataPoints.get(i - 1).getDoubleValue();
            double curr = dataPoints.get(i).getDoubleValue();
            double next = dataPoints.get(i + 1).getDoubleValue();

            // 检测峰值
            if (curr > prev && curr > next && Math.abs(curr - mean) > effectiveThreshold) {
                peakIndices.add(i);
            }
            // 检测谷值
            if (curr < prev && curr < next && Math.abs(curr - mean) > effectiveThreshold) {
                valleyIndices.add(i);
            }
        }

        // 计算周期数（峰谷交替的次数）
        int periodCount = Math.min(peakIndices.size(), valleyIndices.size());

        // 检测周期是否规律
        boolean isRegularPeriod = false;
        double avgPeriodLength = 0;
        double periodVariance = 0;

        if (periodCount >= minPeriods && peakIndices.size() >= 2) {
            // 计算峰值之间的间隔
            List<Integer> peakIntervals = new ArrayList<>();
            for (int i = 1; i < peakIndices.size(); i++) {
                peakIntervals.add(peakIndices.get(i) - peakIndices.get(i - 1));
            }

            if (!peakIntervals.isEmpty()) {
                // 计算平均周期长度
                int sumIntervals = 0;
                for (int interval : peakIntervals) {
                    sumIntervals += interval;
                }
                avgPeriodLength = (double) sumIntervals / peakIntervals.size();

                // 计算周期长度的方差
                double sumSquaredDiff = 0;
                for (int interval : peakIntervals) {
                    double diff = interval - avgPeriodLength;
                    sumSquaredDiff += diff * diff;
                }
                periodVariance = sumSquaredDiff / peakIntervals.size();

                // 如果方差较小，说明周期比较规律
                // 变异系数（CV）< 0.3 认为是规律的
                double cv = avgPeriodLength > 0 ? Math.sqrt(periodVariance) / avgPeriodLength : 1;
                isRegularPeriod = cv < 0.3;
            }
        }

        if (periodCount >= minPeriods && isRegularPeriod) {
            // 计算波动幅度
            double maxValue = Double.MIN_VALUE;
            double minValue = Double.MAX_VALUE;
            for (DataPoint dp : dataPoints) {
                double v = dp.getDoubleValue();
                maxValue = Math.max(maxValue, v);
                minValue = Math.min(minValue, v);
            }
            double amplitude = maxValue - minValue;

            Map<String, Object> details = new HashMap<>();
            details.put("periodCount", periodCount);
            details.put("avgPeriodLength", avgPeriodLength);
            details.put("periodVariance", periodVariance);
            details.put("peakCount", peakIndices.size());
            details.put("valleyCount", valleyIndices.size());
            details.put("amplitude", amplitude);
            details.put("mean", mean);
            details.put("dataPointCount", dataPoints.size());

            // 置信度：周期数越多且越规律，置信度越高
            double regularityScore = 1 - Math.min(1, Math.sqrt(periodVariance) / avgPeriodLength);
            double confidence = Math.min(1.0, (periodCount / (double) (minPeriods * 2)) * regularityScore);

            String description = String.format(
                    "检测到周期性波动：%d 个周期，平均周期长度 %.1f，波动幅度 %.4f",
                    periodCount, avgPeriodLength, amplitude
            );

            log.debug("[PeriodicFluctuationDetector] 检测到异常: {}", description);
            return PatternDetectionResult.detected(PATTERN_TYPE, description, confidence, details);
        }

        return PatternDetectionResult.notDetected(PATTERN_TYPE);
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
