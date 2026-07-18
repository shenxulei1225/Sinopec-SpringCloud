package cn.iocoder.yudao.module.alarm.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.cheers.x.framework.common.core.ArrayValuable;

import java.util.Arrays;

/**
 * 告警级别枚举
 * 
 * 对应字典类型：alarm_level
 *
 * @author 告警管理模块
 */
public enum AlarmLevelEnum implements ArrayValuable<String> {

    INFO("INFO", "信息"),
    WARNING("WARNING", "警告"),
    CRITICAL("CRITICAL", "严重"),
    EMERGENCY("EMERGENCY", "紧急");

    public static final String[] ARRAYS = Arrays.stream(values()).map(AlarmLevelEnum::getLevel).toArray(String[]::new);

    /**
     * 级别编码
     */
    private final String level;
    /**
     * 级别名称
     */
    private final String name;

    AlarmLevelEnum(String level, String name) {
        this.level = level;
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }

    /**
     * 根据级别编码获取枚举
     *
     * @param level 级别编码
     * @return 枚举
     */
    public static AlarmLevelEnum getByLevel(String level) {
        return ArrayUtil.firstMatch(item -> item.getLevel().equals(level), values());
    }

    /**
     * 判断是否为紧急级别
     *
     * @param level 级别编码
     * @return 是否紧急
     */
    public static boolean isEmergency(String level) {
        return EMERGENCY.getLevel().equals(level);
    }

    /**
     * 判断是否为严重级别
     *
     * @param level 级别编码
     * @return 是否严重
     */
    public static boolean isCritical(String level) {
        return CRITICAL.getLevel().equals(level);
    }

    /**
     * 获取升级超时时间（分钟）
     * 
     * @return 超时时间
     */
    public int getEscalationTimeoutMinutes() {
        return switch (this) {
            case INFO -> 30;
            case WARNING -> 15;
            case CRITICAL -> 5;
            case EMERGENCY -> 1;
        };
    }

}
