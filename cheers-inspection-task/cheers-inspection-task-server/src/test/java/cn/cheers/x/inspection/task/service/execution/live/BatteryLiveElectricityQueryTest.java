package cn.cheers.x.inspection.task.service.execution.live;

import cn.cheers.x.device.protocolgateway.api.DjiCloudAircraftStatusApi;
import cn.cheers.x.device.protocolgateway.api.GroundStationLiveStatusApi;
import cn.cheers.x.device.protocolgateway.api.dto.AircraftFlightStatusDTO;
import cn.cheers.x.device.protocolgateway.api.dto.GroundStationLiveStatusDTO;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatteryLiveElectricityQueryTest {

    @Mock
    private GroundStationLiveStatusApi groundStationLiveStatusApi;
    @Mock
    private DjiCloudAircraftStatusApi aircraftStatusApi;

    private BatteryLiveElectricityQuery query;

    @BeforeEach
    void setUp() {
        query = new BatteryLiveElectricityQuery(groundStationLiveStatusApi, aircraftStatusApi);
    }

    @Test
    void readsRobotElectricityFromGroundStation() {
        ExecutionDeviceBinding binding = binding("robot-ws", "robot-1");
        when(groundStationLiveStatusApi.latest("robot-1"))
                .thenReturn(CommonResult.success(new GroundStationLiveStatusDTO("76.5%", 1L)));

        assertEquals("76.5%", query.currentPercent(binding, 14400, null));
    }

    @Test
    void readsDjiBatteryPercentFromLatestFrame() {
        ExecutionDeviceBinding binding = binding("dji-pilot-1.16.1", "dock-1");
        when(aircraftStatusApi.status("dock-1")).thenReturn(CommonResult.success(djiFrame(76, 720)));

        assertEquals("76", query.currentPercent(binding, 1800, null));
    }

    @Test
    void convertsDjiRemainSecondsWhenPercentIsMissing() {
        ExecutionDeviceBinding binding = binding("dji-dock-1.16.1", "dock-1");
        when(aircraftStatusApi.status("dock-1")).thenReturn(CommonResult.success(djiFrame(null, 720)));

        assertEquals("40", query.currentPercent(binding, 1800, null));
    }

    @Test
    void readsCurrentStatusWhenDjiFrameHasNoElectricity() {
        ExecutionDeviceBinding binding = binding("dji-pilot-1.16.1", "dock-1");
        when(aircraftStatusApi.status("dock-1")).thenReturn(CommonResult.success(
                AircraftFlightStatusDTO.gap("还没有飞机状态", "dock-1")));
        when(groundStationLiveStatusApi.latest("dock-1"))
                .thenReturn(CommonResult.success(new GroundStationLiveStatusDTO("82.9", 1L)));

        assertEquals("82.9", query.currentPercent(binding, 1800, null));
    }

    @Test
    void returnsEmptyWhenDeviceHasNotReported() {
        ExecutionDeviceBinding binding = binding("robot-ws", "robot-1");
        when(groundStationLiveStatusApi.latest("robot-1")).thenReturn(CommonResult.success(null));

        assertNull(query.currentPercent(binding, 14400, null));
    }

    @Test
    void doesNotInventFullChargeForUnknownProtocol() {
        assertNull(query.currentPercent(binding("unitree-go2", "dog-1"), 14400, null));
        assertNull(query.currentPercent(binding("robot-ws", ""), 14400, null));
        assertNull(query.currentPercent(null, 14400, null));
    }

    private static ExecutionDeviceBinding binding(String protocol, String deviceId) {
        ExecutionDeviceBinding binding = new ExecutionDeviceBinding();
        binding.setProtocolCode(protocol);
        binding.setLogicalDeviceId(deviceId);
        return binding;
    }

    private static AircraftFlightStatusDTO djiFrame(Integer batteryPercent, Integer remainSeconds) {
        return new AircraftFlightStatusDTO(
                null, "aircraft-1", 1L, "P", "手动飞行", batteryPercent, remainSeconds, 5, 18, 6,
                10.0, 1.0, 2.0, 0.0, 3, 4, null, 5L, 1L, 35, null, null, null);
    }

}
