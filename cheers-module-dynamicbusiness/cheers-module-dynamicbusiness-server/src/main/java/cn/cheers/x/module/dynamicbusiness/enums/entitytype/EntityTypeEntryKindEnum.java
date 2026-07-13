package cn.cheers.x.module.dynamicbusiness.enums.entitytype;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据类型侧边栏入口类型。
 */
@Getter
@AllArgsConstructor
public enum EntityTypeEntryKindEnum {

    NATIVE("NATIVE"),
    SCOPED("SCOPED");

    private final String code;

    public static EntityTypeEntryKindEnum fromCode(String code) {
        if (code == null || code.isBlank()) {
            return NATIVE;
        }
        for (EntityTypeEntryKindEnum value : values()) {
            if (value.code.equalsIgnoreCase(code.trim())) {
                return value;
            }
        }
        return NATIVE;
    }

    public boolean isScoped() {
        return this == SCOPED;
    }
}
