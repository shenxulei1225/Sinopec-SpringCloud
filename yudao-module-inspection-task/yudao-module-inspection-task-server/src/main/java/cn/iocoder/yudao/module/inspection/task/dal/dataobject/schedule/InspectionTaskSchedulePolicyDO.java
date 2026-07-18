package cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 巡检任务排期策略 DO。
 *
 * <p>定义任务的排期规则，如：
 * <ul>
 *     <li>每天 9:00 执行</li>
 *     <li>每周一 9:00 执行</li>
 *     <li>每月1号 9:00 执行</li>
 * </ul>
 */
@TableName("inspection_task_schedule_policy")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskSchedulePolicyDO extends BaseDO {

    /**
     * 主键 ID。
     */
    @TableId
    private Long id;

    /**
     * 策略编码。
     */
    private String policyCode;

    /**
     * 策略名称。
     */
    private String policyName;

    /**
     * 策略类型。
     *
     * <ul>
     *     <li>1 - 周期策略</li>
     *     <li>2 - 事件策略</li>
     *     <li>3 - 手动策略</li>
     * </ul>
     */
    private Integer policyType;

    /**
     * cron 表达式（策略类型为周期策略时使用）。
     */
    private String cronExpression;

    /**
     * 周期描述（策略类型为周期策略时使用）。
     */
    private String cycleDescription;

    /**
     * 触发时间点（HH:mm 格式，策略类型为周期策略时使用）。
     */
    private String triggerTime;

    /**
     * 是否启用。
     */
    private Boolean enabled;

    /**
     * 备注。
     */
    private String remark;
}