package cn.cheers.x.device.protocolgateway.protocol.uplink;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SyncReplyWaiterTest {

    private final SyncReplyWaiter waiter = new SyncReplyWaiter();

    @Test
    void completeBeforeAwait_stillReturnsReply() {
        CompletableFuture<String> pending = waiter.register("d1", "m1", 500104);
        assertTrue(waiter.completeIfPending("d1", "m1", 500104, "{\"code\":200}"));
        assertEquals("{\"code\":200}", waiter.await(pending, Duration.ofMillis(50)).orElse(""));
    }

    @Test
    void timeout_isEmpty() {
        CompletableFuture<String> pending = waiter.register("d1", "m1", 500104);
        assertTrue(waiter.await(pending, Duration.ofMillis(30)).isEmpty());
        assertFalse(pending.isDone());
    }
}
