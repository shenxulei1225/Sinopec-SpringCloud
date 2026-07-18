package cn.cheers.x.module.dynamicbusiness.dal.dataobject.category;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 分类级权限 DO
 * 
 * 控制不同角色可以访问哪些分类
 * 当用户拥有多个角色时，权限取并集（最宽松）
 */
@TableName("dynamic_category_permission")
@KeySequence("dynamic_category_permission_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryPermissionDO extends TenantBaseDO {

    /**
     * 权限ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 是否可查看
     */
    private Boolean canView;

    /**
     * 是否可管理（创建、编辑、删除子分类）
     */
    private Boolean canManage;
}
