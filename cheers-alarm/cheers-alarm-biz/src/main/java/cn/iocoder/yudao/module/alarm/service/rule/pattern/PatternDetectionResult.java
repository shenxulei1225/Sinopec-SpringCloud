package cn.iocoder.yudao.module.alarm.service.rule.pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 模式检测结果
 *
 * @author 告警管理模块
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatternDetectionResult {

    /**
     * 是否检测到异常模式
     */
    private boolean detected;

    /**
     * 模式类型
     */
    private String patternType;

    /**
     * 检测描述
     */
    private String description;

    /**
     * 置信度（0-1）
     */
    private double confidence;

    /**
     * 额外信息（如检测到的具体数值、统计信息等）
     */
    private Map<String, Object> details;

    /**
     * 创建未检测到异常的结果
     */
    public static PatternDetectionResult notDetected(String patternType) {
        return PatternDetectionResult.builder()
                .detected(false)
                .patternType(patternType)
                .description("未检测到异常模式")
                .confidence(0.0)
                .build();
    }

    /**
     * 创建检测到异常的结果
     */
    public static PatternDetectionResult detected(String patternType, String description, 
                                                   double confidence, Map<String, Object> details) {
        return PatternDetectionResult.builder()
                .detected(true)
                .patternType(patternType)
                .description(description)
                .confidence(confidence)
                .details(details)
                .build();
    }
}
