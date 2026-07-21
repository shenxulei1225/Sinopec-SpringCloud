package cn.cheers.x.module.platform.runtime.service;

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
            Long siteId,
            List<SlotStatus> slotStatuses);

    List<ScheduleSlotDTO> listSlotsByJobId(String runtimeJobId);
}
