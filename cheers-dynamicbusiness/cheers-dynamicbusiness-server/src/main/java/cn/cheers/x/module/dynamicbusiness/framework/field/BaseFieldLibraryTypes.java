package cn.cheers.x.module.dynamicbusiness.framework.field;

/**
 * 字段库类型 ↔ 专用表固定列 data_type 映射。
 */
public final class BaseFieldLibraryTypes {

    private BaseFieldLibraryTypes() {
    }

    public static String toBaseDataType(String libraryType) {
        if (libraryType == null || libraryType.isBlank()) {
            return "TEXT";
        }
        return switch (libraryType.trim()) {
            case "STRING", "TEXT", "LONG_TEXT" -> "TEXT";
            case "NUMBER", "DECIMAL", "INTEGER" -> "NUMBER";
            case "DATE" -> "DATE";
            case "DATETIME" -> "DATETIME";
            case "BOOLEAN" -> "BOOLEAN";
            case "ENUM" -> "ENUM";
            case "JSON" -> "JSON";
            case "ENTITY_REF" -> "REF";
            case "ENTITY_REF_MULTI" -> "REF_Multi";
            default -> libraryType.trim();
        };
    }
}
