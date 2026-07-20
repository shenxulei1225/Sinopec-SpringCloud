package cn.iocoder.yudao.module.emergency.dal.dataobject.event;

import cn.iocoder.yudao.module.emergency.dal.dataobject.EmergencyBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 事件研判
 * 
 * 按照数据模型.md中的定义实现
 * 表名：emergency_event_assess
 */
@TableName("emergency_event_assess")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyEventAssessDO extends EmergencyBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的应急事件ID
     */
    private Long emergencyEventId;

    /**
     * 研判时间
     */
    private LocalDateTime assessTime;

    /**
     * 原事件级别
     */
    private String originalLevel;

    /**
     * 研判后事件级别
     */
    private String newLevel;

    /**
     * 研判理由（结论性说明）
     */
    private String reason;

    /**
     * 会议记录（可包含参与人、讨论过程等，记录集体决策过程）
     */
    private String meetingRecord;
}

