package cn.iocoder.yudao.module.inspection.task.enums;

import lombok.Getter;

@Getter
public enum InspectionConflictStrategyEnum {

    STRICT(1, "严格保持原时间"),
    ALLOW_DELAY(2, "允许延后"),
    ALLOW_ADVANCE(3, "允许提前"),
    AUTO_RESOLVE(4, "允许自动求解"),
    MANUAL_CONFIRM(5, "人工确认");

    private final Integer code;
    private final String desc;

    InspectionConflictStrategyEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
