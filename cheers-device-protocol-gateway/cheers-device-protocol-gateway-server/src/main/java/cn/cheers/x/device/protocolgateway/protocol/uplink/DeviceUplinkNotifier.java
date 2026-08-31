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
 * 上行业务回调：按配置 URL 推送事件；未配置则只记日志。
 * <p>不依赖巡检模块；业务服务自行暴露接收端。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceUplinkNotifier {

    private final DeviceProtocolGatewayProperties properties;
    private final RestClient.Builder restClientBuilder;

    public void notifyBusiness(DeviceUplinkEventDTO event) {
        String url = properties.getUplinkNotifyUrl();
        if (!StringUtils.hasText(url)) {
            log.debug("[device-protocol] 未配置 uplink-notify-url，跳过业务回调 deviceId={} opcode={}",
                    event.deviceId(), event.opcode());
            return;
        }
        try {
            restClientBuilder.build()
                    .post()
                    .uri(url.trim())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(event)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            log.warn("[device-protocol] 上行业务回调失败 url={} deviceId={} opcode={}: {}",
                    url, event.deviceId(), event.opcode(), ex.getMessage());
        }
    }
}
