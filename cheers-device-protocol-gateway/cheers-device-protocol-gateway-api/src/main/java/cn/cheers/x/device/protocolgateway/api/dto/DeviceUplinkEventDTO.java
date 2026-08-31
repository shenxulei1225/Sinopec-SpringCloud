package cn.cheers.x.device.protocolgateway.api.dto;

/**
 * 设备上行事件（网关 → 业务回调）。
 * <p>网关不解析巡检业务语义；业务侧按 opcode / templateId 回写任务态。
 *
 * @param deviceId           逻辑设备标识
 * @param opcode             外层操作码
 * @param payloadJson        原始报文
 * @param receivedAtEpochMs  收到时间
 */
public record DeviceUplinkEventDTO(
        String deviceId,
        int opcode,
        String payloadJson,
        long receivedAtEpochMs
) {
}
