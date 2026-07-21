package cn.cheers.x.module.platform.runtime.convert;

import cn.cheers.x.module.platform.contract.ContractVersions;
import cn.cheers.x.module.platform.contract.dto.runtime.RuntimeJobDTO;
import cn.cheers.x.module.platform.contract.dto.slot.AssignedResourceDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.enums.RuntimeJobStatus;
import cn.cheers.x.module.platform.contract.enums.SlotLockState;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.runtime.dal.dataobject.RuntimeJobDO;
import cn.cheers.x.module.platform.runtime.dal.dataobject.ScheduleSlotDO;
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

    public static ScheduleSlotDTO toSlotDto(ScheduleSlotDO slot) {
        if (slot == null) {
            return null;
        }
        return ScheduleSlotDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .slotId(slot.getId())
                .runtimeJobId(slot.getRuntimeJobId())
                .workId(slot.getWorkId())
                .entityTypeCode(slot.getEntityTypeCode())
                .plannedStart(formatTime(slot.getPlannedStart()))
                .plannedEnd(formatTime(slot.getPlannedEnd()))
                .actualStart(formatTime(slot.getActualStart()))
                .actualEnd(formatTime(slot.getActualEnd()))
                .assignedResources(parseAssignedResources(slot.getAssignedResources()))
                .lockState(parseLockState(slot.getLockState()))
                .slotStatus(parseSlotStatus(slot.getSlotStatus()))
                .policySnapshotId(slot.getPolicySnapshotId())
                .decisionTraceId(slot.getDecisionTraceId())
                .build();
    }

    public static RuntimeJobDO toJobDo(RuntimeJobDTO dto, Long siteId) {
        return RuntimeJobDO.builder()
                .id(dto.getRuntimeJobId())
                .entityTypeCode(dto.getEntityTypeCode())
                .triggerAction(dto.getTriggerAction())
                .status(dto.getStatus() != null ? dto.getStatus().name() : null)
                .sourceWorkIds(JSON.toJSONString(dto.getSourceWorkIds() != null ? dto.getSourceWorkIds() : Collections.emptyList()))
                .policySnapshotId(dto.getPolicySnapshotId())
                .orchestrationRef(dto.getOrchestrationRef())
                .siteId(siteId)
                .build();
    }

    public static ScheduleSlotDO toSlotDo(ScheduleSlotDTO dto, Long siteId) {
        return ScheduleSlotDO.builder()
                .id(dto.getSlotId())
                .runtimeJobId(dto.getRuntimeJobId())
                .workId(dto.getWorkId())
                .entityTypeCode(dto.getEntityTypeCode())
                .plannedStart(parseTime(dto.getPlannedStart()))
                .plannedEnd(parseTime(dto.getPlannedEnd()))
                .actualStart(parseTime(dto.getActualStart()))
                .actualEnd(parseTime(dto.getActualEnd()))
                .assignedResources(dto.getAssignedResources() != null
                        ? JSON.toJSONString(dto.getAssignedResources()) : null)
                .lockState(dto.getLockState() != null ? dto.getLockState().name() : SlotLockState.NONE.name())
                .slotStatus(dto.getSlotStatus() != null ? dto.getSlotStatus().name() : SlotStatus.PLANNED.name())
                .policySnapshotId(dto.getPolicySnapshotId())
                .decisionTraceId(dto.getDecisionTraceId())
                .siteId(siteId)
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

    private static SlotStatus parseSlotStatus(String slotStatus) {
        return slotStatus != null ? SlotStatus.valueOf(slotStatus) : SlotStatus.PLANNED;
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
