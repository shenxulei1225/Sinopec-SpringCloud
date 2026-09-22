package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.runtime.RuntimeJobDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.runtime.convert.RuntimeConvert;
import cn.cheers.x.module.platform.runtime.dal.dataobject.ResourceReservationDO;
import cn.cheers.x.module.platform.runtime.dal.dataobject.RuntimeJobDO;
import cn.cheers.x.module.platform.runtime.dal.mysql.ResourceReservationMapper;
import cn.cheers.x.module.platform.runtime.dal.mysql.RuntimeJobMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platform.runtime.enums.ErrorCodeConstants.RUNTIME_JOB_NOT_EXISTS;

/**
 * 占窗落库：同一窗口编号已在表里（含已中止）就更新这一行。
 * <p>不负责冲突判定、待执行账、试排计算。禁止再插一条同编号把生成任务打成主键冲突。
 */
@Service
public class RuntimePersistServiceImpl implements RuntimePersistService {

    @Resource
    private RuntimeJobMapper runtimeJobMapper;
    @Resource
    private ResourceReservationMapper resourceReservationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveJobWithSlots(RuntimeJobDTO job, List<ScheduleSlotDTO> slots, Long facilityId) {
        saveJobWithReservations(job, slots == null ? List.of()
                : slots.stream().map(ScheduleSlotDTO::legacyToReservation).toList(), facilityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveJobWithReservations(RuntimeJobDTO job, List<ResourceReservationDTO> reservations, Long facilityId) {
        RuntimeJobDO jobDO = RuntimeConvert.toJobDo(job, facilityId);
        runtimeJobMapper.insert(jobDO);
        insertReservations(reservations, facilityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void appendSlots(String runtimeJobId, List<ScheduleSlotDTO> slots, Long facilityId) {
        appendReservations(runtimeJobId, slots == null ? List.of()
                : slots.stream().map(ScheduleSlotDTO::legacyToReservation).toList(), facilityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void appendReservations(String runtimeJobId, List<ResourceReservationDTO> reservations, Long facilityId) {
        if (!StringUtils.hasText(runtimeJobId)) {
            throw exception(RUNTIME_JOB_NOT_EXISTS);
        }
        if (runtimeJobMapper.selectById(runtimeJobId) == null) {
            throw exception(RUNTIME_JOB_NOT_EXISTS);
        }
        insertReservations(reservations, facilityId);
    }

    private void insertReservations(List<ResourceReservationDTO> reservations, Long facilityId) {
        if (reservations == null || reservations.isEmpty()) {
            return;
        }
        for (ResourceReservationDTO reservation : reservations) {
            ResourceReservationDO row = RuntimeConvert.toReservationDo(reservation, facilityId);
            // 生成任务再次落同一份试排窗口编号；上次中止留下的行必须更新，不能当新行插入。
            if (StringUtils.hasText(row.getId()) && resourceReservationMapper.selectById(row.getId()) != null) {
                resourceReservationMapper.updateById(row);
            } else {
                resourceReservationMapper.insert(row);
            }
        }
    }
}
