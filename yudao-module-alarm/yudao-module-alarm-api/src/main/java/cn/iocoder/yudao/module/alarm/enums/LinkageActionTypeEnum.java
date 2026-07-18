package cn.iocoder.yudao.module.alarm.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.cheers.x.framework.common.core.ArrayValuable;

import java.util.Arrays;

/**
 * 联动动作类型枚举
 * 
 * 对应字典类型：linkage_action_type
 *
 * @author 告警管理模块
 */
public enum LinkageActionTypeEnum implements ArrayValuable<String> {

    NOTIFICATION("NOTIFICATION", "发送通知"),
    WORK_ORDER("WORK_ORDER", "创建工单"),
    DEVICE_CONTROL("DEVICE_CONTROL", "设备控制"),
    VIDEO_LINKAGE("VIDEO_LINKAGE", "视频联动"),
    ACCESS_CONTROL("ACCESS_CONTROL", "门禁控制"),
    FIRE_CONTROL("FIRE_CONTROL", "消防控制"),
    SCRIPT("SCRIPT", "执行脚本"),
    API_CALL("API_CALL", "调用API");

    public static final String[] ARRAYS = Arrays.stream(values()).map(LinkageActionTypeEnum::getType).toArray(String[]::new);

    /**
     * 类型编码
     */
    private final String type;
    /**
     * 类型名称
     */
    private final String name;

    LinkageActionTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }

    /**
     * 根据类型编码获取枚举
     *
     * @param type 类型编码
     * @return 枚举
     */
    public static LinkageActionTypeEnum getByType(String type) {
        return ArrayUtil.firstMatch(item -> item.getType().equals(type), values());
    }

    /**
     * 根据值获取枚举（别名方法）
     *
     * @param value 类型编码
     * @return 枚举
     */
    public static LinkageActionTypeEnum getByValue(String value) {
        return getByType(value);
    }

    /**
     * 获取描述信息
     *
     * @return 描述
     */
    public String getDescription() {
        return name;
    }

    /**
     * 判断是否为消防控制类型
     * 消防控制需要优先执行
     *
     * @param type 类型编码
     * @return 是否消防控制
     */
    public static boolean isFireControl(String type) {
        return FIRE_CONTROL.getType().equals(type);
    }

    /**
     * 判断是否为设备控制类型
     *
     * @param type 类型编码
     * @return 是否设备控制
     */
    public static boolean isDeviceControl(String type) {
        return DEVICE_CONTROL.getType().equals(type);
    }

    /**
     * 判断是否为通知类型
     *
     * @param type 类型编码
     * @return 是否通知
     */
    public static boolean isNotification(String type) {
        return NOTIFICATION.getType().equals(type);
    }

}
