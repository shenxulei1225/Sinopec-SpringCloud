package cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 实体访问权限 DO
 * 
 * 控制不同角色可以访问哪些业务实体
 * 当用户拥有多个角色时，权限取并集（最宽松）
 */
@TableName("dynamic_entity_access_permission")
@KeySequence("dynamic_entity_access_permission_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityAccessPermissionDO extends TenantBaseDO {

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
     * 实体ID
     */
    private Long entityId;

    /**
     * 是否可查看
     */
    private Boolean canView;
}
