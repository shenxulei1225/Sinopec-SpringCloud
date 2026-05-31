package cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 字段级权限 DO
 * 
 * 控制不同角色可以查看/编辑哪些字段
 * 当用户拥有多个角色时，权限取并集（最宽松）
 */
@TableName("dynamic_entity_field_permission")
@KeySequence("dynamic_entity_field_permission_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityFieldPermissionDO extends TenantBaseDO {

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
     * 模型ID
     */
    private Long modelId;

    /**
     * 字段ID
     */
    private Long fieldId;

    /**
     * 是否可查看
     */
    private Boolean canView;

    /**
     * 是否可编辑
     */
    private Boolean canEdit;
}
