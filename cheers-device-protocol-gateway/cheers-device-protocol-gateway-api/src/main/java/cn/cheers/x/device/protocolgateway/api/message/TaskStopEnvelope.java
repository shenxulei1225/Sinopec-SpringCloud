package cn.cheers.x.device.protocolgateway.api.message;

/**
 * 任务终止（外层 500205）。
 */
public record TaskStopEnvelope(
        String msgId,
        String taskId,
        String deviceId
) {
}
