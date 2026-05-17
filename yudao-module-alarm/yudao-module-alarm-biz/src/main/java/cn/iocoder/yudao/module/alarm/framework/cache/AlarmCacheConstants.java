package cn.iocoder.yudao.module.alarm.framework.cache;

/**
 * 告警模块缓存常量
 *
 * @author 告警管理模块
 */
public interface AlarmCacheConstants {

    /**
     * 告警统计缓存 Key 前缀
     */
    String ALARM_STATISTICS_KEY = "alarm:statistics:";

    /**
     * 实时告警统计缓存 Key
     */
    String ALARM_REALTIME_STATISTICS_KEY = ALARM_STATISTICS_KEY + "realtime";

    /**
     * 今日告警统计缓存 Key
     */
    String ALARM_TODAY_STATISTICS_KEY = ALARM_STATISTICS_KEY + "today";

    /**
     * 告警级别统计缓存 Key
     */
    String ALARM_LEVEL_COUNT_KEY = ALARM_STATISTICS_KEY + "level:count";

    /**
     * 告警类型树缓存 Key
     */
    String ALARM_TYPE_TREE_KEY = "alarm:type:tree";

    /**
     * 告警类型路径缓存 Key 前缀
     */
    String ALARM_TYPE_PATH_KEY_PREFIX = "alarm:type:path:";

    /**
     * 告警类型实体缓存 Key 前缀
     */
    String ALARM_TYPE_ENTITY_KEY_PREFIX = "alarm:type:entity:";

    /**
     * 告警规则缓存 Key
     */
    String ALARM_RULES_ENABLED_KEY = "alarm:rules:enabled";

    /**
     * 联动规则缓存 Key
     */
    String LINKAGE_RULES_ENABLED_KEY = "alarm:linkage:rules:enabled";

    /**
     * 告警详情缓存 Key 前缀
     */
    String ALARM_DETAIL_KEY_PREFIX = "alarm:detail:";

    /**
     * 实时告警列表缓存 Key
     */
    String ALARM_REALTIME_LIST_KEY = "alarm:realtime:list";

    /**
     * 告警抑制检查缓存 Key 前缀
     * 格式：alarm:suppression:{deviceId}:{alarmTypeId}
     */
    String ALARM_SUPPRESSION_KEY_PREFIX = "alarm:suppression:";

    /**
     * WebSocket 消息批量发送缓存 Key
     */
    String ALARM_WEBSOCKET_BATCH_KEY = "alarm:websocket:batch";

    /**
     * 统计缓存过期时间（秒）- 30秒
     */
    int STATISTICS_CACHE_EXPIRE_SECONDS = 30;

    /**
     * 告警类型缓存过期时间（秒）- 5分钟
     */
    int ALARM_TYPE_CACHE_EXPIRE_SECONDS = 300;

    /**
     * 规则缓存过期时间（秒）- 1分钟
     */
    int RULES_CACHE_EXPIRE_SECONDS = 60;

    /**
     * 告警详情缓存过期时间（秒）- 2分钟
     */
    int ALARM_DETAIL_CACHE_EXPIRE_SECONDS = 120;

    /**
     * 实时告警列表缓存过期时间（秒）- 10秒
     */
    int REALTIME_LIST_CACHE_EXPIRE_SECONDS = 10;

    /**
     * 告警抑制缓存过期时间（秒）- 5分钟
     */
    int ALARM_SUPPRESSION_CACHE_EXPIRE_SECONDS = 300;

}
