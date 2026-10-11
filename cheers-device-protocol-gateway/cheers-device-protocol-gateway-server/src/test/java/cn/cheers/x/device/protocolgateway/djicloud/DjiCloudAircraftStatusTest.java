package cn.cheers.x.device.protocolgateway.djicloud;

import cn.cheers.x.device.protocolgateway.api.dto.AircraftFlightStatusDTO;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DjiCloudAircraftStatusTest {

    @Test
    void keepsTheLatestAircraftFrameAndSkipsTheDock() {
        DjiCloudOnlineRegistry online = new DjiCloudOnlineRegistry();
        DjiCloudAircraftStatusRegistry status = new DjiCloudAircraftStatusRegistry();
        DjiCloudAircraftStatusService service = new DjiCloudAircraftStatusService(online, status);
        online.markOnline("dock-1", java.util.List.of("aircraft-1"));

        status.accept("thing/product/dock-1/osd", """
                {"timestamp":1,"data":{"drone_in_dock":1,"job_number":2}}
                """.getBytes(StandardCharsets.UTF_8));
        assertEquals("还没有这架飞机的状态上报", service.forGateway("dock-1").gap());

        status.accept("thing/product/aircraft-1/osd", """
                {"timestamp":1667220873846,"data":{
                  "gear":1,"mode_code":3,"elevation":130.2,
                  "horizontal_speed":5.2,"vertical_speed":-0.4,"home_distance":222.0,"attitude_head":35,
                  "battery":{"capacity_percent":80,"remain_flight_time":720},
                  "position_state":{"quality":5,"gps_number":18,"rtk_number":6},
                  "storage":{"total":32000000,"used":1200000},
                  "cameras":[{"remain_photo_num":1215,"remain_record_duration":90,"recording_state":0,"record_time":4}]
                }}
                """.getBytes(StandardCharsets.UTF_8));

        AircraftFlightStatusDTO frame = service.forGateway("dock-1");
        assertNull(frame.gap());
        assertEquals("aircraft-1", frame.aircraftSn());
        assertEquals("P", frame.gear());
        assertEquals("手动飞行", frame.flightMode());
        assertEquals(80, frame.batteryPercent());
        assertEquals(720, frame.remainFlightTimeSeconds());
        assertEquals(5, frame.gnssQuality());
        assertEquals(18, frame.gpsNumber());
        assertEquals(6, frame.rtkNumber());
        assertEquals(222.0, frame.homeDistanceMeters());
        assertEquals(5.2, frame.horizontalSpeedMetersPerSecond());
        assertEquals(130.2, frame.elevationMeters());
        assertEquals(-0.4, frame.verticalSpeedMetersPerSecond());
        assertEquals(1215, frame.remainPhotoNum());
        assertEquals(90, frame.remainRecordDurationSeconds());
        assertNull(frame.recordTimeSeconds());
        assertEquals(32000000L, frame.storageTotalKb());
        assertEquals(35, frame.headingDegrees());
        assertNull(frame.cameras());
        assertNull(frame.gimbals());
    }

    @Test
    void keepsThePayloadGimbalPitchThatTheLivePictureStepsFrom() {
        DjiCloudAircraftStatusRegistry status = new DjiCloudAircraftStatusRegistry();
        status.accept("thing/product/aircraft-1/osd", """
                {"timestamp":3,"data":{"elevation":1,"82-0-0":{
                  "payload_index":"82-0-0","gimbal_pitch":-45.5,"gimbal_yaw":10
                }}}
                """.getBytes(StandardCharsets.UTF_8));
        AircraftFlightStatusDTO frame = status.get("aircraft-1");
        assertEquals(1, frame.gimbals().size());
        assertEquals("82-0-0", frame.gimbals().get(0).payloadIndex());
        assertEquals(-45.5, frame.gimbals().get(0).gimbalPitchDegrees());
    }

    @Test
    void keepsCameraExposureEnumsAndLeavesMissingApertureBlank() {
        DjiCloudAircraftStatusRegistry status = new DjiCloudAircraftStatusRegistry();
        status.accept("thing/product/aircraft-1/osd", """
                {"timestamp":2,"data":{"elevation":1,"cameras":[{
                  "payload_index":"81-0-0","remain_photo_num":10,
                  "wide_exposure_mode":1,"wide_iso":5,"wide_shutter_speed":12,"wide_exposure_value":16,
                  "zoom_exposure_mode":4,"zoom_iso":3,"zoom_shutter_speed":20,"zoom_exposure_value":7
                }]}}
                """.getBytes(StandardCharsets.UTF_8));
        AircraftFlightStatusDTO frame = status.get("aircraft-1");
        assertEquals(1, frame.cameras().size());
        assertEquals("81-0-0", frame.cameras().get(0).payloadIndex());
        assertEquals(1, frame.cameras().get(0).wideExposureMode());
        assertEquals(5, frame.cameras().get(0).wideIso());
        assertEquals(12, frame.cameras().get(0).wideShutterSpeed());
        assertEquals(16, frame.cameras().get(0).wideExposureValue());
        assertEquals(4, frame.cameras().get(0).zoomExposureMode());
        assertEquals(3, frame.cameras().get(0).zoomIso());
        assertEquals(20, frame.cameras().get(0).zoomShutterSpeed());
        assertEquals(7, frame.cameras().get(0).zoomExposureValue());
    }

    @Test
    void unreadableBatteryAndMissingHeightStayBlank() {
        DjiCloudAircraftStatusRegistry status = new DjiCloudAircraftStatusRegistry();
        status.accept("thing/product/aircraft-1/osd", """
                {"data":{"horizontal_speed":0,"battery":{"capacity_percent":32767,"remain_flight_time":0}}}
                """.getBytes(StandardCharsets.UTF_8));
        AircraftFlightStatusDTO frame = status.get("aircraft-1");
        assertNull(frame.batteryPercent());
        assertEquals(0, frame.remainFlightTimeSeconds());
        assertEquals(0.0, frame.horizontalSpeedMetersPerSecond());
        assertNull(frame.elevationMeters());
        assertNull(frame.gear());
    }

    @Test
    void doesNotPickOneAircraftWhenTheGatewayHasSeveral() {
        DjiCloudOnlineRegistry online = new DjiCloudOnlineRegistry();
        online.markOnline("dock-1", java.util.List.of("aircraft-1", "aircraft-2"));
        DjiCloudAircraftStatusService service = new DjiCloudAircraftStatusService(
                online, new DjiCloudAircraftStatusRegistry());
        assertEquals("这台网关下有多架飞机，没有写明看哪一架", service.forGateway("dock-1").gap());
    }

    @Test
    void saysWhenTheGatewayHasNotReportedAnAircraft() {
        DjiCloudAircraftStatusService service = new DjiCloudAircraftStatusService(
                new DjiCloudOnlineRegistry(), new DjiCloudAircraftStatusRegistry());
        assertEquals("这台网关还没有报上挂着的飞机", service.forGateway("dock-1").gap());
    }
}
