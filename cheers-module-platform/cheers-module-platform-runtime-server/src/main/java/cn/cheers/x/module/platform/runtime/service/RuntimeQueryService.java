package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.module.platform.contract.dto.runtime.RuntimeJobDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * L4 查询服务。
 */
public interface RuntimeQueryService {

    RuntimeJobDTO getJob(String runtimeJobId);

    List<ScheduleSlotDTO> listSlots(String businessTypeCode, OffsetDateTime from, OffsetDateTime to);

    List<ScheduleSlotDTO> listSlotsByJobId(String runtimeJobId);
}
