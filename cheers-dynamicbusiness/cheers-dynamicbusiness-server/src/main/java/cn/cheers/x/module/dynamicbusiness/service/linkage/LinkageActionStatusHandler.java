package cn.cheers.x.module.dynamicbusiness.service.linkage;

import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventHandler;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventKinds;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 把统一动作状态反馈合并到联动整体结果。
 *
 * <p>动作执行能力只提供动作执行编号和状态，不认识联动字段。</p>
 */
@Component
@RequiredArgsConstructor
public class LinkageActionStatusHandler implements PlatformEventHandler {

    private final LinkageExecutionService linkageExecutionService;

    @Override
    public boolean supports(String eventKind) {
        return PlatformEventKinds.ACTION_EXECUTION_STATUS_CHANGED.equals(eventKind);
    }

    @Override
    public void handle(PlatformEventMessage message) {
        Long actionExecutionId = positiveLong(message.payload().get("actionExecutionId"));
        String callerType = text(message.payload().get("callerType"));
        String status = text(message.payload().get("status"));
        if (!"LINKAGE".equals(callerType) || actionExecutionId == null || status == null) {
            return;
        }
        linkageExecutionService.applyActionStatus(
                actionExecutionId,
                status,
                text(message.payload().get("error")));
    }

    private static Long positiveLong(Object value) {
        try {
            long parsed = Long.parseLong(String.valueOf(value));
            return parsed > 0 ? parsed : null;
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static String text(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }
}
