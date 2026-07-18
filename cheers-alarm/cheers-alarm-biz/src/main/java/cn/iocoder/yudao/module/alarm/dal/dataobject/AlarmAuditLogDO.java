package cn.iocoder.yudao.module.alarm.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 告警审计日志 DO
 * 
 * 注意：此表不继承 BaseDO，因为审计日志不需要 creator/updater/deleted 等字段
 *
 * @author 告警管理模块
 */
@TableName("alarm_audit_log")
@KeySequence("alarm_audit_log_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmAuditLogDO implements Serializable {

    /**
     * 日志ID
     */
    @TableId
    private Long id;

    /**
     * 告警ID
     */
    private Long alarmId;

    /**
     * 操作类型：CREATE/ACKNOWLEDGE/HANDLE/CLOSE/ESCALATE/LINKAGE_EXECUTE/LINKAGE_RETRY/LINKAGE_MANUAL
     * 
     * 枚举 {@link cn.iocoder.yudao.module.alarm.enums.AlarmAuditOperationTypeEnum}
     */
    private String operationType;

    /**
     * 操作内容
     */
    private String operationContent;

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
    private LocalDateTime operationTime;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
