package cn.cheers.x.module.platform.contract.dto.slot;

import cn.cheers.x.module.platform.contract.enums.SlotLockState;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 计划点（Schedule Slot）— 排程输出 / L4 持久化。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSlotDTO {

    private String contractVersion;
    private String slotId;
    private String runtimeJobId;
    private String workId;
    private String businessTypeCode;
    private String plannedStart;
    private String plannedEnd;
    private List<AssignedResourceDTO> assignedResources;
    private SlotLockState lockState;
    private SlotStatus slotStatus;
    private String policySnapshotId;
    private String decisionTraceId;
}
