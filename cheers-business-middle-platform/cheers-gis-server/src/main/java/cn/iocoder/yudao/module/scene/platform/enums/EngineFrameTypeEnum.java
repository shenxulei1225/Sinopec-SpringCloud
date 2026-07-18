package cn.iocoder.yudao.module.scene.platform.enums;

public enum EngineFrameTypeEnum {

    THREE,
    UE,
    CESIUM,
    CUSTOM;

    public static EngineFrameTypeEnum of(String value) {
        if (value == null || value.isBlank()) {
            return THREE;
        }
        return EngineFrameTypeEnum.valueOf(value.trim().toUpperCase());
    }
}
