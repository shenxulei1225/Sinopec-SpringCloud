package cn.cheers.x.module.dynamicbusiness.dal.dataobject.category;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 分类节点与 system 用户关联（人员编组）。
 */
@TableName("dynamic_category_user_relation")
@KeySequence("dynamic_category_user_relation_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUserRelationDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;

    /** system 用户 id（AdminUser） */
    private Long userId;

    private Integer sort;
}
