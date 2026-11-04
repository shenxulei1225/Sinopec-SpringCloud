package cn.cheers.x.module.dynamicbusiness.service.strategy;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 策略保存或删除后加一。
 * 数据处理用这个数决定要不要重新读规则，不在每条读数上查库。
 */
@Component
public class StrategyRuleVersion {

    private final AtomicLong version = new AtomicLong();

    public long current() {
        return version.get();
    }

    public void bump() {
        version.incrementAndGet();
    }
}
