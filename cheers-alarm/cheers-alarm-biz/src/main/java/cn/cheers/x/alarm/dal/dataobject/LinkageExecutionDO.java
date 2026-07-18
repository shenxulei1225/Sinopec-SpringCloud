package cn.cheers.x.alarm.dal.dataobject;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 联动执行记录 DO
 *
 * @author 告警管理模块
 */
@TableName("linkage_execution")
@KeySequence("linkage_execution_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkageExecutionDO extends BaseDO {

    /**
     * 执行记录ID
     */
    @TableId
    private Long id;

    /**
     * 告警ID
     */
    private Long alarmId;

    /**
     * 联动规则ID
     */
    private Long linkageRuleId;

    /**
     * 动作类型
     * 
     * 枚举 {@link cn.cheers.x.alarm.enums.LinkageActionTypeEnum}
     * 字典：linkage_action_type
     */
    private String actionType;

    /**
     * 动作配置（JSON）
     */
    private String actionConfig;

    /**
     * 目标设备ID
     */
    private Long targetDeviceId;

    /**
     * 目标设备名称
     */
    private String targetDeviceName;

    /**
     * 执行状态
     * 
     * 枚举 {@link cn.cheers.x.alarm.enums.LinkageExecutionStatusEnum}
     * 字典：linkage_execution_status
     */
    private String executionStatus;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 执行结果
     */
    private String executionResult;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 执行耗时（毫秒）
     */
    private Long durationMs;

    /**
     * 是否需要人工介入
     */
    private Boolean manualIntervention;

    /**
     * 租户ID
     */
    private Long tenantId;

}
