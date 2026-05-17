package cn.iocoder.yudao.module.alarm.service.rule.pattern;

import java.util.List;
import java.util.Map;

/**
 * 模式检测器接口
 *
 * <p>采用策略模式，支持多种异常模式检测算法</p>
 *
 * @author 告警管理模块
 */
public interface PatternDetector {

    /**
     * 获取检测器支持的模式类型
     *
     * @return 模式类型标识
     */
    String getPatternType();

    /**
     * 执行模式检测
     *
     * @param dataPoints 时序数据点列表（按时间升序排列）
     * @param params     检测参数（如阈值、窗口大小等）
     * @return 检测结果
     */
    PatternDetectionResult detect(List<DataPoint> dataPoints, Map<String, Object> params);

    /**
     * 获取检测器描述
     *
     * @return 检测器描述
     */
    default String getDescription() {
        return getPatternType() + " 模式检测器";
    }

    /**
     * 获取默认参数
     *
     * @return 默认参数映射
     */
    default Map<String, Object> getDefaultParams() {
        return Map.of();
    }
}
