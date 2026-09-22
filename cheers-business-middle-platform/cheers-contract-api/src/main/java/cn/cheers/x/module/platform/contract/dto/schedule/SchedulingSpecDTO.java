package cn.cheers.x.module.platform.contract.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排程规格（Phase 1 可内嵌于请求体）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulingSpecDTO {

    private String mode;
    private String horizonStart;
    private String horizonEnd;
    /**
     * 冲突时先做什么（引擎注册方案，不是用户现场编规则）：
     * <ul>
     *   <li>{@code defer_slot} — 允许调整后继续排</li>
     *   <li>{@code reject_batch} — 同资源重叠则整批失败，不调整</li>
     *   <li>{@code priority_preempt} — 本批高优先级先按原窗安置，其余再按安置偏好调整</li>
     * </ul>
     */
    private String conflictStrategy;

    /**
     * 空闲不够时是否先换同类候选设备。空则先在首选设备上判断插入/挪已有。
     */
    private String placementPreference;

    /**
     * 空闲区间不够时，是否允许挪动已有任务排期，给新任务腾出计划窗。
     */
    private Boolean allowShiftExisting;

    /**
     * 已有任务单侧最多可提前或延后的分钟数。允许挪动时必填。
     */
    private Integer maxShiftMinutes;

    /**
     * 任务完成后间隔（分钟）：下一条占窗最早开始 = 上一条 candidate_end + taskGapMinutes。
     */
    private Integer taskGapMinutes;
}
