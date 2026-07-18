package cn.cheers.x.promotion.framework.rpc.config;

import cn.cheers.x.infra.api.websocket.WebSocketSenderApi;
import cn.cheers.x.member.api.user.MemberUserApi;
import cn.cheers.x.product.api.category.ProductCategoryApi;
import cn.cheers.x.product.api.sku.ProductSkuApi;
import cn.cheers.x.product.api.spu.ProductSpuApi;
import cn.cheers.x.system.api.social.SocialClientApi;
import cn.cheers.x.system.api.user.AdminUserApi;
import cn.cheers.x.trade.api.order.TradeOrderApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "promotionRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {ProductSkuApi.class, ProductSpuApi.class, ProductCategoryApi.class,
        MemberUserApi.class, TradeOrderApi.class, AdminUserApi.class, SocialClientApi.class,
        WebSocketSenderApi.class})
public class RpcConfiguration {
}
