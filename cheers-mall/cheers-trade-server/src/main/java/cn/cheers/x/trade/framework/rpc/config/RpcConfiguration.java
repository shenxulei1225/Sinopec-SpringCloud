package cn.cheers.x.trade.framework.rpc.config;

import cn.cheers.x.member.api.address.MemberAddressApi;
import cn.cheers.x.member.api.config.MemberConfigApi;
import cn.cheers.x.member.api.level.MemberLevelApi;
import cn.cheers.x.member.api.point.MemberPointApi;
import cn.cheers.x.member.api.user.MemberUserApi;
import cn.cheers.x.pay.api.order.PayOrderApi;
import cn.cheers.x.pay.api.refund.PayRefundApi;
import cn.cheers.x.pay.api.transfer.PayTransferApi;
import cn.cheers.x.pay.api.wallet.PayWalletApi;
import cn.cheers.x.product.api.category.ProductCategoryApi;
import cn.cheers.x.product.api.comment.ProductCommentApi;
import cn.cheers.x.product.api.sku.ProductSkuApi;
import cn.cheers.x.product.api.spu.ProductSpuApi;
import cn.cheers.x.promotion.api.bargain.BargainActivityApi;
import cn.cheers.x.promotion.api.bargain.BargainRecordApi;
import cn.cheers.x.promotion.api.combination.CombinationRecordApi;
import cn.cheers.x.promotion.api.coupon.CouponApi;
import cn.cheers.x.promotion.api.discount.DiscountActivityApi;
import cn.cheers.x.promotion.api.point.PointActivityApi;
import cn.cheers.x.promotion.api.reward.RewardActivityApi;
import cn.cheers.x.promotion.api.seckill.SeckillActivityApi;
import cn.cheers.x.system.api.notify.NotifyMessageSendApi;
import cn.cheers.x.system.api.social.SocialClientApi;
import cn.cheers.x.system.api.social.SocialUserApi;
import cn.cheers.x.system.api.user.AdminUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "tradeRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {
        BargainActivityApi.class, BargainRecordApi.class, CombinationRecordApi.class,
        CouponApi.class, DiscountActivityApi.class, RewardActivityApi.class, SeckillActivityApi.class, PointActivityApi.class,
        MemberUserApi.class, MemberPointApi.class, MemberLevelApi.class, MemberAddressApi.class, MemberConfigApi.class,
        ProductSpuApi.class, ProductSkuApi.class, ProductCommentApi.class, ProductCategoryApi.class,
        PayOrderApi.class, PayRefundApi.class, PayTransferApi.class, PayWalletApi.class,
        AdminUserApi.class, NotifyMessageSendApi.class, SocialClientApi.class, SocialUserApi.class
})
public class RpcConfiguration {
}
