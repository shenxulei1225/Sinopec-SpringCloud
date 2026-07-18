package cn.cheers.x.module.dynamicbusiness.enums.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一实体查询结果形态。
 */
@Getter
@AllArgsConstructor
public enum EntityQueryResultShape {

    PAGE("PAGE"),
    LIST("LIST"),
    TREE("TREE");

    private final String code;

    public static EntityQueryResultShape ofNullable(String code) {
        if (code == null || code.isBlank()) {
            return PAGE;
        }
        for (EntityQueryResultShape shape : values()) {
            if (shape.code.equalsIgnoreCase(code)) {
                return shape;
            }
        }
        return PAGE;
    }
}

