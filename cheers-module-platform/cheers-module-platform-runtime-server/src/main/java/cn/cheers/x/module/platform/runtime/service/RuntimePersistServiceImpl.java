package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.module.platform.contract.dto.runtime.RuntimeJobDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.runtime.convert.RuntimeConvert;
import cn.cheers.x.module.platform.runtime.dal.dataobject.RuntimeJobDO;
import cn.cheers.x.module.platform.runtime.dal.dataobject.ScheduleSlotDO;
import cn.cheers.x.module.platform.runtime.dal.mysql.RuntimeJobMapper;
import cn.cheers.x.module.platform.runtime.dal.mysql.ScheduleSlotMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RuntimePersistServiceImpl implements RuntimePersistService {

    @Resource
    private RuntimeJobMapper runtimeJobMapper;
    @Resource
    private ScheduleSlotMapper scheduleSlotMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveJobWithSlots(RuntimeJobDTO job, List<ScheduleSlotDTO> slots, Long siteId) {
        RuntimeJobDO jobDO = RuntimeConvert.toJobDo(job, siteId);
        runtimeJobMapper.insert(jobDO);
        if (slots == null || slots.isEmpty()) {
            return;
        }
        for (ScheduleSlotDTO slot : slots) {
            ScheduleSlotDO slotDO = RuntimeConvert.toSlotDo(slot, siteId);
            scheduleSlotMapper.insert(slotDO);
        }
    }
}
