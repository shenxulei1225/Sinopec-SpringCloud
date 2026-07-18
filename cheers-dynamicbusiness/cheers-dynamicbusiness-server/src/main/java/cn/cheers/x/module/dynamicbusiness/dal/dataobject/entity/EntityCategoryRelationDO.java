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
 * <h3>索引建议</h3>
 * <pre>
 * -- 核心索引：根据 Category 查询 Entity（最常用）
 * CREATE INDEX idx_category_business ON dynamic_entity_category_relation 
 *     (category_id, entity_type_code);
 * 
 * -- 辅助索引：根据 Entity 查询 Category
 * CREATE INDEX idx_entity ON dynamic_entity_category_relation (entity_id);
 * 
 * -- 辅助索引：根据业务类型批量操作
 * CREATE INDEX idx_business_type ON dynamic_entity_category_relation (entity_type_code);
 * </pre>
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
     * 业务类型编码
     *
     * <p>用于按业务类型过滤，提高查询性能。</p>
     * <p>例如：equipment、task、personnel</p>
     */
    private String entityTypeCode;

    /**
     * 分类视图下的排序（同一分类上下文内）
     */
    private Integer sort;
}
