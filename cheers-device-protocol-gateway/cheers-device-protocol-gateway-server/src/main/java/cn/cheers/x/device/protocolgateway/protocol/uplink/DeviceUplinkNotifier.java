package cn.cheers.x.device.protocolgateway.protocol.uplink;

import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.device.protocolgateway.config.DeviceProtocolGatewayProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

/**
 * 过渡消费者：把已出队的上报用 HTTP 推给业务。
 * <p>正式主路径是持久化总线；本类待替换，禁止再从收包线程直接调用。
 * <p>不依赖巡检模块；业务服务自行暴露接收端。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceUplinkNotifier implements UplinkEventConsumer {

    private final DeviceProtocolGatewayProperties properties;
    private final RestClient.Builder restClientBuilder;

    @Override
    public void consume(DeviceUplinkEventDTO event) {
        notifyBusiness(event);
    }

    public void notifyBusiness(DeviceUplinkEventDTO event) {
        String url = properties.getUplinkNotifyUrl();
        if (!StringUtils.hasText(url)) {
            log.debug("[device-protocol] 未配置 uplink-notify-url，跳过过渡 HTTP channel={} deviceId={} messageKind={}",
                    event.channelCode(), event.deviceId(), event.messageKind());
            return;
        }
        try {
            restClientBuilder.build()
                    .post()
                    .uri(url.trim())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(event.sample())
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            log.warn("[device-protocol] 过渡 HTTP 回调失败 url={} channel={} deviceId={} messageKind={}: {}",
                    url, event.channelCode(), event.deviceId(), event.messageKind(), ex.getMessage());
        }
    }
}
