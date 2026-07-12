package cn.cheers.x.module.dynamicbusiness.enums.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 实体查询场景枚举
 * 
 * 用于统一实体查询接口，通过 scene 参数明确表达查询意图
 * 
 * @see UI/新接口设计方案（基于Scene枚举）.md
 */
@Getter
@AllArgsConstructor
public enum EntityQueryScene {

    /** 模式A（灵活分类视图）：点击分类查看实体列表 */
    PATTERN_A_C_ENTITIES_BY_CATEGORY ("PATTERN_A_C_ENTITIES_BY_CATEGORY", "模式A和C（分类视图）：点击分类查看实体列表"),

    /** 模式B：点击分类查看实体汇总列表 */
    PATTERN_B_ENTITIES_BY_CATEGORY("PATTERN_B_ENTITIES_BY_CATEGORY", "模式B：点击分类查看实体汇总列表"),

    /** 模式B：点击模型查看实体列表 */
    PATTERN_B_ENTITIES_BY_MODEL("PATTERN_B_ENTITIES_BY_MODEL", "模式B：点击模型查看实体列表"),

    /** 模式A/C：按当前分类体系查看未分类实体 */
    PATTERN_A_C_UNCATEGORIZED_ENTITIES_BY_CATEGORY_TYPE("PATTERN_A_C_UNCATEGORIZED_ENTITIES_BY_CATEGORY_TYPE", "模式A/C：按当前分类体系查看未分类实体"),

    /** 模式B：按当前分类体系查看未分类模型下的实体 */
    PATTERN_B_UNCATEGORIZED_ENTITIES_BY_CATEGORY_TYPE("PATTERN_B_UNCATEGORIZED_ENTITIES_BY_CATEGORY_TYPE", "模式B：按当前分类体系查看未分类模型下的实体"),

    /** 模式A/B/C：按业务类型查看全部实体列表（不受分类体系限制） */
    PATTERN_ABC_ALL_ENTITIES_BY_BUSINESS_TYPE("PATTERN_ABC_ALL_ENTITIES_BY_BUSINESS_TYPE", "模式A/B/C：按业务类型查看全部实体列表"),


    /** 模式D：查看单条实体详情（详情内携带跨业务关联关系摘要） */
    PATTERN_D_ENTITY_DETAILED_INFO ("PATTERN_D_ENTITY_DETAILED_INFO", "模式D：查看实体详情（含跨业务关联信息）"),

    /** 通用：以根实体获取子树 */
    ROOT_ENTITY_SUBTREE("ROOT_ENTITY_SUBTREE", "通用：以根实体获取子树"),

    /** 数据管理三栏：分类范围（含子树，空 categoryIds 回退根分类）+ 可选 modelIds 过滤 */
    DATA_MGMT_ENTITIES_BY_CATEGORY_MODEL("DATA_MGMT_ENTITIES_BY_CATEGORY_MODEL", "数据管理：按分类范围查实体（可选模型过滤）"),

    /** 数据管理：当前分类体系下全部有关联的实体（可选模型过滤） */
    DATA_MGMT_ENTITIES_ALL_IN_CATEGORY_TYPE("DATA_MGMT_ENTITIES_ALL_IN_CATEGORY_TYPE", "数据管理：分类体系下全部关联实体"),

    /** 数据管理：当前分类体系下未挂接分类的实体（可选模型过滤） */
    DATA_MGMT_ENTITIES_UNCATEGORIZED("DATA_MGMT_ENTITIES_UNCATEGORIZED", "数据管理：未分类实体"),

    ;

    /**
     * 场景编码
     */
    private final String code;

    /**
     * 场景名称
     */
    private final String name;

    /**
     * 根据编码获取枚举
     */
    public static EntityQueryScene getByCode(String code) {
        for (EntityQueryScene scene : values()) {
            if (scene.getCode().equals(code)) {
                return scene;
            }
        }
        return null;
    }

    /**
     * 判断编码是否有效
     */
    public static boolean isValid(String code) {
        return getByCode(code) != null;
    }
}
