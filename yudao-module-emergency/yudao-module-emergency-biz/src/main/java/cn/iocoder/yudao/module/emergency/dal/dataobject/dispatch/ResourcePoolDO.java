package cn.iocoder.yudao.module.emergency.dal.dataobject.dispatch;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 资源池
 */
@TableName("resource_pool")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourcePoolDO extends TenantBaseDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 资源名称
     */
    private String name;

    /**
     * 资源类型：personnel/vehicle/equipment/material
     */
    private String type;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态：available/in_use/maintenance/disabled
     */
    private String status;

    /**
     * 位置信息 JSON
     */
    private String location;

    /**
     * 联系信息 JSON
     */
    private String contactInfo;

    /**
     * 容量/数量
     */
    private Integer capacity;

    /**
     * 单位
     */
    private String unit;

    /**
     * 所属组织ID
     */
    private Long organizationId;
}



