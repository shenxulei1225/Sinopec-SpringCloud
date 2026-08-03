package cn.cheers.x.module.dynamicbusiness.dal.dataobject.category;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 分类—分类跨种类关联 DO。
 *
 * <p>宿主分类可见成员分类（如学习主题 → 设备分类）。同种类层级仍用 {@code dynamic_category.parent_id}，
 * 禁止写入本表。</p>
 *
 * <p>运行时物理表为 {@code dynamic_category_category_relation_t{tenantId}}（基表仅模板）。</p>
 */
@TableName("dynamic_category_category_relation")
@KeySequence("dynamic_category_category_relation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCategoryRelationDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 宿主分类节点 ID */
    private Long hostCategoryId;

    /** 宿主分类种类编码（如 inspection_item） */
    private String hostCategoryTypeCode;

    /** 成员分类节点 ID */
    private Long memberCategoryId;

    /** 成员分类种类编码（如 equipment）；必须与宿主种类不同 */
    private String memberCategoryTypeCode;

    /** 同一宿主下成员排序 */
    private Integer sort;
}
