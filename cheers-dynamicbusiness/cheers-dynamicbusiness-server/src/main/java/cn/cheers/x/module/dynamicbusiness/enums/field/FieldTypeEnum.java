package cn.cheers.x.module.dynamicbusiness.enums.field;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 字段类型枚举
 * 
 * 定义系统支持的所有字段类型，包括：
 * - 基础类型：TEXT, NUMBER, DATE, DATETIME, BOOLEAN, ENUM, JSON
 * - 关联类型：ENTITY_REF（单选关联字段）、ENTITY_REF_MULTI（多选关联字段）
 * - 引用类型：REFERENCE（固定列字段使用，引用系统表）
 * 
 * @author yudao
 */
@Getter
@AllArgsConstructor
public enum FieldTypeEnum {

    /**
     * 文本类型
     */
    TEXT("TEXT", "文本", false),

    /**
     * 长文本类型
     */
    LONG_TEXT("LONG_TEXT", "长文本", false),

    /**
     * 数字类型
     */
    NUMBER("NUMBER", "数字", false),

    /**
     * 整数类型
     */
    INTEGER("INTEGER", "整数", false),

    /**
     * 日期类型
     */
    DATE("DATE", "日期", false),

    /**
     * 日期时间类型
     */
    DATETIME("DATETIME", "日期时间", false),

    /**
     * 布尔类型
     */
    BOOLEAN("BOOLEAN", "布尔", false),

    /**
     * 枚举类型
     */
    ENUM("ENUM", "枚举", false),

    /**
     * JSON类型
     */
    JSON("JSON", "JSON", false),

    /**
 * 引用数据（单选）
 *
 * <p>用户在字段库只选「引用数据」并指定目标数据类型；挂到型号后由系统判定：</p>
 * <ul>
 *   <li>目标 = 当前型号所属类型 → 同类型引用（树选，值记在本字段；不参与组织树同步）</li>
 *   <li>目标 ≠ 当前型号所属类型 → 跨类型引用（关联选择）</li>
 * </ul>
 * <p>组织树上级不走本类型用户配置，见系统字段 {@link #ENTITY_SELF_REF}（TREE_PARENT）。</p>
 */
ENTITY_REF("ENTITY_REF", "引用数据", true),

/**
 * 引用数据（多选）
 *
 * 支持关联多个目标实体，受 maxRelations 字段限制。
 * 跨/同类型同样按目标与当前型号是否一致由后台判定。
 */
ENTITY_REF_MULTI("ENTITY_REF_MULTI", "引用数据(多选)", true),

/**
 * 引用数据（批量）
 *
 * 语义上等同于多选引用，在关联处理上与 ENTITY_REF_MULTI 一致。
 */
BATCH_ENTITY_REF("BATCH_ENTITY_REF", "引用数据(批量)", true),

/**
 * 系统组织上级（内部类型，不对用户字段库展示）
 *
 * <p>仅高级分类 ensure 使用：编码 parentId、semanticType=TREE_PARENT；
 * 读写实体上级编号/树路径并同步分类树。用户手建引用一律用 {@link #ENTITY_REF}。</p>
 */
ENTITY_SELF_REF("ENTITY_SELF_REF", "系统组织上级", true),

    /**
     * 系统引用类型（固定列字段使用）
     *
     * 用于固定列字段引用系统表（如用户、部门等）。
     * 与 ENTITY_REF 的区别：
     * - REFERENCE: 引用系统内置表
     * - ENTITY_REF: 引用动态业务实体（对人展示为「引用数据」）
     */
    REFERENCE("REFERENCE", "系统引用", true);

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 是否为关联类型
     */
    private final boolean relationType;

    /**
     * 根据编码获取枚举
     */
    public static FieldTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(e -> e.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }

    /**
     * 判断是否为关联类型
     */
    public static boolean isRelationType(String code) {
        FieldTypeEnum type = getByCode(code);
        return type != null && type.isRelationType();
    }

    /**
     * 判断是否为引用数据（ENTITY_REF 族，含单选/多选/批量）。不含系统组织上级。
     */
    public static boolean isEntityRef(String code) {
        return ENTITY_REF.getCode().equalsIgnoreCase(code)
                || ENTITY_REF_MULTI.getCode().equalsIgnoreCase(code)
                || BATCH_ENTITY_REF.getCode().equalsIgnoreCase(code);
    }

    /** 系统组织上级（内部 ENTITY_SELF_REF）。 */
    public static boolean isEntitySelfRef(String code) {
        return ENTITY_SELF_REF.getCode().equalsIgnoreCase(code);
    }

    /**
     * 相对某型号是否为同类型引用：目标数据类型编码与型号所属类型一致。
     * 用户字段统一存 ENTITY_REF*，由此判定走树选还是跨类型关联 UI。
     */
    public static boolean isSameTypeRefTarget(String modelEntityTypeCode, String targetEntityTypeCode) {
        if (modelEntityTypeCode == null || modelEntityTypeCode.isBlank()
                || targetEntityTypeCode == null || targetEntityTypeCode.isBlank()) {
            return false;
        }
        return modelEntityTypeCode.trim().equalsIgnoreCase(targetEntityTypeCode.trim());
    }

    /**
     * 判断是否为单选实体引用类型（跨类型）
     */
    public static boolean isSingleEntityRef(String code) {
        return ENTITY_REF.getCode().equalsIgnoreCase(code);
    }

    /**
     * 判断是否为多选实体引用类型
     */
    public static boolean isMultiEntityRef(String code) {
        return ENTITY_REF_MULTI.getCode().equalsIgnoreCase(code)
                || BATCH_ENTITY_REF.getCode().equalsIgnoreCase(code);
    }
}
