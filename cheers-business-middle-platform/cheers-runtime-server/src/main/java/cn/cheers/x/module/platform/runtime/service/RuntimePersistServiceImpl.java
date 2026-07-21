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
import org.springframework.util.StringUtils;

import java.util.List;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platform.runtime.enums.ErrorCodeConstants.RUNTIME_JOB_NOT_EXISTS;

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
        insertSlots(slots, siteId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void appendSlots(String runtimeJobId, List<ScheduleSlotDTO> slots, Long siteId) {
        if (!StringUtils.hasText(runtimeJobId)) {
            throw exception(RUNTIME_JOB_NOT_EXISTS);
        }
        if (runtimeJobMapper.selectById(runtimeJobId) == null) {
            throw exception(RUNTIME_JOB_NOT_EXISTS);
        }
        insertSlots(slots, siteId);
    }

    private void insertSlots(List<ScheduleSlotDTO> slots, Long siteId) {
        if (slots == null || slots.isEmpty()) {
            return;
        }
        for (ScheduleSlotDTO slot : slots) {
            ScheduleSlotDO slotDO = RuntimeConvert.toSlotDo(slot, siteId);
            scheduleSlotMapper.insert(slotDO);
        }
    }
}
