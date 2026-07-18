package cn.cheers.x.module.dynamicbusiness.event.listener;

import cn.hutool.core.collection.CollUtil;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation.RelationFieldLibraryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.relation.RelationFieldLibraryMapper;
import cn.cheers.x.module.dynamicbusiness.event.RelationTargetCreatedEvent;
import cn.cheers.x.module.dynamicbusiness.service.relation.RelationFieldLibraryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 关联字段库事件监听器
 * 
 * <p>处理关联目标创建事件，实现以下功能：</p>
 * <ul>
 *   <li>更新字段状态：当关联目标（EntityType 或 Model）被创建时，
 *       自动更新关联字段库中引用该目标的字段状态（从"待建"变为"可用"）</li>
 * </ul>
 * 
 * <h3>需求</h3>
 * <ul>
 *   <li>FR-BDA-014: 系统必须在关联目标创建后自动更新状态为"可用"</li>
 * </ul>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>BR-BDA-011: 状态自动更新 - 关联目标创建后，字段状态自动从"待建"变为"可用"</li>
 * </ul>
 * 
 * @author yudao
 */
@Component
@Slf4j
public class RelationFieldLibraryEventListener {

    @Resource
    private RelationFieldLibraryMapper relationFieldLibraryMapper;

    @Resource
    @Lazy // 避免循环依赖
    private RelationFieldLibraryService relationFieldLibraryService;

    /**
     * 处理关联目标创建事件
     * 
     * <p>当 EntityType 或 Model 被创建时，检查关联字段库中是否有引用该目标的字段，
     * 如果有，则更新这些字段的状态。</p>
     * 
     * <p>状态更新规则：</p>
     * <ul>
     *   <li>如果字段引用的目标现在存在，状态变为"可用"（AVAILABLE）</li>
     *   <li>状态是动态计算的，不存储在数据库中</li>
     * </ul>
     * 
     * @param event 关联目标创建事件
     */
    @EventListener
    @Async // 异步处理，不阻塞主流程
    @Transactional(rollbackFor = Exception.class)
    public void onRelationTargetCreated(RelationTargetCreatedEvent event) {
        log.info("[onRelationTargetCreated][收到关联目标创建事件: {}]", event);

        Long tenantId = event.getTenantId();
        if (tenantId == null) {
            log.warn("[onRelationTargetCreated][租户 ID 为空，跳过处理]");
            return;
        }

        // 在租户上下文中执行
        TenantUtils.execute(tenantId, () -> {
            try {
                if (event.isEntityTypeCreated()) {
                    // 处理 EntityType 创建事件
                    handleEntityTypeCreated(event);
                } else if (event.isModelCreated()) {
                    // 处理 Model 创建事件
                    handleModelCreated(event);
                }

                log.info("[onRelationTargetCreated][关联目标创建事件处理完成: targetType={}, targetCode={}]",
                        event.getTargetType(), event.getTargetCode());
            } catch (Exception e) {
                log.error("[onRelationTargetCreated][处理关联目标创建事件失败: {}]", event, e);
                // 不抛出异常，避免影响主流程
            }
        });
    }

    /**
     * 处理 EntityType 创建事件
     * 
     * <p>当新的 EntityType 被创建时，查找关联字段库中引用该 EntityType 的字段，
     * 并记录日志（状态是动态计算的，不需要更新数据库）。</p>
     * 
     * @param event 关联目标创建事件
     */
    private void handleEntityTypeCreated(RelationTargetCreatedEvent event) {
        String entityTypeCode = event.getTargetCode();

        log.debug("[handleEntityTypeCreated][处理 EntityType 创建: entityTypeCode={}]", entityTypeCode);

        // 查找引用该 EntityType 的所有字段
        List<RelationFieldLibraryDO> affectedFields =
                relationFieldLibraryMapper.selectByRefEntityType(entityTypeCode);

        if (CollUtil.isEmpty(affectedFields)) {
            log.debug("[handleEntityTypeCreated][没有字段引用该 EntityType，跳过处理]");
            return;
        }

        // 记录受影响的字段
        // 注意：字段状态是动态计算的（通过 checkTargetExists 方法），不存储在数据库中
        // 这里只是记录日志，实际状态会在查询时动态计算
        for (RelationFieldLibraryDO field : affectedFields) {
            log.info("[handleEntityTypeCreated][字段状态可能已更新: fieldCode={}, refEntityType={}]",
                    field.getFieldCode(), field.getRefEntityType());
        }

        log.info("[handleEntityTypeCreated][EntityType 创建处理完成: entityTypeCode={}, affectedFieldCount={}]",
                entityTypeCode, affectedFields.size());
    }

    /**
     * 处理 Model 创建事件
     *
     * <p>关联字段按业务级引用，不再跟踪 Model 级事件。</p>
     *
     * @param event 关联目标创建事件
     */
    private void handleModelCreated(RelationTargetCreatedEvent event) {
        log.debug("[handleModelCreated][忽略 Model 创建事件，关联字段按业务级引用]", event.getTargetCode());
    }
}
