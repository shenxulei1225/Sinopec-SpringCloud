package cn.cheers.x.module.dynamicbusiness.dal.dataobject.category;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 分类与实体链接 DO
 * <p>
 * 用于管理"分类即实体"的特殊关联关系。
 * 当分类节点需要作为实体使用时，通过此表建立分类与实体的对应关系。
 * 通过 CategoryEntityLinkService.isEntityCategory(categoryId) 判断分类是否为实体分类。
 */
@TableName("dynamic_category_entity_link")
@KeySequence("dynamic_category_entity_link_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryEntityLinkDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 关联的实体ID
     */
    private Long entityId;

    /**
     * 关联的实体模型ID
     * <p>
     * 用于定义该分类实体的字段结构。
     */
    private Long entityModelId;
}