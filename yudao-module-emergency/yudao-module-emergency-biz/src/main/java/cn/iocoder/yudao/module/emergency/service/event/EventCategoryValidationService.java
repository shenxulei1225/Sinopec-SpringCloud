package cn.iocoder.yudao.module.emergency.service.event;

import cn.cheers.x.framework.common.biz.system.category.CategoryCommonApi;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 事件分类验证服务
 * 
 * 用于验证事件分类ID的有效性，支持缓存和降级策略
 * 
 * 注意：通过RPC调用System模块的CategoryCommonApi，使用Framework中统一的分类功能
 * 
 * @author 系统生成
 */
@Slf4j
@Service
public class EventCategoryValidationService {
    
    @Autowired(required = false) // 允许为空，如果System服务不可用
    private CategoryCommonApi categoryApi;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String CATEGORY_CACHE_KEY = "category:%d:exists";
    private static final long CATEGORY_CACHE_EXPIRE = 3600; // 1小时
    private static final String BUSINESS_TYPE_CODE = "emergency_event"; // 应急事件分类的业务类型编码
    
    /**
     * 验证分类ID有效性
     * 
     * @param categoryId 分类ID
     * @param allowNull 是否允许为空（创建时允许为空，更新时必须设置）
     */
    public void validateCategoryId(Long categoryId, boolean allowNull) {
        if (categoryId == null) {
            if (allowNull) {
                return; // 创建时允许为空
            } else {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_CATEGORY_NOT_EXISTS);
            }
        }
        
        // 先查缓存
        String cacheKey = String.format(CATEGORY_CACHE_KEY, categoryId);
        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                Boolean exists = (Boolean) cached;
                if (!exists) {
                    log.debug("分类ID不存在（缓存）：categoryId={}", categoryId);
                    throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_CATEGORY_NOT_EXISTS);
                }
                log.debug("分类ID有效（缓存）：categoryId={}", categoryId);
                return;
            }
        } catch (Exception e) {
            log.warn("获取分类缓存失败：categoryId={}", categoryId, e);
        }
        
        // 缓存未命中，调用System模块的分类服务
        if (categoryApi == null) {
            log.warn("分类服务不可用，跳过验证：categoryId={}", categoryId);
            // 降级策略：如果System服务不可用，允许通过（避免阻塞业务）
            // 如果需要严格模式，可以抛出异常
            return;
        }
        
        try {
            CommonResult<Boolean> result = categoryApi.existsCategory(categoryId, BUSINESS_TYPE_CODE);
            if (result == null || !Boolean.TRUE.equals(result.getData())) {
                // 缓存不存在结果
                try {
                    redisTemplate.opsForValue().set(cacheKey, false, CATEGORY_CACHE_EXPIRE, TimeUnit.SECONDS);
                } catch (Exception e) {
                    log.warn("设置分类缓存失败：categoryId={}", categoryId, e);
                }
                log.debug("分类ID不存在：categoryId={}", categoryId);
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_CATEGORY_NOT_EXISTS);
            }
            
            // 缓存存在结果
            try {
                redisTemplate.opsForValue().set(cacheKey, true, CATEGORY_CACHE_EXPIRE, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.warn("设置分类缓存失败：categoryId={}", categoryId, e);
            }
            log.debug("分类ID有效：categoryId={}", categoryId);
            
        } catch (cn.cheers.x.framework.common.exception.ServiceException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            // System服务不可用，使用降级策略
            log.warn("分类服务调用失败，使用降级策略：categoryId={}", categoryId, e);
            // 降级策略：允许通过（避免阻塞业务）
            // 如果需要严格模式，可以抛出CATEGORY_SERVICE_UNAVAILABLE异常
            // throw ServiceExceptionUtil.exception(ErrorCodeConstants.CATEGORY_SERVICE_UNAVAILABLE);
        }
    }
    
    /**
     * 验证分类ID有效性（创建时调用，允许为空）
     * 
     * @param categoryId 分类ID
     */
    public void validateCategoryIdForCreate(Long categoryId) {
        validateCategoryId(categoryId, true);
    }
    
    /**
     * 验证分类ID有效性（更新时调用，不允许为空）
     * 
     * @param categoryId 分类ID
     */
    public void validateCategoryIdForUpdate(Long categoryId) {
        validateCategoryId(categoryId, false);
    }
}

