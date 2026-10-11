package cn.cheers.x.module.platform.contract.dto.schedule;

import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 冲突检测报告：描述时间是否重叠，不决定怎么解。
 * 没有重叠时带上按排期模板算出的计划时间点。有重叠时不带计划点。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleConflictReportDTO {

    private Boolean hasConflict;
    private Integer plannedCount;
    private Integer conflictCount;
    private List<ScheduleConflictDeviceDTO> devices;
    /** 没有时间重叠时的计划时间点。有重叠时为空。 */
    private List<ScheduleSlotDTO> slots;
}
