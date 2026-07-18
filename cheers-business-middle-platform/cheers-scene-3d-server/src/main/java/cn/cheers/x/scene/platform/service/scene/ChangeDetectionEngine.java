package cn.cheers.x.scene.platform.service.scene;

import cn.cheers.x.scene.platform.model.RuntimePacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 变化检测引擎 - 阈值判断是否触发推送
 * 根据 Actor 类型和数据指标设定不同的变化阈值
 * 只有变化超过阈值时才触发推送，避免无效数据
 *
 * @author Sinopec
 */
@Slf4j
@Service
public class ChangeDetectionEngine {

    /**
     * 变化阈值配置
     * key: "actorType.metric"
     * value: 阈值（百分比或绝对值）
     */
    private final Map<String, Double> thresholds = new ConcurrentHashMap<>();

    /**
     * 上次推送的值
     * key: "sceneCode:instanceId:metric"
     * value: 上次推送的值
     */
    private final Map<String, Double> lastPushedValues = new ConcurrentHashMap<>();

    /**
     * 初始化阈值配置
     */
    public ChangeDetectionEngine() {
        // 仪表数据阈值（温度、压力、流量等）
        thresholds.put("gauge.temperature", 2.0);    // 温度变化超过 2% 推送
        thresholds.put("gauge.pressure", 1.5);        // 压力变化超过 1.5% 推送
        thresholds.put("gauge.flow", 3.0);            // 流量变化超过 3% 推送
        thresholds.put("gauge.level", 1.0);           // 液位变化超过 1% 推送

        // 巡检设备位置阈值（米）
        thresholds.put("inspector.x", 0.5);           // X 坐标变化超过 0.5 米推送
        thresholds.put("inspector.y", 0.5);           // Y 坐标变化超过 0.5 米推送
        thresholds.put("inspector.z", 0.3);           // Z 坐标变化超过 0.3 米推送

        // 阀门开度阈值（百分比）
        thresholds.put("valve.opening", 5.0);         // 开度变化超过 5% 推送

        // 管道数据阈值
        thresholds.put("pipe.temperature", 3.0);      // 管道温度变化超过 3% 推送
        thresholds.put("pipe.pressure", 2.0);         // 管道压力变化超过 2% 推送
    }

    /**
     * 检测是否需要推送
     *
     * @param packet 运行时数据包
     * @return true-需要推送, false-不需要推送
     */
    public boolean shouldPush(RuntimePacket packet) {
        if (packet.getTriggered() != null && packet.getTriggered()) {
            // 强制推送（如前端主动请求、关键报警）
            return true;
        }

        String metricKey = packet.getActorType() + "." + packet.getMetric();
        Double threshold = thresholds.get(metricKey);

        if (threshold == null) {
            // 未配置阈值的指标，默认推送
            log.debug("[ChangeDetection] 未配置阈值，默认推送, actorType={}, metric={}",
                    packet.getActorType(), packet.getMetric());
            return true;
        }

        String historyKey = packet.getSceneCode() + ":" + packet.getInstanceId() + ":" + packet.getMetric();
        Double lastValue = lastPushedValues.get(historyKey);

        if (lastValue == null) {
            // 首次推送，记录并返回 true
            lastPushedValues.put(historyKey, packet.getValue());
            log.debug("[ChangeDetection] 首次推送，记录初始值, actorType={}, metric={}, value={}",
                    packet.getActorType(), packet.getMetric(), packet.getValue());
            return true;
        }

        // 计算变化百分比
        double absValue = Math.abs(packet.getValue());
        double changePercent;
        if (absValue < 0.001) {
            // 值接近 0 时，使用绝对差值
            changePercent = Math.abs(packet.getValue() - lastValue);
        } else {
            // 使用百分比变化
            changePercent = Math.abs(packet.getValue() - lastValue) / absValue * 100;
        }

        boolean shouldPush = changePercent >= threshold;

        if (shouldPush) {
            // 更新上次推送的值
            lastPushedValues.put(historyKey, packet.getValue());
            log.debug("[ChangeDetection] 变化超过阈值，触发推送, key={}, change={:.2f}%, threshold={}",
                    metricKey, changePercent, threshold);
        } else {
            log.debug("[ChangeDetection] 变化未超过阈值，跳过推送, key={}, change={:.2f}%, threshold={}",
                    metricKey, changePercent, threshold);
        }

        return shouldPush;
    }

    /**
     * 强制推送（重置状态）
     *
     * @param packet 运行时数据包
     */
    public void forcePush(RuntimePacket packet) {
        String historyKey = packet.getSceneCode() + ":" + packet.getInstanceId() + ":" + packet.getMetric();
        lastPushedValues.put(historyKey, packet.getValue());
        log.info("[ChangeDetection] 强制推送，重置状态, actorType={}, metric={}",
                packet.getActorType(), packet.getMetric());
    }

    /**
     * 获取阈值配置
     *
     * @param actorType Actor 类型
     * @param metric    数据指标
     * @return 阈值
     */
    public Double getThreshold(String actorType, String metric) {
        String key = actorType + "." + metric;
        return thresholds.get(key);
    }

    /**
     * 设置阈值配置
     *
     * @param actorType Actor 类型
     * @param metric    数据指标
     * @param threshold 阈值
     */
    public void setThreshold(String actorType, String metric, Double threshold) {
        String key = actorType + "." + metric;
        thresholds.put(key, threshold);
        log.info("[ChangeDetection] 更新阈值配置, key={}, threshold={}", key, threshold);
    }

    /**
     * 清理指定 Actor 的历史数据
     *
     * @param sceneCode  场景编码
     * @param instanceId Actor 实例 ID
     */
    public void clearHistory(String sceneCode, Long instanceId) {
        lastPushedValues.entrySet().removeIf(entry ->
                entry.getKey().startsWith(sceneCode + ":" + instanceId + ":"));
        log.debug("[ChangeDetection] 清理 Actor 历史数据, sceneCode={}, instanceId={}",
                sceneCode, instanceId);
    }
}