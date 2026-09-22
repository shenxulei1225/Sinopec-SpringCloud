package cn.cheers.x.module.platform.contract.dto.slot;

import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.enums.CandidateType;
import cn.cheers.x.module.platform.contract.enums.SlotLockState;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @deprecated 使用 {@link ResourceReservationDTO}。迁移期保留旧 JSON 字段名兼容。
 */
@Deprecated
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSlotDTO {

    private String contractVersion;
    private String slotId;
    private String runtimeJobId;
    private String workId;
    private String entityTypeCode;
    private String plannedStart;
    private String plannedEnd;
    private String actualStart;
    private String actualEnd;
    private String candidateStart;
    private String candidateEnd;
    private List<AssignedResourceDTO> assignedResources;
    private SlotLockState lockState;
    private SlotStatus slotStatus;
    private String policySnapshotId;
    private String decisionTraceId;

    public static ScheduleSlotDTO from(ResourceReservationDTO source) {
        if (source == null) {
            return null;
        }
        return ScheduleSlotDTO.builder()
                .contractVersion(source.getContractVersion())
                .slotId(source.getCandidateId())
                .runtimeJobId(source.getRuntimeJobId())
                .workId(source.getWorkId())
                .entityTypeCode(source.getEntityTypeCode())
                .plannedStart(source.getPlannedStart())
                .plannedEnd(source.getPlannedEnd())
                .actualStart(source.getActualStart())
                .actualEnd(source.getActualEnd())
                .candidateStart(source.getCandidateStart())
                .candidateEnd(source.getCandidateEnd())
                .assignedResources(source.getAssignedResources())
                .lockState(source.getLockState())
                .slotStatus(source.getCandidateStatus())
                .policySnapshotId(source.getPolicySnapshotId())
                .decisionTraceId(source.getDecisionTraceId())
                .build();
    }

    public ResourceReservationDTO toReservation() {
        return ResourceReservationDTO.builder()
                .contractVersion(contractVersion)
                .candidateId(slotId)
                .runtimeJobId(runtimeJobId)
                .candidateType(CandidateType.TASK_EXECUTION)
                .workId(workId)
                .entityTypeCode(entityTypeCode)
                .plannedStart(plannedStart)
                .plannedEnd(plannedEnd)
                .actualStart(actualStart)
                .actualEnd(actualEnd)
                .candidateStart(candidateStart != null ? candidateStart : plannedStart)
                .candidateEnd(candidateEnd != null ? candidateEnd : plannedEnd)
                .assignedResources(assignedResources)
                .lockState(lockState)
                .candidateStatus(slotStatus)
                .policySnapshotId(policySnapshotId)
                .decisionTraceId(decisionTraceId)
                .build();
    }

    public static ResourceReservationDTO legacyToReservation(ScheduleSlotDTO legacy) {
        return legacy == null ? null : legacy.toReservation();
    }

    /** @deprecated 使用 {@link #legacyToReservation(ScheduleSlotDTO)} */
    @Deprecated
    public static ResourceReservationDTO toReservation(ScheduleSlotDTO legacy) {
        return legacyToReservation(legacy);
    }
}
