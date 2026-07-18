/**
 * 模式检测算法包
 *
 * <p>提供多种异常模式检测算法，用于告警规则的 PATTERN 类型条件评估</p>
 *
 * <h2>支持的模式类型</h2>
 * <ul>
 *   <li>CONSECUTIVE_SAME - 连续相同值检测（传感器故障/数据卡死）</li>
 *   <li>SUDDEN_CHANGE - 突变检测（基于 3σ 原则）</li>
 *   <li>TREND - 趋势检测（基于线性回归）</li>
 *   <li>PERIODIC_FLUCTUATION - 周期性波动检测</li>
 * </ul>
 *
 * <h2>使用方式</h2>
 * <pre>
 * // 通过 PatternDetectorManager 获取检测器并执行检测
 * PatternDetectionResult result = patternDetectorManager.detect(
 *     "SUDDEN_CHANGE",
 *     dataPoints,
 *     Map.of("sigmaMultiplier", 3.0)
 * );
 * </pre>
 *
 * @author 告警管理模块
 */
package cn.cheers.x.alarm.service.rule.pattern;
