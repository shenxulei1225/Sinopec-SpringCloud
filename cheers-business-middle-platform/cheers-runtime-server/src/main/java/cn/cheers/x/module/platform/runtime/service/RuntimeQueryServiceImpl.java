package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.runtime.RuntimeJobDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.runtime.convert.RuntimeConvert;
import cn.cheers.x.module.platform.runtime.dal.dataobject.ResourceReservationDO;
import cn.cheers.x.module.platform.runtime.dal.dataobject.RuntimeJobDO;
import cn.cheers.x.module.platform.runtime.dal.mysql.ResourceReservationMapper;
import cn.cheers.x.module.platform.runtime.dal.mysql.RuntimeJobMapper;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.List;

import static cn.cheers.x.module.platform.runtime.enums.ErrorCodeConstants.RUNTIME_JOB_NOT_EXISTS;
import static cn.cheers.x.module.platform.runtime.enums.ErrorCodeConstants.SCHEDULE_SLOT_NOT_EXISTS;
import static cn.cheers.x.module.platform.runtime.enums.ErrorCodeConstants.SCHEDULE_SLOT_QUERY_TIME_RANGE_REQUIRED;
import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
public class RuntimeQueryServiceImpl implements RuntimeQueryService {

    @Resource
    private RuntimeJobMapper runtimeJobMapper;
    @Resource
    private ResourceReservationMapper resourceReservationMapper;

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
            Long facilityId,
            List<SlotStatus> slotStatuses) {
        return listReservations(from, to, resourceId, entityTypeCode, facilityId, slotStatuses).stream()
                .map(ScheduleSlotDTO::from)
                .toList();
    }

    @Override
    public List<ResourceReservationDTO> listReservations(
            OffsetDateTime from,
            OffsetDateTime to,
            String resourceId,
            String entityTypeCode,
            Long facilityId,
            List<SlotStatus> candidateStatuses) {
        if (from == null || to == null) {
            throw exception(SCHEDULE_SLOT_QUERY_TIME_RANGE_REQUIRED);
        }

        LambdaQueryWrapperX<ResourceReservationDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eqIfPresent(ResourceReservationDO::getEntityTypeCode, entityTypeCode)
                .eqIfPresent(ResourceReservationDO::getFacilityId, facilityId)
                .lt(ResourceReservationDO::getCandidateStart, to)
                .gt(ResourceReservationDO::getCandidateEnd, from);
        wrapper.orderByAsc(ResourceReservationDO::getCandidateStart);
        if (candidateStatuses != null && !candidateStatuses.isEmpty()) {
            wrapper.in(ResourceReservationDO::getCandidateStatus,
                    candidateStatuses.stream().map(SlotStatus::name).toList());
        }

        return resourceReservationMapper.selectList(wrapper).stream()
                .map(RuntimeConvert::toReservationDto)
                .filter(dto -> !StringUtils.hasText(resourceId) || matchesResource(dto, resourceId))
                .toList();
    }

    @Override
    public List<ScheduleSlotDTO> listSlotsByJobId(String runtimeJobId) {
        return listReservationsByJobId(runtimeJobId).stream().map(ScheduleSlotDTO::from).toList();
    }

    @Override
    public List<ResourceReservationDTO> listReservationsByJobId(String runtimeJobId) {
        List<ResourceReservationDO> rows = resourceReservationMapper.selectList(
                new LambdaQueryWrapperX<ResourceReservationDO>()
                        .eq(ResourceReservationDO::getRuntimeJobId, runtimeJobId)
                        .orderByAsc(ResourceReservationDO::getCandidateStart));
        return rows.stream().map(RuntimeConvert::toReservationDto).toList();
    }

    @Override
    public ScheduleSlotDTO getSlot(String slotId) {
        return ScheduleSlotDTO.from(getCandidateById(slotId));
    }

    @Override
    public ResourceReservationDTO getCandidateById(String candidateId) {
        if (!StringUtils.hasText(candidateId)) {
            throw exception(SCHEDULE_SLOT_NOT_EXISTS);
        }
        ResourceReservationDO row = resourceReservationMapper.selectById(candidateId.trim());
        if (row == null) {
            throw exception(SCHEDULE_SLOT_NOT_EXISTS);
        }
        return RuntimeConvert.toReservationDto(row);
    }

    private static boolean matchesResource(ResourceReservationDTO reservation, String resourceId) {
        if (reservation.getAssignedResources() == null || reservation.getAssignedResources().isEmpty()) {
            return false;
        }
        return reservation.getAssignedResources().stream()
                .anyMatch(resource -> resourceId.equals(resource.getResourceId()));
    }
}
