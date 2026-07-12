package cn.cheers.x.module.platform.topology.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.type.JsonbStringTypeHandler;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@TableName(value = "platform_topology_graph", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopologyGraphDO extends TenantBaseDO {

    @TableId
    private String id;

    private Long siteId;

    private String status;

    private Integer version;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String nodes;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String edges;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String zoneBoundaries;
}
