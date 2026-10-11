package cn.cheers.x.device.protocolgateway.djicloud;

import cn.cheers.x.device.protocolgateway.config.DeviceProtocolGatewayProperties;
import cn.cheers.x.device.protocolgateway.service.MissionDownlink.DirectCommand;
import cn.cheers.x.device.protocolgateway.service.SyncDownlinkResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * 把对照填好的上云服务报文发到 MQTT，并等到同 tid 的 services_reply。
 * <p>不发地面站启动执行，不写任务账。result=0 只表示设备收下了。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DjiCloudCommandSender {

    private final DjiCloudMqttConnector connector;
    private final DjiCloudServiceReplyWaiter replies;
    private final DeviceProtocolGatewayProperties properties;

    public SyncDownlinkResult send(DirectCommand command) {
        if (command == null || !command.cloudService()) {
            return SyncDownlinkResult.writeFailed("上云下发缺少服务主题");
        }
        if (command.msgId() == null || command.msgId().isBlank()) {
            return SyncDownlinkResult.writeFailed("上云下发缺少 tid");
        }
        CompletableFuture<Integer> pending = replies.register(command.msgId());
        boolean written = connector.publish(command.mqttTopic(), command.wireJson());
        if (!written) {
            replies.cancel(command.msgId());
            return SyncDownlinkResult.writeFailed("上云 MQTT 未连接，指令没有发出：" + command.mqttTopic());
        }
        // 光圈、快门、感光度走指令飞行下行。答卷在 drc/up，不在 services_reply。这里只确认已经发出。
        if (command.mqttTopic().endsWith("/drc/down")) {
            replies.cancel(command.msgId());
            return SyncDownlinkResult.accepted(command.wireJson());
        }
        Duration timeout = Duration.ofMillis(Math.max(1L, properties.getSyncReplyTimeoutMs()));
        return replies.await(pending, timeout)
                .map(result -> result == 0
                        ? SyncDownlinkResult.accepted(command.wireJson())
                        : SyncDownlinkResult.rejected(command.wireJson(),
                        "上云答卷未收下 tid=" + command.msgId() + " result=" + result))
                .orElseGet(() -> SyncDownlinkResult.timeout(
                        "等待上云答卷超时 tid=" + command.msgId()));
    }
}
