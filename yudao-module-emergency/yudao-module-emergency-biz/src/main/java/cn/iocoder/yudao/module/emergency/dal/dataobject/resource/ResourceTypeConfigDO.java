package cn.iocoder.yudao.module.emergency.dal.dataobject.resource;

import cn.iocoder.yudao.module.emergency.dal.dataobject.EmergencyBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 资源类型配置 DO
 *
 * 用于配置资源类型的共享规则，控制资源是否允许多事件共享
 *
 * @author 芋道源码
 */
@TableName("resource_type_config")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceTypeConfigDO extends EmergencyBaseDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 资源类型：personnel/vehicle/equipment/material
     */
    private String resourceType;

    /**
     * 是否允许多事件共享
     * true：允许多个事件同时使用该类型的资源
     * false：不允许，一个资源只能被一个事件使用
     */
    private Boolean allowMultiEventShare;

    /**
     * 描述
     */
    private String description;
}
