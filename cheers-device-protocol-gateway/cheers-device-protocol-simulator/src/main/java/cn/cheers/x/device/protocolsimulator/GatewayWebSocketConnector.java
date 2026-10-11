package cn.cheers.x.device.protocolsimulator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 模拟器作为设备，连到协议网关的设备口。
 * <p>负责：连上、把网关下发交给应答、把应答写回同一条连接。网关没开时只记下连不上，不退出。
 * <p>不负责：协议网关自己的监听。
 * <p>禁止：连回老平台的 8501 端口。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayWebSocketConnector {

    private final SimulatorProperties properties;
    private final SimulatorReplies replies;
    private final SimulatorState state;
    private final ObjectMapper objectMapper;
    private final ExecutorService tasks = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "protocol-simulator-task");
        thread.setDaemon(true);
        return thread;
    });

    private volatile WebSocketClient client;

    @Scheduled(fixedRate = 10_000)
    public void keepConnectedAndReport() {
        connectIfNeeded();
        long now = System.currentTimeMillis();
        state.enabledReports().forEach((deviceId, report) -> {
            send(replies.timedStatus(deviceId, report.sent() + 1, now));
            state.markReported(deviceId);
        });
    }

    public void connectIfNeeded() {
        WebSocketClient current = client;
        if (current != null && !current.isClosed()) {
            return;
        }
        try {
            URI socket = deviceSocket();
            WebSocketClient next = new WebSocketClient(socket, new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    log.info("[protocol-simulator] 已连接 {} 角色 {}", socket, properties.getRole());
                }

                @Override
                public void onMessage(String message) {
                    handle(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info("[protocol-simulator] 连接关闭 code={} reason={}", code, reason);
                }

                @Override
                public void onError(Exception ex) {
                    log.info("[protocol-simulator] 连接失败 {}", ex.getMessage());
                }
            };
            next.connect();
            client = next;
        } catch (Exception ex) {
            log.info("[protocol-simulator] 无法发起连接 {}", ex.getMessage());
        }
    }

    /**
     * 主机和端口用配置里的地址。路径最后一段只用设备编号，机器人和无人机因此连到不同设备。
     */
    private URI deviceSocket() {
        URI configured = URI.create(properties.getWebsocketUrl());
        try {
            return new URI(
                    configured.getScheme(),
                    configured.getUserInfo(),
                    configured.getHost(),
                    configured.getPort(),
                    "/" + properties.getDeviceId(),
                    null,
                    null);
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("模拟器连接地址无效", ex);
        }
    }

    void handle(String message) {
        long now = System.currentTimeMillis();
        for (String outbound : replies.immediate(message, now)) {
            send(outbound);
        }
        startTaskIfAccepted(message);
    }

    private void startTaskIfAccepted(String message) {
        try {
            JsonNode incoming = objectMapper.readTree(message);
            if (incoming.path("opcode").asInt() != 500201) {
                return;
            }
            String templateId = incoming.path("templateId").asText(null);
            String taskId = incoming.path("taskId").asText("");
            state.packet(templateId).ifPresent(saved -> tasks.execute(() -> {
                for (String outbound : replies.runTask(taskId, saved.packagesJson(), System.currentTimeMillis())) {
                    send(outbound);
                }
            }));
        } catch (Exception ex) {
            log.info("[protocol-simulator] 启动任务没有进入逐步上报 {}", ex.getMessage());
        }
    }

    private void send(String text) {
        WebSocketClient current = client;
        if (current == null || !current.isOpen()) {
            log.info("[protocol-simulator] 网关未连接，报文未发出 {}", text);
            return;
        }
        current.send(text);
    }

    @PreDestroy
    public void close() {
        tasks.shutdownNow();
        WebSocketClient current = client;
        if (current != null) {
            current.close();
        }
    }

    @Configuration
    @EnableConfigurationProperties(SimulatorProperties.class)
    static class Beans {

        @Bean
        SimulatorState simulatorState() {
            return new SimulatorState();
        }

        @Bean
        SimulatorReplies simulatorReplies(
                ObjectMapper objectMapper, SimulatorState simulatorState, SimulatorProperties properties) {
            return new SimulatorReplies(
                    objectMapper, simulatorState, properties.getDeviceId(), properties.getRole());
        }
    }
}
