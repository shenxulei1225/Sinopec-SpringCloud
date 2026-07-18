package cn.cheers.x.inspection.task.dal.dataobject.schedule;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 巡检任务排期 DO。
 *
 * <p>表示某次具体的排期执行记录，通常由调度引擎生成。</p>
 */
@TableName(value = "inspection_task_schedule", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskScheduleDO extends BaseDO {

    /**
     * 主键 ID。
     */
    @TableId
    private Long id;

    /**
     * 编排批次 ID。
     */
    private Long planId;

    /**
     * 关联的任务 ID。
     */
    private Long taskId;

    /**
     * 关联的任务名称（冗余字段，用于展示）。
     * <p>注意：此字段由编排时写入或查询时填充，不保证实时性。</p>
     */
    private String taskName;

    /**
     * 计划执行时间。
     */
    private LocalDateTime scheduledTime;

    /**
     * 实际开始时间。
     */
    private LocalDateTime startTime;

    /**
     * 实际结束时间。
     */
    private LocalDateTime endTime;

    /**
     * 状态。
     *
     * <ul>
     *     <li>1 - 待执行</li>
     *     <li>2 - 执行中</li>
     *     <li>3 - 已完成</li>
     *     <li>4 - 已超时</li>
     *     <li>5 - 已取消</li>
     * </ul>
     */
    private Integer status;

    /**
     * 执行人 ID。
     */
    private Long executorId;

    /**
     * 执行人名称。
     */
    private String executorName;

    /**
     * 巡检结果摘要（JSON 格式）。
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object resultSummary;

    /**
     * 备注。
     */
    private String remark;
}
