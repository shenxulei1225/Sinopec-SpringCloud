package cn.cheers.x.scene.platform.enums;

public enum ReferenceFrameTypeEnum {

    ECEF,
    PROJECTED,
    LOCAL;

    public static ReferenceFrameTypeEnum of(String value) {
        if (value == null || value.isBlank()) {
            return ECEF;
        }
        return ReferenceFrameTypeEnum.valueOf(value.trim().toUpperCase());
    }
}
