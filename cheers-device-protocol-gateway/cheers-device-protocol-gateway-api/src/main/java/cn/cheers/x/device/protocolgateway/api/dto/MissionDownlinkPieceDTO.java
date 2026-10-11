package cn.cheers.x.device.protocolgateway.api.dto;

/**
 * 已翻译下行的一段报文，供生成任务落盘与开跑只下发。
 *
 * @param kind         {@code direct} 单条指令；{@code package} 巡检指令包（500104）
 * @param opcode       地面站操作码；上云 direct 可为 null
 * @param msgId        消息编号
 * @param wireJson     已编码报文（联调核对）
 * @param mqttTopic    上云服务 topic；地面站为 null
 * @param deviceId     指令包信封上的设备编号
 * @param templateId   指令包信封上的模板编号
 * @param packagesJson 500104 的 packages 字段（JSON 数组字符串）
 */
public record MissionDownlinkPieceDTO(
        String kind,
        Integer opcode,
        String msgId,
        String wireJson,
        String mqttTopic,
        String deviceId,
        String templateId,
        String packagesJson
) {
}
