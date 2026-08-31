package cn.cheers.x.device.protocolgateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 协议网关可配置项：路径可配，不绑死生产端口。
 */
@Data
@ConfigurationProperties(prefix = "cheers.device-protocol-gateway")
public class DeviceProtocolGatewayProperties {

    /**
     * WebSocket 路径模式，末段须能解析出 deviceId（默认 /{deviceId}）。
     */
    private String pathPattern = "/{deviceId}";

    /**
     * 允许的 Origin；本地联调可用 *。
     */
    private String[] allowedOrigins = new String[]{"*"};

    /**
     * 上行业务回调完整 URL（如 http://inspection-task-server/.../device-uplink/notify）。
     * <p>为空则只记网关日志，不推业务。
     */
    private String uplinkNotifyUrl = "";
}
