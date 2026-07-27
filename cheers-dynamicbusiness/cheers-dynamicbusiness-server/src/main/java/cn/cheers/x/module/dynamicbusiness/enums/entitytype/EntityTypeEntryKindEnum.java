package cn.cheers.x.module.dynamicbusiness.enums.entitytype;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据类型侧边栏入口类型。
 * <p>
 * 产品文案：NATIVE=数据类型（新建数据）；DOMAIN=子数据类型（按业务域过滤）；
 * SCOPE=划分数据（按业务划分，实体成员选用）；CATEGORY=旧「分类即实体」入口（勿与 SCOPE 混用）。
 */
@Getter
@AllArgsConstructor
public enum EntityTypeEntryKindEnum {

    NATIVE("NATIVE"),
    DOMAIN("DOMAIN"),
    SCOPE("SCOPE"),
    /** @deprecated 旧分类即实体入口；新划分数据请用 {@link #SCOPE} */
    CATEGORY("CATEGORY");

    private final String code;

    public static EntityTypeEntryKindEnum fromCode(String code) {
        if (code == null || code.isBlank()) {
            return NATIVE;
        }
        String normalized = code.trim();
        for (EntityTypeEntryKindEnum value : values()) {
            if (value.code.equalsIgnoreCase(normalized)) {
                return value;
            }
        }
        return NATIVE;
    }

    /** 子数据类型入口（entry_kind=DOMAIN）：复用基础类型存储，按业务域过滤。 */
    public boolean isDomainEntry() {
        return this == DOMAIN;
    }

    /** 划分数据入口（entry_kind=SCOPE）。 */
    public boolean isScopeEntry() {
        return this == SCOPE;
    }

    public boolean isCategory() {
        return this == CATEGORY;
    }

    /** 子数据类型、划分数据、旧分类数据均复用基础类型的存储表。 */
    public boolean reusesBaseStorage() {
        return this == DOMAIN || this == SCOPE || this == CATEGORY;
    }
}
