package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 告警写入、改处理或改了挂的分类之后，等这次保存完成，再把这座站的统计推出去。
 * 同一次保存里一座站只推一次。没有所属场站不推，也不改用别的站。
 * 不负责判断阈值，也不在读列表时补统计。
 */
@Component
public class AlarmFacilityNotice {

    private static final Logger log = LoggerFactory.getLogger(AlarmFacilityNotice.class);
    private static final ThreadLocal<Map<Long, Long>> PENDING = ThreadLocal.withInitial(LinkedHashMap::new);

    @Resource
    private AlarmFacilityStatsService alarmFacilityStatsService;
    @Resource
    private AlarmFacilityPublisher alarmFacilityPublisher;

    /**
     * 这些告警记录刚被写入、改了处理，或改了挂的分类。
     * 不是告警目录的记录直接返回。
     */
    public void schedule(String entityTypeCode, Collection<Long> entityIds) {
        if (entityTypeCode == null || !AlarmTypeCatalog.DIRECTORY.equals(entityTypeCode.trim()) || entityIds == null) {
            return;
        }
        Map<Long, Long> found = new LinkedHashMap<>();
        for (Long entityId : entityIds) {
            if (entityId == null || entityId <= 0) {
                continue;
            }
            Long facilityId;
            try {
                facilityId = alarmFacilityStatsService.facilityOf(entityId);
            } catch (RuntimeException ex) {
                log.warn("告警 {} 的所属场站没有读到，不推统计：{}", entityId, ex.getMessage());
                continue;
            }
            if (facilityId == null) {
                log.warn("告警 {} 没有所属场站，不推统计", entityId);
                continue;
            }
            found.put(facilityId, entityId);
        }
        if (found.isEmpty()) {
            return;
        }
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            publishAll(found, TenantContextHolder.getTenantId());
            return;
        }
        Map<Long, Long> pending = PENDING.get();
        boolean first = pending.isEmpty();
        pending.putAll(found);
        if (!first) {
            return;
        }
        Long tenantId = TenantContextHolder.getTenantId();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                Map<Long, Long> batch = new LinkedHashMap<>(PENDING.get());
                PENDING.remove();
                publishAll(batch, tenantId);
            }

            @Override
            public void afterCompletion(int status) {
                PENDING.remove();
            }
        });
    }

    private void publishAll(Map<Long, Long> facilities, Long tenantId) {
        Runnable work = () -> {
            for (Map.Entry<Long, Long> entry : facilities.entrySet()) {
                try {
                    alarmFacilityPublisher.publish(
                            alarmFacilityStatsService.snapshot(entry.getKey(), entry.getValue()));
                } catch (RuntimeException ex) {
                    log.warn("场站 {} 的告警统计没有推送：{}", entry.getKey(), ex.getMessage());
                }
            }
        };
        if (tenantId == null) {
            log.warn("没有租户，告警统计不推送");
            return;
        }
        TenantUtils.execute(tenantId, work);
    }
}
