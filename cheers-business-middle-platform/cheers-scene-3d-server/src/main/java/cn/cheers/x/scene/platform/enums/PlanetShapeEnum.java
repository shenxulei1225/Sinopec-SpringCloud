package cn.cheers.x.scene.platform.enums;

public enum PlanetShapeEnum {

    FLAT_PLANET,
    ROUND_PLANET;

    public static PlanetShapeEnum of(String value) {
        if (value == null || value.isBlank()) {
            return ROUND_PLANET;
        }
        return PlanetShapeEnum.valueOf(value.trim().toUpperCase());
    }
}
