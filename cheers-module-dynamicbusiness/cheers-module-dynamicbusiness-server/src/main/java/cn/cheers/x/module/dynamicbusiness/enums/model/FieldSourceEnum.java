package cn.cheers.x.module.dynamicbusiness.enums.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字段来源枚举
 * 
 * 用于标识 Model 中字段的来源。
 * 
 * @author yudao
 */
@Getter
@AllArgsConstructor
public enum FieldSourceEnum {

    /**
     * 用户手动添加的字段
     */
    USER_ADDED("USER_ADDED", "用户添加");

    /**
     * 枚举值
     */
    private final String value;

    /**
     * 枚举描述
     */
    private final String description;

    /**
     * 根据值获取枚举
     */
    public static FieldSourceEnum fromValue(String value) {
        if (value == null) {
            return USER_ADDED; // 默认为用户添加
        }
        for (FieldSourceEnum source : values()) {
            if (source.getValue().equals(value)) {
                return source;
            }
        }
        return USER_ADDED; // 未知值默认为用户添加
    }
}
