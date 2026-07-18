package cn.cheers.x.inspection.task.enums;

import lombok.Getter;

@Getter
public enum InspectionScheduleModeEnum {

    ONE_TIME(1, "单次"),
    DAILY(2, "每天"),
    WEEKLY(3, "每周"),
    MONTHLY(4, "每月"),
    CUSTOM_INTERVAL(5, "自定义间隔");

    private final Integer code;
    private final String desc;

    InspectionScheduleModeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
