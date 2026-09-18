package cn.cheers.x.device.protocolgateway.datacollection;

import java.util.List;
import java.util.function.Supplier;

/**
 * 说明书目录的短缓存：权威仍是协议目录，这里只避免每包都全量拉。
 * <p>禁止：缓存未命中时编造说明书。
 */
final class DeviceProtocolCatalogCache {

    private static final long TTL_MS = 60_000L;

    private volatile List<EntityRpcProtocolInstructionCatalog.InstructionSpec> specs = List.of();
    private volatile long expireAtEpochMs = 0L;

    synchronized List<EntityRpcProtocolInstructionCatalog.InstructionSpec> getOrLoad(
            Supplier<List<EntityRpcProtocolInstructionCatalog.InstructionSpec>> loader
    ) {
        long now = System.currentTimeMillis();
        if (now < expireAtEpochMs && !specs.isEmpty()) {
            return specs;
        }
        List<EntityRpcProtocolInstructionCatalog.InstructionSpec> loaded = loader.get();
        if (loaded == null) {
            throw new IllegalStateException("说明书目录为空");
        }
        specs = List.copyOf(loaded);
        expireAtEpochMs = now + TTL_MS;
        return specs;
    }
}
