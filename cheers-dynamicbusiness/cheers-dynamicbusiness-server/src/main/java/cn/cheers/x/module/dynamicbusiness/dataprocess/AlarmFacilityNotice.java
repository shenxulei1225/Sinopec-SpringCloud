package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 告警写入、改处理或改了挂的分类之后，等这次保存完成，再把这座站的统计推出去。
 * 同一次保存里一座站只推一次。这一次碰到的告警全部放进报文，不只留最后一条。
 * 没有所属场站不推，也不改用别的站。
 * 不负责判断阈值，也不在读列表时补统计。
 */
@Component
public class AlarmFacilityNotice {

    private static final Logger log = LoggerFactory.getLogger(AlarmFacilityNotice.class);
    private static final ThreadLocal<Map<Long, List<Long>>> PENDING = ThreadLocal.withInitial(LinkedHashMap::new);

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
        Map<Long, List<Long>> found = new LinkedHashMap<>();
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
            appendId(found, facilityId, entityId);
        }
        if (found.isEmpty()) {
            return;
        }
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            publishAll(found, TenantContextHolder.getTenantId());
            return;
        }
        Map<Long, List<Long>> pending = PENDING.get();
        boolean first = pending.isEmpty();
        appendAll(pending, found);
        if (!first) {
            return;
        }
        Long tenantId = TenantContextHolder.getTenantId();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                Map<Long, List<Long>> batch = copyPending(PENDING.get());
                PENDING.remove();
                publishAll(batch, tenantId);
            }

            @Override
            public void afterCompletion(int status) {
                PENDING.remove();
            }
        });
    }

    private void publishAll(Map<Long, List<Long>> facilities, Long tenantId) {
        Runnable work = () -> {
            for (Map.Entry<Long, List<Long>> entry : facilities.entrySet()) {
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

    /** 同一座站按碰到的顺序留下每条告警。重复编号不再加一次。 */
    private static void appendId(Map<Long, List<Long>> target, Long facilityId, Long entityId) {
        List<Long> ids = target.computeIfAbsent(facilityId, ignored -> new ArrayList<>());
        if (!ids.contains(entityId)) {
            ids.add(entityId);
        }
    }

    private static void appendAll(Map<Long, List<Long>> target, Map<Long, List<Long>> extra) {
        for (Map.Entry<Long, List<Long>> entry : extra.entrySet()) {
            for (Long entityId : entry.getValue()) {
                appendId(target, entry.getKey(), entityId);
            }
        }
    }

    private static Map<Long, List<Long>> copyPending(Map<Long, List<Long>> source) {
        Map<Long, List<Long>> copy = new LinkedHashMap<>();
        appendAll(copy, source);
        return copy;
    }
}
