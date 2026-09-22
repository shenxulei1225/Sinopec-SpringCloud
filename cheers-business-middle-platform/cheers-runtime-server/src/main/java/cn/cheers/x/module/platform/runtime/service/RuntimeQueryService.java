package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.runtime.RuntimeJobDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * L4 查询服务。
 */
public interface RuntimeQueryService {

    RuntimeJobDTO getJob(String runtimeJobId);

    List<ScheduleSlotDTO> listSlots(String entityTypeCode, OffsetDateTime from, OffsetDateTime to);

    List<ScheduleSlotDTO> listSlots(
            OffsetDateTime from,
            OffsetDateTime to,
            String resourceId,
            String entityTypeCode,
            Long facilityId,
            List<SlotStatus> slotStatuses);

    List<ScheduleSlotDTO> listSlotsByJobId(String runtimeJobId);

    ScheduleSlotDTO getSlot(String slotId);

    List<ResourceReservationDTO> listReservations(
            OffsetDateTime from,
            OffsetDateTime to,
            String resourceId,
            String entityTypeCode,
            Long facilityId,
            List<SlotStatus> candidateStatuses);

    List<ResourceReservationDTO> listReservationsByJobId(String runtimeJobId);

    ResourceReservationDTO getCandidateById(String candidateId);
}
