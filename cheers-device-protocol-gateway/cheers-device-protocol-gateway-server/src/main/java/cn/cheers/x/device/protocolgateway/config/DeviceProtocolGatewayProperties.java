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
     * 同步下发等待同号业务答卷的超时（毫秒）。超时必须失败，不得当成功。
     */
    private long syncReplyTimeoutMs = 30_000L;

    /**
     * 进程内上报队列容量。满则记错误，不在收包线程里丢掉后假装成功。
     */
    private int uplinkQueueCapacity = 1024;

    /**
     * 过渡：上行 HTTP 回调 URL。正式主路径是持久化总线，本项待删除。
     * <p>为空则过渡消费者只记日志。
     */
    private String uplinkNotifyUrl = "";

    /**
     * 每台设备内存窗口最多留多少条副本。超出丢掉最旧的，不写历史表。
     */
    private int monitorWindowSize = 300;

    /**
     * 读哪一份租户下的协议说明书。设备口没有登录租户，不猜设备身份。
     */
    private long protocolCatalogTenantId = 1L;
}
