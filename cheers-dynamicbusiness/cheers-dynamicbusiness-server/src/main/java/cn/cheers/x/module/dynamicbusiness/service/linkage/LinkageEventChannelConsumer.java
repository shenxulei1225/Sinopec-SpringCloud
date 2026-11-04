package cn.cheers.x.module.dynamicbusiness.service.linkage;

import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventMessage;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventMessageCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 联动从消息通道拿到正文之后的入口。
 *
 * <p>管什么：认出运行时事件后，交给联动自己的处理。</p>
 * <p>不管什么：不连消息通道，不读告警记录，也不让告警包直接调用这里。</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LinkageEventChannelConsumer {

    private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final LinkageEventHandler linkageEventHandler;
    private final LinkageActionStatusHandler linkageActionStatusHandler;

    public void accept(byte[] payload) {
        PlatformEventMessage event = read(payload);
        if (event == null || event.tenantId() <= 0) {
            return;
        }
        TenantUtils.execute(event.tenantId(), () -> {
            try {
                dispatch(event);
            } catch (RuntimeException failure) {
                log.error("联动处理事件失败：{}", failure.getMessage());
            }
        });
    }

    private void dispatch(PlatformEventMessage event) {
        if (linkageEventHandler.supports(event.eventKind())) {
            linkageEventHandler.handle(event);
        }
        if (linkageActionStatusHandler.supports(event.eventKind())) {
            linkageActionStatusHandler.handle(event);
        }
    }

    private PlatformEventMessage read(byte[] payload) {
        if (payload == null || payload.length == 0) {
            return null;
        }
        try {
            return PlatformEventMessageCodec.fromMap(objectMapper.readValue(payload, MAP));
        } catch (Exception ex) {
            log.warn("联动收到的消息不是运行时事件：{}", ex.getMessage());
            return null;
        }
    }
}
