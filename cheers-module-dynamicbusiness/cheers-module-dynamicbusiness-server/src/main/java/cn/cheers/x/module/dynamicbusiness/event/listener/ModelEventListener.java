package cn.cheers.x.module.dynamicbusiness.event.listener;

import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.module.dynamicbusiness.event.ModelCreatedEvent;
import cn.cheers.x.module.dynamicbusiness.service.businesstype.BusinessTypeRelationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Model 事件监听器
 *
 * <p>处理 Model 创建事件，实现以下功能：</p>
 * <ul>
 *   <li>暂时禁用：展开 BusinessType 关联（不再在Model层展开以避免字段爆炸）</li>
 *   <li>预留：未来可能添加其他Model相关的事件处理</li>
 * </ul>
 *
 * <h3>变更说明</h3>
 * <ul>
 *   <li>FR-BDA-072: 已取消自动展开BusinessType关联到Model层面的功能</li>
 *   <li>原因：Model层展开会导致字段爆炸问题</li>
 * </ul>
 *
 * @author yudao
 */
@Component
@Slf4j
public class ModelEventListener {

    @Resource
    @Lazy // 避免循环依赖
    private BusinessTypeRelationService businessTypeRelationService;

    /**
     * 处理 Model 创建事件
     *
     * <p>Model创建事件处理 - 当前已禁用BusinessType关联展开功能</p>
     *
     * @param event Model 创建事件
     */
    @EventListener
    @Async // 异步处理，不阻塞主流程
    @Transactional(rollbackFor = Exception.class)
    public void onModelCreated(ModelCreatedEvent event) {
        log.info("[onModelCreated][收到 Model 创建事件: {}]", event);

        Long tenantId = event.getTenantId();
        if (tenantId == null) {
            log.warn("[onModelCreated][租户 ID 为空，跳过处理]");
            return;
        }

        // 在租户上下文中执行
        TenantUtils.execute(tenantId, () -> {
            try {
                // 已禁用：展开已有的 BusinessType 关联到新 Model
                // expandBusinessTypeRelations(event);

                log.info("[onModelCreated][Model 创建事件处理完成: modelId={}, modelCode={}]",
                        event.getModelId(), event.getModelCode());
            } catch (Exception e) {
                log.error("[onModelCreated][处理 Model 创建事件失败: {}]", event, e);
                // 不抛出异常，避免影响主流程
            }
        });
    }

    /**
     * 展开已有的 BusinessType 关联到新 Model
     *
     * <p>已禁用：当新 Model 创建时，不再自动展开 BusinessType 关联</p>
     *
     * 变更原因：Model层展开会导致字段爆炸问题
     * 原需求：FR-BDA-072
     *
     * @param event Model 创建事件
     */
    private void expandBusinessTypeRelations(ModelCreatedEvent event) {
        // 已禁用：不再在Model层展开BusinessType关联
        log.debug("[expandBusinessTypeRelations][跳过展开 BusinessType 关联: modelCode={}]",
                event.getModelCode());
    }
}
