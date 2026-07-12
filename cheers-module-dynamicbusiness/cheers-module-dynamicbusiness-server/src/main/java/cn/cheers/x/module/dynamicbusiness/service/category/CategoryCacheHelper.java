package cn.cheers.x.module.dynamicbusiness.service.category;

import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTreeRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.redis.RedisKeyConstants;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.List;

public final class CategoryCacheHelper {

    private static final Duration TTL = Duration.ofHours(24);

    private CategoryCacheHelper() {}

    public static List<CategoryTreeRespVO> getCachedTree(StringRedisTemplate redis, String cacheKey) {
        String key = key(cacheKey);
        String json = redis.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        return JSON.parseArray(json, CategoryTreeRespVO.class, JSONReader.Feature.UseLongForInts);
    }

    public static void cacheTree(StringRedisTemplate redis, String cacheKey, List<CategoryTreeRespVO> tree) {
        if (CollectionUtils.isEmpty(tree)) {
            return;
        }
        redis.opsForValue().set(key(cacheKey), JSON.toJSONString(tree), TTL);
    }

    public static void evict(StringRedisTemplate redis, String cacheKey) {
        redis.delete(key(cacheKey));
    }

    private static String key(String cacheKey) {
        return RedisKeyConstants.CATEGORY_TREE + ":" + cacheKey;
    }
}

