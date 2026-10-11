package cn.cheers.x.device.protocolgateway.api.dto;

/**
 * 监控页上的一条协议指令。下发、上报、回执和各种通讯方式都在这里。
 *
 * @param name            指令名称
 * @param opcode          操作码；大疆指令不用操作码区分，这里是 0
 * @param sendAlone       能否按这条指令自己的空包，从地面站连接单独发出
 * @param blockReason     不能这样发出时的原因
 * @param instructionKey  这一条在名单里的键
 * @param communication   mqtt、wayline、https、jsbridge、websocket；地面站下发留空
 * @param direction       downlink、uplink 或 ack
 */
public record ProtocolMonitorInstructionDTO(
        String name,
        int opcode,
        boolean sendAlone,
        String blockReason,
        String instructionKey,
        String communication,
        String direction
) {
}
