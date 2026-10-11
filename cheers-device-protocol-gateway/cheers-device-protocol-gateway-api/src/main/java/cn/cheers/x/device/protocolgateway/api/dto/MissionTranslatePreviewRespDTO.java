package cn.cheers.x.device.protocolgateway.api.dto;

/**
 * 第 3 步试译结果：校验对照与航线/KMZ，不写盘、不下发。
 *
 * @param ok             对照与参数完整、航线可生成
 * @param failureReason  失败原因；成功时为 null
 */
public record MissionTranslatePreviewRespDTO(boolean ok, String failureReason) {
}
