package cn.cheers.x.module.dynamicbusiness.enums.business;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessEntryTypeEnum {

    ENTITY_ADMIN("ENTITY_ADMIN", "实体台账管理"),
    SCOPED_LIST("SCOPED_LIST", "范围列表入口"),
    DASHBOARD("DASHBOARD", "驾驶舱"),
    EXTERNAL("EXTERNAL", "外链或固定路由");

    private final String code;
    private final String name;
}
