package cn.cheers.x.module.platform.contract.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 冲突检测报告：只描述重叠，不决定怎么解。
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
}
