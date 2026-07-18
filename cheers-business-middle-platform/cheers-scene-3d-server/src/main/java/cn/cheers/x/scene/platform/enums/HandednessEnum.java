package cn.cheers.x.scene.platform.enums;

public enum HandednessEnum {

    LEFT,
    RIGHT;

    public static HandednessEnum of(String value) {
        if (value == null || value.isBlank()) {
            return RIGHT;
        }
        return HandednessEnum.valueOf(value.trim().toUpperCase());
    }
}
