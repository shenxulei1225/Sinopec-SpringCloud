package cn.cheers.x.module.platform.contract.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 本任务计划与已有任务在时间轴上相交的区间（含间隔不足挤占的那段）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleConflictOverlapDTO {

    private String start;
    private String end;
}
