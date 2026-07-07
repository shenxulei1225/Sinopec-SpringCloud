package cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

/**
 * 业务类型 DO
 * 
 * 业务含义:用于组织和管理业务元模型,一个 BusinessType 表达一种本质不同的业务对象或过程。
 * 
 * 存储策略:
 * - 字段直接包含存储配置(storageType, dedicatedTableName等),实现元模型与存储一体化。
 * 
 * 业务关联:
 * - BusinessType 可以与其他 BusinessType 建立关联关系
 * - 关联关系存储在 dynamic_business_type_relation 表中
 * 
 * @author yudao
 */
@TableName("dynamic_business_type")
@KeySequence("dynamic_business_type_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessTypeDO extends TenantBaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务类型编码(租户内唯一)
     * 
     * 命名规范:小写字母 + 连字符,如 task-management、production-planning
     */
    private String code;

    /**
     * 业务类型名称
     * 
     * 如:任务管理、生产计划
     */
    private String name;

    /**
     * 父级业务类型id
     */
    private Long parentId;

    /**
     * 描述
     */
    private String description;

    /**
     * 图标
     */
    private String icon;

    /**
     * 别名（业务数据是什么，如：产品、设备、人员等）
     */
    private String alias;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     * 
     * 可选值:active、inactive
     */
    private String status;

    /**
     * 业务类型级别
     *
     * 可选值:{@link #TYPE_LEVEL_SYSTEM}、{@link #TYPE_LEVEL_USER}
     */
    @TableField("type_level")
    private String typeLevel;

    // ========== 常量 ==========

    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";

    public static final String TYPE_LEVEL_SYSTEM = "SYSTEM";
    public static final String TYPE_LEVEL_USER = "USER";

    /**
     * 可用的关联字段定义(JSON格式)
     */
    @TableField(value = "association_fields", typeHandler = JacksonTypeHandler.class)
    private String associationFields;

    /**
     * 存储类型
     *
     * <ul>
     *   <li>GENERIC:通用表存储</li>
     *   <li>DEDICATED:专用表存储</li>
     * </ul>
     */
    private String storageType;

    /**
     * 专用表名
     */
    private String dedicatedTableName;

    /**
     * 是否启用规则引擎
     */
    private Boolean enableRuleEngine;

    /**
     * 物理列映射配置(JSON 格式)
     */
    @TableField(value = "physical_column_mapping", typeHandler = JacksonTypeHandler.class)
    private String physicalColumnMapping;

    /**
     * 判断是否启用
     */
    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public Long getParentId() { return parentId; }
    public Integer getSort() { return sort; }
    public String getStatus() { return status; }
    public String getStorageType() { return storageType; }
    public String getDedicatedTableName() { return dedicatedTableName; }
    public boolean isActive() {
        return STATUS_ACTIVE.equals(this.status);
    }
}
