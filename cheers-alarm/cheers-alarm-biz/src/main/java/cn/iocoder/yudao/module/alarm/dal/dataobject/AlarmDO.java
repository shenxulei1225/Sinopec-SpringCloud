package cn.iocoder.yudao.module.alarm.dal.dataobject;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 告警 DO
 *
 * @author 告警管理模块
 */
@TableName("alarm")
@KeySequence("alarm_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDO extends BaseDO {

    /**
     * 告警ID
     */
    @TableId
    private Long id;

    /**
     * 告警编码，格式：ALM-YYYYMMDD-XXXXX
     */
    private String alarmCode;

    /**
     * 告警类型ID（关联metadata_entity）
     */
    private Long alarmTypeId;

    /**
     * 告警分类ID（关联metadata_category）
     */
    private Long alarmCategoryId;

    /**
     * 告警模型ID（关联metadata_model）
     */
    private Long alarmModelId;

    /**
     * 告警类型路径（如：环境告警 > 水位告警 > 水位超标）
     */
    private String alarmTypePath;

    /**
     * 告警级别：INFO/WARNING/CRITICAL/EMERGENCY
     * 
     * 枚举 {@link cn.iocoder.yudao.module.alarm.enums.AlarmLevelEnum}
     * 字典：alarm_level
     */
    private String alarmLevel;

    /**
     * 告警状态：PENDING/ACKNOWLEDGED/HANDLING/CLOSED
     * 
     * 枚举 {@link cn.iocoder.yudao.module.alarm.enums.AlarmStatusEnum}
     * 字典：alarm_status
     */
    private String alarmStatus;

    /**
     * 告警来源：SYSTEM/MANUAL
     * 
     * 枚举 {@link cn.iocoder.yudao.module.alarm.enums.AlarmSourceEnum}
     * 字典：alarm_source
     */
    private String alarmSource;

    /**
     * 告警内容
     */
    private String alarmContent;

    /**
     * 关联设备ID
     */
    private Long deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 位置ID
     */
    private Long locationId;

    /**
     * 位置名称（如：A区 > 1号防火分区）
     */
    private String locationName;

    /**
     * 触发值（如：水位55cm）
     */
    private String triggerValue;

    /**
     * 阈值（如：50cm）
     */
    private String thresholdValue;

    /**
     * 触发次数（用于告警抑制）
     */
    private Integer triggerCount;

    /**
     * 触发的告警规则ID
     */
    private Long ruleId;

    /**
     * 升级级别（0-未升级，1-已升级）
     */
    private Integer escalationLevel;

    /**
     * 升级时间
     */
    private LocalDateTime escalationTime;

    /**
     * 确认时间
     */
    private LocalDateTime acknowledgeTime;

    /**
     * 确认人ID
     */
    private Long acknowledgeUserId;

    /**
     * 确认人姓名
     */
    private String acknowledgeUserName;

    /**
     * 确认备注
     */
    private String acknowledgeRemark;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 处理人ID
     */
    private Long handleUserId;

    /**
     * 处理人姓名
     */
    private String handleUserName;

    /**
     * 处理措施
     */
    private String handleMeasure;

    /**
     * 处理结果：RESOLVED/UNRESOLVED/FALSE_ALARM
     * 
     * 枚举 {@link cn.iocoder.yudao.module.alarm.enums.AlarmHandleResultEnum}
     * 字典：alarm_handle_result
     */
    private String handleResult;

    /**
     * 关闭时间
     */
    private LocalDateTime closeTime;

    /**
     * 关闭人ID
     */
    private Long closeUserId;

    /**
     * 关闭人姓名
     */
    private String closeUserName;

    /**
     * 关闭原因：HANDLED/FALSE_ALARM/OTHER
     * 
     * 枚举 {@link cn.iocoder.yudao.module.alarm.enums.AlarmCloseReasonEnum}
     * 字典：alarm_close_reason
     */
    private String closeReason;

    /**
     * 关闭备注
     */
    private String closeRemark;

    /**
     * 持续时长（秒）
     */
    private Long durationSeconds;

    /**
     * 租户ID
     */
    private Long tenantId;

}
