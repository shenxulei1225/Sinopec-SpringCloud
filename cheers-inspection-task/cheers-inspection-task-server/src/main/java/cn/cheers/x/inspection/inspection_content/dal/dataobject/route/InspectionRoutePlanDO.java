package cn.cheers.x.inspection.inspection_content.dal.dataobject.route;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 路线方案台账。
 * <p>保存规划路线时写入。主键走 PostgreSQL 序列 {@code inspection_route_plan_seq}，
 * 禁止插入时空着 id——Postgres 上 MyBatis-Plus 不会自动编号。
 */
@TableName("inspection_route_plan")
@KeySequence("inspection_route_plan_seq")
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
