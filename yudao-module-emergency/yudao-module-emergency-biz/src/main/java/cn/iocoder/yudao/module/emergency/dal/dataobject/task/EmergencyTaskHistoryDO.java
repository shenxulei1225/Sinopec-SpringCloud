package cn.iocoder.yudao.module.emergency.dal.dataobject.task;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 任务状态/变更历史
 */
@TableName("emergency_task_history")
@KeySequence("emergency_task_history_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyTaskHistoryDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 响应ID，预警阶段任务为NULL，响应阶段任务关联响应ID
     * 使用 ALWAYS 策略，确保即使为 null 也会参与插入和更新
     */
    @TableField(value = "response_id",
            insertStrategy = FieldStrategy.ALWAYS,
            updateStrategy = FieldStrategy.ALWAYS)
    private Long responseId;

    private Long taskId;

    /**
     * 类型：create/status_change/terminate
     */
    private String type;

    private String fromStatus;

    private String toStatus;

    private String reason;
}










