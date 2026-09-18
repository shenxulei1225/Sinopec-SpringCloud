package cn.cheers.x.device.protocolgateway.protocol.uplink;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 同步下发等待登记：按设备 + 消息 id + 操作码等同一号业务答卷。
 * <p>负责：登记、完成、超时。调用方必须拿着 register 返回的 Future 等待，避免答卷先到被当成超时。
 * <p>不负责：回执是否算成功（回执不得调用完成）、上报进总线、读巡检库。
 * <p>禁止：忙等轮询；把写出成功当成等待完成。
 */
@Component
public class SyncReplyWaiter {

    private final ConcurrentHashMap<String, CompletableFuture<String>> pending = new ConcurrentHashMap<>();

    public String key(String deviceId, String msgId, int opcode) {
        return deviceId + "|" + msgId + "|" + opcode;
    }

    /**
     * 写出前登记。同一键已有等待则结束旧等待（视为被新一次下发取代）。
     *
     * @return 本次等待的 Future，下发方必须拿它来 await，不要再按键回查
     */
    public CompletableFuture<String> register(String deviceId, String msgId, int opcode) {
        String waitKey = key(deviceId, msgId, opcode);
        CompletableFuture<String> future = new CompletableFuture<>();
        CompletableFuture<String> previous = pending.put(waitKey, future);
        if (previous != null) {
            previous.completeExceptionally(new IllegalStateException("同号等待被新一次下发取代"));
        }
        return future;
    }

    /**
     * 对上正在等的同号答卷则完成并返回 true；对不上返回 false（调用方再进总线）。
     */
    public boolean completeIfPending(String deviceId, String msgId, int opcode, String payloadJson) {
        if (msgId == null || msgId.isBlank()) {
            return false;
        }
        CompletableFuture<String> future = pending.remove(key(deviceId, msgId, opcode));
        if (future == null) {
            return false;
        }
        return future.complete(payloadJson);
    }

    public void cancel(String deviceId, String msgId, int opcode) {
        CompletableFuture<String> future = pending.remove(key(deviceId, msgId, opcode));
        if (future != null) {
            future.cancel(true);
        }
    }

    /**
     * 等到答卷或超时。超时返回 empty，由调用方标失败，不得当成功。
     */
    public Optional<String> await(CompletableFuture<String> future, Duration timeout) {
        try {
            return Optional.ofNullable(future.get(timeout.toMillis(), TimeUnit.MILLISECONDS));
        } catch (Exception ex) {
            pending.values().remove(future);
            return Optional.empty();
        }
    }
}
