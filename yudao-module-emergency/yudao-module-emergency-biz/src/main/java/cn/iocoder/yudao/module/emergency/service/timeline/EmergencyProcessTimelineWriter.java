package cn.iocoder.yudao.module.emergency.service.timeline;

import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.platform.runtime.api.ProcessTimelineApi;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionAppendReqDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * 应急关键写操作双写中台过程时间线。中台不可用时只打错误日志，不阻断主业务、不伪造条目。
 */
@Slf4j
@Component
public class EmergencyProcessTimelineWriter {

    public static final String TARGET_TYPE_EMERGENCY_EVENT = "emergency_event";

    @Resource
    private ProcessTimelineApi processTimelineApi;

    public void appendEventAction(Long eventId, String actionCode, String howSummary) {
        if (eventId == null || !StringUtils.hasText(actionCode) || !StringUtils.hasText(howSummary)) {
            log.error("过程时间线双写跳过：缺少 eventId/actionCode/howSummary");
            return;
        }
        try {
            Long actorId = SecurityFrameworkUtils.getLoginUserId();
            String actorName = SecurityFrameworkUtils.getLoginUserNickname();
            processTimelineApi.append(ProcessTimelineActionAppendReqDTO.builder()
                            .targetType(TARGET_TYPE_EMERGENCY_EVENT)
                            .targetId(String.valueOf(eventId))
                            .occurredAt(OffsetDateTime.now(ZoneOffset.UTC))
                            .actorId(actorId != null ? String.valueOf(actorId) : null)
                            .actorName(actorName)
                            .actionCode(actionCode.trim())
                            .howSummary(howSummary.trim())
                            .build())
                    .checkError();
        } catch (Exception e) {
            log.error("过程时间线双写失败: eventId={}, actionCode={}", eventId, actionCode, e);
        }
    }
}
