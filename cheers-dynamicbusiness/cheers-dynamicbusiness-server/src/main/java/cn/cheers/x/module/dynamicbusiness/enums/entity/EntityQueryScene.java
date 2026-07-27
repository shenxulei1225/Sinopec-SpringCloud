package cn.cheers.x.module.dynamicbusiness.enums.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 实体统一查询场景（query-by-scene）。
 *
 * <p>正式场景：{@link #ENTITIES_BY_CATEGORY}、{@link #ENTITIES_BY_MODEL}、
 * {@link #ENTITIES_BY_CATEGORY_LINK}、{@link #ENTITIES_DETAIL}。
 * {@link #ROOT_ENTITY_SUBTREE} 仅兼容保留，本期数据管理不对对接。</p>
 */
@AllArgsConstructor
public enum EntityQueryScene {

    /** 有分类列：按分类范围查实体（含子树；未选节点≡整树）；可叠 modelIds；可选 categoryViaRefPathCode 走经 REF 反查 */
    ENTITIES_BY_CATEGORY("ENTITIES_BY_CATEGORY", "按分类查实体"),

    /** 无分类列：按型号查实体；未传 modelIds 时按类型（可叠业务域/划分） */
    ENTITIES_BY_MODEL("ENTITIES_BY_MODEL", "按型号或类型查实体"),

    /** 节点绑实体：点分类节点取绑定实体 */
    ENTITIES_BY_CATEGORY_LINK("ENTITIES_BY_CATEGORY_LINK", "分类节点绑定实体"),

    /** 按实体编号取详情 */
    ENTITIES_DETAIL("ENTITIES_DETAIL", "实体详情"),

    /** 实体 parent_id 子树；本期数据管理不对对接，仅兼容保留 */
    ROOT_ENTITY_SUBTREE("ROOT_ENTITY_SUBTREE", "实体子树（兼容）"),
    ;

    private final String code;
    private final String name;

    private static final Map<String, EntityQueryScene> BY_CODE = new HashMap<>();

    static {
        for (EntityQueryScene scene : values()) {
            BY_CODE.put(scene.code, scene);
        }
        // 旧码短暂映射；新前端只发正式 code
        BY_CODE.put("PATTERN_A_C_ENTITIES_BY_CATEGORY", ENTITIES_BY_CATEGORY);
        BY_CODE.put("DATA_MGMT_ENTITIES_BY_CATEGORY_MODEL", ENTITIES_BY_CATEGORY);
        BY_CODE.put("DATA_MGMT_ENTITIES_ALL_IN_CATEGORY_TYPE", ENTITIES_BY_CATEGORY);
        BY_CODE.put("PATTERN_B_ENTITIES_BY_CATEGORY", ENTITIES_BY_CATEGORY);
        BY_CODE.put("PATTERN_B_ENTITIES_BY_MODEL", ENTITIES_BY_MODEL);
        BY_CODE.put("PATTERN_ABC_ALL_ENTITIES_BY_BUSINESS_TYPE", ENTITIES_BY_MODEL);
        BY_CODE.put("PATTERN_D_ENTITY_DETAILED_INFO", ENTITIES_DETAIL);
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @JsonCreator
    public static EntityQueryScene from(String value) {
        EntityQueryScene scene = getByCode(value);
        if (scene == null) {
            throw new IllegalArgumentException("不支持的查询场景: " + value);
        }
        return scene;
    }

    public static EntityQueryScene getByCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return BY_CODE.get(code.trim());
    }

    public static boolean isValid(String code) {
        return getByCode(code) != null;
    }
}
