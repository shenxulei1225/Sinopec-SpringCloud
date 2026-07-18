package cn.cheers.x.inspection.inspection_content.service.source;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象来源适配器注册表。
 *
 * <p>管理所有 ObjectSourceAdapter 实现，根据 sourceType 分发查询请求。</p>
 *
 * <p>使用方式：</p>
 * <ul>
 *     <li>Spring 自动注入所有 ObjectSourceAdapter 实现</li>
 *     <li>每个适配器通过 {@link ObjectSourceAdapter#getSourceCode()} 提供唯一的 sourceType</li>
 *     <li>通过 {@link #getAdapter(String)} 获取对应适配器</li>
 * </ul>
 *
 * <p>适配器示例：</p>
 * <ul>
 *     <li>facility → FacilityObjectSourceAdapter</li>
 *     <li>device → DeviceObjectSourceAdapter</li>
 *     <li>area → AreaObjectSourceAdapter</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ObjectSourceRegistry {

    private final List<ObjectSourceAdapter> adapters;

    /**
     * 适配器映射，key 为 sourceType。
     */
    private final Map<String, ObjectSourceAdapter> adapterMap = new HashMap<>();

    /**
     * 初始化注册表。
     */
    @PostConstruct
    public void init() {
        for (ObjectSourceAdapter adapter : adapters) {
            String sourceType = adapter.getSourceCode();
            if (sourceType == null || sourceType.isEmpty()) {
                log.warn("ObjectSourceAdapter 实现类 {} 未提供 sourceCode，跳过注册",
                        adapter.getClass().getSimpleName());
                continue;
            }
            if (adapterMap.containsKey(sourceType)) {
                log.warn("发现重复的 sourceType [{}]，使用第一个注册的适配器: {}",
                        sourceType, adapterMap.get(sourceType).getClass().getSimpleName());
                continue;
            }
            adapterMap.put(sourceType, adapter);
            log.info("注册对象来源适配器: {} -> {}", sourceType, adapter.getClass().getSimpleName());
        }
        log.info("对象来源适配器注册完成，共注册 {} 个适配器: {}",
                adapterMap.size(), adapterMap.keySet());
    }

    /**
     * 获取指定来源类型的适配器。
     *
     * @param sourceType 来源类型
     * @return 对应的适配器，不存在时返回 null
     */
    public ObjectSourceAdapter getAdapter(String sourceType) {
        return adapterMap.get(sourceType);
    }

    /**
     * 获取所有已注册的来源类型。
     *
     * @return 来源类型列表
     */
    public List<String> getAllSourceTypes() {
        return List.copyOf(adapterMap.keySet());
    }

    /**
     * 批量获取对象详情。
     *
     * @param sourceType 来源类型
     * @param objectCodes 对象编码列表
     * @return 对象详情映射
     */
    public Map<String, ObjectSourceAdapter.ObjectDetail> batchGetDetails(String sourceType, List<String> objectCodes) {
        ObjectSourceAdapter adapter = adapterMap.get(sourceType);
        if (adapter == null) {
            log.warn("未找到来源类型 [{}] 的适配器", sourceType);
            return Map.of();
        }
        return adapter.listObjectDetails(objectCodes);
    }

    /**
     * 检查来源类型是否已注册。
     *
     * @param sourceType 来源类型
     * @return 是否已注册
     */
    public boolean isRegistered(String sourceType) {
        return adapterMap.containsKey(sourceType);
    }
}
