package cn.cheers.x.module.platform.contract.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 按设备汇总的冲突天。没有冲突的天不出现。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleConflictDeviceDTO {

    private String resourceId;
    private String resourceType;
    private List<ScheduleConflictDayDTO> days;
}
