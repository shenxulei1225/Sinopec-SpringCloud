package cn.iocoder.yudao.module.emergency.dal.dataobject.command;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 指令步骤
 * 
 * 指令执行的具体步骤，具有时间控制
 */
@TableName("emergency_command_step")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyCommandStepDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 指令ID
     */
    private Long commandId;

    /**
     * 步骤内容
     */
    private String stepContent;

    /**
     * 执行时限（分钟）
     * 必须大于0且不超过1440分钟（24小时）
     */
    private Integer timeLimitMinutes;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 状态：pending/in_progress/completed/timeout/cancelled
     */
    private String status;

    /**
     * 超时标记
     */
    private Boolean timeoutFlag;

    /**
     * 实际执行时长（分钟）
     */
    private Integer actualDurationMinutes;

    /**
     * 关联预案步骤ID（可选）
     */
    private Long planStepId;

    /**
     * 执行人ID
     */
    private Long executorId;

    /**
     * 执行人姓名
     */
    private String executorName;

    /**
     * 超时原因（超时后必须填写）
     */
    private String timeoutReason;

    /**
     * 处理措施（超时后必须填写）
     */
    private String handlingMeasures;
}

