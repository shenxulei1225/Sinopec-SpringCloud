package cn.iocoder.yudao.module.alarm.framework.cache;

import cn.iocoder.yudao.module.alarm.service.rule.pattern.DataPoint;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 设备数据缓存服务
 *
 * <p>使用 Redis 实现滑动窗口缓存，存储设备历史数据供模式检测使用</p>
 *
 * <p>缓存结构：
 * <ul>
 *   <li>Key: alarm:device:data:{deviceId}:{fieldCode}</li>
 *   <li>Value: JSON 数组，包含时间戳和数值</li>
 *   <li>过期时间: 默认 1 小时</li>
 * </ul>
 * </p>
 *
 * @author 告警管理模块
 */
@Slf4j
@Service
public class DeviceDataCacheService {

    /**
     * 缓存 Key 前缀
     */
    private static final String CACHE_KEY_PREFIX = "alarm:device:data:";

    /**
     * 默认缓存过期时间（秒）- 1 小时
     */
    private static final int DEFAULT_EXPIRE_SECONDS = 3600;

    /**
     * 默认最大数据点数量
     */
    private static final int DEFAULT_MAX_DATA_POINTS = 100;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 添加数据点到缓存
     *
     * @param deviceId  设备 ID
     * @param fieldCode 字段编码
     * @param value     数值
     */
    public void addDataPoint(String deviceId, String fieldCode, BigDecimal value) {
        addDataPoint(deviceId, fieldCode, value, LocalDateTime.now());
    }

    /**
     * 添加数据点到缓存
     *
     * @param deviceId  设备 ID
     * @param fieldCode 字段编码
     * @param value     数值
     * @param timestamp 时间戳
     */
    public void addDataPoint(String deviceId, String fieldCode, BigDecimal value, LocalDateTime timestamp) {
        String cacheKey = buildCacheKey(deviceId, fieldCode);

        try {
            // 获取现有数据
            List<CachedDataPoint> dataPoints = getDataPointsInternal(cacheKey);

            // 添加新数据点
            CachedDataPoint newPoint = new CachedDataPoint(
                    timestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                    value.doubleValue()
            );
            dataPoints.add(newPoint);

            // 限制数据点数量（保留最新的）
            if (dataPoints.size() > DEFAULT_MAX_DATA_POINTS) {
                dataPoints = dataPoints.subList(dataPoints.size() - DEFAULT_MAX_DATA_POINTS, dataPoints.size());
            }

            // 保存到缓存
            String json = objectMapper.writeValueAsString(dataPoints);
            stringRedisTemplate.opsForValue().set(cacheKey, json, DEFAULT_EXPIRE_SECONDS, TimeUnit.SECONDS);

            log.debug("[DeviceDataCacheService] 添加数据点: deviceId={}, fieldCode={}, value={}", 
                    deviceId, fieldCode, value);
        } catch (JsonProcessingException e) {
            log.error("[DeviceDataCacheService] 序列化数据点失败: {}", e.getMessage());
        }
    }

    /**
     * 批量添加数据点
     *
     * @param deviceId  设备 ID
     * @param fieldCode 字段编码
     * @param values    数值映射（时间戳毫秒 -> 数值）
     */
    public void addDataPoints(String deviceId, String fieldCode, Map<Long, BigDecimal> values) {
        if (values == null || values.isEmpty()) {
            return;
        }

        String cacheKey = buildCacheKey(deviceId, fieldCode);

        try {
            // 获取现有数据
            List<CachedDataPoint> dataPoints = getDataPointsInternal(cacheKey);

            // 添加新数据点
            for (Map.Entry<Long, BigDecimal> entry : values.entrySet()) {
                dataPoints.add(new CachedDataPoint(entry.getKey(), entry.getValue().doubleValue()));
            }

            // 按时间排序
            dataPoints.sort((a, b) -> Long.compare(a.timestamp, b.timestamp));

            // 限制数据点数量
            if (dataPoints.size() > DEFAULT_MAX_DATA_POINTS) {
                dataPoints = dataPoints.subList(dataPoints.size() - DEFAULT_MAX_DATA_POINTS, dataPoints.size());
            }

            // 保存到缓存
            String json = objectMapper.writeValueAsString(dataPoints);
            stringRedisTemplate.opsForValue().set(cacheKey, json, DEFAULT_EXPIRE_SECONDS, TimeUnit.SECONDS);

            log.debug("[DeviceDataCacheService] 批量添加数据点: deviceId={}, fieldCode={}, count={}", 
                    deviceId, fieldCode, values.size());
        } catch (JsonProcessingException e) {
            log.error("[DeviceDataCacheService] 序列化数据点失败: {}", e.getMessage());
        }
    }

    /**
     * 获取数据点列表
     *
     * @param deviceId  设备 ID
     * @param fieldCode 字段编码
     * @return 数据点列表（按时间升序）
     */
    public List<DataPoint> getDataPoints(String deviceId, String fieldCode) {
        String cacheKey = buildCacheKey(deviceId, fieldCode);
        List<CachedDataPoint> cachedPoints = getDataPointsInternal(cacheKey);

        List<DataPoint> result = new ArrayList<>();
        for (CachedDataPoint cp : cachedPoints) {
            result.add(new DataPoint(cp.timestamp, cp.value));
        }
        return result;
    }

    /**
     * 获取指定时间窗口内的数据点
     *
     * @param deviceId       设备 ID
     * @param fieldCode      字段编码
     * @param timeWindowSecs 时间窗口（秒）
     * @return 数据点列表（按时间升序）
     */
    public List<DataPoint> getDataPointsInWindow(String deviceId, String fieldCode, int timeWindowSecs) {
        List<DataPoint> allPoints = getDataPoints(deviceId, fieldCode);
        if (allPoints.isEmpty()) {
            return allPoints;
        }

        long cutoffTime = System.currentTimeMillis() - (timeWindowSecs * 1000L);
        List<DataPoint> result = new ArrayList<>();
        
        for (DataPoint dp : allPoints) {
            long pointTime = dp.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            if (pointTime >= cutoffTime) {
                result.add(dp);
            }
        }
        
        return result;
    }

    /**
     * 获取最近 N 个数据点
     *
     * @param deviceId  设备 ID
     * @param fieldCode 字段编码
     * @param count     数量
     * @return 数据点列表（按时间升序）
     */
    public List<DataPoint> getRecentDataPoints(String deviceId, String fieldCode, int count) {
        List<DataPoint> allPoints = getDataPoints(deviceId, fieldCode);
        if (allPoints.size() <= count) {
            return allPoints;
        }
        return allPoints.subList(allPoints.size() - count, allPoints.size());
    }

    /**
     * 清除设备字段的缓存数据
     *
     * @param deviceId  设备 ID
     * @param fieldCode 字段编码
     */
    public void clearCache(String deviceId, String fieldCode) {
        String cacheKey = buildCacheKey(deviceId, fieldCode);
        stringRedisTemplate.delete(cacheKey);
        log.debug("[DeviceDataCacheService] 清除缓存: deviceId={}, fieldCode={}", deviceId, fieldCode);
    }

    /**
     * 构建缓存 Key
     */
    private String buildCacheKey(String deviceId, String fieldCode) {
        return CACHE_KEY_PREFIX + deviceId + ":" + fieldCode;
    }

    /**
     * 从缓存获取数据点（内部方法）
     */
    private List<CachedDataPoint> getDataPointsInternal(String cacheKey) {
        try {
            String json = stringRedisTemplate.opsForValue().get(cacheKey);
            if (json != null && !json.isEmpty()) {
                return objectMapper.readValue(json, new TypeReference<List<CachedDataPoint>>() {});
            }
        } catch (JsonProcessingException e) {
            log.error("[DeviceDataCacheService] 反序列化数据点失败: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * 缓存数据点（内部类）
     */
    private static class CachedDataPoint {
        public long timestamp;
        public double value;

        public CachedDataPoint() {}

        public CachedDataPoint(long timestamp, double value) {
            this.timestamp = timestamp;
            this.value = value;
        }
    }
}
