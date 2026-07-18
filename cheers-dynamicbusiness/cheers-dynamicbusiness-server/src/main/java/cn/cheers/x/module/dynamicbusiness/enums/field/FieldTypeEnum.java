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
     * 实体引用类型（单选关联字段）
     * 
     * 用于建立业务实体之间的单选关联关系。
     * 需要配合 ModelFieldAssignmentDO.refLibraryId 使用，
     * 指向 RelationFieldLibraryDO 中定义的关联目标。
     * 
     * 存储格式：{ "device_id": 301, "device_id_name": "挖掘机" }
     * 
     * 需求：FR-BDA-030~034
     */
    ENTITY_REF("ENTITY_REF", "实体引用", true),

    /**
     * 多选实体引用类型（多选关联字段）
     * 
     * 用于建立业务实体之间的多选关联关系。
     * 支持关联多个目标实体，受 maxRelations 字段限制。
     * 
     * 存储格式：{ "devices": [301, 302], "devices_names": ["挖掘机", "吊车"] }
     * 
     * 需求：多选关联支持
     */
    ENTITY_REF_MULTI("ENTITY_REF_MULTI", "多选实体引用", true),

    /**
     * 批量实体引用类型
     *
     * 用于批量建立实体之间的关联关系，语义上等同于多选实体引用，
     * 在关联处理和目标信息解析上与 ENTITY_REF_MULTI 一致。
     */
    BATCH_ENTITY_REF("BATCH_ENTITY_REF", "批量实体引用", true),

    /**
     * 系统引用类型（固定列字段使用）
     * 
     * 用于固定列字段引用系统表（如用户、部门等）。
     * 与 ENTITY_REF 的区别：
     * - REFERENCE: 引用系统内置表
     * - ENTITY_REF: 引用动态业务实体
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
     * 判断是否为实体引用类型（包括单选和多选）
     */
    public static boolean isEntityRef(String code) {
        return ENTITY_REF.getCode().equalsIgnoreCase(code)
                || ENTITY_REF_MULTI.getCode().equalsIgnoreCase(code)
                || BATCH_ENTITY_REF.getCode().equalsIgnoreCase(code);
    }

    /**
     * 判断是否为单选实体引用类型
     */
    public static boolean isSingleEntityRef(String code) {
        return ENTITY_REF.getCode().equalsIgnoreCase(code);
    }

    /**
     * 判断是否为多选实体引用类型
     */
    public static boolean isMultiEntityRef(String code) {
        return ENTITY_REF_MULTI.getCode().equalsIgnoreCase(code);
    }
}
