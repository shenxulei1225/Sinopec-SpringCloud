package cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 巡检任务排期需求 DO。
 *
 * <p>排期需求是一个配置容器，包含多个模板组合。</p>
 *
 * <h3>核心概念</h3>
 * <ul>
 *   <li>排期需求：一个任务可以有多个模板组合</li>
 *   <li>模板组合：包含原始模板配置、用户修改的字段、是否启用</li>
 *   <li>恢复功能：使用 originalConfig + changedFields 计算当前显示值</li>
 * </ul>
 */
@TableName(value = "inspection_task_schedule_requirement", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskScheduleRequirementDO extends TenantBaseDO {

    @TableId
    private Long id;

    // ==================== 基础信息 ====================

    /**
     * 需求编码。
     */
    private String requirementCode;

    /**
     * 需求名称。
     */
    private String requirementName;

    /**
     * 关联的任务 ID。
     */
    private Long taskId;

    /**
     * 关联的排期策略 ID。
     */
    private Long schedulePolicyId;

    // ==================== 模板组合配置 ====================

    /**
     * 模板组合配置列表。
     * <p>一个排期需求可包含多个模板组合，每个模板组合记录原始模板配置和用户修改的字段。</p>
     * <p>存储时使用 JSON 序列化，查询时自动反序列化。</p>
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<ScheduleTemplateConfig> scheduleTemplates = new ArrayList<>();

    // ==================== 扩展字段 ====================

    /**
     * 排期需求说明。
     */
    private String description;

    // ==================== 内部类 ====================

    /**
     * 排期模板组合配置。
     * <p>用于存储原始模板配置和用户修改的字段。</p>
     * <p>注意：此类属于 DO 层，不引用任何 VO 或外部类型。</p>
     */
    @Data
    public static class ScheduleTemplateConfig {

        /**
         * 模板ID。
         */
        private Long templateId;

        /**
         * 模板名称。
         */
        private String templateName;

        /**
         * 原始模板完整配置。
         * <p>以 Map 结构存储模板的核心配置字段，便于恢复模板时使用。</p>
         * <p>包含字段：scheduleMode, timePoints, taskCycleMinutes, anchorTime,
         * dailyExecutionCount, weekDays, monthDays, specificDates 等</p>
         */
        private Map<String, Object> originalConfig;

        /**
         * 用户修改的字段（key为字段名，value为修改后的值）。
         * <p>用于记录用户在模板基础上做了哪些修改。</p>
         * <p>恢复时：将 changedFields 中的字段从 originalConfig 中移除，恢复原始值。</p>
         */
        private Map<String, Object> changedFields;

        /**
         * 是否启用。
         * <p>true-启用（参与排期计算），false-禁用</p>
         */
        private Boolean enabled;
    }
}
