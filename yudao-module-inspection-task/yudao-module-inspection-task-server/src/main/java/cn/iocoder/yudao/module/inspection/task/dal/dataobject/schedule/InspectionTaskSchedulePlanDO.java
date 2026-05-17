package cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 巡检任务编排批次 DO。
 *
 * <p>编排引擎生成的一批排期计划，包含多个排期点。</p>
 */
@TableName("inspection_task_schedule_plan")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskSchedulePlanDO extends BaseDO {

    /**
     * 主键 ID。
     */
    @TableId
    private Long id;

    /**
     * 批次编码。
     */
    private String planCode;

    /**
     * 关联的任务 ID。
     */
    private Long taskId;

    /**
     * 排期需求 ID（关联到 InspectionTaskScheduleRequirement）。
     */
    private Long scheduleRequirementId;

    /**
     * 编排时间范围开始。
     */
    private LocalDate horizonStartDate;

    /**
     * 编排时间范围结束。
     */
    private LocalDate horizonEndDate;

    /**
     * 编排的排期点数量。
     */
    private Integer scheduleCount;

    /**
     * 状态。
     *
     * <ul>
     *     <li>1 - 待生效</li>
     *     <li>2 - 已激活</li>
     *     <li>3 - 已停用</li>
     * </ul>
     */
    private Integer planStatus;

    /**
     * 触发类型。
     *
     * <ul>
     *     <li>1 - 手动编排</li>
     *     <li>2 - 定时任务触发</li>
     *     <li>3 - 故障重排</li>
     * </ul>
     */
    private Integer triggerType;

    /**
     * 触发人。
     */
    private String triggerBy;

    /**
     * 激活人ID。
     */
    private Long activatedBy;

    /**
     * 激活时间。
     */
    private LocalDateTime activatedAt;

    /**
     * 备注。
     */
    private String remark;
}
