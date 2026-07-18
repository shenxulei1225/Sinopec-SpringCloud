package cn.cheers.x.statistics.service.pay;

import cn.cheers.x.pay.enums.refund.PayRefundStatusEnum;
import cn.cheers.x.pay.enums.wallet.PayWalletBizTypeEnum;
import cn.cheers.x.statistics.dal.mysql.pay.PayWalletStatisticsMapper;
import cn.cheers.x.statistics.service.pay.bo.RechargeSummaryRespBO;
import cn.cheers.x.statistics.service.trade.bo.WalletSummaryRespBO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

/**
 * 钱包的统计 Service 实现类
 *
 * @author owen
 */
@Service
@Validated
public class PayWalletStatisticsServiceImpl implements PayWalletStatisticsService {

    @Resource
    private PayWalletStatisticsMapper payWalletStatisticsMapper;

    @Override
    public WalletSummaryRespBO getWalletSummary(LocalDateTime beginTime, LocalDateTime endTime) {
        WalletSummaryRespBO paySummary = payWalletStatisticsMapper.selectRechargeSummaryByPayTimeBetween(
                beginTime, endTime, true);
        WalletSummaryRespBO refundSummary = payWalletStatisticsMapper.selectRechargeSummaryByRefundTimeBetween(
                beginTime, endTime, PayRefundStatusEnum.SUCCESS.getStatus());
        Integer walletPayPrice = payWalletStatisticsMapper.selectPriceSummaryByBizTypeAndCreateTimeBetween(
                beginTime, endTime, PayWalletBizTypeEnum.PAYMENT.getType());
        // 拼接
        paySummary.setWalletPayPrice(walletPayPrice)
                .setRechargeRefundCount(refundSummary.getRechargeRefundCount())
                .setRechargeRefundPrice(refundSummary.getRechargeRefundPrice());
        return paySummary;
    }

    @Override
    public RechargeSummaryRespBO getUserRechargeSummary(LocalDateTime beginTime, LocalDateTime endTime) {
        return payWalletStatisticsMapper.selectRechargeSummaryGroupByWalletId(beginTime, endTime, true);
    }

    @Override
    public Integer getRechargePriceSummary() {
        return payWalletStatisticsMapper.selectRechargePriceSummary(Boolean.TRUE);
    }

}
