package cn.cheers.x.device.protocolgateway.api.dto;

/**
 * 网关正式开跑结果：指令包下发 + 启动执行。
 * <p>success=false 时 failureReason 说明缺口（离线、写出失败等），调用方不得当成功。
 *
 * @param success           500104 与 500201 均已写出
 * @param online            下发前逻辑设备是否在注册表有当前连接
 * @param commandSent       500104 是否写出
 * @param startupSent       500201 是否写出
 * @param failureReason     失败原因；成功时为 null
 * @param commandWireJson   已编码的 500104 报文（便于联调核对）
 */
public record MissionStartRespDTO(
        boolean success,
        boolean online,
        boolean commandSent,
        boolean startupSent,
        String failureReason,
        String commandWireJson
) {
}
