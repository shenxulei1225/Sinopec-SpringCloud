package cn.cheers.x.alarm.service.rule.pattern;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模式检测器管理器
 *
 * <p>统一管理所有模式检测器，提供检测器查找和执行功能</p>
 *
 * @author 告警管理模块
 */
@Slf4j
@Component
public class PatternDetectorManager {

    @Resource
    private List<PatternDetector> detectors;

    /**
     * 检测器映射（patternType -> detector）
     */
    private final Map<String, PatternDetector> detectorMap = new HashMap<>();

    @PostConstruct
    public void init() {
        if (detectors != null) {
            for (PatternDetector detector : detectors) {
                detectorMap.put(detector.getPatternType(), detector);
                log.info("[PatternDetectorManager] 注册模式检测器: {} - {}", 
                        detector.getPatternType(), detector.getDescription());
            }
        }
        log.info("[PatternDetectorManager] 共注册 {} 个模式检测器", detectorMap.size());
    }

    /**
     * 获取检测器
     *
     * @param patternType 模式类型
     * @return 检测器，如果不存在返回 null
     */
    public PatternDetector getDetector(String patternType) {
        return detectorMap.get(patternType);
    }

    /**
     * 执行模式检测
     *
     * @param patternType 模式类型
     * @param dataPoints  数据点列表
     * @param params      检测参数
     * @return 检测结果
     */
    public PatternDetectionResult detect(String patternType, List<DataPoint> dataPoints, 
                                          Map<String, Object> params) {
        PatternDetector detector = getDetector(patternType);
        if (detector == null) {
            log.warn("[PatternDetectorManager] 未找到模式检测器: {}", patternType);
            return PatternDetectionResult.notDetected(patternType);
        }

        try {
            return detector.detect(dataPoints, params);
        } catch (Exception e) {
            log.error("[PatternDetectorManager] 模式检测异常: patternType={}, error={}", 
                    patternType, e.getMessage(), e);
            return PatternDetectionResult.notDetected(patternType);
        }
    }

    /**
     * 获取所有支持的模式类型
     *
     * @return 模式类型列表
     */
    public List<String> getSupportedPatternTypes() {
        return List.copyOf(detectorMap.keySet());
    }

    /**
     * 检查是否支持指定的模式类型
     *
     * @param patternType 模式类型
     * @return 是否支持
     */
    public boolean isSupported(String patternType) {
        return detectorMap.containsKey(patternType);
    }
}
