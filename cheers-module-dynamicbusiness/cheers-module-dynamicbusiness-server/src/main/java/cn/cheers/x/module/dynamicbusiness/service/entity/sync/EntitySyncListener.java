package cn.cheers.x.module.dynamicbusiness.service.entity.sync;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Entity 变更监听器
 * 
 * <p>监听 Entity 的保存、更新、删除事件，触发异步同步到查询索引。</p>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>FR-034: Entity 保存时自动同步到查询索引</li>
 *   <li>FR-035: 同步采用异步方式，不阻塞主业务流程</li>
 * </ul>
 * 
 * <h3>使用方式</h3>
 * <p>在 EntityService 中发布事件：</p>
 * <pre>
 * applicationEventPublisher.publishEvent(new EntitySavedEvent(entity));
 * applicationEventPublisher.publishEvent(new EntityUpdatedEvent(entity));
 * applicationEventPublisher.publishEvent(new EntityDeletedEvent(entityId));
 * </pre>
 * 
 * @author 扩展字段查询服务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EntitySyncListener {

    private final EntitySyncService entitySyncService;

    /**
     * 监听 Entity 保存事件
     * 
     * @param event Entity 保存事件
     */
    @EventListener
    @Async("entitySyncExecutor")
    public void onEntitySaved(EntitySavedEvent event) {
        EntityDO entity = event.getEntity();
        log.debug("收到 Entity 保存事件: entityId={}, modelId={}", entity.getId(), entity.getModelId());
        
        try {
            entitySyncService.syncEntity(entity);
            log.debug("Entity 保存后同步成功: entityId={}", entity.getId());
        } catch (Exception e) {
            log.error("Entity 保存后同步失败: entityId={}, error={}", entity.getId(), e.getMessage(), e);
            // 同步失败会被 EntitySyncService 记录到失败日志，由重试服务处理
        }
    }

    /**
     * 监听 Entity 更新事件
     * 
     * @param event Entity 更新事件
     */
    @EventListener
    @Async("entitySyncExecutor")
    public void onEntityUpdated(EntityUpdatedEvent event) {
        EntityDO entity = event.getEntity();
        log.debug("收到 Entity 更新事件: entityId={}, modelId={}", entity.getId(), entity.getModelId());
        
        try {
            entitySyncService.syncEntity(entity);
            log.debug("Entity 更新后同步成功: entityId={}", entity.getId());
        } catch (Exception e) {
            log.error("Entity 更新后同步失败: entityId={}, error={}", entity.getId(), e.getMessage(), e);
        }
    }

    /**
     * 监听 Entity 删除事件
     * 
     * @param event Entity 删除事件
     */
    @EventListener
    @Async("entitySyncExecutor")
    public void onEntityDeleted(EntityDeletedEvent event) {
        Long entityId = event.getEntityId();
        log.debug("收到 Entity 删除事件: entityId={}", entityId);
        
        try {
            entitySyncService.deleteFromIndex(entityId);
            log.debug("Entity 删除后从索引移除成功: entityId={}", entityId);
        } catch (Exception e) {
            log.error("Entity 删除后从索引移除失败: entityId={}, error={}", entityId, e.getMessage(), e);
        }
    }

    // ==================== 事件类定义 ====================

    /**
     * Entity 保存事件
     */
    public static class EntitySavedEvent {
        private final EntityDO entity;

        public EntitySavedEvent(EntityDO entity) {
            this.entity = entity;
        }

        public EntityDO getEntity() {
            return entity;
        }
    }

    /**
     * Entity 更新事件
     */
    public static class EntityUpdatedEvent {
        private final EntityDO entity;

        public EntityUpdatedEvent(EntityDO entity) {
            this.entity = entity;
        }

        public EntityDO getEntity() {
            return entity;
        }
    }

    /**
     * Entity 删除事件
     */
    public static class EntityDeletedEvent {
        private final Long entityId;

        public EntityDeletedEvent(Long entityId) {
            this.entityId = entityId;
        }

        public Long getEntityId() {
            return entityId;
        }
    }
}
