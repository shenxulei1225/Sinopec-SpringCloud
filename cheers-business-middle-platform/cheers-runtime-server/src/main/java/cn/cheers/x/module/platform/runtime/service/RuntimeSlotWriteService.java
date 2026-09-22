package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotReleaseReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotStatusUpdateReqDTO;

public interface RuntimeSlotWriteService {

    void updateSlotStatus(RuntimeSlotStatusUpdateReqDTO request);

    void releaseUnfinished(RuntimeSlotReleaseReqDTO request);

    /** 启用排程：将候选占窗定稿为计划起止时刻。 */
    void finalizePlannedSchedule(String runtimeJobId);
}
