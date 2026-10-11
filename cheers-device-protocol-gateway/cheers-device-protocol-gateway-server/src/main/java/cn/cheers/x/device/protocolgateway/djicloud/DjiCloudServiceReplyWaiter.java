package cn.cheers.x.device.protocolgateway.djicloud;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * 上云服务答卷：按 tid 等 thing/product/{网关}/services_reply。
 * <p>result=0 只表示机场或遥控器收下了这条指令，不是任务完成。
 */
@Component
public class DjiCloudServiceReplyWaiter {

    private final ObjectMapper objectMapper;
    private final ConcurrentMap<String, CompletableFuture<Integer>> pending = new ConcurrentHashMap<>();

    public DjiCloudServiceReplyWaiter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<Integer> register(String tid) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        pending.put(tid, future);
        return future;
    }

    public void cancel(String tid) {
        CompletableFuture<Integer> future = pending.remove(tid);
        if (future != null) {
            future.cancel(false);
        }
    }

    /**
     * 答卷对上 tid 就结束等待。对不上的包忽略。
     */
    public void completeFromPayload(byte[] payload) {
        Optional<Ack> ack = read(payload);
        if (ack.isEmpty()) {
            return;
        }
        CompletableFuture<Integer> future = pending.remove(ack.get().tid());
        if (future != null) {
            future.complete(ack.get().result());
        }
    }

    public Optional<Integer> await(CompletableFuture<Integer> pendingReply, Duration timeout) {
        try {
            return Optional.of(pendingReply.get(Math.max(1L, timeout.toMillis()), TimeUnit.MILLISECONDS));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private Optional<Ack> read(byte[] payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            if (root == null || !root.hasNonNull("tid")) {
                return Optional.empty();
            }
            JsonNode data = root.get("data");
            if (data == null || !data.has("result")) {
                return Optional.empty();
            }
            return Optional.of(new Ack(root.get("tid").asText(), data.get("result").asInt()));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private record Ack(String tid, int result) {
    }
}
