package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.event.EntityNameChangedEvent;
import cn.cheers.x.module.dynamicbusiness.service.computed.precompute.EntityChangeEvent;
import cn.cheers.x.module.dynamicbusiness.service.entity.sync.EntitySyncListener.EntityDeletedEvent;
import cn.cheers.x.module.dynamicbusiness.service.entity.sync.EntitySyncListener.EntitySavedEvent;
import cn.cheers.x.module.dynamicbusiness.service.entity.sync.EntitySyncListener.EntityUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 实体写操作后的领域事件发布（与 {@link EntityChangeEvent}、同步监听事件对齐）。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EntityLifecycleEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishEntityCreatedEvent(Long modelId, Long entityId, String businessTypeCode, EntityDO entity) {
        try {
            if (entity != null) {
                eventPublisher.publishEvent(new EntitySavedEvent(entity));
            }
            eventPublisher.publishEvent(EntityChangeEvent.created(this, modelId, entityId, businessTypeCode));
        } catch (Exception e) {
            log.warn("[publishEntityCreatedEvent] 发布事件失败: entityId={}", entityId, e);
        }
    }

    public void publishEntityUpdatedEvent(Long modelId, Long entityId, String businessTypeCode,
            List<String> changedFields, EntityDO entity) {
        try {
            if (entity != null) {
                eventPublisher.publishEvent(new EntityUpdatedEvent(entity));
            }
            eventPublisher.publishEvent(EntityChangeEvent.updated(this, modelId, entityId, businessTypeCode, changedFields));
        } catch (Exception e) {
            log.warn("[publishEntityUpdatedEvent] 发布事件失败: entityId={}", entityId, e);
        }
    }

    public void publishEntityDeletedEvent(Long modelId, Long entityId, String businessTypeCode) {
        try {
            eventPublisher.publishEvent(new EntityDeletedEvent(entityId));
            eventPublisher.publishEvent(EntityChangeEvent.deleted(this, modelId, entityId, businessTypeCode));
        } catch (Exception e) {
            log.warn("[publishEntityDeletedEvent] 发布事件失败: entityId={}", entityId, e);
        }
    }

    public void publishEntityNameChangedEvent(Long entityId, String oldName, String newName,
            String businessTypeCode, String modelCode, Long tenantId) {
        try {
            eventPublisher.publishEvent(new EntityNameChangedEvent(
                    this, entityId, oldName, newName, businessTypeCode, modelCode, tenantId));
        } catch (Exception e) {
            log.warn("[publishEntityNameChangedEvent] 发布事件失败: entityId={}", entityId, e);
        }
    }
}
