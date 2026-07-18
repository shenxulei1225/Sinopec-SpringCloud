package cn.iocoder.yudao.module.alarm.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.cheers.x.framework.common.core.ArrayValuable;

import java.util.Arrays;

/**
 * 告警状态枚举
 * 
 * 对应字典类型：alarm_status
 *
 * @author 告警管理模块
 */
public enum AlarmStatusEnum implements ArrayValuable<String> {

    PENDING("PENDING", "待确认"),
    ACKNOWLEDGED("ACKNOWLEDGED", "已确认"),
    HANDLING("HANDLING", "处理中"),
    CLOSED("CLOSED", "已关闭");

    public static final String[] ARRAYS = Arrays.stream(values()).map(AlarmStatusEnum::getStatus).toArray(String[]::new);

    /**
     * 状态编码
     */
    private final String status;
    /**
     * 状态名称
     */
    private final String name;

    AlarmStatusEnum(String status, String name) {
        this.status = status;
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }

    /**
     * 根据状态编码获取枚举
     *
     * @param status 状态编码
     * @return 枚举
     */
    public static AlarmStatusEnum getByStatus(String status) {
        return ArrayUtil.firstMatch(item -> item.getStatus().equals(status), values());
    }

    /**
     * 判断是否为已关闭状态
     *
     * @param status 状态编码
     * @return 是否已关闭
     */
    public static boolean isClosed(String status) {
        return CLOSED.getStatus().equals(status);
    }

    /**
     * 判断是否为待确认状态
     *
     * @param status 状态编码
     * @return 是否待确认
     */
    public static boolean isPending(String status) {
        return PENDING.getStatus().equals(status);
    }

    /**
     * 判断是否可以确认（只有待确认状态可以确认）
     *
     * @param status 状态编码
     * @return 是否可以确认
     */
    public static boolean canAcknowledge(String status) {
        return PENDING.getStatus().equals(status);
    }

    /**
     * 判断是否可以处理（只有已确认状态可以处理）
     *
     * @param status 状态编码
     * @return 是否可以处理
     */
    public static boolean canHandle(String status) {
        return ACKNOWLEDGED.getStatus().equals(status);
    }

    /**
     * 判断是否可以关闭（只有处理中状态可以关闭）
     *
     * @param status 状态编码
     * @return 是否可以关闭
     */
    public static boolean canClose(String status) {
        return HANDLING.getStatus().equals(status);
    }

}
