package cn.cheers.x.module.dynamicbusiness.event.listener;

import cn.cheers.x.module.dynamicbusiness.event.EntityDataChangedEvent;
import cn.cheers.x.module.dynamicbusiness.service.computed.precompute.PrecomputeScheduler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 预计算事件监听器
 * 
 * 监听实体数据变化事件，触发预计算字段的重算。
 * 
 * @author yudao
 */
@Component
@Slf4j
public class PrecomputeEventListener {

    @Resource
    private PrecomputeScheduler precomputeScheduler;

    /**
     * 处理实体数据变化事件
     * 
     * @param event 实体数据变化事件
     */
    @EventListener
    @Async
    public void handleEntityDataChanged(EntityDataChangedEvent event) {
        log.debug("[handleEntityDataChanged][收到实体数据变化事件，modelId={}, entityId={}, changeType={}]",
                event.getModelId(), event.getEntityId(), event.getChangeType());
        
        switch (event.getChangeType()) {
            case CREATED:
                // 新创建的实体，提交预计算任务
                precomputeScheduler.submitNormalTask(event.getModelId(), event.getEntityId());
                break;
                
            case UPDATED:
                // 更新的实体，触发数据变化重算
                precomputeScheduler.onDataChanged(event.getModelId(), event.getEntityId());
                break;
                
            case DELETED:
                // 删除的实体，不需要预计算，但可能需要清理缓存
                // 缓存会自动过期，这里不做额外处理
                log.debug("[handleEntityDataChanged][实体已删除，跳过预计算，entityId={}]", 
                        event.getEntityId());
                break;
        }
    }
}
