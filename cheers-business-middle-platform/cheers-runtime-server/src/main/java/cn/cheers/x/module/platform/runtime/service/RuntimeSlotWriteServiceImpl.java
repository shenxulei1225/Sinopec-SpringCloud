package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.module.platform.contract.enums.SlotLockState;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionAppendReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotReleaseReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotStatusUpdateReqDTO;
import cn.cheers.x.module.platform.runtime.dal.dataobject.ResourceReservationDO;
import cn.cheers.x.module.platform.runtime.dal.mysql.ResourceReservationMapper;
import cn.cheers.x.module.platform.runtime.enums.RuntimeSlotReleaseMode;
import com.alibaba.fastjson2.JSON;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platform.runtime.enums.ErrorCodeConstants.SCHEDULE_SLOT_NOT_EXISTS;

@Service
public class RuntimeSlotWriteServiceImpl implements RuntimeSlotWriteService {

    private static final String TARGET_TYPE_SCHEDULE_SLOT = "schedule_slot";
    private static final String TARGET_TYPE_RUNTIME_JOB = "runtime_job";
    private static final String ACTION_SLOT_STATUS_UPDATE = "slot.status_update";
    private static final String ACTION_SLOT_RELEASE_UNFINISHED = "slot.release_unfinished";
    private static final String ACTION_SLOT_HOLD_PAUSE = "slot.hold_pause";

    @Resource
    private ResourceReservationMapper resourceReservationMapper;
    @Resource
    private ProcessTimelineService processTimelineService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSlotStatus(RuntimeSlotStatusUpdateReqDTO request) {
        ResourceReservationDO row = resourceReservationMapper.selectById(request.getSlotId());
        if (row == null) {
            throw exception(SCHEDULE_SLOT_NOT_EXISTS);
        }

        row.setCandidateStatus(request.getSlotStatus().name());
        if (request.getActualStart() != null) {
            row.setActualStart(request.getActualStart());
        }
        if (request.getActualEnd() != null) {
            row.setActualEnd(request.getActualEnd());
        }
        resourceReservationMapper.updateById(row);

        OffsetDateTime occurredAt = request.getActualEnd() != null
                ? request.getActualEnd()
                : OffsetDateTime.now();
        Long facilityId = request.getFacilityId() != null ? request.getFacilityId() : row.getFacilityId();

        processTimelineService.append(ProcessTimelineActionAppendReqDTO.builder()
                .targetType(TARGET_TYPE_SCHEDULE_SLOT)
                .targetId(row.getId())
                .occurredAt(occurredAt)
                .actionCode(ACTION_SLOT_STATUS_UPDATE)
                .howSummary(buildHowSummary(request))
                .payloadJson(buildPayloadJson(request, row))
                .facilityId(facilityId)
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseUnfinished(RuntimeSlotReleaseReqDTO request) {
        List<ResourceReservationDO> rows = resourceReservationMapper.selectList(
                new LambdaQueryWrapperX<ResourceReservationDO>()
                        .eq(ResourceReservationDO::getRuntimeJobId, request.getRuntimeJobId())
                        .orderByAsc(ResourceReservationDO::getPlannedStart));

        List<String> affectedReservationIds = new ArrayList<>();
        Long facilityId = request.getFacilityId();

        switch (request.getMode()) {
            case YIELD_PAUSE, ABORT -> {
                for (ResourceReservationDO row : rows) {
                    if (SlotStatus.COMPLETED.name().equals(row.getCandidateStatus())) {
                        if (facilityId == null) {
                            facilityId = row.getFacilityId();
                        }
                        continue;
                    }
                    if (!SlotStatus.CANCELLED.name().equals(row.getCandidateStatus())) {
                        row.setCandidateStatus(SlotStatus.CANCELLED.name());
                        resourceReservationMapper.updateById(row);
                        affectedReservationIds.add(row.getId());
                    }
                    if (facilityId == null) {
                        facilityId = row.getFacilityId();
                    }
                }
                appendReleaseTimeline(request, facilityId, ACTION_SLOT_RELEASE_UNFINISHED,
                        buildReleaseSummary(request.getMode(), affectedReservationIds.size()), affectedReservationIds);
            }
            case HOLD_PAUSE -> {
                for (ResourceReservationDO row : rows) {
                    if (SlotStatus.COMPLETED.name().equals(row.getCandidateStatus())
                            || SlotStatus.CANCELLED.name().equals(row.getCandidateStatus())) {
                        if (facilityId == null) {
                            facilityId = row.getFacilityId();
                        }
                        continue;
                    }
                    if (!SlotLockState.LOCKED.name().equals(row.getLockState())) {
                        row.setLockState(SlotLockState.LOCKED.name());
                        resourceReservationMapper.updateById(row);
                        affectedReservationIds.add(row.getId());
                    }
                    if (facilityId == null) {
                        facilityId = row.getFacilityId();
                    }
                }
                appendReleaseTimeline(request, facilityId, ACTION_SLOT_HOLD_PAUSE,
                        buildHoldPauseSummary(affectedReservationIds.size()), affectedReservationIds);
            }
            default -> throw new IllegalArgumentException("Unsupported release mode: " + request.getMode());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finalizePlannedSchedule(String runtimeJobId) {
        if (!StringUtils.hasText(runtimeJobId)) {
            return;
        }
        List<ResourceReservationDO> rows = resourceReservationMapper.selectList(
                new LambdaQueryWrapperX<ResourceReservationDO>()
                        .eq(ResourceReservationDO::getRuntimeJobId, runtimeJobId.trim()));
        for (ResourceReservationDO row : rows) {
            if (row.getPlannedStart() != null && row.getPlannedEnd() != null) {
                continue;
            }
            if (row.getCandidateStart() == null || row.getCandidateEnd() == null) {
                continue;
            }
            row.setPlannedStart(row.getCandidateStart());
            row.setPlannedEnd(row.getCandidateEnd());
            resourceReservationMapper.updateById(row);
        }
    }

    private void appendReleaseTimeline(RuntimeSlotReleaseReqDTO request, Long facilityId, String actionCode,
                                       String howSummary, List<String> affectedReservationIds) {
        processTimelineService.append(ProcessTimelineActionAppendReqDTO.builder()
                .targetType(TARGET_TYPE_RUNTIME_JOB)
                .targetId(request.getRuntimeJobId())
                .occurredAt(OffsetDateTime.now())
                .actionCode(actionCode)
                .howSummary(howSummary)
                .payloadJson(buildReleasePayloadJson(request, affectedReservationIds))
                .facilityId(facilityId)
                .build());
    }

    private static String buildReleaseSummary(RuntimeSlotReleaseMode mode, int cancelledCount) {
        String prefix = switch (mode) {
            case YIELD_PAUSE -> "让路暂停";
            case ABORT -> "中止";
            default -> mode.name();
        };
        return prefix + "：释放 " + cancelledCount + " 个未执行占窗段";
    }

    private static String buildHoldPauseSummary(int lockedCount) {
        return "挂起暂停：标记 " + lockedCount + " 个未执行占窗段为锁定（不释放占用）";
    }

    private static String buildReleasePayloadJson(RuntimeSlotReleaseReqDTO request, List<String> affectedReservationIds) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("runtimeJobId", request.getRuntimeJobId());
        payload.put("mode", request.getMode().name());
        payload.put("affectedReservationIds", affectedReservationIds);
        payload.put("affectedSlotIds", affectedReservationIds);
        if (StringUtils.hasText(request.getReason())) {
            payload.put("reason", request.getReason().trim());
        }
        return JSON.toJSONString(payload);
    }

    private static String buildHowSummary(RuntimeSlotStatusUpdateReqDTO request) {
        String summary = "占窗段状态更新为 " + request.getSlotStatus().name();
        if (StringUtils.hasText(request.getReason())) {
            return summary + "：" + request.getReason().trim();
        }
        return summary;
    }

    private static String buildPayloadJson(RuntimeSlotStatusUpdateReqDTO request, ResourceReservationDO row) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("candidateId", row.getId());
        payload.put("slotId", row.getId());
        payload.put("runtimeJobId", row.getRuntimeJobId());
        payload.put("workId", row.getWorkId());
        payload.put("candidateStatus", request.getSlotStatus().name());
        payload.put("slotStatus", request.getSlotStatus().name());
        if (request.getActualStart() != null) {
            payload.put("actualStart", request.getActualStart().toString());
            payload.put("actualStart", request.getActualStart().toString());
        }
        if (request.getActualEnd() != null) {
            payload.put("actualEnd", request.getActualEnd().toString());
            payload.put("actualEnd", request.getActualEnd().toString());
        }
        if (StringUtils.hasText(request.getReason())) {
            payload.put("reason", request.getReason().trim());
        }
        return JSON.toJSONString(payload);
    }
}
