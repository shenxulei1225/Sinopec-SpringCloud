package cn.iocoder.yudao.module.alarm.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;

import java.util.Arrays;

/**
 * 告警来源枚举
 * 
 * 对应字典类型：alarm_source
 *
 * @author 告警管理模块
 */
public enum AlarmSourceEnum implements ArrayValuable<String> {

    SYSTEM("SYSTEM", "系统自动"),
    MANUAL("MANUAL", "人工上报");

    public static final String[] ARRAYS = Arrays.stream(values()).map(AlarmSourceEnum::getSource).toArray(String[]::new);

    /**
     * 来源编码
     */
    private final String source;
    /**
     * 来源名称
     */
    private final String name;

    AlarmSourceEnum(String source, String name) {
        this.source = source;
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public String getName() {
        return name;
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }

    /**
     * 根据来源编码获取枚举
     *
     * @param source 来源编码
     * @return 枚举
     */
    public static AlarmSourceEnum getBySource(String source) {
        return ArrayUtil.firstMatch(item -> item.getSource().equals(source), values());
    }

    /**
     * 判断是否为系统自动触发
     *
     * @param source 来源编码
     * @return 是否系统自动
     */
    public static boolean isSystem(String source) {
        return SYSTEM.getSource().equals(source);
    }

    /**
     * 判断是否为人工上报
     *
     * @param source 来源编码
     * @return 是否人工上报
     */
    public static boolean isManual(String source) {
        return MANUAL.getSource().equals(source);
    }

}
