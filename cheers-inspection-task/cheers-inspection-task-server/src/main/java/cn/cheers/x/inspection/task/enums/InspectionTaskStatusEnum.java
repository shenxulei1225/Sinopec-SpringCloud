package cn.cheers.x.inspection.task.enums;

import lombok.Getter;

@Getter
public enum InspectionTaskStatusEnum {

    DRAFT(0, "草稿"),
    ENABLED(1, "启用"),
    DISABLED(2, "停用"),
    ARCHIVED(3, "归档");

    private final Integer code;
    private final String desc;

    InspectionTaskStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static InspectionTaskStatusEnum valueOf(Integer code) {
        if (code == null) {
            return null;
        }
        for (InspectionTaskStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
