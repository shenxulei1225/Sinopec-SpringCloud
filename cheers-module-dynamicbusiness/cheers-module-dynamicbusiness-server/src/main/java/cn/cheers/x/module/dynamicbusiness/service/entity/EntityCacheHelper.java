package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.redis.RedisKeyConstants;
import com.alibaba.fastjson2.JSON;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public final class EntityCacheHelper {

    private static final Duration TTL = Duration.ofHours(24);

    private EntityCacheHelper() {}

    public static EntityDO getCachedEntity(StringRedisTemplate redis, Long id) {
        String key = entityKey(id);
        String json = redis.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        return JSON.parseObject(json, EntityDO.class);
    }

    public static void cacheEntity(StringRedisTemplate redis, EntityDO entity) {
        if (entity == null || entity.getId() == null) {
            return;
        }
        redis.opsForValue().set(entityKey(entity.getId()), JSON.toJSONString(entity), TTL);
    }

    public static void evictEntity(StringRedisTemplate redis, Long id) {
        redis.delete(entityKey(id));
    }

    public static List<EntityRespVO> getCachedEntityTree(StringRedisTemplate redis, Long modelId) {
        String key = entityTreeKey(modelId);
        String json = redis.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        return JSON.parseArray(json, EntityRespVO.class);
    }

    public static void cacheEntityTree(StringRedisTemplate redis, Long modelId, List<EntityRespVO> tree) {
        if (tree == null) {
            return;
        }
        redis.opsForValue().set(entityTreeKey(modelId), JSON.toJSONString(tree), TTL);
    }

    public static void evictEntityTree(StringRedisTemplate redis, Long modelId) {
        redis.delete(entityTreeKey(modelId));
    }

    public static void evictAllEntityTrees(StringRedisTemplate redis) {
        Set<String> keys = redis.keys(RedisKeyConstants.ENTITY_TREE + ":*");
        if (keys != null && !keys.isEmpty()) {
            redis.delete(keys);
        }
    }

    public static List<EntityDO> getCachedEntityList(StringRedisTemplate redis, String businessTypeCode, Long modelId) {
        String key = entityListKey(businessTypeCode, modelId);
        String json = redis.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        return JSON.parseArray(json, EntityDO.class);
    }

    public static void cacheEntityList(StringRedisTemplate redis, String businessTypeCode, Long modelId, List<EntityDO> entities) {
        if (entities == null) {
            return;
        }
        redis.opsForValue().set(entityListKey(businessTypeCode, modelId), JSON.toJSONString(entities), TTL);
    }

    public static void evictEntityList(StringRedisTemplate redis, String businessTypeCode, Long modelId) {
        redis.delete(entityListKey(businessTypeCode, modelId));
    }

    public static void evictAllEntityLists(StringRedisTemplate redis) {
        Set<String> keys = redis.keys(RedisKeyConstants.ENTITY_LIST + ":*");
        if (keys != null && !keys.isEmpty()) {
            redis.delete(keys);
        }
    }

    public static void evictAllEntityCaches(StringRedisTemplate redis) {
        evictAllEntityTrees(redis);
        evictAllEntityLists(redis);
        Set<String> entityKeys = redis.keys(RedisKeyConstants.ENTITY + ":*");
        if (entityKeys != null && !entityKeys.isEmpty()) {
            redis.delete(entityKeys);
        }
    }

    private static String entityKey(Long id) {
        return RedisKeyConstants.ENTITY + ":" + id;
    }

    private static String entityTreeKey(Long modelId) {
        return RedisKeyConstants.ENTITY_TREE + ":" + (modelId == null ? "all" : modelId);
    }

    private static String entityListKey(String businessTypeCode, Long modelId) {
        String btc = businessTypeCode == null ? "all" : businessTypeCode;
        String mid = modelId == null ? "all" : String.valueOf(modelId);
        return RedisKeyConstants.ENTITY_LIST + ":" + btc + ":" + mid;
    }
}
