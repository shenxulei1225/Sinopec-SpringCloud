package cn.cheers.x.device.protocolgateway.api.message;

/**
 * 平台 → 地面站：巡检任务指令包下发信封（外层 500104）。
 * <p>{@code packagesJson} 必须是 JSON <strong>数组字符串</strong>（协议 packages 字段类型为 String），
 * 不是已解析的 Java List。
 *
 * @param msgId        消息编号
 * @param deviceId     设备会话编号（可空，部分样例未带）
 * @param templateId   任务模板编号
 * @param packagesJson 指令包 JSON 数组文本
 */
public record CommandSendEnvelope(
        String msgId,
        String deviceId,
        String templateId,
        String packagesJson
) {
}
