package cn.iocoder.yudao.module.emergency.framework.common.util;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁工具类
 * 
 * 基于Redisson实现分布式锁，用于并发控制
 * 
 * @author 系统生成
 */
@Slf4j
@Component
public class DistributedLockUtil {
    
    @Autowired
    private RedissonClient redissonClient;
    
    /**
     * 获取锁并执行（有返回值）
     * 
     * @param lockKey 锁键
     * @param waitTime 等待时间（秒）
     * @param leaseTime 锁持有时间（秒）
     * @param supplier 业务逻辑
     * @return 执行结果
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // 尝试获取锁，最多等待waitTime秒
            boolean acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (!acquired) {
                log.warn("获取分布式锁失败：lockKey={}", lockKey);
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.LOCK_ACQUIRE_FAILED);
            }
            
            log.debug("获取分布式锁成功：lockKey={}", lockKey);
            
            // 执行业务逻辑
            return supplier.get();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取分布式锁被中断：lockKey={}", lockKey, e);
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.LOCK_ACQUIRE_INTERRUPTED);
        } finally {
            // 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("释放分布式锁：lockKey={}", lockKey);
            }
        }
    }
    
    /**
     * 获取锁并执行（无返回值）
     * 
     * @param lockKey 锁键
     * @param waitTime 等待时间（秒）
     * @param leaseTime 锁持有时间（秒）
     * @param runnable 业务逻辑
     */
    public void executeWithLock(String lockKey, long waitTime, long leaseTime, Runnable runnable) {
        executeWithLock(lockKey, waitTime, leaseTime, () -> {
            runnable.run();
            return null;
        });
    }
}








