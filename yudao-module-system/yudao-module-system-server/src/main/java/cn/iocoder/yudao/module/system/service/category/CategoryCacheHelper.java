package cn.iocoder.yudao.module.system.service.category;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryTreeRespVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.List;

@Slf4j
public final class CategoryCacheHelper {

    private static final String CACHE_PREFIX = "system:category:tree:";
    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private CategoryCacheHelper() {
    }

    public static List<CategoryTreeRespVO> getCachedTree(StringRedisTemplate stringRedisTemplate, String cacheKey) {
        if (stringRedisTemplate == null || cacheKey == null || cacheKey.isEmpty()) {
            return null;
        }
        String value = stringRedisTemplate.opsForValue().get(CACHE_PREFIX + cacheKey);
        if (value == null || value.isEmpty()) {
            return null;
        }
        return JsonUtils.parseArray(value, CategoryTreeRespVO.class);
    }

    public static void cacheTree(StringRedisTemplate stringRedisTemplate, String cacheKey, List<CategoryTreeRespVO> tree) {
        if (stringRedisTemplate == null || cacheKey == null || cacheKey.isEmpty() || tree == null) {
            return;
        }
        stringRedisTemplate.opsForValue().set(CACHE_PREFIX + cacheKey, JsonUtils.toJsonString(tree), CACHE_TTL);
    }

    public static void evict(StringRedisTemplate stringRedisTemplate, String cacheKey) {
        if (stringRedisTemplate == null || cacheKey == null || cacheKey.isEmpty()) {
            return;
        }
        stringRedisTemplate.delete(CACHE_PREFIX + cacheKey);
    }
}
