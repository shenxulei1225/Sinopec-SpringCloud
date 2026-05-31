package cn.cheers.x.module.dynamicbusiness.service.group;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.group.GroupDO;
import cn.cheers.x.module.dynamicbusiness.dal.redis.RedisKeyConstants;
import com.alibaba.fastjson2.JSON;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public final class GroupCacheHelper {

    private static final Duration TTL = Duration.ofHours(24);

    private GroupCacheHelper() {
    }

    public static GroupDO getCachedGroup(StringRedisTemplate redis, String groupType, Long id) {
        String json = redis.opsForValue().get(groupKey(groupType, id));
        return json == null ? null : JSON.parseObject(json, GroupDO.class);
    }

    public static void cacheGroup(StringRedisTemplate redis, String groupType, GroupDO group) {
        if (group == null || group.getId() == null) return;
        redis.opsForValue().set(groupKey(groupType, group.getId()), JSON.toJSONString(group), TTL);
    }

    public static <T> List<T> getCachedTree(StringRedisTemplate redis, String groupType, Long tenantId, Class<T> clazz) {
        String json = redis.opsForValue().get(treeKey(groupType, tenantId));
        return json == null ? null : JSON.parseArray(json, clazz);
    }

    public static void cacheTree(StringRedisTemplate redis, String groupType, Long tenantId, List<?> tree) {
        if (tree == null || tree.isEmpty()) return;
        redis.opsForValue().set(treeKey(groupType, tenantId), JSON.toJSONString(tree), TTL);
    }

    public static void evictGroup(StringRedisTemplate redis, String groupType, Long id) {
        redis.delete(groupKey(groupType, id));
    }

    public static void evictTree(StringRedisTemplate redis, String groupType, Long tenantId) {
        redis.delete(treeKey(groupType, tenantId));
    }

    public static void evictAllByType(StringRedisTemplate redis, String groupType) {
        Set<String> keys = redis.keys(RedisKeyConstants.GROUP + ":" + groupType + ":*");
        if (keys != null && !keys.isEmpty()) redis.delete(keys);
        Set<String> treeKeys = redis.keys(RedisKeyConstants.GROUP_TREE + ":" + groupType + ":*");
        if (treeKeys != null && !treeKeys.isEmpty()) redis.delete(treeKeys);
    }

    private static String groupKey(String groupType, Long id) {
        return RedisKeyConstants.GROUP + ":" + groupType + ":" + id;
    }

    private static String treeKey(String groupType, Long tenantId) {
        return RedisKeyConstants.GROUP_TREE + ":" + groupType + ":" + (tenantId == null ? "0" : tenantId);
    }
}
