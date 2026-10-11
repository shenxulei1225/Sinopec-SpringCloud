package cn.cheers.x.device.protocolgateway.api.dto;

import java.util.List;

/**
 * 一架飞机最近一帧定时上报里，实时画面要看的那些数，以及这一帧摄像机已经报上的曝光档。
 * <p>没报到的字段是 null，不是 0。gap 有字时这一帧不能当状态用。
 * <p>cameras 只保留定时上报里广角和变焦的曝光模式、感光度、快门、曝光值。没有这些键就是 null。
 * 这一帧没有光圈，也没有航线里的环境亮度。曝光值不能拿去填环境亮度。
 * gimbals 是各云台这一帧报上的当前俯仰角。没有就是 null，不拿 0 填。
 */
public record AircraftFlightStatusDTO(
        String gap,
        String aircraftSn,
        Long reportedAtEpochMs,
        String gear,
        String flightMode,
        Integer batteryPercent,
        Integer remainFlightTimeSeconds,
        Integer gnssQuality,
        Integer gpsNumber,
        Integer rtkNumber,
        Double homeDistanceMeters,
        Double horizontalSpeedMetersPerSecond,
        Double elevationMeters,
        Double verticalSpeedMetersPerSecond,
        Integer remainPhotoNum,
        Integer remainRecordDurationSeconds,
        Integer recordTimeSeconds,
        Long storageTotalKb,
        Long storageUsedKb,
        Integer headingDegrees,
        List<CameraExposureStatusDTO> cameras,
        List<PayloadGimbalAttitudeDTO> gimbals
) {
    public static AircraftFlightStatusDTO gap(String gap, String aircraftSn) {
        return new AircraftFlightStatusDTO(
                gap, aircraftSn, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null);
    }
}
