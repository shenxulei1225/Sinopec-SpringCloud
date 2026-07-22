package cn.cheers.x.module.dynamicbusiness.enums.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一实体查询结果字段粒度。
 * <p>LIGHT：列表/树选择等场景，保留 id + baseFields（共用基础字段）；FULL：含 customFields 等完整明细（表单/详情）。
 */
@Getter
@AllArgsConstructor
public enum EntityQueryResultDetail {
    FULL("FULL"),
    LIGHT("LIGHT");

    private final String code;

    public static EntityQueryResultDetail ofNullable(String code) {
        if (code == null || code.isBlank()) {
            return LIGHT;
        }
        for (EntityQueryResultDetail detail : values()) {
            if (detail.code.equalsIgnoreCase(code)) {
                return detail;
            }
        }
        return LIGHT;
    }
}
