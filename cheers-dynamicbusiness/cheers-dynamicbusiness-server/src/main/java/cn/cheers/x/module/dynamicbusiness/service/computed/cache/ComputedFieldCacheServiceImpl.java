package cn.cheers.x.module.dynamicbusiness.service.computed.cache;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.computed.ComputedFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.computed.ComputedFieldMapper;
import cn.cheers.x.module.dynamicbusiness.service.computed.executor.ComputedFieldExecutor;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 计算字段缓存服务实现
 * 
 * 提供完整的缓存管理功能，包括：
 * - 基于 Redis 的分布式缓存读写
 * - 基于 Caffeine 的本地二级缓存（L2 Cache）
 * - 缓存过期和刷新机制
 * - 缓存统计和监控
 * - 批量缓存操作
 * - 缓存预热功能
 * - 异步缓存刷新
 * 
 * 缓存策略说明：
 * - REALTIME: 不使用缓存，每次实时计算
 * - CACHED: 使用 Redis 缓存，支持 TTL 过期
 * - PRECOMPUTED: 预计算存储，使用更长的 TTL，数据变化时触发重算
 * 
 * @author yudao
 */
@Service
@Slf4j
public class ComputedFieldCacheServiceImpl implements ComputedFieldCacheService {

    /** 缓存 Key 前缀 */
    private static final String CACHE_KEY_PREFIX = "computed_field:";
    
    /** 预计算缓存 Key 前缀 */
    private static final String PRECOMPUTED_KEY_PREFIX = "computed_field_precomputed:";
    
    /** 缓存统计 Key 前缀 */
    private static final String STATS_KEY_PREFIX = "computed_field_stats:";
    
    /** 默认缓存过期时间（分钟） */
    private static final int DEFAULT_TTL_MINUTES = 5;
    
    /** 预计算默认过期时间（分钟） */
    private static final int DEFAULT_PRECOMPUTED_TTL_MINUTES = 60;
    
    /** 本地缓存最大容量 */
    private static final int LOCAL_CACHE_MAX_SIZE = 10000;
    
    /** 本地缓存过期时间（秒） */
    private static final int LOCAL_CACHE_EXPIRE_SECONDS = 60;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ComputedFieldMapper computedFieldMapper;

    @Resource
    private ComputedFieldExecutor computedFieldExecutor;

    /** 缓存命中计数器 */
    private final AtomicLong hitCount = new AtomicLong(0);
    
    /** 缓存未命中计数器 */
    private final AtomicLong missCount = new AtomicLong(0);
    
    /** 本地二级缓存（L2 Cache）- 减少 Redis 访问 */
    private Cache<String, Object> localCache;
    
    /** 预计算任务队列 - 记录待预计算的实体 */
    private final Set<String> precomputeQueue = Collections.synchronizedSet(new LinkedHashSet<>());
    
    @PostConstruct
    public void init() {
        // 初始化本地缓存
        localCache = Caffeine.newBuilder()
                .maximumSize(LOCAL_CACHE_MAX_SIZE)
                .expireAfterWrite(LOCAL_CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS)
                .recordStats()
                .build();
        log.info("[init][计算字段缓存服务初始化完成，本地缓存容量={}，过期时间={}秒]", 
                LOCAL_CACHE_MAX_SIZE, LOCAL_CACHE_EXPIRE_SECONDS);
    }

    // ========== 缓存读写操作 ==========

    @Override
    public Object getCachedValue(Long modelId, String fieldCode, Long entityId) {
        String cacheKey = buildCacheKey(modelId, fieldCode, entityId);
        
        // 1. 先从本地缓存获取（L2 Cache）
        Object localValue = localCache.getIfPresent(cacheKey);
        if (localValue != null) {
            hitCount.incrementAndGet();
            log.debug("[getCachedValue][本地缓存命中，key={}]", cacheKey);
            return localValue;
        }
        
        // 2. 从 Redis 获取
        String cachedValue = stringRedisTemplate.opsForValue().get(cacheKey);
        
        if (cachedValue != null) {
            hitCount.incrementAndGet();
            Object parsedValue = parseValue(cachedValue);
            // 写入本地缓存
            localCache.put(cacheKey, parsedValue);
            log.debug("[getCachedValue][Redis 缓存命中，key={}]", cacheKey);
            return parsedValue;
        } else {
            missCount.incrementAndGet();
            log.debug("[getCachedValue][缓存未命中，key={}]", cacheKey);
            return null;
        }
    }

    @Override
    public void setCachedValue(Long modelId, String fieldCode, Long entityId, Object value, int ttlMinutes) {
        if (value == null) {
            return;
        }
        
        String cacheKey = buildCacheKey(modelId, fieldCode, entityId);
        String stringValue = serializeValue(value);
        int ttl = ttlMinutes > 0 ? ttlMinutes : DEFAULT_TTL_MINUTES;
        
        // 1. 写入 Redis
        stringRedisTemplate.opsForValue().set(cacheKey, stringValue, ttl, TimeUnit.MINUTES);
        
        // 2. 写入本地缓存
        localCache.put(cacheKey, value);
        
        log.debug("[setCachedValue][缓存设置成功，key={}, ttl={}分钟]", cacheKey, ttl);
    }
    
    /**
     * 获取预计算缓存值
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param entityId 实体 ID
     * @return 预计算的值
     */
    public Object getPrecomputedValue(Long modelId, String fieldCode, Long entityId) {
        String cacheKey = buildPrecomputedCacheKey(modelId, fieldCode, entityId);
        
        // 1. 先从本地缓存获取
        Object localValue = localCache.getIfPresent(cacheKey);
        if (localValue != null) {
            hitCount.incrementAndGet();
            log.debug("[getPrecomputedValue][本地缓存命中，key={}]", cacheKey);
            return localValue;
        }
        
        // 2. 从 Redis 获取
        String cachedValue = stringRedisTemplate.opsForValue().get(cacheKey);
        
        if (cachedValue != null) {
            hitCount.incrementAndGet();
            Object parsedValue = parseValue(cachedValue);
            localCache.put(cacheKey, parsedValue);
            log.debug("[getPrecomputedValue][Redis 缓存命中，key={}]", cacheKey);
            return parsedValue;
        } else {
            missCount.incrementAndGet();
            log.debug("[getPrecomputedValue][预计算缓存未命中，key={}]", cacheKey);
            return null;
        }
    }
    
    /**
     * 设置预计算缓存值
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param entityId 实体 ID
     * @param value 预计算值
     * @param ttlMinutes 过期时间（分钟）
     */
    public void setPrecomputedValue(Long modelId, String fieldCode, Long entityId, Object value, int ttlMinutes) {
        if (value == null) {
            return;
        }
        
        String cacheKey = buildPrecomputedCacheKey(modelId, fieldCode, entityId);
        String stringValue = serializeValue(value);
        int ttl = ttlMinutes > 0 ? ttlMinutes : DEFAULT_PRECOMPUTED_TTL_MINUTES;
        
        // 1. 写入 Redis（使用较长的 TTL）
        stringRedisTemplate.opsForValue().set(cacheKey, stringValue, ttl, TimeUnit.MINUTES);
        
        // 2. 写入本地缓存
        localCache.put(cacheKey, value);
        
        log.debug("[setPrecomputedValue][预计算缓存设置成功，key={}, ttl={}分钟]", cacheKey, ttl);
    }
    
    /**
     * 触发异步预计算
     * 
     * @param modelId Model ID
     * @param entityId 实体 ID
     */
    @Async
    public void triggerAsyncPrecompute(Long modelId, Long entityId) {
        String queueKey = modelId + ":" + entityId;
        
        // 避免重复计算
        if (precomputeQueue.contains(queueKey)) {
            log.debug("[triggerAsyncPrecompute][预计算任务已在队列中，跳过，modelId={}, entityId={}]", 
                    modelId, entityId);
            return;
        }
        
        precomputeQueue.add(queueKey);
        
        try {
            // 获取该 Model 的所有预计算字段
            List<ComputedFieldDO> precomputedFields = computedFieldMapper.selectByModelId(modelId)
                    .stream()
                    .filter(ComputedFieldDO::isPrecomputed)
                    .collect(Collectors.toList());
            
            if (CollectionUtils.isEmpty(precomputedFields)) {
                return;
            }
            
            // 执行预计算
            for (ComputedFieldDO field : precomputedFields) {
                try {
                    Object value = computedFieldExecutor.execute(entityId, field, Collections.emptyMap());
                    if (value != null) {
                        int ttl = field.getCacheTtlMinutes() != null ? 
                                field.getCacheTtlMinutes() * 10 : DEFAULT_PRECOMPUTED_TTL_MINUTES;
                        setPrecomputedValue(modelId, field.getFieldCode(), entityId, value, ttl);
                    }
                } catch (Exception e) {
                    log.error("[triggerAsyncPrecompute][预计算失败，fieldCode={}, entityId={}]", 
                            field.getFieldCode(), entityId, e);
                }
            }
            
            log.info("[triggerAsyncPrecompute][异步预计算完成，modelId={}, entityId={}, 字段数={}]", 
                    modelId, entityId, precomputedFields.size());
        } finally {
            precomputeQueue.remove(queueKey);
        }
    }
    
    /**
     * 批量触发异步预计算
     * 
     * @param modelId Model ID
     * @param entityIds 实体 ID 列表
     * @return 触发的任务数
     */
    public CompletableFuture<Integer> batchTriggerAsyncPrecompute(Long modelId, List<Long> entityIds) {
        if (CollectionUtils.isEmpty(entityIds)) {
            return CompletableFuture.completedFuture(0);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            int count = 0;
            for (Long entityId : entityIds) {
                try {
                    triggerAsyncPrecompute(modelId, entityId);
                    count++;
                } catch (Exception e) {
                    log.error("[batchTriggerAsyncPrecompute][触发预计算失败，entityId={}]", entityId, e);
                }
            }
            return count;
        });
    }
    
    /**
     * 使缓存失效（数据变化时调用）
     * 
     * @param modelId Model ID
     * @param entityId 实体 ID
     * @param triggerPrecompute 是否触发预计算
     */
    public void invalidateCache(Long modelId, Long entityId, boolean triggerPrecompute) {
        // 1. 清除该实体的所有缓存
        List<ComputedFieldDO> fields = computedFieldMapper.selectByModelId(modelId);
        for (ComputedFieldDO field : fields) {
            String cacheKey = buildCacheKey(modelId, field.getFieldCode(), entityId);
            String precomputedKey = buildPrecomputedCacheKey(modelId, field.getFieldCode(), entityId);
            
            // 清除 Redis 缓存
            stringRedisTemplate.delete(cacheKey);
            stringRedisTemplate.delete(precomputedKey);
            
            // 清除本地缓存
            localCache.invalidate(cacheKey);
            localCache.invalidate(precomputedKey);
        }
        
        log.info("[invalidateCache][缓存失效，modelId={}, entityId={}, 字段数={}]", 
                modelId, entityId, fields.size());
        
        // 2. 如果需要，触发预计算
        if (triggerPrecompute) {
            triggerAsyncPrecompute(modelId, entityId);
        }
    }

    @Override
    public Map<Long, Object> batchGetCachedValues(Long modelId, String fieldCode, List<Long> entityIds) {
        if (CollectionUtils.isEmpty(entityIds)) {
            return Collections.emptyMap();
        }
        
        List<String> keys = entityIds.stream()
                .map(entityId -> buildCacheKey(modelId, fieldCode, entityId))
                .collect(Collectors.toList());
        
        List<String> values = stringRedisTemplate.opsForValue().multiGet(keys);
        
        Map<Long, Object> result = new HashMap<>();
        for (int i = 0; i < entityIds.size(); i++) {
            String value = values != null && i < values.size() ? values.get(i) : null;
            if (value != null) {
                result.put(entityIds.get(i), parseValue(value));
                hitCount.incrementAndGet();
            } else {
                missCount.incrementAndGet();
            }
        }
        
        log.debug("[batchGetCachedValues][批量获取缓存，请求数={}，命中数={}]", 
                entityIds.size(), result.size());
        return result;
    }

    @Override
    public void batchSetCachedValues(Long modelId, String fieldCode, Map<Long, Object> values, int ttlMinutes) {
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        
        int ttl = ttlMinutes > 0 ? ttlMinutes : DEFAULT_TTL_MINUTES;
        
        // 使用 pipeline 批量设置
        Map<String, String> keyValues = new HashMap<>();
        for (Map.Entry<Long, Object> entry : values.entrySet()) {
            if (entry.getValue() != null) {
                String key = buildCacheKey(modelId, fieldCode, entry.getKey());
                keyValues.put(key, serializeValue(entry.getValue()));
            }
        }
        
        if (!keyValues.isEmpty()) {
            stringRedisTemplate.opsForValue().multiSet(keyValues);
            // 设置过期时间（需要逐个设置）
            for (String key : keyValues.keySet()) {
                stringRedisTemplate.expire(key, ttl, TimeUnit.MINUTES);
            }
            log.debug("[batchSetCachedValues][批量设置缓存成功，数量={}，ttl={}分钟]", 
                    keyValues.size(), ttl);
        }
    }

    // ========== 缓存过期和刷新 ==========

    @Override
    public int refreshFieldCache(Long modelId, String fieldCode) {
        String pattern = buildCacheKeyPattern(modelId, fieldCode, "*");
        Set<String> keys = stringRedisTemplate.keys(pattern);
        
        int count = 0;
        if (!CollectionUtils.isEmpty(keys)) {
            Long deleted = stringRedisTemplate.delete(keys);
            count = deleted != null ? deleted.intValue() : 0;
            
            // 同时清除本地缓存
            for (String key : keys) {
                localCache.invalidate(key);
            }
            
            log.info("[refreshFieldCache][刷新字段缓存成功，modelId={}, fieldCode={}, 清除数量={}]", 
                    modelId, fieldCode, count);
        }
        
        // 同时清除预计算缓存
        String precomputedPattern = buildPrecomputedCacheKeyPattern(modelId, fieldCode, "*");
        Set<String> precomputedKeys = stringRedisTemplate.keys(precomputedPattern);
        if (!CollectionUtils.isEmpty(precomputedKeys)) {
            Long deleted = stringRedisTemplate.delete(precomputedKeys);
            int precomputedCount = deleted != null ? deleted.intValue() : 0;
            count += precomputedCount;
            
            for (String key : precomputedKeys) {
                localCache.invalidate(key);
            }
        }
        
        return count;
    }

    @Override
    public int refreshModelCache(Long modelId) {
        String pattern = buildCacheKeyPattern(modelId, "*", "*");
        Set<String> keys = stringRedisTemplate.keys(pattern);
        
        int count = 0;
        if (!CollectionUtils.isEmpty(keys)) {
            Long deleted = stringRedisTemplate.delete(keys);
            count = deleted != null ? deleted.intValue() : 0;
            
            // 同时清除本地缓存
            for (String key : keys) {
                localCache.invalidate(key);
            }
            
            log.info("[refreshModelCache][刷新 Model 缓存成功，modelId={}, 清除数量={}]", 
                    modelId, count);
        }
        
        // 同时清除预计算缓存
        String precomputedPattern = buildPrecomputedCacheKeyPattern(modelId, "*", "*");
        Set<String> precomputedKeys = stringRedisTemplate.keys(precomputedPattern);
        if (!CollectionUtils.isEmpty(precomputedKeys)) {
            Long deleted = stringRedisTemplate.delete(precomputedKeys);
            int precomputedCount = deleted != null ? deleted.intValue() : 0;
            count += precomputedCount;
            
            for (String key : precomputedKeys) {
                localCache.invalidate(key);
            }
        }
        
        return count;
    }

    @Override
    public int refreshEntityCache(Long entityId) {
        // 需要遍历所有可能的 modelId，这里使用通配符
        String pattern = CACHE_KEY_PREFIX + "*:*:" + entityId;
        Set<String> keys = stringRedisTemplate.keys(pattern);
        
        int count = 0;
        if (!CollectionUtils.isEmpty(keys)) {
            Long deleted = stringRedisTemplate.delete(keys);
            count = deleted != null ? deleted.intValue() : 0;
            
            // 同时清除本地缓存
            for (String key : keys) {
                localCache.invalidate(key);
            }
            
            log.info("[refreshEntityCache][刷新实体缓存成功，entityId={}, 清除数量={}]", 
                    entityId, count);
        }
        
        // 同时清除预计算缓存
        String precomputedPattern = PRECOMPUTED_KEY_PREFIX + "*:*:" + entityId;
        Set<String> precomputedKeys = stringRedisTemplate.keys(precomputedPattern);
        if (!CollectionUtils.isEmpty(precomputedKeys)) {
            Long deleted = stringRedisTemplate.delete(precomputedKeys);
            int precomputedCount = deleted != null ? deleted.intValue() : 0;
            count += precomputedCount;
            
            for (String key : precomputedKeys) {
                localCache.invalidate(key);
            }
        }
        
        return count;
    }

    @Override
    public boolean refreshSingleCache(Long modelId, String fieldCode, Long entityId) {
        String cacheKey = buildCacheKey(modelId, fieldCode, entityId);
        Boolean deleted = stringRedisTemplate.delete(cacheKey);
        boolean success = Boolean.TRUE.equals(deleted);
        
        // 同时清除本地缓存
        localCache.invalidate(cacheKey);
        
        // 同时清除预计算缓存
        String precomputedKey = buildPrecomputedCacheKey(modelId, fieldCode, entityId);
        stringRedisTemplate.delete(precomputedKey);
        localCache.invalidate(precomputedKey);
        
        if (success) {
            log.debug("[refreshSingleCache][刷新单个缓存成功，key={}]", cacheKey);
        }
        return success;
    }

    @Override
    public int batchRefreshEntityCache(Long modelId, List<Long> entityIds) {
        if (CollectionUtils.isEmpty(entityIds)) {
            return 0;
        }
        
        // 获取该 Model 的所有计算字段
        List<ComputedFieldDO> fields = computedFieldMapper.selectByModelId(modelId);
        if (CollectionUtils.isEmpty(fields)) {
            return 0;
        }
        
        // 构建所有需要删除的 key
        Set<String> keysToDelete = new HashSet<>();
        for (ComputedFieldDO field : fields) {
            for (Long entityId : entityIds) {
                keysToDelete.add(buildCacheKey(modelId, field.getFieldCode(), entityId));
            }
        }
        
        if (!keysToDelete.isEmpty()) {
            Long deleted = stringRedisTemplate.delete(keysToDelete);
            int count = deleted != null ? deleted.intValue() : 0;
            log.info("[batchRefreshEntityCache][批量刷新实体缓存成功，modelId={}, 实体数={}，清除数量={}]", 
                    modelId, entityIds.size(), count);
            return count;
        }
        return 0;
    }

    // ========== 缓存预热 ==========

    @Override
    public int warmupFieldCache(ComputedFieldDO field, List<Long> entityIds) {
        if (field == null || CollectionUtils.isEmpty(entityIds)) {
            return 0;
        }
        
        // 只对 CACHED 策略的字段进行预热
        if (!field.isCached()) {
            log.debug("[warmupFieldCache][字段不是 CACHED 策略，跳过预热，fieldCode={}]", 
                    field.getFieldCode());
            return 0;
        }
        
        int successCount = 0;
        int ttl = field.getCacheTtlMinutes() != null ? field.getCacheTtlMinutes() : DEFAULT_TTL_MINUTES;
        
        for (Long entityId : entityIds) {
            try {
                // 检查是否已有缓存
                if (isCached(field.getModelId(), field.getFieldCode(), entityId)) {
                    continue;
                }
                
                // 执行计算
                Object value = computedFieldExecutor.execute(entityId, field, Collections.emptyMap());
                
                // 存入缓存
                if (value != null) {
                    setCachedValue(field.getModelId(), field.getFieldCode(), entityId, value, ttl);
                    successCount++;
                }
            } catch (Exception e) {
                log.warn("[warmupFieldCache][预热失败，fieldCode={}, entityId={}]", 
                        field.getFieldCode(), entityId, e);
            }
        }
        
        log.info("[warmupFieldCache][字段缓存预热完成，fieldCode={}, 请求数={}，成功数={}]", 
                field.getFieldCode(), entityIds.size(), successCount);
        return successCount;
    }

    @Override
    public int warmupModelCache(Long modelId, List<Long> entityIds) {
        if (modelId == null || CollectionUtils.isEmpty(entityIds)) {
            return 0;
        }
        
        // 获取该 Model 的所有 CACHED 策略的计算字段
        List<ComputedFieldDO> fields = computedFieldMapper.selectByModelId(modelId);
        List<ComputedFieldDO> cachedFields = fields.stream()
                .filter(ComputedFieldDO::isCached)
                .collect(Collectors.toList());
        
        if (CollectionUtils.isEmpty(cachedFields)) {
            log.debug("[warmupModelCache][Model 没有 CACHED 策略的字段，跳过预热，modelId={}]", modelId);
            return 0;
        }
        
        int totalSuccess = 0;
        for (ComputedFieldDO field : cachedFields) {
            totalSuccess += warmupFieldCache(field, entityIds);
        }
        
        log.info("[warmupModelCache][Model 缓存预热完成，modelId={}, 字段数={}，总成功数={}]", 
                modelId, cachedFields.size(), totalSuccess);
        return totalSuccess;
    }

    // ========== 缓存统计和监控 ==========

    @Override
    public CacheStatistics getCacheStatistics(Long modelId) {
        String pattern;
        if (modelId != null) {
            pattern = buildCacheKeyPattern(modelId, "*", "*");
        } else {
            pattern = CACHE_KEY_PREFIX + "*";
        }
        
        Set<String> keys = stringRedisTemplate.keys(pattern);
        long totalKeys = keys != null ? keys.size() : 0;
        
        // 加上预计算缓存的 key 数量
        String precomputedPattern;
        if (modelId != null) {
            precomputedPattern = buildPrecomputedCacheKeyPattern(modelId, "*", "*");
        } else {
            precomputedPattern = PRECOMPUTED_KEY_PREFIX + "*";
        }
        Set<String> precomputedKeys = stringRedisTemplate.keys(precomputedPattern);
        totalKeys += precomputedKeys != null ? precomputedKeys.size() : 0;
        
        long hits = hitCount.get();
        long misses = missCount.get();
        double hitRate = (hits + misses) > 0 ? (double) hits / (hits + misses) : 0.0;
        
        // 获取本地缓存统计
        CacheStats localStats = localCache.stats();
        hits += localStats.hitCount();
        
        // 估算内存使用（简化计算）
        long memoryUsage = totalKeys * 100; // 假设每个 key 平均 100 字节
        memoryUsage += localCache.estimatedSize() * 50; // 本地缓存估算
        
        return new CacheStatistics(totalKeys, hits, misses, hitRate, memoryUsage);
    }
    
    /**
     * 获取本地缓存统计信息
     * 
     * @return 本地缓存统计
     */
    public LocalCacheStatistics getLocalCacheStatistics() {
        CacheStats stats = localCache.stats();
        return new LocalCacheStatistics(
                localCache.estimatedSize(),
                stats.hitCount(),
                stats.missCount(),
                stats.hitRate(),
                stats.evictionCount()
        );
    }
    
    /**
     * 本地缓存统计信息
     */
    public record LocalCacheStatistics(
            long estimatedSize,
            long hitCount,
            long missCount,
            double hitRate,
            long evictionCount
    ) {}

    @Override
    public long getCacheKeyCount(Long modelId, String fieldCode) {
        String pattern = buildCacheKeyPattern(modelId, fieldCode, "*");
        Set<String> keys = stringRedisTemplate.keys(pattern);
        return keys != null ? keys.size() : 0;
    }

    @Override
    public Set<String> getCacheKeys(Long modelId, String fieldCode) {
        String pattern;
        if (StringUtils.hasText(fieldCode)) {
            pattern = buildCacheKeyPattern(modelId, fieldCode, "*");
        } else {
            pattern = buildCacheKeyPattern(modelId, "*", "*");
        }
        
        Set<String> keys = stringRedisTemplate.keys(pattern);
        return keys != null ? keys : Collections.emptySet();
    }

    @Override
    public boolean isCached(Long modelId, String fieldCode, Long entityId) {
        String cacheKey = buildCacheKey(modelId, fieldCode, entityId);
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(cacheKey));
    }

    @Override
    public long getCacheTtl(Long modelId, String fieldCode, Long entityId) {
        String cacheKey = buildCacheKey(modelId, fieldCode, entityId);
        Long ttl = stringRedisTemplate.getExpire(cacheKey, TimeUnit.SECONDS);
        return ttl != null ? ttl : -2;
    }

    // ========== 缓存清理 ==========

    @Override
    public int clearAllCache() {
        String pattern = CACHE_KEY_PREFIX + "*";
        Set<String> keys = stringRedisTemplate.keys(pattern);
        
        int count = 0;
        if (!CollectionUtils.isEmpty(keys)) {
            Long deleted = stringRedisTemplate.delete(keys);
            count = deleted != null ? deleted.intValue() : 0;
        }
        
        // 清除预计算缓存
        String precomputedPattern = PRECOMPUTED_KEY_PREFIX + "*";
        Set<String> precomputedKeys = stringRedisTemplate.keys(precomputedPattern);
        if (!CollectionUtils.isEmpty(precomputedKeys)) {
            Long deleted = stringRedisTemplate.delete(precomputedKeys);
            count += deleted != null ? deleted.intValue() : 0;
        }
        
        // 清除本地缓存
        localCache.invalidateAll();
        
        // 重置统计计数器
        hitCount.set(0);
        missCount.set(0);
        
        log.info("[clearAllCache][清除所有缓存成功，清除数量={}]", count);
        return count;
    }

    @Override
    public int clearExpiredCache() {
        // Redis 会自动清理过期的 key，此方法主要用于触发清理或记录日志
        // 清理本地缓存中可能过期的条目
        localCache.cleanUp();
        log.info("[clearExpiredCache][触发过期缓存清理（Redis 自动处理，本地缓存已清理）]");
        return 0;
    }
    
    /**
     * 清除本地缓存
     */
    public void clearLocalCache() {
        localCache.invalidateAll();
        log.info("[clearLocalCache][清除本地缓存成功]");
    }

    // ========== 私有方法 ==========

    /**
     * 构建缓存 Key
     * 格式：computed_field:{modelId}:{fieldCode}:{entityId}
     */
    private String buildCacheKey(Long modelId, String fieldCode, Long entityId) {
        return CACHE_KEY_PREFIX + modelId + ":" + fieldCode + ":" + entityId;
    }
    
    /**
     * 构建预计算缓存 Key
     * 格式：computed_field_precomputed:{modelId}:{fieldCode}:{entityId}
     */
    private String buildPrecomputedCacheKey(Long modelId, String fieldCode, Long entityId) {
        return PRECOMPUTED_KEY_PREFIX + modelId + ":" + fieldCode + ":" + entityId;
    }

    /**
     * 构建缓存 Key 模式（用于批量操作）
     */
    private String buildCacheKeyPattern(Long modelId, String fieldCode, String entityId) {
        return CACHE_KEY_PREFIX + modelId + ":" + fieldCode + ":" + entityId;
    }
    
    /**
     * 构建预计算缓存 Key 模式（用于批量操作）
     */
    private String buildPrecomputedCacheKeyPattern(Long modelId, String fieldCode, String entityId) {
        return PRECOMPUTED_KEY_PREFIX + modelId + ":" + fieldCode + ":" + entityId;
    }

    /**
     * 序列化值为字符串
     */
    private String serializeValue(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    /**
     * 解析缓存值
     */
    private Object parseValue(String value) {
        if (value == null) {
            return null;
        }
        
        try {
            // 尝试解析为数字
            if (value.contains(".")) {
                return new BigDecimal(value).doubleValue();
            } else {
                return Long.parseLong(value);
            }
        } catch (NumberFormatException e) {
            // 如果不是数字，返回原字符串
            return value;
        }
    }
}
