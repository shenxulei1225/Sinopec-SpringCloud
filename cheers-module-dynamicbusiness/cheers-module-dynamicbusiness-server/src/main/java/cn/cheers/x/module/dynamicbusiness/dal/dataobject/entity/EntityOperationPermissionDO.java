package cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 实体操作权限 DO
 * 
 * 控制不同角色可以对实体执行哪些操作（创建、更新、删除）
 * 当用户拥有多个角色时，权限取并集（最宽松）
 */
@TableName("dynamic_entity_operation_permission")
@KeySequence("dynamic_entity_operation_permission_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityOperationPermissionDO extends TenantBaseDO {

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
     * 实体ID（为空时表示对模型下所有实体的权限）
     */
    private Long entityId;

    /**
     * 模型ID（用于模型级别的权限控制）
     */
    private Long modelId;

    /**
     * 是否可创建
     */
    private Boolean canCreate;

    /**
     * 是否可更新
     */
    private Boolean canUpdate;

    /**
     * 是否可删除
     */
    private Boolean canDelete;
}
