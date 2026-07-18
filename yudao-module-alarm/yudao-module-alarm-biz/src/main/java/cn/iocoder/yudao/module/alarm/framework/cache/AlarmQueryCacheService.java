package cn.iocoder.yudao.module.alarm.framework.cache;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.alarm.AlarmRespVO;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.alarm.AlarmDetailRespVO;
import cn.iocoder.yudao.module.alarm.dal.dataobject.AlarmRuleDO;
import cn.iocoder.yudao.module.alarm.dal.dataobject.LinkageRuleDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 告警查询缓存服务
 * 
 * <p>提供告警查询相关的缓存操作，优化查询性能：
 * <ul>
 *   <li>告警详情缓存 - 减少详情页重复查询</li>
 *   <li>启用规则缓存 - 减少规则匹配时的数据库查询</li>
 *   <li>联动规则缓存 - 减少联动执行时的数据库查询</li>
 *   <li>热点告警缓存 - 缓存高频访问的告警数据</li>
 * </ul>
 * </p>
 *
 * @author 告警管理模块
 */
@Service
@Slf4j
public class AlarmQueryCacheService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // ========== 告警详情缓存 ==========

    /**
     * 获取告警详情缓存
     *
     * @param alarmId 告警ID
     * @return 告警详情，缓存未命中返回 null
     */
    public AlarmDetailRespVO getAlarmDetailCache(Long alarmId) {
        try {
            String key = AlarmCacheConstants.ALARM_DETAIL_KEY_PREFIX + alarmId;
            String json = stringRedisTemplate.opsForValue().get(key);
            if (StrUtil.isNotBlank(json)) {
                log.debug("[getAlarmDetailCache] 缓存命中: alarmId={}", alarmId);
                return JSONUtil.toBean(json, AlarmDetailRespVO.class);
            }
        } catch (Exception e) {
            log.warn("[getAlarmDetailCache] 获取缓存失败: alarmId={}, error={}", alarmId, e.getMessage());
        }
        return null;
    }

    /**
     * 设置告警详情缓存
     *
     * @param alarmId 告警ID
     * @param detail  告警详情
     */
    public void setAlarmDetailCache(Long alarmId, AlarmDetailRespVO detail) {
        try {
            String key = AlarmCacheConstants.ALARM_DETAIL_KEY_PREFIX + alarmId;
            String json = JSONUtil.toJsonStr(detail);
            stringRedisTemplate.opsForValue().set(key, json, 
                    AlarmCacheConstants.ALARM_DETAIL_CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            log.debug("[setAlarmDetailCache] 设置缓存成功: alarmId={}", alarmId);
        } catch (Exception e) {
            log.warn("[setAlarmDetailCache] 设置缓存失败: alarmId={}, error={}", alarmId, e.getMessage());
        }
    }

    /**
     * 清除告警详情缓存
     * 
     * <p>在告警状态变更时调用</p>
     *
     * @param alarmId 告警ID
     */
    public void clearAlarmDetailCache(Long alarmId) {
        try {
            String key = AlarmCacheConstants.ALARM_DETAIL_KEY_PREFIX + alarmId;
            stringRedisTemplate.delete(key);
            log.debug("[clearAlarmDetailCache] 清除缓存成功: alarmId={}", alarmId);
        } catch (Exception e) {
            log.warn("[clearAlarmDetailCache] 清除缓存失败: alarmId={}, error={}", alarmId, e.getMessage());
        }
    }

    // ========== 启用规则缓存 ==========

    /**
     * 获取启用的告警规则缓存
     *
     * @return 启用的告警规则列表，缓存未命中返回 null
     */
    @SuppressWarnings("unchecked")
    public List<AlarmRuleDO> getEnabledAlarmRulesCache() {
        try {
            String json = stringRedisTemplate.opsForValue().get(AlarmCacheConstants.ALARM_RULES_ENABLED_KEY);
            if (StrUtil.isNotBlank(json)) {
                log.debug("[getEnabledAlarmRulesCache] 缓存命中");
                return JSONUtil.toList(json, AlarmRuleDO.class);
            }
        } catch (Exception e) {
            log.warn("[getEnabledAlarmRulesCache] 获取缓存失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 设置启用的告警规则缓存
     *
     * @param rules 启用的告警规则列表
     */
    public void setEnabledAlarmRulesCache(List<AlarmRuleDO> rules) {
        try {
            String json = JSONUtil.toJsonStr(rules);
            stringRedisTemplate.opsForValue().set(AlarmCacheConstants.ALARM_RULES_ENABLED_KEY, json, 
                    AlarmCacheConstants.RULES_CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            log.debug("[setEnabledAlarmRulesCache] 设置缓存成功: count={}", rules.size());
        } catch (Exception e) {
            log.warn("[setEnabledAlarmRulesCache] 设置缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 清除告警规则缓存
     */
    public void clearAlarmRulesCache() {
        try {
            stringRedisTemplate.delete(AlarmCacheConstants.ALARM_RULES_ENABLED_KEY);
            log.debug("[clearAlarmRulesCache] 清除缓存成功");
        } catch (Exception e) {
            log.warn("[clearAlarmRulesCache] 清除缓存失败: {}", e.getMessage());
        }
    }

    // ========== 联动规则缓存 ==========

    /**
     * 获取启用的联动规则缓存
     *
     * @return 启用的联动规则列表，缓存未命中返回 null
     */
    @SuppressWarnings("unchecked")
    public List<LinkageRuleDO> getEnabledLinkageRulesCache() {
        try {
            String json = stringRedisTemplate.opsForValue().get(AlarmCacheConstants.LINKAGE_RULES_ENABLED_KEY);
            if (StrUtil.isNotBlank(json)) {
                log.debug("[getEnabledLinkageRulesCache] 缓存命中");
                return JSONUtil.toList(json, LinkageRuleDO.class);
            }
        } catch (Exception e) {
            log.warn("[getEnabledLinkageRulesCache] 获取缓存失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 设置启用的联动规则缓存
     *
     * @param rules 启用的联动规则列表
     */
    public void setEnabledLinkageRulesCache(List<LinkageRuleDO> rules) {
        try {
            String json = JSONUtil.toJsonStr(rules);
            stringRedisTemplate.opsForValue().set(AlarmCacheConstants.LINKAGE_RULES_ENABLED_KEY, json, 
                    AlarmCacheConstants.RULES_CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            log.debug("[setEnabledLinkageRulesCache] 设置缓存成功: count={}", rules.size());
        } catch (Exception e) {
            log.warn("[setEnabledLinkageRulesCache] 设置缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 清除联动规则缓存
     */
    public void clearLinkageRulesCache() {
        try {
            stringRedisTemplate.delete(AlarmCacheConstants.LINKAGE_RULES_ENABLED_KEY);
            log.debug("[clearLinkageRulesCache] 清除缓存成功");
        } catch (Exception e) {
            log.warn("[clearLinkageRulesCache] 清除缓存失败: {}", e.getMessage());
        }
    }

    // ========== 热点告警缓存 ==========

    /**
     * 获取实时告警列表缓存
     * 
     * <p>用于首页快速展示实时告警</p>
     *
     * @return 实时告警列表，缓存未命中返回 null
     */
    @SuppressWarnings("unchecked")
    public List<AlarmRespVO> getRealTimeAlarmsCache() {
        try {
            String json = stringRedisTemplate.opsForValue().get(AlarmCacheConstants.ALARM_REALTIME_LIST_KEY);
            if (StrUtil.isNotBlank(json)) {
                log.debug("[getRealTimeAlarmsCache] 缓存命中");
                return JSONUtil.toList(json, AlarmRespVO.class);
            }
        } catch (Exception e) {
            log.warn("[getRealTimeAlarmsCache] 获取缓存失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 设置实时告警列表缓存
     *
     * @param alarms 实时告警列表
     */
    public void setRealTimeAlarmsCache(List<AlarmRespVO> alarms) {
        try {
            String json = JSONUtil.toJsonStr(alarms);
            stringRedisTemplate.opsForValue().set(AlarmCacheConstants.ALARM_REALTIME_LIST_KEY, json, 
                    AlarmCacheConstants.REALTIME_LIST_CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            log.debug("[setRealTimeAlarmsCache] 设置缓存成功: count={}", alarms.size());
        } catch (Exception e) {
            log.warn("[setRealTimeAlarmsCache] 设置缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 清除实时告警列表缓存
     * 
     * <p>在告警状态变更时调用</p>
     */
    public void clearRealTimeAlarmsCache() {
        try {
            stringRedisTemplate.delete(AlarmCacheConstants.ALARM_REALTIME_LIST_KEY);
            log.debug("[clearRealTimeAlarmsCache] 清除缓存成功");
        } catch (Exception e) {
            log.warn("[clearRealTimeAlarmsCache] 清除缓存失败: {}", e.getMessage());
        }
    }

    // ========== 批量清除缓存 ==========

    /**
     * 清除所有告警相关缓存
     * 
     * <p>在系统维护或数据同步时调用</p>
     */
    public void clearAllAlarmCache() {
        try {
            // 清除统计缓存
            Set<String> statisticsKeys = stringRedisTemplate.keys(AlarmCacheConstants.ALARM_STATISTICS_KEY + "*");
            if (CollUtil.isNotEmpty(statisticsKeys)) {
                stringRedisTemplate.delete(statisticsKeys);
            }
            
            // 清除详情缓存
            Set<String> detailKeys = stringRedisTemplate.keys(AlarmCacheConstants.ALARM_DETAIL_KEY_PREFIX + "*");
            if (CollUtil.isNotEmpty(detailKeys)) {
                stringRedisTemplate.delete(detailKeys);
            }
            
            // 清除规则缓存
            clearAlarmRulesCache();
            clearLinkageRulesCache();
            
            // 清除实时列表缓存
            clearRealTimeAlarmsCache();
            
            log.info("[clearAllAlarmCache] 清除所有告警缓存成功");
        } catch (Exception e) {
            log.error("[clearAllAlarmCache] 清除缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 告警状态变更时清除相关缓存
     * 
     * <p>在告警确认、处理、关闭时调用</p>
     *
     * @param alarmId 告警ID
     */
    public void onAlarmStatusChange(Long alarmId) {
        // 清除告警详情缓存
        clearAlarmDetailCache(alarmId);
        // 清除实时告警列表缓存
        clearRealTimeAlarmsCache();
        // 清除统计缓存（统计数据会变化）
        try {
            stringRedisTemplate.delete(AlarmCacheConstants.ALARM_REALTIME_STATISTICS_KEY);
            stringRedisTemplate.delete(AlarmCacheConstants.ALARM_TODAY_STATISTICS_KEY);
            stringRedisTemplate.delete(AlarmCacheConstants.ALARM_LEVEL_COUNT_KEY);
        } catch (Exception e) {
            log.warn("[onAlarmStatusChange] 清除统计缓存失败: {}", e.getMessage());
        }
    }

}
