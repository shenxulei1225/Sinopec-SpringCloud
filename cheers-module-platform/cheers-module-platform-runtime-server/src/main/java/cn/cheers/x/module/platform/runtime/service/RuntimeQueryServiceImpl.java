package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.module.platform.contract.dto.runtime.RuntimeJobDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.runtime.convert.RuntimeConvert;
import cn.cheers.x.module.platform.runtime.dal.dataobject.RuntimeJobDO;
import cn.cheers.x.module.platform.runtime.dal.dataobject.ScheduleSlotDO;
import cn.cheers.x.module.platform.runtime.dal.mysql.RuntimeJobMapper;
import cn.cheers.x.module.platform.runtime.dal.mysql.ScheduleSlotMapper;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

import static cn.cheers.x.module.platform.runtime.enums.ErrorCodeConstants.RUNTIME_JOB_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
public class RuntimeQueryServiceImpl implements RuntimeQueryService {

    @Resource
    private RuntimeJobMapper runtimeJobMapper;
    @Resource
    private ScheduleSlotMapper scheduleSlotMapper;

    @Override
    public RuntimeJobDTO getJob(String runtimeJobId) {
        RuntimeJobDO job = runtimeJobMapper.selectById(runtimeJobId);
        if (job == null) {
            throw exception(RUNTIME_JOB_NOT_EXISTS);
        }
        return RuntimeConvert.toJobDto(job);
    }

    @Override
    public List<ScheduleSlotDTO> listSlots(String businessTypeCode, OffsetDateTime from, OffsetDateTime to) {
        List<ScheduleSlotDO> slots = scheduleSlotMapper.selectList(new LambdaQueryWrapperX<ScheduleSlotDO>()
                .eqIfPresent(ScheduleSlotDO::getBusinessTypeCode, businessTypeCode)
                .geIfPresent(ScheduleSlotDO::getPlannedStart, from)
                .leIfPresent(ScheduleSlotDO::getPlannedEnd, to)
                .orderByAsc(ScheduleSlotDO::getPlannedStart));
        return slots.stream().map(RuntimeConvert::toSlotDto).toList();
    }

    @Override
    public List<ScheduleSlotDTO> listSlotsByJobId(String runtimeJobId) {
        List<ScheduleSlotDO> slots = scheduleSlotMapper.selectList(new LambdaQueryWrapperX<ScheduleSlotDO>()
                .eq(ScheduleSlotDO::getRuntimeJobId, runtimeJobId)
                .orderByAsc(ScheduleSlotDO::getPlannedStart));
        return slots.stream().map(RuntimeConvert::toSlotDto).toList();
    }
}
