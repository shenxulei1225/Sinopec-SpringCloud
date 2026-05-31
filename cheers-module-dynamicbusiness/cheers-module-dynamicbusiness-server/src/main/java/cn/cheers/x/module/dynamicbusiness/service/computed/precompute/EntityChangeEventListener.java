package cn.cheers.x.module.dynamicbusiness.service.computed.precompute;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 实体变化事件监听器
 * 
 * 监听实体的创建、更新、删除事件，触发计算字段的预计算
 * 
 * @author yudao
 */
@Component
@Slf4j
public class EntityChangeEventListener {

    @Resource
    private PrecomputeService precomputeService;

    /**
     * 处理实体变化事件
     * 
     * 异步处理，避免阻塞主业务流程
     */
    @EventListener
    @Async("precomputeExecutor")
    public void handleEntityChangeEvent(EntityChangeEvent event) {
        log.debug("[handleEntityChangeEvent][收到实体变化事件，type={}, modelId={}, entityId={}]",
                event.getEventType(), event.getModelId(), event.getEntityId());

        try {
            switch (event.getEventType()) {
                case CREATED:
                    precomputeService.onEntityCreated(event.getModelId(), event.getEntityId());
                    break;
                case UPDATED:
                    precomputeService.onEntityUpdated(event.getModelId(), event.getEntityId(), 
                            event.getChangedFields());
                    break;
                case DELETED:
                    precomputeService.onEntityDeleted(event.getModelId(), event.getEntityId());
                    break;
                default:
                    log.warn("[handleEntityChangeEvent][未知的事件类型，type={}]", event.getEventType());
            }
        } catch (Exception e) {
            log.error("[handleEntityChangeEvent][处理实体变化事件失败，type={}, entityId={}]",
                    event.getEventType(), event.getEntityId(), e);
        }
    }
}
