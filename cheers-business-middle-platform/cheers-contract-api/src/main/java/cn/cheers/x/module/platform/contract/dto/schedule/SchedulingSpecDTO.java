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
     * 冲突策略，合法值：
     * <ul>
     *   <li>{@code defer_slot} — 同资源重叠时顺延到最早可安置时刻</li>
     *   <li>{@code reject_batch} — 同资源重叠则整批失败</li>
     *   <li>{@code priority_preempt} — 按 priority 数值高者优先占窗，低优先级顺延；无法安置则失败</li>
     * </ul>
     */
    private String conflictStrategy;
}
