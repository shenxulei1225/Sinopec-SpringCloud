package cn.cheers.x.inspection.task.enums;

import lombok.Getter;

@Getter
public enum InspectionSchedulePatternEnum {

    FIXED_POINT(1, "固定点"),
    TIME_WINDOW(2, "时间窗");

    private final Integer code;
    private final String desc;

    InspectionSchedulePatternEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
