package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.module.platform.contract.dto.runtime.RuntimeJobDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.runtime.convert.RuntimeConvert;
import cn.cheers.x.module.platform.runtime.dal.dataobject.RuntimeJobDO;
import cn.cheers.x.module.platform.runtime.dal.dataobject.ScheduleSlotDO;
import cn.cheers.x.module.platform.runtime.dal.mysql.RuntimeJobMapper;
import cn.cheers.x.module.platform.runtime.dal.mysql.ScheduleSlotMapper;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.List;

import static cn.cheers.x.module.platform.runtime.enums.ErrorCodeConstants.RUNTIME_JOB_NOT_EXISTS;
import static cn.cheers.x.module.platform.runtime.enums.ErrorCodeConstants.SCHEDULE_SLOT_QUERY_TIME_RANGE_REQUIRED;
import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;

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
    public List<ScheduleSlotDTO> listSlots(String entityTypeCode, OffsetDateTime from, OffsetDateTime to) {
        return listSlots(from, to, null, entityTypeCode, null, null);
    }

    @Override
    public List<ScheduleSlotDTO> listSlots(
            OffsetDateTime from,
            OffsetDateTime to,
            String resourceId,
            String entityTypeCode,
            Long siteId,
            List<SlotStatus> slotStatuses) {
        if (from == null || to == null) {
            throw exception(SCHEDULE_SLOT_QUERY_TIME_RANGE_REQUIRED);
        }

        LambdaQueryWrapperX<ScheduleSlotDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eqIfPresent(ScheduleSlotDO::getEntityTypeCode, entityTypeCode)
                .eqIfPresent(ScheduleSlotDO::getSiteId, siteId)
                .lt(ScheduleSlotDO::getPlannedStart, to)
                .gt(ScheduleSlotDO::getPlannedEnd, from);
        wrapper.orderByAsc(ScheduleSlotDO::getPlannedStart);
        if (slotStatuses != null && !slotStatuses.isEmpty()) {
            wrapper.in(ScheduleSlotDO::getSlotStatus,
                    slotStatuses.stream().map(SlotStatus::name).toList());
        }

        return scheduleSlotMapper.selectList(wrapper).stream()
                .map(RuntimeConvert::toSlotDto)
                .filter(dto -> !StringUtils.hasText(resourceId) || matchesResource(dto, resourceId))
                .toList();
    }

    @Override
    public List<ScheduleSlotDTO> listSlotsByJobId(String runtimeJobId) {
        List<ScheduleSlotDO> slots = scheduleSlotMapper.selectList(new LambdaQueryWrapperX<ScheduleSlotDO>()
                .eq(ScheduleSlotDO::getRuntimeJobId, runtimeJobId)
                .orderByAsc(ScheduleSlotDO::getPlannedStart));
        return slots.stream().map(RuntimeConvert::toSlotDto).toList();
    }

    private static boolean matchesResource(ScheduleSlotDTO slot, String resourceId) {
        if (slot.getAssignedResources() == null || slot.getAssignedResources().isEmpty()) {
            return false;
        }
        return slot.getAssignedResources().stream()
                .anyMatch(resource -> resourceId.equals(resource.getResourceId()));
    }
}
