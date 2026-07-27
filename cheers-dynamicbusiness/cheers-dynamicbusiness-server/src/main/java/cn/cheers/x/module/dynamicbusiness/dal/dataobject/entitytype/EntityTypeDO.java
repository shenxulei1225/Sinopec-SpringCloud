package cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype;

import cn.cheers.x.framework.mybatis.core.type.JsonbStringTypeHandler;
import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

/**
 * 业务类型 DO
 * 
 * 业务含义:用于组织和管理业务元模型,一个 EntityType 表达一种本质不同的业务对象或过程。
 * 
 * 存储策略:
 * - 字段直接包含存储配置(storageType, dedicatedTableName等),实现元模型与存储一体化。
 * 
 * 业务关联:
 * - EntityType 可以与其他 EntityType 建立关联关系
 * - 关联关系存储在 dynamic_entity_type_relation 表中
 * 
 * @author yudao
 */
@TableName(value = "dynamic_entity_type", autoResultMap = true)
@KeySequence("dynamic_entity_type_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityTypeDO extends TenantBaseDO {

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
     * 父级业务类型id（历史字段，新数据请使用 groupName 分组）
     */
    private Long parentId;

    /**
     * 数据类型分组名（仅导航归类，无继承语义）
     */
    @TableField(value = "group_name", updateStrategy = FieldStrategy.ALWAYS)
    private String groupName;

    /**
     * 入口类型：NATIVE=数据类型；DOMAIN=子数据类型；SCOPE=划分数据；CATEGORY=旧分类即实体
     */
    @TableField("entry_kind")
    private String entryKind;

    /**
     * 子数据类型/划分/分类数据时指向的存储数据类型编码（如 task、equipment）
     */
    @TableField("base_entity_type_code")
    private String baseEntityTypeCode;

    /**
     * 业务域（Domain）；子数据类型入口必填，其余入口为空。
     */
    @TableField("domain")
    private String domain;

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

    public static final String ENTRY_KIND_NATIVE = "NATIVE";
    public static final String ENTRY_KIND_DOMAIN = "DOMAIN";
    public static final String ENTRY_KIND_SCOPE = "SCOPE";
    /** 旧「分类即实体」入口；新划分数据请用 {@link #ENTRY_KIND_SCOPE} */
    public static final String ENTRY_KIND_CATEGORY = "CATEGORY";

    /**
     * 可用的关联字段定义(JSON格式)
     */
    @TableField(value = "association_fields", typeHandler = JsonbStringTypeHandler.class)
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
    @TableField(value = "physical_column_mapping", typeHandler = JsonbStringTypeHandler.class)
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
