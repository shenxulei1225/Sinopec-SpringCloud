package cn.cheers.x.alarm.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.cheers.x.framework.common.core.ArrayValuable;

import java.util.Arrays;

/**
 * 告警审计操作类型枚举
 *
 * @author 告警管理模块
 */
public enum AlarmAuditOperationTypeEnum implements ArrayValuable<String> {

    CREATE("CREATE", "创建告警"),
    ACKNOWLEDGE("ACKNOWLEDGE", "确认告警"),
    HANDLE("HANDLE", "处理告警"),
    CLOSE("CLOSE", "关闭告警"),
    ESCALATE("ESCALATE", "升级告警"),
    LINKAGE_EXECUTE("LINKAGE_EXECUTE", "执行联动"),
    LINKAGE_RETRY("LINKAGE_RETRY", "重试联动"),
    LINKAGE_MANUAL("LINKAGE_MANUAL", "人工介入联动");

    public static final String[] ARRAYS = Arrays.stream(values()).map(AlarmAuditOperationTypeEnum::getType).toArray(String[]::new);

    /**
     * 操作类型编码
     */
    private final String type;
    /**
     * 操作类型名称
     */
    private final String name;

    AlarmAuditOperationTypeEnum(String type, String name) {
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
    public static AlarmAuditOperationTypeEnum getByType(String type) {
        return ArrayUtil.firstMatch(item -> item.getType().equals(type), values());
    }

}
