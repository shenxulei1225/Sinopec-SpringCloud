package cn.cheers.x.module.dynamicbusiness.event.listener;

import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.module.dynamicbusiness.event.FieldDefinitionChangedEvent;
import cn.cheers.x.module.dynamicbusiness.service.relation.BidirectionalRelationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 双向关联缓存监听器
 * 
 * <p>处理字段定义变更事件，实现以下功能：</p>
 * <ul>
 *   <li>清除关联发现缓存：当 ENTITY_REF 类型字段发生变更时，清除相关的关联发现缓存</li>
 * </ul>
 * 
 * <h3>缓存一致性</h3>
 * <p>双向关联查询服务使用 Caffeine 缓存来存储关联发现结果，以提高查询性能。
 * 当字段定义发生变更时（特别是 ENTITY_REF 类型字段），需要清除相关缓存以保证数据一致性。</p>
 * 
 * <h3>触发条件</h3>
 * <ul>
 *   <li>ENTITY_REF 类型字段被创建</li>
 *   <li>ENTITY_REF 类型字段被更新（如修改引用的 Model）</li>
 *   <li>ENTITY_REF 类型字段被删除</li>
 *   <li>ENTITY_REF 类型字段被分配到 Model</li>
 *   <li>ENTITY_REF 类型字段从 Model 移除</li>
 * </ul>
 * 
 * @author yudao
 */
@Component
@Slf4j
public class BidirectionalRelationCacheListener {

    @Resource
    @Lazy // 避免循环依赖
    private BidirectionalRelationService bidirectionalRelationService;

    /**
     * 处理字段定义变更事件
     * 
     * <p>当字段定义发生变更时，检查是否为 ENTITY_REF 类型字段，
     * 如果是，则清除相关的关联发现缓存。</p>
     * 
     * @param event 字段定义变更事件
     */
    @EventListener
    @Async // 异步处理，不阻塞主流程
    public void onFieldDefinitionChanged(FieldDefinitionChangedEvent event) {
        log.info("[onFieldDefinitionChanged][收到字段定义变更事件: {}]", event);

        // 只处理关联字段（ENTITY_REF 类型）的变更
        if (!event.isRelationFieldChange()) {
            log.debug("[onFieldDefinitionChanged][非关联字段变更，跳过缓存清除: fieldType={}]", 
                    event.getFieldType());
            return;
        }

        Long tenantId = event.getTenantId();
        if (tenantId == null) {
            // 如果没有租户 ID，直接清除缓存（不需要租户上下文）
            clearCache(event);
            return;
        }

        // 在租户上下文中执行
        TenantUtils.execute(tenantId, () -> {
            try {
                clearCache(event);
            } catch (Exception e) {
                log.error("[onFieldDefinitionChanged][清除缓存失败: {}]", event, e);
                // 不抛出异常，避免影响主流程
            }
        });
    }

    /**
     * 清除关联发现缓存
     * 
     * <p>根据事件类型和变更范围，决定清除哪些缓存：</p>
     * <ul>
     *   <li>如果是 Model 级别的变更（分配/移除），只清除相关 Model 的缓存</li>
     *   <li>如果是字段级别的变更（创建/更新/删除），清除所有缓存</li>
     * </ul>
     * 
     * @param event 字段定义变更事件
     */
    private void clearCache(FieldDefinitionChangedEvent event) {
        FieldDefinitionChangedEvent.ChangeType changeType = event.getChangeType();
        String modelCode = event.getModelCode();

        log.debug("[clearCache][开始清除缓存: changeType={}, modelCode={}, fieldCode={}]",
                changeType, modelCode, event.getFieldCode());

        switch (changeType) {
            case ASSIGNED:
            case UNASSIGNED:
                // Model 级别的变更，清除相关 Model 的缓存
                if (modelCode != null) {
                    bidirectionalRelationService.clearDiscoveryCache(modelCode);
                    log.info("[clearCache][清除 Model 关联发现缓存: modelCode={}]", modelCode);
                } else {
                    // 如果没有 modelCode，清除所有缓存
                    bidirectionalRelationService.clearDiscoveryCache(null);
                    log.info("[clearCache][清除所有关联发现缓存（无 modelCode）]");
                }
                break;

            case CREATED:
            case UPDATED:
            case DELETED:
                // 字段级别的变更，可能影响多个 Model，清除所有缓存
                bidirectionalRelationService.clearDiscoveryCache(null);
                log.info("[clearCache][清除所有关联发现缓存: changeType={}]", changeType);
                break;

            default:
                log.warn("[clearCache][未知的变更类型: {}]", changeType);
                // 保守策略：清除所有缓存
                bidirectionalRelationService.clearDiscoveryCache(null);
                break;
        }

        log.info("[clearCache][缓存清除完成: changeType={}, fieldCode={}]",
                changeType, event.getFieldCode());
    }
}
