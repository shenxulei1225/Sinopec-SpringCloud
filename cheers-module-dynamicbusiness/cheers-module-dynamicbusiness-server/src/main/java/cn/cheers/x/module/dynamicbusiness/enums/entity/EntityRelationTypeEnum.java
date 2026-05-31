package cn.cheers.x.module.dynamicbusiness.enums.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 实体关联关系类型枚举
 */
@Getter
@AllArgsConstructor
public enum EntityRelationTypeEnum {

    ONE_TO_ONE("ONE_TO_ONE", "一对一"),
    ONE_TO_MANY("ONE_TO_MANY", "一对多"),
    MANY_TO_MANY("MANY_TO_MANY", "多对多");

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 根据编码获取枚举
     */
    public static EntityRelationTypeEnum getByCode(String code) {
        for (EntityRelationTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断编码是否有效
     */
    public static boolean isValid(String code) {
        return getByCode(code) != null;
    }
}
