package cn.cheers.x.module.dynamicbusiness.enums.datamgmt;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据目录编排配方 id（平台内置；写入库后运行时只读该目录自己的行）。
 */
@Getter
@AllArgsConstructor
public enum OrchestrationRecipeIdEnum {

    LEDGER_3COL("recipe-ledger-3col"),
    CATEGORY_AS_ENTITY("recipe-category-as-entity"),
    FOREIGN_MODEL("recipe-foreign-model"),
    CONFIG_OBJECT_3COL("recipe-config-object-3col"),
    PARTITION_ENTITY("recipe-partition-entity");

    private final String recipeId;

    public static OrchestrationRecipeIdEnum fromRecipeId(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String normalized = raw.trim();
        for (OrchestrationRecipeIdEnum value : values()) {
            if (value.recipeId.equalsIgnoreCase(normalized)) {
                return value;
            }
        }
        return null;
    }
}
