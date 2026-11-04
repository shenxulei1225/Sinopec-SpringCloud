package cn.cheers.x.module.dynamicbusiness.service.event;

import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.module.dynamicbusiness.service.action.execution.ActionExecutionDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;

/**
 * 把已经成立的业务事件写入可靠投递队列。
 *
 * <p>调用方应在保存业务状态的同一事务内调用本服务。</p>
 * <p>提交成功后立刻发到消息通道。循环只补送失败和重启未完成的。</p>
 * <p>禁止：发布失败后直接调用联动，或丢弃事件后假装成功。</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlatformEventOutboxService {

    private final PlatformEventOutboxMapper mapper;
    private final ObjectProvider<PlatformEventOutboxDeliveryService> deliveryProvider;

    @Transactional(rollbackFor = Exception.class)
    public void enqueue(String eventKind, PlatformEventEnvelope event) {
        PlatformEventOutboxDO row = new PlatformEventOutboxDO();
        row.setEventId(event.eventId());
        row.setEventTopicId(event.eventTopicId());
        row.setEventCode(event.eventCode());
        row.setEventKind(eventKind);
        row.setFacilityId(event.facilityId());
        row.setSourceEntityTypeCode(event.sourceEntityTypeCode());
        row.setSourceEntityId(event.sourceEntityId());
        row.setPhase(event.phase());
        row.setOccurredAt(LocalDateTime.ofInstant(event.occurredAt(), ZoneOffset.UTC));
        row.setPayload(new LinkedHashMap<>(event.attributes()));
        row.setStatus(PlatformEventOutboxStatus.PENDING);
        row.setAttempts(0);
        row.setAvailableAt(LocalDateTime.now());
        row.setTenantId(event.tenantId());
        mapper.insert(row);
        kickAfterCommit(row.getId());
    }

    /**
     * 动作执行器完成后发布统一状态变化。它是平台内部契约，不占用用户事件主题。
     */
    @Transactional(rollbackFor = Exception.class)
    public void enqueueActionStatus(ActionExecutionDO execution) {
        PlatformEventOutboxDO row = new PlatformEventOutboxDO();
        row.setEventId("action-execution:" + execution.getId() + ":" + execution.getStatus());
        row.setEventTopicId(0L);
        row.setEventCode(PlatformEventKinds.ACTION_EXECUTION_STATUS_CHANGED);
        row.setEventKind(PlatformEventKinds.ACTION_EXECUTION_STATUS_CHANGED);
        row.setFacilityId(execution.getFacilityId());
        row.setSourceEntityTypeCode("action_execution");
        row.setSourceEntityId(execution.getId());
        row.setOccurredAt(LocalDateTime.now());
        row.setPayload(new LinkedHashMap<>(java.util.Map.of(
                "actionExecutionId", execution.getId(),
                "callerType", execution.getCallerType(),
                "callerReference", execution.getCallerReference(),
                "status", execution.getStatus(),
                "error", execution.getErrorMessage() == null ? "" : execution.getErrorMessage())));
        row.setStatus(PlatformEventOutboxStatus.PENDING);
        row.setAttempts(0);
        row.setAvailableAt(LocalDateTime.now());
        row.setTenantId(execution.getTenantId());
        mapper.insert(row);
        kickAfterCommit(row.getId());
    }

    private void kickAfterCommit(Long outboxId) {
        Long tenantId = TenantContextHolder.getTenantId();
        Runnable kick = () -> publishNow(outboxId, tenantId);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    kick.run();
                }
            });
            return;
        }
        kick.run();
    }

    private void publishNow(Long outboxId, Long tenantId) {
        PlatformEventOutboxDeliveryService delivery = deliveryProvider.getIfAvailable();
        if (delivery == null || outboxId == null) {
            return;
        }
        try {
            if (tenantId != null && tenantId > 0) {
                TenantUtils.execute(tenantId, () -> delivery.deliver(outboxId));
            } else {
                delivery.deliver(outboxId);
            }
        } catch (RuntimeException ex) {
            log.warn("事件提交后没有发出，稍后重试：{}", ex.getMessage());
            try {
                delivery.recordFailure(outboxId, ex);
            } catch (RuntimeException recordEx) {
                log.error("事件 {} 投递失败后没有重新排队：{}", outboxId, recordEx.getMessage());
            }
        }
    }
}
