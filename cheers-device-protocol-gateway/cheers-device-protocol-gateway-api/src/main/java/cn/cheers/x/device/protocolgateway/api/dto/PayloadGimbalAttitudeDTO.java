package cn.cheers.x.device.protocolgateway.api.dto;

/**
 * 一架云台在这一帧报上的俯仰角。
 * <p>角度是度。没报到的云台不出现在列表里，不拿 0 填。
 */
public record PayloadGimbalAttitudeDTO(
        String payloadIndex,
        Double gimbalPitchDegrees
) {
}
