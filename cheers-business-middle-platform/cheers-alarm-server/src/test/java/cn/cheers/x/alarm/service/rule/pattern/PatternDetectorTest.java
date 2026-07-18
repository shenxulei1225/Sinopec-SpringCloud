package cn.cheers.x.alarm.service.rule.pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 模式检测器单元测试
 *
 * @author 告警管理模块
 */
class PatternDetectorTest {

    // ==================== 连续相同值检测器测试 ====================

    @Test
    @DisplayName("连续相同值检测 - 检测到连续相同值")
    void testConsecutiveSameDetector_detected() {
        ConsecutiveSameDetector detector = new ConsecutiveSameDetector();
        
        // 构造测试数据：连续 6 个相同值
        List<DataPoint> dataPoints = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 10; i++) {
            double value = (i >= 2 && i <= 7) ? 25.0 : 20.0 + i; // 索引 2-7 是连续相同值
            dataPoints.add(new DataPoint(now.plusMinutes(i), value));
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("count", 5); // 阈值设为 5
        
        PatternDetectionResult result = detector.detect(dataPoints, params);
        
        assertTrue(result.isDetected(), "应该检测到连续相同值");
        assertEquals("CONSECUTIVE_SAME", result.getPatternType());
        assertNotNull(result.getDetails());
        assertEquals(6, result.getDetails().get("consecutiveCount"));
    }

    @Test
    @DisplayName("连续相同值检测 - 未检测到")
    void testConsecutiveSameDetector_notDetected() {
        ConsecutiveSameDetector detector = new ConsecutiveSameDetector();
        
        // 构造测试数据：没有连续相同值
        List<DataPoint> dataPoints = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 10; i++) {
            dataPoints.add(new DataPoint(now.plusMinutes(i), 20.0 + i));
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("count", 5);
        
        PatternDetectionResult result = detector.detect(dataPoints, params);
        
        assertFalse(result.isDetected(), "不应该检测到连续相同值");
    }

    // ==================== 突变检测器测试 ====================

    @Test
    @DisplayName("突变检测 - 检测到突变")
    void testSuddenChangeDetector_detected() {
        SuddenChangeDetector detector = new SuddenChangeDetector();
        
        // 构造测试数据：正常值 + 一个突变值
        // 需要确保突变值偏离均值超过 3 个标准差
        List<DataPoint> dataPoints = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        // 前 9 个正常值（均值 25，标准差约 0.1）
        for (int i = 0; i < 9; i++) {
            dataPoints.add(new DataPoint(now.plusMinutes(i), 25.0));
        }
        // 最后一个是突变值（需要远超 3σ）
        dataPoints.add(new DataPoint(now.plusMinutes(9), 50.0)); // 偏离 25 个单位
        
        Map<String, Object> params = new HashMap<>();
        params.put("sigmaMultiplier", 3.0);
        
        PatternDetectionResult result = detector.detect(dataPoints, params);
        
        assertTrue(result.isDetected(), "应该检测到突变");
        assertEquals("SUDDEN_CHANGE", result.getPatternType());
        assertNotNull(result.getDetails());
    }

    @Test
    @DisplayName("突变检测 - 未检测到")
    void testSuddenChangeDetector_notDetected() {
        SuddenChangeDetector detector = new SuddenChangeDetector();
        
        // 构造测试数据：所有值都在正常范围内
        List<DataPoint> dataPoints = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 10; i++) {
            dataPoints.add(new DataPoint(now.plusMinutes(i), 25.0 + (i % 2 == 0 ? 0.5 : -0.5)));
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("sigmaMultiplier", 3.0);
        
        PatternDetectionResult result = detector.detect(dataPoints, params);
        
        assertFalse(result.isDetected(), "不应该检测到突变");
    }

    // ==================== 趋势检测器测试 ====================

    @Test
    @DisplayName("趋势检测 - 检测到上升趋势")
    void testTrendDetector_upTrend() {
        TrendDetector detector = new TrendDetector();
        
        // 构造测试数据：明显的上升趋势
        List<DataPoint> dataPoints = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 10; i++) {
            dataPoints.add(new DataPoint(now.plusMinutes(i), 20.0 + i * 2)); // 每次增加 2
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("slopeThreshold", 0.5);
        params.put("rSquaredThreshold", 0.8);
        params.put("direction", "UP");
        
        PatternDetectionResult result = detector.detect(dataPoints, params);
        
        assertTrue(result.isDetected(), "应该检测到上升趋势");
        assertEquals("TREND", result.getPatternType());
        assertEquals("上升", result.getDetails().get("direction"));
    }

    @Test
    @DisplayName("趋势检测 - 检测到下降趋势")
    void testTrendDetector_downTrend() {
        TrendDetector detector = new TrendDetector();
        
        // 构造测试数据：明显的下降趋势
        List<DataPoint> dataPoints = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 10; i++) {
            dataPoints.add(new DataPoint(now.plusMinutes(i), 50.0 - i * 2)); // 每次减少 2
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("slopeThreshold", 0.5);
        params.put("rSquaredThreshold", 0.8);
        params.put("direction", "DOWN");
        
        PatternDetectionResult result = detector.detect(dataPoints, params);
        
        assertTrue(result.isDetected(), "应该检测到下降趋势");
        assertEquals("TREND", result.getPatternType());
        assertEquals("下降", result.getDetails().get("direction"));
    }

    @Test
    @DisplayName("趋势检测 - 无明显趋势")
    void testTrendDetector_noTrend() {
        TrendDetector detector = new TrendDetector();
        
        // 构造测试数据：随机波动，无明显趋势
        List<DataPoint> dataPoints = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        double[] values = {25, 26, 24, 27, 23, 26, 24, 25, 26, 24};
        for (int i = 0; i < values.length; i++) {
            dataPoints.add(new DataPoint(now.plusMinutes(i), values[i]));
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("slopeThreshold", 0.5);
        params.put("rSquaredThreshold", 0.8);
        
        PatternDetectionResult result = detector.detect(dataPoints, params);
        
        assertFalse(result.isDetected(), "不应该检测到趋势");
    }

    // ==================== 周期性波动检测器测试 ====================

    @Test
    @DisplayName("周期性波动检测 - 检测到周期性")
    void testPeriodicFluctuationDetector_detected() {
        PeriodicFluctuationDetector detector = new PeriodicFluctuationDetector();
        
        // 构造测试数据：明显的周期性波动（正弦波形）
        List<DataPoint> dataPoints = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 20; i++) {
            // 周期为 5，振幅为 10
            double value = 50 + 10 * Math.sin(2 * Math.PI * i / 5);
            dataPoints.add(new DataPoint(now.plusMinutes(i), value));
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("minPeriods", 2);
        params.put("amplitudeThreshold", 0.1);
        
        PatternDetectionResult result = detector.detect(dataPoints, params);
        
        assertTrue(result.isDetected(), "应该检测到周期性波动");
        assertEquals("PERIODIC_FLUCTUATION", result.getPatternType());
    }

    @Test
    @DisplayName("周期性波动检测 - 未检测到")
    void testPeriodicFluctuationDetector_notDetected() {
        PeriodicFluctuationDetector detector = new PeriodicFluctuationDetector();
        
        // 构造测试数据：单调递增，无周期性
        List<DataPoint> dataPoints = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 20; i++) {
            dataPoints.add(new DataPoint(now.plusMinutes(i), 20.0 + i));
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("minPeriods", 2);
        
        PatternDetectionResult result = detector.detect(dataPoints, params);
        
        assertFalse(result.isDetected(), "不应该检测到周期性波动");
    }

    // ==================== 边界条件测试 ====================

    @Test
    @DisplayName("数据点不足时返回未检测")
    void testInsufficientDataPoints() {
        ConsecutiveSameDetector detector = new ConsecutiveSameDetector();
        
        // 只有 1 个数据点
        List<DataPoint> dataPoints = List.of(
                new DataPoint(LocalDateTime.now(), 25.0)
        );
        
        PatternDetectionResult result = detector.detect(dataPoints, Map.of());
        
        assertFalse(result.isDetected(), "数据点不足时不应该检测到异常");
    }

    @Test
    @DisplayName("空数据点列表返回未检测")
    void testEmptyDataPoints() {
        SuddenChangeDetector detector = new SuddenChangeDetector();
        
        PatternDetectionResult result = detector.detect(new ArrayList<>(), Map.of());
        
        assertFalse(result.isDetected(), "空数据点列表不应该检测到异常");
    }

    @Test
    @DisplayName("null 数据点列表返回未检测")
    void testNullDataPoints() {
        TrendDetector detector = new TrendDetector();
        
        PatternDetectionResult result = detector.detect(null, Map.of());
        
        assertFalse(result.isDetected(), "null 数据点列表不应该检测到异常");
    }
}
