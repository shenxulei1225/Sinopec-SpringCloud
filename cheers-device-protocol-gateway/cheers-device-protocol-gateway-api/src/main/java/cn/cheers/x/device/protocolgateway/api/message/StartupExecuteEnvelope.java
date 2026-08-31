package cn.cheers.x.device.protocolgateway.api.message;

/**
 * 启动执行已下发任务（外层 500201）。
 */
public record StartupExecuteEnvelope(
        String msgId,
        String taskId,
        String deviceId,
        String templateId,
        long executionTimeEpochSeconds
) {
}
