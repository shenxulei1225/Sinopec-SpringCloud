package cn.iocoder.yudao.module.inspection.task.dal.dataobject.execution;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 巡检任务执行记录 DO。
 *
 * <p>表示某条计划点真实执行后的结果记录。</p>
 */
@TableName("inspection_task_execution")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskExecutionDO extends BaseDO {

    /**
     * 主键 ID。
     */
    @TableId
    private Long id;

    /**
     * 执行记录编码。
     */
    private String executionCode;

    /**
     * 任务 ID。
     */
    private Long taskId;

    /**
     * 计划批次 ID。
     *
     * <p>关联 {@link cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule.InspectionTaskSchedulePlanDO}。</p>
     */
    private Long planId;

    /**
     * 对应计划点 ID。
     */
    private Long scheduleId;

    /**
     * 资源 ID。
     */
    private Long resourceId;

    /**
     * 执行日期。
     */
    private LocalDate executionDate;

    /**
     * 实际开始时间。
     */
    private LocalDateTime executionStartTime;

    /**
     * 实际结束时间。
     */
    private LocalDateTime executionEndTime;

    /**
     * 执行状态。
     */
    private Integer executionStatus;

    /**
     * 执行结果状态。
     */
    private Integer resultStatus;

    /**
     * 结果明细 JSON。
     */
    private String resultJson;

    /**
     * 异常明细 JSON。
     */
    private String exceptionJson;
}
