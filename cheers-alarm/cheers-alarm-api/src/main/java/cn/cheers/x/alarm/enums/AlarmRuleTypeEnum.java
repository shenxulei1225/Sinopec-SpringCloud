package cn.cheers.x.alarm.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.cheers.x.framework.common.core.ArrayValuable;

import java.util.Arrays;

/**
 * 告警规则类型枚举
 * 
 * 对应字典类型：alarm_rule_type
 *
 * @author 告警管理模块
 */
public enum AlarmRuleTypeEnum implements ArrayValuable<String> {

    THRESHOLD("THRESHOLD", "阈值告警"),
    RATE("RATE", "变化率告警"),
    PATTERN("PATTERN", "异常模式告警"),
    COMBINATION("COMBINATION", "组合条件告警");

    public static final String[] ARRAYS = Arrays.stream(values()).map(AlarmRuleTypeEnum::getType).toArray(String[]::new);

    /**
     * 类型编码
     */
    private final String type;
    /**
     * 类型名称
     */
    private final String name;

    AlarmRuleTypeEnum(String type, String name) {
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
    public static AlarmRuleTypeEnum getByType(String type) {
        return ArrayUtil.firstMatch(item -> item.getType().equals(type), values());
    }

    /**
     * 判断是否为阈值告警类型
     *
     * @param type 类型编码
     * @return 是否阈值告警
     */
    public static boolean isThreshold(String type) {
        return THRESHOLD.getType().equals(type);
    }

    /**
     * 判断是否为组合条件告警类型
     *
     * @param type 类型编码
     * @return 是否组合条件告警
     */
    public static boolean isCombination(String type) {
        return COMBINATION.getType().equals(type);
    }

}
