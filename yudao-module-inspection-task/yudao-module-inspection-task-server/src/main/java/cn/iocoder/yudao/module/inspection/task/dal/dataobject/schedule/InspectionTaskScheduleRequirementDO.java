package cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 巡检任务排期需求 DO。
 *
 * <p>表示用户提交的排期需求，描述"何时执行"的配置。
 * 当 isTemplate=true 时，表示该需求已保存为模板，可被其他任务复用。</p>
 *
 * <h3>排期模式</h3>
 * <ul>
 *   <li>固定时间点模式（1）：用户指定具体时刻，如 09:00、14:00</li>
 *   <li>间隔执行模式（2）：用户指定周期/间隔，算法按固定间隔生成时刻</li>
 *   <li>自动编排模式（3）：用户只指定次数，算法根据 policy 自动计算时刻</li>
 * </ul>
 */
@TableName(value = "inspection_task_schedule_requirement", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskScheduleRequirementDO extends BaseDO {

    @TableId
    private Long id;

    // ==================== 模板管理 ====================

    /**
     * 是否为模板。
     * <p>true-已保存为模板，false-任务直接使用的临时配置</p>
     */
    private Boolean isTemplate;

    /**
     * 模板名称。
     * <p>仅当 isTemplate=true 时需要填写，用于标识模板。</p>
     */
    private String templateName;

    /**
     * 关联的排期策略 ID。
     */
    private Long policyId;

    // ==================== 排期模式 ====================

    /**
     * 排期模式：1-固定时间点 2-间隔执行 3-自动编排
     * <p>固定时间点：指定具体时刻执行，如每天9:00、14:00</p>
     * <p>间隔执行：按周期或间隔循环执行</p>
     * <p>自动编排：只指定次数，由算法根据 policy 自动计算时刻</p>
     */
    private Integer scheduleMode;

    // ==================== 固定时间点模式配置 ====================

    /**
     * 固定执行时间点集合。
     * <p>例如：[09:00, 14:00, 18:00] 表示每天这三个时刻执行</p>
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<LocalTime> timePoints = new ArrayList<>();

    // ==================== 间隔执行模式配置 ====================

    /**
     * 任务周期（分钟）。
     * <p>下一个任务开始时间 - 上一个任务开始时间</p>
     * <p>例如：120 表示每2小时执行一次</p>
     */
    private Integer taskCycleMinutes;

    /**
     * 任务间隔（分钟）。
     * <p>下一个任务开始时间 - 上一个任务完成时间</p>
     * <p>例如：90 表示上次完成后休息90分钟再开始下一次</p>
     */
    private Integer taskGapMinutes;

    /**
     * 每日允许执行开始时间。
     * <p>仅在间隔执行/自动编排模式下生效，用于限定每天的开始时间范围</p>
     * <p>例如：08:00 表示每天从08:00开始计算周期</p>
     */
    private LocalTime windowStartTime;

    /**
     * 每日允许执行结束时间。
     * <p>仅在间隔执行/自动编排模式下生效，用于限定每天的结束时间范围</p>
     * <p>例如：20:00 表示每天20:00后不再开始新任务</p>
     */
    private LocalTime windowEndTime;

    /**
     * 间隔执行的基准时间。
     * <p>每天以此时间为起点开始计算周期</p>
     * <p>例如：08:00 表示每天08:00重新开始计算</p>
     */
    private LocalTime anchorTime;

    // ==================== 自动编排模式配置 ====================

    /**
     * 自动编排模式：每日执行次数。
     * <p>仅在 scheduleMode=3（自动编排）时生效。</p>
     * <p>算法根据此次数和 policy 配置，自动计算每天的执行时刻。</p>
     * <p>例如：3 表示每天自动安排3个执行时刻</p>
     */
    private Integer dailyExecutionCount;

    // ==================== 执行次数范围约束 ====================

    /**
     * 每日最少执行次数。
     * <p>国标要求每2小时巡检 → 每天≥12次</p>
     * <p>null 表示不限制</p>
     */
    private Integer minExecutionsPerDay;

    /**
     * 每日最多次数。
     * <p>限流控制 → 每天≤6次</p>
     * <p>null 表示不限制</p>
     */
    private Integer maxExecutionsPerDay;

    /**
     * 每周最少执行次数。
     * <p>每周例会巡检 → 每周≥1次</p>
     * <p>null 表示不限制</p>
     */
    private Integer minExecutionsPerWeek;

    /**
     * 每周最多次数。
     * <p>null 表示不限制</p>
     */
    private Integer maxExecutionsPerWeek;

    /**
     * 每月最少执行次数。
     * <p>月度巡检 → 每月≥1次</p>
     * <p>null 表示不限制</p>
     */
    private Integer minExecutionsPerMonth;

    /**
     * 每月最多次数。
     * <p>null 表示不限制</p>
     */
    private Integer maxExecutionsPerMonth;

    // ==================== 日期范围 ====================

    /**
     * 开始日期。
     * <p>排期生效的起始日期</p>
     */
    private LocalDate startDate;

    /**
     * 结束日期。
     * <p>排期生效的结束日期，null表示永久有效</p>
     */
    private LocalDate endDate;

    // ==================== 重复维度 ====================

    /**
     * 重复维度：1-一次性 2-每日 3-每周 4-每月
     * <p>一次性：仅在开始日期执行一次</p>
     * <p>每日：按任务周期/间隔每天重复</p>
     * <p>每周：仅在指定的星期执行</p>
     * <p>每月：仅在指定的日期执行</p>
     */
    private Integer repeatMode;

    /**
     * 每周重复日集合（1=周一, 7=周日）。
     * <p>例如：[1,3,5] 表示周一、周三、周五执行</p>
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Integer> weekDays = new ArrayList<>();

    /**
     * 每月重复日集合（1-31）。
     * <p>例如：[1,15] 表示每月1号和15号执行</p>
     * <p>超过该月最大天数时取月末最后一天</p>
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Integer> monthDays = new ArrayList<>();

    // ==================== 扩展字段 ====================

    /**
     * 排期需求说明。
     */
    private String description;
}
