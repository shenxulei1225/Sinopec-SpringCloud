package cn.cheers.x.inspection.inspection_content.dal.dataobject.binding;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 对象↔拓扑停靠点绑定 DO。
 */
@TableName("inspection_object_station_binding")
@Data
@EqualsAndHashCode(callSuper = true)
public class ObjectStationBindingDO extends BaseDO {

    @TableId
    private Long id;

    private Long facilityId;

    private Long objectId;

    private String stationNodeId;

    private Integer workMinutes;

    private Integer sortNo;
}
