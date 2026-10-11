package cn.cheers.x.inspection.task.service.execution.live;

import cn.cheers.x.device.protocolgateway.api.DjiCloudAircraftStatusApi;
import cn.cheers.x.device.protocolgateway.api.dto.AircraftFlightStatusDTO;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatrolLiveAircraftStatusServiceTest {

    @Mock
    private EntityRpcApi entityRpcApi;
    @Mock
    private DjiCloudAircraftStatusApi aircraftStatusApi;

    private PatrolLiveAircraftStatusService service;

    @BeforeEach
    void setUp() {
        service = new PatrolLiveAircraftStatusService(entityRpcApi, aircraftStatusApi);
    }

    @Test
    void asksTheGatewayByTheRecordedGatewaySn() {
        when(entityRpcApi.getEntity(9L, PatrolLiveAircraftStatusService.RECORD_TYPE))
                .thenReturn(CommonResult.success(record("dji-dock-1.16.1", "dock-1")));
        when(aircraftStatusApi.status("dock-1")).thenReturn(CommonResult.success(
                new AircraftFlightStatusDTO(
                        null, "aircraft-1", 1L, "P", "手动飞行", 80, 720, 5, 18, 6,
                        10.0, 1.0, 2.0, 0.0, 3, 4, null, 5L, 1L, 35, null, null)));

        AircraftFlightStatusDTO status = service.load(9L);

        assertEquals("aircraft-1", status.aircraftSn());
        assertEquals(80, status.batteryPercent());
    }

    @Test
    void doesNotAskTheGatewayWhenThisRunIsNotDjiCloud() {
        when(entityRpcApi.getEntity(9L, PatrolLiveAircraftStatusService.RECORD_TYPE))
                .thenReturn(CommonResult.success(record("unitree-go2", "dog-1")));

        AircraftFlightStatusDTO status = service.load(9L);

        assertEquals("这次执行不是大疆机场或大疆 Pilot，没有这路飞机状态", status.gap());
        verify(aircraftStatusApi, never()).status(org.mockito.ArgumentMatchers.any());
    }

    private static EntityRespDTO record(String protocol, String gatewaySn) {
        EntityRespDTO record = new EntityRespDTO();
        record.setId(9L);
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("protocolVersion", protocol);
        snapshot.put("logicalDeviceId", gatewaySn);
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("standard_snapshot", snapshot);
        record.setCustomFields(fields);
        return record;
    }
}
