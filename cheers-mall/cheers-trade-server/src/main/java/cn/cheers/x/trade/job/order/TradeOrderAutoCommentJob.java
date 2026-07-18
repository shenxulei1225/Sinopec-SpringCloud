package cn.cheers.x.trade.job.order;

import cn.cheers.x.framework.tenant.core.job.TenantJob;
import cn.cheers.x.trade.service.order.TradeOrderUpdateService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 交易订单的自动评论 Job
 *
 * 
 */
@Component
public class TradeOrderAutoCommentJob {

    @Resource
    private TradeOrderUpdateService tradeOrderUpdateService;

    @XxlJob("tradeOrderAutoCommentJob")
    @TenantJob // 多租户
    public String execute() {
        int count = tradeOrderUpdateService.createOrderItemCommentBySystem();
        return String.format("评论订单 %s 个", count);
    }

}
