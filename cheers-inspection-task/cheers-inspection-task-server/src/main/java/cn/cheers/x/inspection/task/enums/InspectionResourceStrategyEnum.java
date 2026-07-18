package cn.cheers.x.inspection.task.enums;

import lombok.Getter;

@Getter
public enum InspectionResourceStrategyEnum {

    FIXED(1, "固定资源"),
    POOL(2, "资源池"),
    FLEXIBLE(3, "灵活分配");

    private final Integer code;
    private final String desc;

    InspectionResourceStrategyEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
