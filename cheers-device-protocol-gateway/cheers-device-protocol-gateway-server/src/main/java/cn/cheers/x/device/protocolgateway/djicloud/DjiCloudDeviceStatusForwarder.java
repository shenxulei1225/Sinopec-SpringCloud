package cn.cheers.x.device.protocolgateway.djicloud;

import cn.cheers.x.device.protocolgateway.api.channel.AccessChannelCodes;
import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.datacollection.ProtocolQualifyStatus;
import cn.cheers.x.device.protocolgateway.api.dto.AircraftFlightStatusDTO;
import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.device.protocolgateway.api.dto.PayloadGimbalAttitudeDTO;
import cn.cheers.x.device.protocolgateway.protocol.uplink.UplinkEventPublisher;
import cn.cheers.x.module.dynamicbusiness.api.push.InspectionPushTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 飞机定时上报收成「设备实时状态」。
 * <p>负责：留下最新一帧，并按这台网关把这一帧交给巡检。巡检再按已经存好的名字规则发给页面。
 * <p>不负责：写执行过程账、按设备编号猜这次执行。
 * <p>禁止：对不上唯一网关时改用飞机序列号发给页面。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DjiCloudDeviceStatusForwarder {

    private final DjiCloudAircraftStatusRegistry statusRegistry;
    private final DjiCloudOnlineRegistry onlineRegistry;
    private final UplinkEventPublisher uplinkEventPublisher;

    /**
     * 不是飞机状态的报文不发。这一帧对不上唯一一台已应答的网关时只留在内存，不发给页面。
     */
    public void accept(String topic, byte[] payload) {
        AircraftFlightStatusDTO frame = statusRegistry.accept(topic, payload);
        if (frame == null) {
            return;
        }
        String topicSn = DjiCloudAircraftStatusRegistry.aircraftSn(topic);
        String gatewaySn = onlineRegistry.singleGatewayOf(topicSn);
        if (gatewaySn == null || gatewaySn.isBlank()) {
            log.error("[dji-cloud] 飞机状态对不上唯一网关，不发给页面 sn={}", topicSn);
            return;
        }
        long reportedAt = frame.reportedAtEpochMs() == null
                ? System.currentTimeMillis() : frame.reportedAtEpochMs();
        uplinkEventPublisher.publish(DeviceUplinkEventDTO.of(new CollectionSample(
                AccessChannelCodes.INSPECTION,
                gatewaySn,
                InspectionPushTopics.DEVICE_STATUS.code(),
                ProtocolQualifyStatus.QUALIFIED,
                List.of(),
                fields(frame),
                null,
                "",
                reportedAt)));
    }

    /** 实时画面要看的那些数。没报到的不放进报文，也不填 0。 */
    static Map<String, Object> fields(AircraftFlightStatusDTO frame) {
        Map<String, Object> fields = new LinkedHashMap<>();
        put(fields, "aircraftSn", frame.aircraftSn());
        put(fields, "gear", frame.gear());
        put(fields, "flightMode", frame.flightMode());
        put(fields, "batteryPercent", frame.batteryPercent());
        put(fields, "remainFlightTimeSeconds", frame.remainFlightTimeSeconds());
        put(fields, "gnssQuality", frame.gnssQuality());
        put(fields, "gpsNumber", frame.gpsNumber());
        put(fields, "rtkNumber", frame.rtkNumber());
        put(fields, "homeDistanceMeters", frame.homeDistanceMeters());
        put(fields, "horizontalSpeedMetersPerSecond", frame.horizontalSpeedMetersPerSecond());
        put(fields, "elevationMeters", frame.elevationMeters());
        put(fields, "verticalSpeedMetersPerSecond", frame.verticalSpeedMetersPerSecond());
        put(fields, "remainPhotoNum", frame.remainPhotoNum());
        put(fields, "remainRecordDurationSeconds", frame.remainRecordDurationSeconds());
        put(fields, "recordTimeSeconds", frame.recordTimeSeconds());
        put(fields, "storageTotalKb", frame.storageTotalKb());
        put(fields, "storageUsedKb", frame.storageUsedKb());
        put(fields, "headingDegrees", frame.headingDegrees());
        if (frame.gimbals() != null && !frame.gimbals().isEmpty()) {
            List<Map<String, Object>> gimbals = new ArrayList<>();
            for (PayloadGimbalAttitudeDTO gimbal : frame.gimbals()) {
                if (gimbal == null) {
                    continue;
                }
                Map<String, Object> row = new LinkedHashMap<>();
                put(row, "payloadIndex", gimbal.payloadIndex());
                put(row, "gimbalPitchDegrees", gimbal.gimbalPitchDegrees());
                if (!row.isEmpty()) {
                    gimbals.add(row);
                }
            }
            if (!gimbals.isEmpty()) {
                fields.put("gimbals", gimbals);
            }
        }
        return fields;
    }

    private static void put(Map<String, Object> fields, String key, Object value) {
        if (value != null) {
            fields.put(key, value);
        }
    }
}
