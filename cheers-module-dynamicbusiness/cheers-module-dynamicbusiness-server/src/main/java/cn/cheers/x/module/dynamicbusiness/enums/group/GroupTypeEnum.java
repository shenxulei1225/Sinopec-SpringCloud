package cn.cheers.x.module.dynamicbusiness.enums.group;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GroupTypeEnum {

    FIELD("FIELD"),
    UNIT("UNIT"),
    /** 数据类型侧栏分组（平铺，无继承语义；成员通过 entity_type.group_name 关联） */
    ENTITY_TYPE("ENTITY_TYPE");

    private final String code;
}
