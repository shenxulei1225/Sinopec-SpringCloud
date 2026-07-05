package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.module.platform.contract.dto.runtime.RuntimeJobDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;

import java.util.List;

/**
 * L4 持久化服务（供编排 persist 阶段调用）。
 */
public interface RuntimePersistService {

    /**
     * 保存运行作业及其计划点。
     */
    void saveJobWithSlots(RuntimeJobDTO job, List<ScheduleSlotDTO> slots, Long siteId);
}
