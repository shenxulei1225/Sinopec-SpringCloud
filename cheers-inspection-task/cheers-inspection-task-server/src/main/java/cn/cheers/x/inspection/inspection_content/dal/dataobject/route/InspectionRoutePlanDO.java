package cn.cheers.x.inspection.inspection_content.dal.dataobject.route;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 路线方案台账 DO。
 */
@TableName("inspection_route_plan")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionRoutePlanDO extends BaseDO {

    @TableId
    private Long id;

    private Long facilityId;

    private String name;

    private String networkRef;

    private String inspectionType;

    private String stopIds;

    private String plannedRoute;

    private Integer durationEstimateMinutes;

    private Long taskId;
}
