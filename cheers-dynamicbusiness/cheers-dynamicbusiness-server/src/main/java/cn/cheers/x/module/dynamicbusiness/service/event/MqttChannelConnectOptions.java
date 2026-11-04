package cn.cheers.x.module.dynamicbusiness.service.event;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.util.StringUtils;

/**
 * 平台发到消息通道、联动订消息通道时用的连接身份。
 *
 * <p>管什么：带上平台用户名和口令。</p>
 * <p>不管什么：不决定主题，不调用联动入口，不给页面连接发号。</p>
 */
public final class MqttChannelConnectOptions {

    public static final String PLATFORM_USERNAME = "platform:event";

    private MqttChannelConnectOptions() {
    }

    public static MqttConnectOptions require(String username, String password, boolean cleanSession) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new ServiceException(500, "事件通道没有连接口令");
        }
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(cleanSession);
        options.setUserName(username.trim());
        options.setPassword(password.toCharArray());
        return options;
    }
}
