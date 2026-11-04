package cn.cheers.x.module.dynamicbusiness.devicehistory;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 打开历史库配置。不注册第二个给业务表用的数据源。
 */
@Configuration
@EnableConfigurationProperties({DevicePointHistoryProperties.class, GatewaySubscriptionProperties.class})
public class DevicePointHistoryConfiguration {
}
