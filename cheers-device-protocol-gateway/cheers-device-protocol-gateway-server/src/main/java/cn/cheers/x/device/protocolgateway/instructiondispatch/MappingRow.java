package cn.cheers.x.device.protocolgateway.instructiondispatch;

import java.util.List;

/**
 * 一条对照：动作 + 协议版本 → 哪条协议指令 + 参数路径。
 */
public record MappingRow(
        long actionId,
        String protocolVersion,
        long instructionId,
        List<SlotMapping> slots
) {
    public MappingRow {
        if (actionId <= 0) {
            throw new IllegalArgumentException("对照缺少动作");
        }
        if (protocolVersion == null || protocolVersion.isBlank()) {
            throw new IllegalArgumentException("对照缺少协议版本");
        }
        if (instructionId <= 0) {
            throw new IllegalArgumentException("对照缺少协议指令");
        }
        protocolVersion = protocolVersion.trim();
        slots = slots == null ? List.of() : List.copyOf(slots);
    }
}
