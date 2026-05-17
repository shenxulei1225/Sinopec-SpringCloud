package cn.iocoder.yudao.module.alarm.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 联动规则 DO
 *
 * @author 告警管理模块
 */
@TableName("linkage_rule")
@KeySequence("linkage_rule_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkageRuleDO extends BaseDO {

    /**
     * 规则ID
     */
    @TableId
    private Long id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则编码
     */
    private String ruleCode;

    /**
     * 关联的告警规则ID（可为空，表示适用所有）
     */
    private Long alarmRuleId;

    /**
     * 适用的告警类型ID（关联metadata_entity）
     */
    private Long alarmTypeId;

    /**
     * 适用的告警分类ID（关联metadata_category）
     */
    private Long alarmCategoryId;

    /**
     * 适用的告警级别
     * 
     * 枚举 {@link cn.iocoder.yudao.module.alarm.enums.AlarmLevelEnum}
     * 字典：alarm_level
     */
    private String alarmLevel;

    /**
     * 触发条件表达式（JSON格式）
     */
    private String conditionExpression;

    /**
     * 联动动作配置（JSON数组）
     * 
     * 示例：[{"type":"DEVICE_CONTROL","deviceId":1,"action":"START"}]
     */
    private String actions;

    /**
     * 执行模式：SERIAL-串行，PARALLEL-并行
     * 
     * 枚举 {@link cn.iocoder.yudao.module.alarm.enums.LinkageExecutionModeEnum}
     * 字典：linkage_execution_mode
     */
    private String executionMode;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 规则描述
     */
    private String description;

    /**
     * 租户ID
     */
    private Long tenantId;

}
