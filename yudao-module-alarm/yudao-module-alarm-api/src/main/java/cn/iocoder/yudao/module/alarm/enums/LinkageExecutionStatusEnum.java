package cn.iocoder.yudao.module.alarm.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;

import java.util.Arrays;

/**
 * 联动执行状态枚举
 * 
 * 对应字典类型：linkage_execution_status
 *
 * @author 告警管理模块
 */
public enum LinkageExecutionStatusEnum implements ArrayValuable<String> {

    PENDING("PENDING", "待执行"),
    EXECUTING("EXECUTING", "执行中"),
    SUCCESS("SUCCESS", "成功"),
    FAILED("FAILED", "失败"),
    RETRY("RETRY", "重试中");

    /**
     * 状态编码
     */
    private final String status;
    /**
     * 状态名称
     */
    private final String name;

    LinkageExecutionStatusEnum(String status, String name) {
        this.status = status;
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public static final String[] ARRAYS = Arrays.stream(values()).map(LinkageExecutionStatusEnum::getStatus).toArray(String[]::new);

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
    public static LinkageExecutionStatusEnum valueOfStatus(String status) {
        return ArrayUtil.firstMatch(item -> item.getStatus().equals(status), values());
    }

    /**
     * 根据值获取枚举（别名方法）
     *
     * @param value 状态编码
     * @return 枚举
     */
    public static LinkageExecutionStatusEnum getByValue(String value) {
        return valueOfStatus(value);
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
     * 判断是否为成功状态
     *
     * @param status 状态编码
     * @return 是否成功
     */
    public static boolean isSuccess(String status) {
        return SUCCESS.getStatus().equals(status);
    }

    /**
     * 判断是否为失败状态
     *
     * @param status 状态编码
     * @return 是否失败
     */
    public static boolean isFailed(String status) {
        return FAILED.getStatus().equals(status);
    }

    /**
     * 判断是否为终态（成功或失败）
     *
     * @param status 状态编码
     * @return 是否终态
     */
    public static boolean isTerminal(String status) {
        return SUCCESS.getStatus().equals(status) || FAILED.getStatus().equals(status);
    }

    /**
     * 判断是否可以重试
     *
     * @param status 状态编码
     * @return 是否可以重试
     */
    public static boolean canRetry(String status) {
        return FAILED.getStatus().equals(status) || RETRY.getStatus().equals(status);
    }

}
