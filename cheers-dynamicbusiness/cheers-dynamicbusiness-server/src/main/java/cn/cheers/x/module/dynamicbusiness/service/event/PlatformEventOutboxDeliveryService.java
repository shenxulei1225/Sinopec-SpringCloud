package cn.cheers.x.module.dynamicbusiness.service.event;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.service.push.PushCatalogQueryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * 认领一条 outbox，发到消息通道。
 *
 * <p>管什么：同一事务里认领并发出。发出失败则回滚认领，稍后重试。</p>
 * <p>不管什么：不调用联动入口，不补事件主题的名字规则。</p>
 */
@Service
@RequiredArgsConstructor
public class PlatformEventOutboxDeliveryService {

    private final PlatformEventOutboxMapper mapper;
    private final PushCatalogQueryService pushCatalogQueryService;
    private final PlatformEventChannelPublisher publisher;
    private final ObjectMapper objectMapper;

    @Transactional(rollbackFor = Exception.class)
    public void deliver(long outboxId) {
        if (mapper.claim(outboxId) == 0) {
            return;
        }
        PlatformEventOutboxDO row = mapper.selectById(outboxId);
        PlatformEventMessage message = toMessage(row);
        try {
            publisher.publish(resolveTopic(message), objectMapper.writeValueAsBytes(
                    PlatformEventMessageCodec.toMap(message)));
        } catch (JsonProcessingException ex) {
            throw new ServiceException(500, "事件正文无法写出");
        }
        row.setStatus(PlatformEventOutboxStatus.PROCESSED);
        row.setProcessedAt(LocalDateTime.now());
        row.setLastError(null);
        mapper.updateById(row);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(long outboxId, RuntimeException failure) {
        PlatformEventOutboxDO row = mapper.selectById(outboxId);
        if (row == null || PlatformEventOutboxStatus.PROCESSED.equals(row.getStatus())) {
            return;
        }
        row.setStatus(PlatformEventOutboxStatus.PENDING);
        int attempts = row.getAttempts() == null ? 1 : row.getAttempts();
        row.setAvailableAt(LocalDateTime.now().plusSeconds(Math.min(300L, 1L << Math.min(attempts, 8))));
        row.setLastError(clip(failure.getMessage()));
        mapper.updateById(row);
    }

    /**
     * 用户事件按事件主题已经写下的名字规则发出。动作状态变化走平台内部主题。
     */
    String resolveTopic(PlatformEventMessage message) {
        if (PlatformEventKinds.ACTION_EXECUTION_STATUS_CHANGED.equals(message.eventKind())
                || message.eventTopicId() <= 0) {
            return PlatformEventChannelTopics.ACTION_STATUS;
        }
        String filled = PlatformEventChannelTopics.fill(
                pushCatalogQueryService.namePatternByTopicId(message.eventTopicId()),
                message.facilityId(),
                message.sourceEntityId());
        if (filled == null) {
            throw new ServiceException(400, "事件主题没有名字规则，没有发出");
        }
        return filled;
    }

    private static PlatformEventMessage toMessage(PlatformEventOutboxDO row) {
        return new PlatformEventMessage(
                row.getEventId(),
                row.getEventTopicId(),
                row.getEventCode(),
                row.getEventKind(),
                row.getTenantId() == null ? 0L : row.getTenantId(),
                row.getFacilityId(),
                row.getSourceEntityTypeCode(),
                row.getSourceEntityId(),
                row.getPhase(),
                row.getOccurredAt().toInstant(ZoneOffset.UTC),
                row.getPayload() == null ? Map.of() : Map.copyOf(row.getPayload()));
    }

    private static String clip(String value) {
        if (value == null) {
            return "未知错误";
        }
        return value.length() <= 2000 ? value : value.substring(0, 2000);
    }
}
