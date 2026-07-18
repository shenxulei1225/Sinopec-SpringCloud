package cn.cheers.x.module.dynamicbusiness.dal.dataobject.field;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 字段定义 DO
 */
@TableName("dynamic_field")
@KeySequence("dynamic_field_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
        * 字段编码，全局唯一
        */
    private String code;

    /**
        * 字段名称
        */
    private String name;

    /**
     * 字段类型（TEXT/NUMBER/DATE/BOOLEAN/ENUM/JSON 等），对应表列 {@code dynamic_field.type}。
     */
    @TableField("type")
    private String type;

    /**
        * 单位（数值类字段必填）
        */
    private String unit;

    /**
     * 描述
     */
    private String description;

    /**
        * 字段来源（SYSTEM/USER）
        */
    private String source;

    /**
        * 状态（1启用，0禁用）
        */
    private Integer status;

    // ========== 扩展字段查询相关 ==========

    /**
     * 最大关联数量（ENTITY_REF_MULTI 类型专用）
     *
     * 限制多选关联字段可以关联的最大实体数量。
     * - null 或 0：不限制
     * - 正整数：最多可关联的实体数量
     *
     * 需求：多选关联支持
     */
    private Integer maxRelations;

    /**
     * 索引策略
     *
     * 根据字段类型自动选择合适的索引策略：
     * - GIN: 适用于等值查询和包含查询（文本、布尔、枚举、关联字段）
     * - BTREE: 适用于范围查询和排序（数字、日期字段）
     * - NONE: 不创建索引（大文本、文件、复杂 JSON 等）
     *
     * 需求：FR-BDA-083
     */
    private String indexStrategy;

    /**
     * 枚举选项列表（ENUM 类型专用）
     *
     * JSON 数组格式，如：[{"label":"待处理","value":"pending"},{"label":"已完成","value":"completed"}]
     * 每个选项包含 label（显示名称）和 value（存储值）
     */
    private String options;

    /**
     * 引用 Provider 编码（REFERENCE/ENTITY_REF 可选）
     */
    private String providerCode;

    /**
     * 引用语义类型（USER/DEPT/ROLE/MATERIAL...）
     */
    private String semanticType;
}

