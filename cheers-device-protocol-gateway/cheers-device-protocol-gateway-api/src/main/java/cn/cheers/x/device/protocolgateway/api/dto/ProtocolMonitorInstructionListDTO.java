package cn.cheers.x.device.protocolgateway.api.dto;

import java.util.List;

/**
 * 这台设备能选的下发指令。
 *
 * @param protocol     设备默认使用的协议编码；没有则为空
 * @param protocolName 协议的中文名
 * @param note               列不出来时的原因
 * @param instructions       下发指令
 * @param suggestedStreamUrl 这台设备已生成任务上分配的收流地址。没有任务或还没分配时为空
 */
public record ProtocolMonitorInstructionListDTO(
        String protocol,
        String protocolName,
        String note,
        List<ProtocolMonitorInstructionDTO> instructions,
        String suggestedStreamUrl
) {
}
