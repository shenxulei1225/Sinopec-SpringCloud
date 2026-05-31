package cn.cheers.x.module.dynamicbusiness.enums.group;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GroupTypeEnum {

    FIELD("FIELD"),
    UNIT("UNIT");

    private final String code;
}
