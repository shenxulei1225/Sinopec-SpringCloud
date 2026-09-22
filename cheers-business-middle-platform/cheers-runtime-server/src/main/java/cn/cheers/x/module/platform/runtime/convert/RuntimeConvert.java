package cn.cheers.x.module.platform.runtime.convert;

import cn.cheers.x.module.platform.contract.ContractVersions;
import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.runtime.RuntimeJobDTO;
import cn.cheers.x.module.platform.contract.dto.slot.AssignedResourceDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.enums.CandidateType;
import cn.cheers.x.module.platform.contract.enums.RuntimeJobStatus;
import cn.cheers.x.module.platform.contract.enums.SlotLockState;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.runtime.dal.dataobject.ResourceReservationDO;
import cn.cheers.x.module.platform.runtime.dal.dataobject.RuntimeJobDO;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * L4 DO ↔ 契约 DTO 转换。
 */
public final class RuntimeConvert {

    private static final DateTimeFormatter ISO_OFFSET = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private RuntimeConvert() {
    }

    public static RuntimeJobDTO toJobDto(RuntimeJobDO job) {
        if (job == null) {
            return null;
        }
        return RuntimeJobDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .runtimeJobId(job.getId())
                .entityTypeCode(job.getEntityTypeCode())
                .triggerAction(job.getTriggerAction())
                .status(parseJobStatus(job.getStatus()))
                .sourceWorkIds(parseStringList(job.getSourceWorkIds()))
                .policySnapshotId(job.getPolicySnapshotId())
                .orchestrationRef(job.getOrchestrationRef())
                .createdAt(formatTime(job.getCreateTime()))
                .build();
    }

    public static ResourceReservationDTO toReservationDto(ResourceReservationDO row) {
        if (row == null) {
            return null;
        }
        return ResourceReservationDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .candidateId(row.getId())
                .runtimeJobId(row.getRuntimeJobId())
                .candidateType(parseCandidateType(row.getCandidateType()))
                .workId(row.getWorkId())
                .entityTypeCode(row.getEntityTypeCode())
                .plannedStart(formatTime(row.getPlannedStart()))
                .plannedEnd(formatTime(row.getPlannedEnd()))
                .actualStart(formatTime(row.getActualStart()))
                .actualEnd(formatTime(row.getActualEnd()))
                .candidateStart(formatTime(row.getCandidateStart()))
                .candidateEnd(formatTime(row.getCandidateEnd()))
                .assignedResources(parseAssignedResources(row.getAssignedResources()))
                .lockState(parseLockState(row.getLockState()))
                .candidateStatus(parseCandidateStatus(row.getCandidateStatus()))
                .policySnapshotId(row.getPolicySnapshotId())
                .decisionTraceId(row.getDecisionTraceId())
                .build();
    }

    /** @deprecated 迁移期：{@link ScheduleSlotDTO#from(ResourceReservationDTO)} */
    @Deprecated
    public static ScheduleSlotDTO toSlotDto(ResourceReservationDO row) {
        return ScheduleSlotDTO.from(toReservationDto(row));
    }

    public static RuntimeJobDO toJobDo(RuntimeJobDTO dto, Long facilityId) {
        return RuntimeJobDO.builder()
                .id(dto.getRuntimeJobId())
                .entityTypeCode(dto.getEntityTypeCode())
                .triggerAction(dto.getTriggerAction())
                .status(dto.getStatus() != null ? dto.getStatus().name() : null)
                .sourceWorkIds(JSON.toJSONString(dto.getSourceWorkIds() != null ? dto.getSourceWorkIds() : Collections.emptyList()))
                .policySnapshotId(dto.getPolicySnapshotId())
                .orchestrationRef(dto.getOrchestrationRef())
                .facilityId(facilityId)
                .build();
    }

    public static ResourceReservationDO toReservationDo(ResourceReservationDTO dto, Long facilityId) {
        CandidateType type = dto.getCandidateType() != null
                ? dto.getCandidateType() : CandidateType.TASK_EXECUTION;
        return ResourceReservationDO.builder()
                .id(dto.getCandidateId())
                .runtimeJobId(dto.getRuntimeJobId())
                .candidateType(type.name())
                .workId(dto.getWorkId())
                .entityTypeCode(dto.getEntityTypeCode())
                .plannedStart(parseTime(dto.getPlannedStart()))
                .plannedEnd(parseTime(dto.getPlannedEnd()))
                .actualStart(parseTime(dto.getActualStart()))
                .actualEnd(parseTime(dto.getActualEnd()))
                .candidateStart(parseTime(dto.getCandidateStart()))
                .candidateEnd(parseTime(dto.getCandidateEnd()))
                .assignedResources(dto.getAssignedResources() != null
                        ? JSON.toJSONString(dto.getAssignedResources()) : null)
                .lockState(dto.getLockState() != null ? dto.getLockState().name() : SlotLockState.NONE.name())
                .candidateStatus(dto.getCandidateStatus() != null
                        ? dto.getCandidateStatus().name() : SlotStatus.PLANNED.name())
                .policySnapshotId(dto.getPolicySnapshotId())
                .decisionTraceId(dto.getDecisionTraceId())
                .facilityId(facilityId)
                .build();
    }

    private static List<String> parseStringList(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        return JSON.parseObject(json, new TypeReference<List<String>>() {
        });
    }

    private static List<AssignedResourceDTO> parseAssignedResources(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        return JSON.parseObject(json, new TypeReference<List<AssignedResourceDTO>>() {
        });
    }

    private static RuntimeJobStatus parseJobStatus(String status) {
        return status != null ? RuntimeJobStatus.valueOf(status) : null;
    }

    private static SlotLockState parseLockState(String lockState) {
        return lockState != null ? SlotLockState.valueOf(lockState) : SlotLockState.NONE;
    }

    private static SlotStatus parseCandidateStatus(String candidateStatus) {
        return candidateStatus != null ? SlotStatus.valueOf(candidateStatus) : SlotStatus.PLANNED;
    }

    private static CandidateType parseCandidateType(String candidateType) {
        return candidateType != null ? CandidateType.valueOf(candidateType) : CandidateType.TASK_EXECUTION;
    }

    private static String formatTime(OffsetDateTime time) {
        return time != null ? ISO_OFFSET.format(time) : null;
    }

    private static String formatTime(java.time.LocalDateTime time) {
        return time != null ? time.atZone(java.time.ZoneId.systemDefault()).format(ISO_OFFSET) : null;
    }

    private static OffsetDateTime parseTime(String text) {
        return text != null && !text.isBlank() ? OffsetDateTime.parse(text) : null;
    }
}
