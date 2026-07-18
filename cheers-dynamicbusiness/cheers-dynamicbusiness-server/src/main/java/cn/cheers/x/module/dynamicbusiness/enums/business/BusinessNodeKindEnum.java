package cn.cheers.x.module.dynamicbusiness.enums.business;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessNodeKindEnum {

    GROUP("GROUP", "分组"),
    LEAF("LEAF", "叶子业务");

    private final String code;
    private final String name;
}
