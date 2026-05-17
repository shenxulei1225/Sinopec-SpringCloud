package cn.iocoder.yudao.module.alarm.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;

import java.util.Arrays;

/**
 * 联动执行模式枚举
 * 
 * 对应字典类型：linkage_execution_mode
 *
 * @author 告警管理模块
 */
public enum LinkageExecutionModeEnum implements ArrayValuable<String> {

    SERIAL("SERIAL", "串行执行"),
    PARALLEL("PARALLEL", "并行执行");

    /**
     * 模式编码
     */
    private final String mode;
    /**
     * 模式名称
     */
    private final String name;

    LinkageExecutionModeEnum(String mode, String name) {
        this.mode = mode;
        this.name = name;
    }

    public String getMode() {
        return mode;
    }

    public String getName() {
        return name;
    }

    public static final String[] ARRAYS = Arrays.stream(values()).map(LinkageExecutionModeEnum::getMode).toArray(String[]::new);

    @Override
    public String[] array() {
        return ARRAYS;
    }

    /**
     * 根据模式编码获取枚举
     *
     * @param mode 模式编码
     * @return 枚举
     */
    public static LinkageExecutionModeEnum valueOfMode(String mode) {
        return ArrayUtil.firstMatch(item -> item.getMode().equals(mode), values());
    }

    /**
     * 判断是否为串行执行
     *
     * @param mode 模式编码
     * @return 是否串行执行
     */
    public static boolean isSerial(String mode) {
        return SERIAL.getMode().equals(mode);
    }

    /**
     * 判断是否为并行执行
     *
     * @param mode 模式编码
     * @return 是否并行执行
     */
    public static boolean isParallel(String mode) {
        return PARALLEL.getMode().equals(mode);
    }

}
