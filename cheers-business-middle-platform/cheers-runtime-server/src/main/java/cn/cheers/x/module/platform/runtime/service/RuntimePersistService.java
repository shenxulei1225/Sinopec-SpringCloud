package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.runtime.RuntimeJobDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;

import java.util.List;

/**
 * L4 持久化服务（供编排 persist 阶段调用）。
 */
public interface RuntimePersistService {

    /**
     * @deprecated 使用 {@link #saveJobWithReservations}
     */
    @Deprecated
    void saveJobWithSlots(RuntimeJobDTO job, List<ScheduleSlotDTO> slots, Long facilityId);

    void saveJobWithReservations(RuntimeJobDTO job, List<ResourceReservationDTO> reservations, Long facilityId);

    /**
     * @deprecated 使用 {@link #appendReservations}
     */
    @Deprecated
    void appendSlots(String runtimeJobId, List<ScheduleSlotDTO> slots, Long facilityId);

    void appendReservations(String runtimeJobId, List<ResourceReservationDTO> reservations, Long facilityId);
}
