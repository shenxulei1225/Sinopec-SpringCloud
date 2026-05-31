package cn.cheers.x.module.dynamicbusiness.enums.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一实体查询结果字段粒度。
 */
@Getter
@AllArgsConstructor
public enum EntityQueryResultDetail {
    FULL("FULL"),
    LIGHT("LIGHT");

    private final String code;

    public static EntityQueryResultDetail ofNullable(String code) {
        if (code == null || code.isBlank()) {
            return FULL;
        }
        for (EntityQueryResultDetail detail : values()) {
            if (detail.code.equalsIgnoreCase(code)) {
                return detail;
            }
        }
        return FULL;
    }
}
