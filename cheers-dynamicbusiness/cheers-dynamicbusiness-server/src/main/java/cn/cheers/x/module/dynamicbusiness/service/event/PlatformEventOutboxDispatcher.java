package cn.cheers.x.module.dynamicbusiness.service.event;

import cn.cheers.x.framework.tenant.core.service.TenantFrameworkService;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 补送还没发出的平台事件。
 *
 * <p>主路径是业务提交成功后立刻发到消息通道。本循环只处理失败和重启未完成的。</p>
 * <p>每个租户独立处理。不调用联动入口。</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PlatformEventOutboxDispatcher {

    private static final int BATCH_SIZE = 50;
    private static final int STALE_SECONDS = 15;

    private final TenantFrameworkService tenantFrameworkService;
    private final PlatformEventOutboxMapper mapper;
    private final PlatformEventOutboxDeliveryService deliveryService;

    @Scheduled(fixedDelayString = "${dynamicbusiness.event-outbox.delay-ms:1000}")
    public void dispatch() {
        List<Long> tenantIds = tenantFrameworkService.getTenantIds();
        if (tenantIds == null) {
            return;
        }
        for (Long tenantId : tenantIds) {
            if (tenantId == null || tenantId <= 0) {
                continue;
            }
            TenantUtils.execute(tenantId, this::dispatchCurrentTenant);
        }
    }

    private void dispatchCurrentTenant() {
        mapper.reclaimStale(LocalDateTime.now().minusSeconds(STALE_SECONDS));
        for (PlatformEventOutboxDO row : mapper.selectPending(BATCH_SIZE)) {
            try {
                deliveryService.deliver(row.getId());
            } catch (RuntimeException failure) {
                log.error("平台事件 {} 投递失败：{}", row.getEventId(), failure.getMessage());
                deliveryService.recordFailure(row.getId(), failure);
            }
        }
    }
}
