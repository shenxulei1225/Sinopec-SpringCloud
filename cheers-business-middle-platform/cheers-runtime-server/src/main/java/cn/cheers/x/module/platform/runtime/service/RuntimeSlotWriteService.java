package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotStatusUpdateReqDTO;

public interface RuntimeSlotWriteService {

    void updateSlotStatus(RuntimeSlotStatusUpdateReqDTO request);
}
