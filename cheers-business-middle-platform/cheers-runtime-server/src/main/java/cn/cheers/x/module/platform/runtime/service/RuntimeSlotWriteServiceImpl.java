package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.module.platform.contract.enums.SlotLockState;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionAppendReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotReleaseReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotStatusUpdateReqDTO;
import cn.cheers.x.module.platform.runtime.dal.dataobject.ScheduleSlotDO;
import cn.cheers.x.module.platform.runtime.dal.mysql.ScheduleSlotMapper;
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
    private ScheduleSlotMapper scheduleSlotMapper;
    @Resource
    private ProcessTimelineService processTimelineService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSlotStatus(RuntimeSlotStatusUpdateReqDTO request) {
        ScheduleSlotDO slot = scheduleSlotMapper.selectById(request.getSlotId());
        if (slot == null) {
            throw exception(SCHEDULE_SLOT_NOT_EXISTS);
        }

        slot.setSlotStatus(request.getSlotStatus().name());
        if (request.getActualStart() != null) {
            slot.setActualStart(request.getActualStart());
        }
        if (request.getActualEnd() != null) {
            slot.setActualEnd(request.getActualEnd());
        }
        scheduleSlotMapper.updateById(slot);

        OffsetDateTime occurredAt = request.getActualEnd() != null
                ? request.getActualEnd()
                : OffsetDateTime.now();
        Long facilityId = request.getFacilityId() != null ? request.getFacilityId() : slot.getFacilityId();

        processTimelineService.append(ProcessTimelineActionAppendReqDTO.builder()
                .targetType(TARGET_TYPE_SCHEDULE_SLOT)
                .targetId(slot.getId())
                .occurredAt(occurredAt)
                .actionCode(ACTION_SLOT_STATUS_UPDATE)
                .howSummary(buildHowSummary(request))
                .payloadJson(buildPayloadJson(request, slot))
                .facilityId(facilityId)
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseUnfinished(RuntimeSlotReleaseReqDTO request) {
        List<ScheduleSlotDO> slots = scheduleSlotMapper.selectList(new LambdaQueryWrapperX<ScheduleSlotDO>()
                .eq(ScheduleSlotDO::getRuntimeJobId, request.getRuntimeJobId())
                .orderByAsc(ScheduleSlotDO::getPlannedStart));

        List<String> affectedSlotIds = new ArrayList<>();
        Long facilityId = request.getFacilityId();

        switch (request.getMode()) {
            case YIELD_PAUSE, ABORT -> {
                for (ScheduleSlotDO slot : slots) {
                    if (SlotStatus.COMPLETED.name().equals(slot.getSlotStatus())) {
                        if (facilityId == null) {
                            facilityId = slot.getFacilityId();
                        }
                        continue;
                    }
                    if (!SlotStatus.CANCELLED.name().equals(slot.getSlotStatus())) {
                        slot.setSlotStatus(SlotStatus.CANCELLED.name());
                        scheduleSlotMapper.updateById(slot);
                        affectedSlotIds.add(slot.getId());
                    }
                    if (facilityId == null) {
                        facilityId = slot.getFacilityId();
                    }
                }
                appendReleaseTimeline(request, facilityId, ACTION_SLOT_RELEASE_UNFINISHED,
                        buildReleaseSummary(request.getMode(), affectedSlotIds.size()), affectedSlotIds);
            }
            case HOLD_PAUSE -> {
                for (ScheduleSlotDO slot : slots) {
                    if (SlotStatus.COMPLETED.name().equals(slot.getSlotStatus())
                            || SlotStatus.CANCELLED.name().equals(slot.getSlotStatus())) {
                        if (facilityId == null) {
                            facilityId = slot.getFacilityId();
                        }
                        continue;
                    }
                    if (!SlotLockState.LOCKED.name().equals(slot.getLockState())) {
                        slot.setLockState(SlotLockState.LOCKED.name());
                        scheduleSlotMapper.updateById(slot);
                        affectedSlotIds.add(slot.getId());
                    }
                    if (facilityId == null) {
                        facilityId = slot.getFacilityId();
                    }
                }
                appendReleaseTimeline(request, facilityId, ACTION_SLOT_HOLD_PAUSE,
                        buildHoldPauseSummary(affectedSlotIds.size()), affectedSlotIds);
            }
            default -> throw new IllegalArgumentException("Unsupported release mode: " + request.getMode());
        }
    }

    private void appendReleaseTimeline(RuntimeSlotReleaseReqDTO request, Long facilityId, String actionCode,
                                       String howSummary, List<String> affectedSlotIds) {
        processTimelineService.append(ProcessTimelineActionAppendReqDTO.builder()
                .targetType(TARGET_TYPE_RUNTIME_JOB)
                .targetId(request.getRuntimeJobId())
                .occurredAt(OffsetDateTime.now())
                .actionCode(actionCode)
                .howSummary(howSummary)
                .payloadJson(buildReleasePayloadJson(request, affectedSlotIds))
                .facilityId(facilityId)
                .build());
    }

    private static String buildReleaseSummary(RuntimeSlotReleaseMode mode, int cancelledCount) {
        String prefix = switch (mode) {
            case YIELD_PAUSE -> "让路暂停";
            case ABORT -> "中止";
            default -> mode.name();
        };
        String summary = prefix + "：释放 " + cancelledCount + " 个未执行计划点";
        return summary;
    }

    private static String buildHoldPauseSummary(int lockedCount) {
        return "挂起暂停：标记 " + lockedCount + " 个未执行计划点为锁定（不释放占用）";
    }

    private static String buildReleasePayloadJson(RuntimeSlotReleaseReqDTO request, List<String> affectedSlotIds) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("runtimeJobId", request.getRuntimeJobId());
        payload.put("mode", request.getMode().name());
        payload.put("affectedSlotIds", affectedSlotIds);
        if (StringUtils.hasText(request.getReason())) {
            payload.put("reason", request.getReason().trim());
        }
        return JSON.toJSONString(payload);
    }

    private static String buildHowSummary(RuntimeSlotStatusUpdateReqDTO request) {
        String summary = "计划点状态更新为 " + request.getSlotStatus().name();
        if (StringUtils.hasText(request.getReason())) {
            return summary + "：" + request.getReason().trim();
        }
        return summary;
    }

    private static String buildPayloadJson(RuntimeSlotStatusUpdateReqDTO request, ScheduleSlotDO slot) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("slotId", slot.getId());
        payload.put("runtimeJobId", slot.getRuntimeJobId());
        payload.put("workId", slot.getWorkId());
        payload.put("slotStatus", request.getSlotStatus().name());
        if (request.getActualStart() != null) {
            payload.put("actualStart", request.getActualStart().toString());
        }
        if (request.getActualEnd() != null) {
            payload.put("actualEnd", request.getActualEnd().toString());
        }
        if (StringUtils.hasText(request.getReason())) {
            payload.put("reason", request.getReason().trim());
        }
        return JSON.toJSONString(payload);
    }
}
