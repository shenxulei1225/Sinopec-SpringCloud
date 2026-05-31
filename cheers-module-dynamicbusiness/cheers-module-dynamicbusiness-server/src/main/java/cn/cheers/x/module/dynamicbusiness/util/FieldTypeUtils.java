package cn.cheers.x.module.dynamicbusiness.util;

import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;

import java.util.Set;

/**
 * 字段类型工具类
 * 
 * 提供字段类型相关的工具方法，包括：
 * - 智能默认可查询判断
 * - 索引策略判断
 * 
 * 智能默认规则（需求：FR-081, FR-082, FR-083, FR-084）：
 * - 常用类型（STRING、INTEGER、DECIMAL、DATE、DATETIME、BOOLEAN、SELECT、ENTITY_REF）默认可查询
 * - 大文本和文件类型（TEXT、FILE、IMAGE）默认不可查询
 * 
 * @author yudao
 */
public class FieldTypeUtils {

    /**
     * 默认可查询的字段类型集合
     * 
     * 包含：TEXT（单行文本）、NUMBER、INTEGER、DATE、DATETIME、BOOLEAN、ENUM、ENTITY_REF、REFERENCE
     * 
     * 注意：这里的 TEXT 对应 FieldTypeEnum.TEXT，是单行文本类型
     * LONG_TEXT 是长文本/多行文本类型，默认不可查询
     */
    private static final Set<String> DEFAULT_SEARCHABLE_TYPES = Set.of(
            FieldTypeEnum.TEXT.getCode(),           // 单行文本
            FieldTypeEnum.NUMBER.getCode(),         // 数字（小数）
            FieldTypeEnum.INTEGER.getCode(),        // 整数
            FieldTypeEnum.DATE.getCode(),           // 日期
            FieldTypeEnum.DATETIME.getCode(),       // 日期时间
            FieldTypeEnum.BOOLEAN.getCode(),        // 布尔
            FieldTypeEnum.ENUM.getCode(),           // 枚举（单选/多选）
            FieldTypeEnum.ENTITY_REF.getCode(),     // 实体引用
            FieldTypeEnum.REFERENCE.getCode()       // 系统引用
    );

    /**
     * 默认不可查询的字段类型集合
     * 
     * 包含：LONG_TEXT（长文本）、JSON（复杂结构）
     * 
     * 注意：FILE 和 IMAGE 类型在当前枚举中未定义，
     * 如果后续添加，需要将其加入此集合
     */
    private static final Set<String> DEFAULT_NOT_SEARCHABLE_TYPES = Set.of(
            FieldTypeEnum.LONG_TEXT.getCode(),      // 长文本/多行文本
            FieldTypeEnum.JSON.getCode()            // JSON（复杂结构，不适合索引）
    );

    /**
     * 判断字段类型是否默认可查询
     * 
     * 智能默认规则：
     * - 常用类型默认可查询（true）
     * - 大文本和复杂类型默认不可查询（false）
     * - 未知类型默认不可查询（false）
     * 
     * @param fieldType 字段类型编码
     * @return true 表示默认可查询，false 表示默认不可查询
     */
    public static boolean isDefaultSearchable(String fieldType) {
        if (fieldType == null || fieldType.isEmpty()) {
            return false;
        }
        
        // 转换为大写进行比较（兼容大小写）
        String upperType = fieldType.toUpperCase();
        
        // 检查是否在默认可查询集合中
        if (DEFAULT_SEARCHABLE_TYPES.contains(upperType)) {
            return true;
        }
        
        // 检查是否在默认不可查询集合中
        if (DEFAULT_NOT_SEARCHABLE_TYPES.contains(upperType)) {
            return false;
        }
        
        // 未知类型默认不可查询
        return false;
    }

    /**
     * 判断字段类型是否默认可排序
     * 
     * 可排序的类型通常是可以进行比较的类型：
     * - 数值类型：NUMBER、INTEGER
     * - 日期类型：DATE、DATETIME
     * - 文本类型：TEXT（字典序排序）
     * 
     * @param fieldType 字段类型编码
     * @return true 表示默认可排序，false 表示默认不可排序
     */
    public static boolean isDefaultSortable(String fieldType) {
        if (fieldType == null || fieldType.isEmpty()) {
            return false;
        }
        
        String upperType = fieldType.toUpperCase();
        
        // 数值和日期类型默认可排序
        return Set.of(
                FieldTypeEnum.NUMBER.getCode(),
                FieldTypeEnum.INTEGER.getCode(),
                FieldTypeEnum.DATE.getCode(),
                FieldTypeEnum.DATETIME.getCode(),
                FieldTypeEnum.TEXT.getCode()
        ).contains(upperType);
    }

    /**
     * 获取字段类型的索引策略
     * 
     * 索引策略说明：
     * - GIN_INDEX: 使用 PostgreSQL GIN 索引，适合等值和包含查询
     * - FIELD_INDEX_TABLE: 使用 entity_field_index 索引表，适合范围查询和排序
     * - NONE: 不创建索引
     * 
     * @param fieldType 字段类型编码
     * @return 索引策略
     */
    public static IndexStrategy getIndexStrategy(String fieldType) {
        if (fieldType == null || fieldType.isEmpty()) {
            return IndexStrategy.NONE;
        }
        
        String upperType = fieldType.toUpperCase();
        
        // 数值和日期类型使用索引表（支持范围查询）
        if (Set.of(
                FieldTypeEnum.NUMBER.getCode(),
                FieldTypeEnum.INTEGER.getCode(),
                FieldTypeEnum.DATE.getCode(),
                FieldTypeEnum.DATETIME.getCode()
        ).contains(upperType)) {
            return IndexStrategy.FIELD_INDEX_TABLE;
        }
        
        // 文本、布尔、枚举、引用类型使用 GIN 索引
        if (Set.of(
                FieldTypeEnum.TEXT.getCode(),
                FieldTypeEnum.BOOLEAN.getCode(),
                FieldTypeEnum.ENUM.getCode(),
                FieldTypeEnum.ENTITY_REF.getCode(),
                FieldTypeEnum.REFERENCE.getCode()
        ).contains(upperType)) {
            return IndexStrategy.GIN_INDEX;
        }
        
        // 其他类型不创建索引
        return IndexStrategy.NONE;
    }

    /**
     * 索引策略枚举
     */
    public enum IndexStrategy {
        /**
         * 使用 PostgreSQL GIN 索引
         * 适合等值查询和包含查询
         */
        GIN_INDEX,
        
        /**
         * 使用 entity_field_index 索引表
         * 适合范围查询和排序
         */
        FIELD_INDEX_TABLE,
        
        /**
         * 不创建索引
         */
        NONE
    }
}
