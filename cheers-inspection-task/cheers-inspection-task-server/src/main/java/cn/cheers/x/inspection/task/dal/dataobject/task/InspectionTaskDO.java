package cn.cheers.x.inspection.task.dal.dataobject.task;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.model.task.ResourcePolicy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 巡检任务 DO。
 */
@TableName(value = "inspection_task", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 父任务ID。
     */
    private Long parentId;

    /**
     * 分类ID。
     */
    private Long categoryId;

    /**
     * 任务编码。
     */
    private String taskCode;

    /**
     * 任务名称。
     */
    private String taskName;

    /**
     * 状态。
     */
    private Integer status;

    /**
     * 是否启用。
     */
    private Boolean enabled;

    /**
     * 备注。
     */
    private String remark;

    // ==================== 继承配置 ====================

    /**
     * 是否继承父任务排期。
     */
    private Boolean inheritParentSchedule;

    /**
     * 是否继承父任务资源策略。
     */
    private Boolean inheritParentResourcePolicy;

    // ==================== 任务内容 ====================

    /**
     * 巡检内容（JSON 存储）。
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private InspectionContent inspectionContent;

    // ==================== 排期配置 ====================

    /**
     * 排期需求ID。
     */
    private Long scheduleRequirementId;

    /**
     * 排期策略ID。
     */
    private Long schedulePolicyId;

    /**
     * 资源策略（JSON 存储）。
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ResourcePolicy resourcePolicy;

    // ==================== 编排结果 ====================

    /**
     * 当前激活的编排批次ID。
     */
    private Long activePlanId;

    /**
     * 历史编排批次ID列表。
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> planIds = new ArrayList<>();

    // ==================== 路线快照（确认后写入） ====================

    /**
     * 已确认路网引用。
     */
    private String networkRef;

    /**
     * 已确认规划路线（JSON）。
     */
    private String plannedRoute;

    /**
     * 已确认路线时长估算（分钟）。
     */
    private Integer durationEstimateMinutes;

    /**
     * 巡检类型：HUMAN|GROUND_ROBOT|UAV。
     */
    private String inspectionType;

    /**
     * 关联路线方案台账 ID。
     */
    private Long routePlanId;
}
