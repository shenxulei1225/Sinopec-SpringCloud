package cn.iocoder.yudao.module.alarm.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;

import java.util.Arrays;

/**
 * 告警处理结果枚举
 * 
 * 对应字典类型：alarm_handle_result
 *
 * @author 告警管理模块
 */
public enum AlarmHandleResultEnum implements ArrayValuable<String> {

    RESOLVED("RESOLVED", "已解决"),
    UNRESOLVED("UNRESOLVED", "未解决"),
    FALSE_ALARM("FALSE_ALARM", "误报");

    public static final String[] ARRAYS = Arrays.stream(values()).map(AlarmHandleResultEnum::getResult).toArray(String[]::new);

    /**
     * 结果编码
     */
    private final String result;
    /**
     * 结果名称
     */
    private final String name;

    AlarmHandleResultEnum(String result, String name) {
        this.result = result;
        this.name = name;
    }

    public String getResult() {
        return result;
    }

    public String getName() {
        return name;
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }

    /**
     * 根据结果编码获取枚举
     *
     * @param result 结果编码
     * @return 枚举
     */
    public static AlarmHandleResultEnum getByResult(String result) {
        return ArrayUtil.firstMatch(item -> item.getResult().equals(result), values());
    }

    /**
     * 判断是否为已解决
     *
     * @param result 结果编码
     * @return 是否已解决
     */
    public static boolean isResolved(String result) {
        return RESOLVED.getResult().equals(result);
    }

    /**
     * 判断是否为误报
     *
     * @param result 结果编码
     * @return 是否误报
     */
    public static boolean isFalseAlarm(String result) {
        return FALSE_ALARM.getResult().equals(result);
    }

}
