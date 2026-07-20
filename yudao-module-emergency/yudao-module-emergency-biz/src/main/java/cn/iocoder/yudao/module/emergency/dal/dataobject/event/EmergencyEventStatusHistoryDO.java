package cn.iocoder.yudao.module.emergency.dal.dataobject.event;

import cn.iocoder.yudao.module.emergency.dal.dataobject.EmergencyBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 事件状态变更历史
 */
@TableName("emergency_event_status_history")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyEventStatusHistoryDO extends EmergencyBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 事件ID
     */
    @TableField("emergency_event_id")
    private Long eventId;

    /**
     * 原状态
     */
    private String fromStatus;

    /**
     * 新状态
     */
    private String toStatus;

    /**
     * 操作原因
     */
    private String reason;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;
}

