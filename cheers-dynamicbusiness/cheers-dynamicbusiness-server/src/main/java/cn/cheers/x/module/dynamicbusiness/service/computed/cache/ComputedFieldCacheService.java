package cn.cheers.x.module.dynamicbusiness.service.computed.cache;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.computed.ComputedFieldDO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 计算字段缓存服务接口
 * 
 * 提供计算字段的缓存管理功能，包括：
 * - 缓存读写操作
 * - 缓存过期和刷新
 * - 缓存统计和监控
 * - 批量缓存操作
 * - 缓存预热
 * 
 * @author yudao
 */
public interface ComputedFieldCacheService {

    // ========== 缓存读写操作 ==========

    /**
     * 从缓存获取计算字段值
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param entityId 实体 ID
     * @return 缓存的值，如果不存在返回 null
     */
    Object getCachedValue(Long modelId, String fieldCode, Long entityId);

    /**
     * 将计算字段值存入缓存
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param entityId 实体 ID
     * @param value 计算值
     * @param ttlMinutes 缓存过期时间（分钟）
     */
    void setCachedValue(Long modelId, String fieldCode, Long entityId, Object value, int ttlMinutes);

    /**
     * 批量获取缓存值
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param entityIds 实体 ID 列表
     * @return 实体 ID 到缓存值的映射
     */
    Map<Long, Object> batchGetCachedValues(Long modelId, String fieldCode, List<Long> entityIds);

    /**
     * 批量设置缓存值
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param values 实体 ID 到值的映射
     * @param ttlMinutes 缓存过期时间（分钟）
     */
    void batchSetCachedValues(Long modelId, String fieldCode, Map<Long, Object> values, int ttlMinutes);

    // ========== 缓存过期和刷新 ==========

    /**
     * 刷新指定字段的所有缓存
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @return 清除的缓存数量
     */
    int refreshFieldCache(Long modelId, String fieldCode);

    /**
     * 刷新指定 Model 的所有计算字段缓存
     * 
     * @param modelId Model ID
     * @return 清除的缓存数量
     */
    int refreshModelCache(Long modelId);

    /**
     * 刷新指定实体的所有计算字段缓存
     * 
     * @param entityId 实体 ID
     * @return 清除的缓存数量
     */
    int refreshEntityCache(Long entityId);

    /**
     * 刷新指定实体的指定字段缓存
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param entityId 实体 ID
     * @return 是否成功删除
     */
    boolean refreshSingleCache(Long modelId, String fieldCode, Long entityId);

    /**
     * 批量刷新实体缓存
     * 
     * @param modelId Model ID
     * @param entityIds 实体 ID 列表
     * @return 清除的缓存数量
     */
    int batchRefreshEntityCache(Long modelId, List<Long> entityIds);

    // ========== 缓存预热 ==========

    /**
     * 预热指定字段的缓存
     * 
     * @param field 计算字段配置
     * @param entityIds 需要预热的实体 ID 列表
     * @return 预热成功的数量
     */
    int warmupFieldCache(ComputedFieldDO field, List<Long> entityIds);

    /**
     * 预热指定 Model 的所有缓存字段
     * 
     * @param modelId Model ID
     * @param entityIds 需要预热的实体 ID 列表
     * @return 预热成功的数量
     */
    int warmupModelCache(Long modelId, List<Long> entityIds);

    // ========== 缓存统计和监控 ==========

    /**
     * 获取缓存统计信息
     * 
     * @param modelId Model ID（可选，为 null 时返回全局统计）
     * @return 缓存统计信息
     */
    CacheStatistics getCacheStatistics(Long modelId);

    /**
     * 获取指定字段的缓存键数量
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @return 缓存键数量
     */
    long getCacheKeyCount(Long modelId, String fieldCode);

    /**
     * 获取所有缓存键
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码（可选）
     * @return 缓存键集合
     */
    Set<String> getCacheKeys(Long modelId, String fieldCode);

    /**
     * 检查缓存是否存在
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param entityId 实体 ID
     * @return 是否存在
     */
    boolean isCached(Long modelId, String fieldCode, Long entityId);

    /**
     * 获取缓存剩余过期时间（秒）
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param entityId 实体 ID
     * @return 剩余过期时间（秒），-1 表示永不过期，-2 表示不存在
     */
    long getCacheTtl(Long modelId, String fieldCode, Long entityId);

    // ========== 缓存清理 ==========

    /**
     * 清除所有计算字段缓存
     * 
     * @return 清除的缓存数量
     */
    int clearAllCache();

    /**
     * 清除过期的缓存（由 Redis 自动处理，此方法用于手动触发）
     * 
     * @return 清除的缓存数量
     */
    int clearExpiredCache();
    
    // ========== 预计算支持 ==========
    
    /**
     * 获取预计算缓存值
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param entityId 实体 ID
     * @return 预计算的值
     */
    Object getPrecomputedValue(Long modelId, String fieldCode, Long entityId);
    
    /**
     * 设置预计算缓存值
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param entityId 实体 ID
     * @param value 预计算值
     * @param ttlMinutes 过期时间（分钟）
     */
    void setPrecomputedValue(Long modelId, String fieldCode, Long entityId, Object value, int ttlMinutes);
    
    /**
     * 触发异步预计算
     * 
     * @param modelId Model ID
     * @param entityId 实体 ID
     */
    void triggerAsyncPrecompute(Long modelId, Long entityId);
    
    /**
     * 使缓存失效（数据变化时调用）
     * 
     * @param modelId Model ID
     * @param entityId 实体 ID
     * @param triggerPrecompute 是否触发预计算
     */
    void invalidateCache(Long modelId, Long entityId, boolean triggerPrecompute);
    
    /**
     * 清除本地缓存
     */
    void clearLocalCache();

    /**
     * 缓存统计信息
     */
    record CacheStatistics(
            long totalKeys,
            long hitCount,
            long missCount,
            double hitRate,
            long memoryUsageBytes
    ) {
        /**
         * 创建空统计
         */
        public static CacheStatistics empty() {
            return new CacheStatistics(0, 0, 0, 0.0, 0);
        }
    }
}
