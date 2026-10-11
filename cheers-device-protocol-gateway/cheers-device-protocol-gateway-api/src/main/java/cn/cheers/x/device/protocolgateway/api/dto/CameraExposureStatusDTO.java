package cn.cheers.x.device.protocolgateway.api.dto;

/**
 * 飞机定时上报里，一路摄像机当前的曝光档。
 * <p>数值保持官网的整数枚举，不换算成航线里的光圈、快门秒数或感光度数字。
 * 不负责：光圈。定时上报没有光圈。环境亮度也不在这里，曝光值是另一项。
 */
public record CameraExposureStatusDTO(
        String payloadIndex,
        Integer wideExposureMode,
        Integer wideIso,
        Integer wideShutterSpeed,
        Integer wideExposureValue,
        Integer zoomExposureMode,
        Integer zoomIso,
        Integer zoomShutterSpeed,
        Integer zoomExposureValue
) {
}
