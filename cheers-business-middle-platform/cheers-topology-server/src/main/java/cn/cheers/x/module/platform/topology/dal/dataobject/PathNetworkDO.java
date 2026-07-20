package cn.cheers.x.module.platform.topology.dal.dataobject;

import cn.cheers.x.framework.mybatis.core.type.JsonbStringTypeHandler;
import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@TableName(value = "platform_path_network", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PathNetworkDO extends TenantBaseDO {

    @TableId
    private String id;

    private String networkKind;

    private Long facilityId;

    private String scopeId;

    private String status;

    private Integer version;

    /** 路网显示名称（实例元数据） */
    private String displayName;

    /** 说明 */
    private String description;

    /** 适用设备类型 JSON 数组，如 ["HUMAN","GROUND_ROBOT"] */
    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String applicableEquipmentTypes;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String nodes;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String edges;
}
