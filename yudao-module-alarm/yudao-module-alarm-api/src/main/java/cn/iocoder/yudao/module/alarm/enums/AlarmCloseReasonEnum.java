package cn.iocoder.yudao.module.alarm.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;

import java.util.Arrays;

/**
 * 告警关闭原因枚举
 * 
 * 对应字典类型：alarm_close_reason
 *
 * @author 告警管理模块
 */
public enum AlarmCloseReasonEnum implements ArrayValuable<String> {

    HANDLED("HANDLED", "已处理"),
    FALSE_ALARM("FALSE_ALARM", "误报"),
    OTHER("OTHER", "其他");

    public static final String[] ARRAYS = Arrays.stream(values()).map(AlarmCloseReasonEnum::getReason).toArray(String[]::new);

    /**
     * 原因编码
     */
    private final String reason;
    /**
     * 原因名称
     */
    private final String name;

    AlarmCloseReasonEnum(String reason, String name) {
        this.reason = reason;
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public String getName() {
        return name;
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }

    /**
     * 根据原因编码获取枚举
     *
     * @param reason 原因编码
     * @return 枚举
     */
    public static AlarmCloseReasonEnum getByReason(String reason) {
        return ArrayUtil.firstMatch(item -> item.getReason().equals(reason), values());
    }

    /**
     * 判断是否为已处理
     *
     * @param reason 原因编码
     * @return 是否已处理
     */
    public static boolean isHandled(String reason) {
        return HANDLED.getReason().equals(reason);
    }

    /**
     * 判断是否为误报
     *
     * @param reason 原因编码
     * @return 是否误报
     */
    public static boolean isFalseAlarm(String reason) {
        return FALSE_ALARM.getReason().equals(reason);
    }

}
