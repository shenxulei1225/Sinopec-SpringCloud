package cn.iocoder.yudao.module.scene.platform.enums;

public enum CoordinateSpaceTypeEnum {

    GEOGRAPHIC,
    PROJECTED,
    ECEF,
    LOCAL_TANGENT,
    ENGINE;

    public static CoordinateSpaceTypeEnum of(String value) {
        if (value == null || value.isBlank()) {
            return GEOGRAPHIC;
        }
        String normalized = value.trim().toUpperCase();
        if ("LOCAL".equals(normalized) || "ENU".equals(normalized) || "LOCAL_TANGENT".equals(normalized)) {
            return LOCAL_TANGENT;
        }
        return CoordinateSpaceTypeEnum.valueOf(normalized);
    }
}
