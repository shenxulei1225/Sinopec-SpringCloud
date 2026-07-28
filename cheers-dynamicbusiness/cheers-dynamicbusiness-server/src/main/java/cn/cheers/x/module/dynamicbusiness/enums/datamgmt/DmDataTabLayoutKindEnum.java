package cn.cheers.x.module.dynamicbusiness.enums.datamgmt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DmDataTabLayoutKindEnum {

    CATEGORY("CATEGORY"),
    MODEL("MODEL"),
    ENTITY("ENTITY"),
    /** 实体列上方筛选区（配置落在 ENTITY 列表 props；本维仅作种类标识，不参与 enabled 开关） */
    FILTER("FILTER"),
    DETAIL("DETAIL");

    private final String code;

    public static boolean isValid(String code) {
        if (code == null || code.isBlank()) {
            return false;
        }
        for (DmDataTabLayoutKindEnum value : values()) {
            if (value.code.equalsIgnoreCase(code)) {
                return true;
            }
        }
        return false;
    }

    public static DmDataTabLayoutKindEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (DmDataTabLayoutKindEnum value : values()) {
            if (value.code.equalsIgnoreCase(code)) {
                return value;
            }
        }
        return null;
    }
}
