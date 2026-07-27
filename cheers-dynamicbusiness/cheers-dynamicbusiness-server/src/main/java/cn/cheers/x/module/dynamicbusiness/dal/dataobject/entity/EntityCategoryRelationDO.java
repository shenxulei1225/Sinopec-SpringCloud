package cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 业务实体与分类关联 DO
 * 
 * <p>存储实体与分类的多对多关联关系。</p>
 * 
 * <h3>身份约定</h3>
 * <p>唯一身份为「租户 + 实际存储类型 + 实体ID + 分类ID」（见 V24 迁移的
 * {@code uk_dynamic_entity_category_relation_identity}）；{@code domain} 是实体业务域的
 * 镜像，不参与唯一键。一个实体在同一分类上最多一行：{@code deleted=false} 表示已关联，
 * {@code deleted=true} 表示显式排除（型号挂分类推导时用于剔除）。</p>
 */
@TableName("dynamic_entity_category_relation")
@KeySequence("dynamic_entity_category_relation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityCategoryRelationDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 实体ID
     */
    private Long entityId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 实际存储类型编码（如 equipment、task）。
     *
     * <p>注册编码（如子数据类型入口 task_patrol）不得写入本列：
     * 实体身份是「实际存储类型 + 实体ID」，业务域另由 {@link #domain} 承载。</p>
     */
    private String entityTypeCode;

    /**
     * 业务域（Domain），实体行 domain 的同步镜像。
     *
     * <p>随实体迁移业务域而更新，仅作查询维度，不参与关联唯一身份
     * （唯一键为 租户 + entityTypeCode + entityId + categoryId）。</p>
     */
    private String domain;

    /**
     * 分类视图下的排序（同一分类上下文内）
     */
    private Integer sort;
}
