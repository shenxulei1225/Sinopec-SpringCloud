package cn.cheers.x.module.dynamicbusiness.service.field;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.redis.RedisKeyConstants;
import com.alibaba.fastjson2.JSON;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.List;

/**
 * 字段缓存帮助类
 */
public final class FieldCacheHelper {

    /**
     * 缓存过期时间：24小时
     */
    private static final Duration TTL = Duration.ofHours(24);

    private FieldCacheHelper() {}

    // =====================================================
    // 单个字段缓存
    // =====================================================

    /**
     * 获取缓存的字段
     */
    public static FieldDO getCachedField(StringRedisTemplate redis, Long id) {
        String key = fieldKey(id);
        String json = redis.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        return JSON.parseObject(json, FieldDO.class);
    }

    /**
     * 缓存字段
     */
    public static void cacheField(StringRedisTemplate redis, FieldDO field) {
        if (field == null || field.getId() == null) {
            return;
        }
        redis.opsForValue().set(fieldKey(field.getId()), JSON.toJSONString(field), TTL);
    }

    /**
     * 清除字段缓存
     */
    public static void evictField(StringRedisTemplate redis, Long id) {
        redis.delete(fieldKey(id));
    }

    // =====================================================
    // 字段列表缓存（按类型）
    // =====================================================

    /**
     * 获取缓存的字段列表
     */
    public static List<FieldDO> getCachedFieldList(StringRedisTemplate redis, String type) {
        String key = fieldListKey(type);
        String json = redis.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        return JSON.parseArray(json, FieldDO.class);
    }

    /**
     * 缓存字段列表
     */
    public static void cacheFieldList(StringRedisTemplate redis, String type, List<FieldDO> fields) {
        if (fields == null || fields.isEmpty()) {
            return;
        }
        redis.opsForValue().set(fieldListKey(type), JSON.toJSONString(fields), TTL);
    }

    /**
     * 清除字段列表缓存
     */
    public static void evictFieldList(StringRedisTemplate redis, String type) {
        redis.delete(fieldListKey(type));
    }

    /**
     * 清除所有字段列表缓存
     */
    public static void evictAllFieldLists(StringRedisTemplate redis) {
        // 使用模式匹配删除所有字段列表缓存
        redis.delete(redis.keys(RedisKeyConstants.FIELD_LIST + ":*"));
    }

    // =====================================================
    // Key 生成方法
    // =====================================================

    private static String fieldKey(Long id) {
        return RedisKeyConstants.FIELD + ":" + id;
    }

    private static String fieldListKey(String type) {
        return RedisKeyConstants.FIELD_LIST + ":" + (type == null ? "all" : type);
    }
}
