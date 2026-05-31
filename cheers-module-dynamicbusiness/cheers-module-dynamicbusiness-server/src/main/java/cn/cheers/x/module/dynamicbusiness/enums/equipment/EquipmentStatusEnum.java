package cn.cheers.x.module.dynamicbusiness.enums.equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备状态枚举
 */
@Getter
@AllArgsConstructor
public enum EquipmentStatusEnum {

    NORMAL("NORMAL", "正常运行"),
    FAULT("FAULT", "故障"),
    MAINTENANCE("MAINTENANCE", "维护中"),
    SCRAPPED("SCRAPPED", "已报废");

    /**
     * 状态值
     */
    private final String status;

    /**
     * 状态名称
     */
    private final String name;

    /**
     * 根据状态值获取枚举
     */
    public static EquipmentStatusEnum getByStatus(String status) {
        for (EquipmentStatusEnum value : values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return null;
    }
}
