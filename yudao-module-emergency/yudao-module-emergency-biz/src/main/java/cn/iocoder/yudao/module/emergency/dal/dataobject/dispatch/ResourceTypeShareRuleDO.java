package cn.iocoder.yudao.module.emergency.dal.dataobject.dispatch;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 资源类型共享规则配置
 */
@TableName("resource_type_share_rule")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceTypeShareRuleDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资源类型：personnel/vehicle/equipment/material
     */
    private String resourceType;

    /**
     * 是否允许多事件共享：true-允许，false-不允许
     */
    private Boolean allowMultiEventShare;

    /**
     * 最大共享数量（当允许多事件共享时，限制最多可被多少个事件共享，NULL表示无限制）
     */
    private Integer maxShareCount;

    /**
     * 规则描述
     */
    private String description;
}



