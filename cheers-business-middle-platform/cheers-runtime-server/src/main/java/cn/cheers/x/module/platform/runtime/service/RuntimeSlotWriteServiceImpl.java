package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionAppendReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotStatusUpdateReqDTO;
import cn.cheers.x.module.platform.runtime.dal.dataobject.ScheduleSlotDO;
import cn.cheers.x.module.platform.runtime.dal.mysql.ScheduleSlotMapper;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platform.runtime.enums.ErrorCodeConstants.SCHEDULE_SLOT_NOT_EXISTS;

@Service
public class RuntimeSlotWriteServiceImpl implements RuntimeSlotWriteService {

    private static final String TARGET_TYPE_SCHEDULE_SLOT = "schedule_slot";
    private static final String ACTION_SLOT_STATUS_UPDATE = "slot.status_update";

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
        Long siteId = request.getSiteId() != null ? request.getSiteId() : slot.getSiteId();

        processTimelineService.append(ProcessTimelineActionAppendReqDTO.builder()
                .targetType(TARGET_TYPE_SCHEDULE_SLOT)
                .targetId(slot.getId())
                .occurredAt(occurredAt)
                .actionCode(ACTION_SLOT_STATUS_UPDATE)
                .howSummary(buildHowSummary(request))
                .payloadJson(buildPayloadJson(request, slot))
                .siteId(siteId)
                .build());
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
