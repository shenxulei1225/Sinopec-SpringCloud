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
 * {@link #ENTITIES_UNCATEGORIZED}、{@link #ENTITIES_BY_CATEGORY_LINK}、{@link #ENTITIES_DETAIL}。</p>
 */
@AllArgsConstructor
public enum EntityQueryScene {

    /** 有分类列：按分类范围查实体（含子树；未选节点≡整树）；可叠 modelIds；须显式传 categoryTypeCode；可选 categoryViaRefPathCode 走经 REF 反查 */
    ENTITIES_BY_CATEGORY("ENTITIES_BY_CATEGORY", "按分类查实体"),

    /** 无分类列：按型号查实体；未传 modelIds 时按类型（可叠业务域/划分） */
    ENTITIES_BY_MODEL("ENTITIES_BY_MODEL", "按型号或类型查实体"),

    /**
     * 数据管理「未分类」：当前 categoryTypeCode 下未挂接任何分类节点的实体
     * （差集 = 类型范围内实体 − relation/link 已关联该种类任一节点）。
     */
    ENTITIES_UNCATEGORIZED("ENTITIES_UNCATEGORIZED", "未挂分类的实体"),

    /** 节点绑实体：点分类节点取绑定实体 */
    ENTITIES_BY_CATEGORY_LINK("ENTITIES_BY_CATEGORY_LINK", "分类节点绑定实体"),

    /** 按实体编号取详情 */
    ENTITIES_DETAIL("ENTITIES_DETAIL", "实体详情"),
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
