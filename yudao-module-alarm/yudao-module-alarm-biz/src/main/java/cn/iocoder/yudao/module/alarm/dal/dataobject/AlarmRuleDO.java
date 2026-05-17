package cn.iocoder.yudao.module.alarm.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 告警规则 DO
 *
 * @author 告警管理模块
 */
@TableName("alarm_rule")
@KeySequence("alarm_rule_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmRuleDO extends BaseDO {

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
     * 规则类型：THRESHOLD/RATE/PATTERN/COMBINATION
     * 
     * 枚举 {@link cn.iocoder.yudao.module.alarm.enums.AlarmRuleTypeEnum}
     * 字典：alarm_rule_type
     */
    private String ruleType;

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
     * 告警级别
     * 
     * 枚举 {@link cn.iocoder.yudao.module.alarm.enums.AlarmLevelEnum}
     * 字典：alarm_level
     */
    private String alarmLevel;

    /**
     * 适用设备类型
     */
    private String deviceType;

    /**
     * 条件表达式（JSON格式）
     * 
     * 示例：{"field":"waterLevel","operator":">","value":50}
     */
    private String conditionExpression;

    /**
     * 告警内容模板
     */
    private String alarmContentTemplate;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 优先级（数值越大优先级越高）
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
