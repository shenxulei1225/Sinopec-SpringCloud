package cn.iocoder.yudao.module.scene.platform.enums;

public enum LocalFrameTypeEnum {

    ENU,
    NED,
    NWU,
    CUSTOM;

    public static LocalFrameTypeEnum of(String value) {
        if (value == null || value.isBlank()) {
            return ENU;
        }
        return LocalFrameTypeEnum.valueOf(value.trim().toUpperCase());
    }
}
