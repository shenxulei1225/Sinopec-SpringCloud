package cn.cheers.x.module.dynamicbusiness.enums.entitytype;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据类型侧边栏入口类型。
 * <p>
 * 产品文案：NATIVE=独立数据，SCOPED=分域数据，CATEGORY=分类数据。
 */
@Getter
@AllArgsConstructor
public enum EntityTypeEntryKindEnum {

    NATIVE("NATIVE"),
    SCOPED("SCOPED"),
    CATEGORY("CATEGORY");

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

    public boolean isCategory() {
        return this == CATEGORY;
    }

    /** 分域数据与分类数据均复用基础类型的存储表。 */
    public boolean reusesBaseStorage() {
        return this == SCOPED || this == CATEGORY;
    }
}
