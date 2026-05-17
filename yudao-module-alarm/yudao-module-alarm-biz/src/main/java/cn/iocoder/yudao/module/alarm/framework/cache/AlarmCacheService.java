package cn.iocoder.yudao.module.alarm.framework.cache;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.alarm.AlarmCountByLevelVO;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.alarm.TodayAlarmStatisticsVO;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.statistics.AlarmStatisticsRespVO;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.type.AlarmTypeCategoryVO;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.type.AlarmTypeEntityVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.module.alarm.framework.cache.AlarmCacheConstants.*;

/**
 * 告警缓存服务
 * 
 * <p>提供告警模块的缓存操作，包括：
 * <ul>
 *   <li>实时统计数据缓存</li>
 *   <li>告警类型树缓存</li>
 *   <li>告警规则缓存</li>
 * </ul>
 * </p>
 *
 * @author 告警管理模块
 */
@Service
@Slf4j
public class AlarmCacheService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // ========== 统计数据缓存 ==========

    /**
     * 获取实时告警统计缓存
     */
    public AlarmStatisticsRespVO getRealTimeStatisticsCache() {
        try {
            String json = stringRedisTemplate.opsForValue().get(ALARM_REALTIME_STATISTICS_KEY);
            if (StrUtil.isNotBlank(json)) {
                return JSONUtil.toBean(json, AlarmStatisticsRespVO.class);
            }
        } catch (Exception e) {
            log.warn("[getRealTimeStatisticsCache] 获取缓存失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 设置实时告警统计缓存
     */
    public void setRealTimeStatisticsCache(AlarmStatisticsRespVO statistics) {
        try {
            String json = JSONUtil.toJsonStr(statistics);
            stringRedisTemplate.opsForValue().set(ALARM_REALTIME_STATISTICS_KEY, json, 
                    STATISTICS_CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            log.debug("[setRealTimeStatisticsCache] 设置缓存成功");
        } catch (Exception e) {
            log.warn("[setRealTimeStatisticsCache] 设置缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 获取今日告警统计缓存
     */
    public TodayAlarmStatisticsVO getTodayStatisticsCache() {
        try {
            String json = stringRedisTemplate.opsForValue().get(ALARM_TODAY_STATISTICS_KEY);
            if (StrUtil.isNotBlank(json)) {
                return JSONUtil.toBean(json, TodayAlarmStatisticsVO.class);
            }
        } catch (Exception e) {
            log.warn("[getTodayStatisticsCache] 获取缓存失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 设置今日告警统计缓存
     */
    public void setTodayStatisticsCache(TodayAlarmStatisticsVO statistics) {
        try {
            String json = JSONUtil.toJsonStr(statistics);
            stringRedisTemplate.opsForValue().set(ALARM_TODAY_STATISTICS_KEY, json, 
                    STATISTICS_CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            log.debug("[setTodayStatisticsCache] 设置缓存成功");
        } catch (Exception e) {
            log.warn("[setTodayStatisticsCache] 设置缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 获取告警级别统计缓存
     */
    public AlarmCountByLevelVO getLevelCountCache() {
        try {
            String json = stringRedisTemplate.opsForValue().get(ALARM_LEVEL_COUNT_KEY);
            if (StrUtil.isNotBlank(json)) {
                return JSONUtil.toBean(json, AlarmCountByLevelVO.class);
            }
        } catch (Exception e) {
            log.warn("[getLevelCountCache] 获取缓存失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 设置告警级别统计缓存
     */
    public void setLevelCountCache(AlarmCountByLevelVO levelCount) {
        try {
            String json = JSONUtil.toJsonStr(levelCount);
            stringRedisTemplate.opsForValue().set(ALARM_LEVEL_COUNT_KEY, json, 
                    STATISTICS_CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            log.debug("[setLevelCountCache] 设置缓存成功");
        } catch (Exception e) {
            log.warn("[setLevelCountCache] 设置缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 清除统计缓存
     * 
     * <p>在告警状态变更时调用，确保统计数据及时更新</p>
     */
    public void clearStatisticsCache() {
        try {
            stringRedisTemplate.delete(ALARM_REALTIME_STATISTICS_KEY);
            stringRedisTemplate.delete(ALARM_TODAY_STATISTICS_KEY);
            stringRedisTemplate.delete(ALARM_LEVEL_COUNT_KEY);
            log.debug("[clearStatisticsCache] 清除统计缓存成功");
        } catch (Exception e) {
            log.warn("[clearStatisticsCache] 清除缓存失败: {}", e.getMessage());
        }
    }

    // ========== 告警类型缓存 ==========

    /**
     * 获取告警类型树缓存
     */
    @SuppressWarnings("unchecked")
    public List<AlarmTypeCategoryVO> getAlarmTypeTreeCache() {
        try {
            String json = stringRedisTemplate.opsForValue().get(ALARM_TYPE_TREE_KEY);
            if (StrUtil.isNotBlank(json)) {
                return JSONUtil.toList(json, AlarmTypeCategoryVO.class);
            }
        } catch (Exception e) {
            log.warn("[getAlarmTypeTreeCache] 获取缓存失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 设置告警类型树缓存
     */
    public void setAlarmTypeTreeCache(List<AlarmTypeCategoryVO> tree) {
        try {
            String json = JSONUtil.toJsonStr(tree);
            stringRedisTemplate.opsForValue().set(ALARM_TYPE_TREE_KEY, json, 
                    ALARM_TYPE_CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            log.debug("[setAlarmTypeTreeCache] 设置缓存成功");
        } catch (Exception e) {
            log.warn("[setAlarmTypeTreeCache] 设置缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 获取告警类型路径缓存
     */
    public String getAlarmTypePathCache(Long entityId) {
        try {
            String key = ALARM_TYPE_PATH_KEY_PREFIX + entityId;
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.warn("[getAlarmTypePathCache] 获取缓存失败: entityId={}, error={}", entityId, e.getMessage());
        }
        return null;
    }

    /**
     * 设置告警类型路径缓存
     */
    public void setAlarmTypePathCache(Long entityId, String path) {
        try {
            String key = ALARM_TYPE_PATH_KEY_PREFIX + entityId;
            stringRedisTemplate.opsForValue().set(key, path, 
                    ALARM_TYPE_CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            log.debug("[setAlarmTypePathCache] 设置缓存成功: entityId={}", entityId);
        } catch (Exception e) {
            log.warn("[setAlarmTypePathCache] 设置缓存失败: entityId={}, error={}", entityId, e.getMessage());
        }
    }

    /**
     * 获取告警类型实体缓存
     */
    public AlarmTypeEntityVO getAlarmTypeEntityCache(Long entityId) {
        try {
            String key = ALARM_TYPE_ENTITY_KEY_PREFIX + entityId;
            String json = stringRedisTemplate.opsForValue().get(key);
            if (StrUtil.isNotBlank(json)) {
                return JSONUtil.toBean(json, AlarmTypeEntityVO.class);
            }
        } catch (Exception e) {
            log.warn("[getAlarmTypeEntityCache] 获取缓存失败: entityId={}, error={}", entityId, e.getMessage());
        }
        return null;
    }

    /**
     * 设置告警类型实体缓存
     */
    public void setAlarmTypeEntityCache(Long entityId, AlarmTypeEntityVO entity) {
        try {
            String key = ALARM_TYPE_ENTITY_KEY_PREFIX + entityId;
            String json = JSONUtil.toJsonStr(entity);
            stringRedisTemplate.opsForValue().set(key, json, 
                    ALARM_TYPE_CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            log.debug("[setAlarmTypeEntityCache] 设置缓存成功: entityId={}", entityId);
        } catch (Exception e) {
            log.warn("[setAlarmTypeEntityCache] 设置缓存失败: entityId={}, error={}", entityId, e.getMessage());
        }
    }

    /**
     * 清除告警类型缓存
     */
    public void clearAlarmTypeCache() {
        try {
            stringRedisTemplate.delete(ALARM_TYPE_TREE_KEY);
            // 注意：路径和实体缓存使用前缀，这里只清除树缓存
            // 路径和实体缓存会自动过期
            log.debug("[clearAlarmTypeCache] 清除告警类型树缓存成功");
        } catch (Exception e) {
            log.warn("[clearAlarmTypeCache] 清除缓存失败: {}", e.getMessage());
        }
    }

}
