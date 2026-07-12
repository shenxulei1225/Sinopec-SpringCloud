package cn.cheers.x.module.dynamicbusiness.enums.datamgmt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DmDimensionKindEnum {

    CATEGORY("CATEGORY"),
    MODEL("MODEL"),
    ENTITY("ENTITY"),
    DETAIL("DETAIL");

    private final String code;

    public static boolean isValid(String code) {
        if (code == null || code.isBlank()) {
            return false;
        }
        for (DmDimensionKindEnum value : values()) {
            if (value.code.equalsIgnoreCase(code)) {
                return true;
            }
        }
        return false;
    }

    public static DmDimensionKindEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (DmDimensionKindEnum value : values()) {
            if (value.code.equalsIgnoreCase(code)) {
                return value;
            }
        }
        return null;
    }
}
