package cn.cheers.x.module.platform.contract.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 某一设备某一天的冲突画像：当天计划、当天已有、重叠段。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleConflictDayDTO {

    /** YYYY-MM-DD */
    private String date;
    private List<ScheduleConflictWindowDTO> planned;
    private List<ScheduleConflictWindowDTO> existing;
    private List<ScheduleConflictOverlapDTO> overlaps;
}
