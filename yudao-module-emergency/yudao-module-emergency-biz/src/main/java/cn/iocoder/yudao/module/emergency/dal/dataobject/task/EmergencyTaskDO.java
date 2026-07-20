package cn.iocoder.yudao.module.emergency.dal.dataobject.task;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import cn.iocoder.yudao.module.emergency.framework.mybatis.PostgreSQLJsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 应急任务
 */
@TableName(value = "emergency_task", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyTaskDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务编号（唯一，自动生成，格式：TASK-YYYYMMDD-XXXXXX）
     */
    @TableField("task_code")
    private String taskCode;

    @TableField("event_id")
    private Long eventId;

    /**
     * 响应ID（预警阶段时为NULL）
     */
    @TableField("response_id")
    private Long responseId;

    @TableField("plan_step_id")
    private Long planStepId;

    /**
     * 父任务ID（用于构建任务层级结构）
     * 顶级任务的parentId为null，子任务的parentId指向父任务ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 指令ID（用于子任务，记录该子任务来自哪个指令）
     */
    @TableField("command_id")
    private Long commandId;

    /**
     * 任务标题
     */
    @TableField("title")
    private String title;

    /**
     * 任务内容
     */
    @TableField("content")
    private String content;

    /**
     * 任务类型
     */
    @TableField("task_type")
    private String taskType;

    /**
     * 任务阶段（WARNING预警/RESPONSE响应）
     */
    @TableField("stage")
    private String stage;

    /**
     * 任务状态（UNASSIGNED/pending/in_progress/completed/terminated）
     */
    @TableField("status")
    private String status;

    /**
     * 优先级（LOW/NORMAL/HIGH/URGENT）
     */
    @TableField("priority")
    private String priority;

    /**
     * 是否关键任务
     */
    @TableField("is_key")
    private Boolean isKey;

    /**
     * 分配人
     */
    @TableField("assignee")
    private String assignee;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 完成时间
     */
    @TableField("complete_time")
    private LocalDateTime completeTime;

    /**
     * 截止时间
     */
    @TableField("due_time")
    private LocalDateTime dueTime;

    /**
     * 执行时限（分钟，从预案步骤传递，用于计算due_time和超时告警）
     */
    @TableField("time_limit")
    private Integer timeLimit;

    /**
     * 实际耗时（分钟）
     */
    @TableField("actual_duration")
    private Integer actualDuration;

    /**
     * 执行记录（JSON数组）
     */
    @TableField(value = "execution_records", typeHandler = PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> executionRecords;

    /**
     * 乐观锁版本号
     */
    @TableField("version")
    private Integer version;
}

