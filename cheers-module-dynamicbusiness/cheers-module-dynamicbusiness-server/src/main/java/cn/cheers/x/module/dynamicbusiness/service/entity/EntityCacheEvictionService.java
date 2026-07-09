package cn.cheers.x.module.dynamicbusiness.service.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 实体相关 Redis 缓存失效（封装 {@link EntityCacheHelper}，供各 Service 注入使用）。
 */
@Component
@RequiredArgsConstructor
public class EntityCacheEvictionService {

    private final StringRedisTemplate stringRedisTemplate;

    public void evictEntityCaches(Long modelId, String entityTypeCode) {
        EntityCacheHelper.evictEntityTree(stringRedisTemplate, modelId);
        EntityCacheHelper.evictEntityList(stringRedisTemplate, entityTypeCode, modelId);
    }

    public void evictEntity(Long entityId) {
        EntityCacheHelper.evictEntity(stringRedisTemplate, entityId);
    }
}
