package cn.cheers.x.device.protocolgateway.protocol.uplink;

import cn.cheers.x.device.protocolgateway.api.dto.GroundStationLiveStatusDTO;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.service.DeviceProtocolDownlinkService;
import cn.cheers.x.device.protocolgateway.service.SyncDownlinkResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroundStationLiveElectricityQueryTest {

    @Mock
    private GroundStationLiveStatusStore store;
    @Mock
    private DeviceProtocolDownlinkService downlinkService;

    private GroundStationLiveElectricityQuery query;

    @BeforeEach
    void setUp() {
        query = new GroundStationLiveElectricityQuery(
                store, new EnvelopeJsonCodec(new ObjectMapper()), downlinkService);
    }

    @Test
    void usesCachedElectricityWithoutAskingTheDevice() {
        when(store.latest("dev-1")).thenReturn(new GroundStationLiveStatusStore.Frame("76.5", 9L));

        GroundStationLiveStatusDTO dto = query.read("dev-1");

        assertEquals("76.5", dto.electricity());
        assertEquals(9L, dto.receivedAtEpochMs());
        verify(downlinkService, never()).sendSyncCommand(any(), any(), anyInt(), any());
    }

    @Test
    void asksCurrentStatusWhenCacheIsEmpty() {
        when(store.latest("dev-1"))
                .thenReturn(null)
                .thenReturn(new GroundStationLiveStatusStore.Frame("82.9", 11L));
        when(downlinkService.sendSyncCommand(eq("dev-1"), any(), eq(TransportOpcode.REAL_TIME_STATUS.code()), any()))
                .thenReturn(SyncDownlinkResult.accepted("{\"code\":200}"));

        GroundStationLiveStatusDTO dto = query.read("dev-1");

        assertEquals("82.9", dto.electricity());
        ArgumentCaptor<String> wire = ArgumentCaptor.forClass(String.class);
        verify(downlinkService).sendSyncCommand(
                eq("dev-1"), wire.capture(), eq(TransportOpcode.REAL_TIME_STATUS.code()), any());
        org.junit.jupiter.api.Assertions.assertTrue(wire.getValue().contains("\"opcode\":500101"));
    }

    @Test
    void returnsEmptyWhenDeviceDoesNotAnswer() {
        when(store.latest("dev-1")).thenReturn(null);
        when(downlinkService.sendSyncCommand(eq("dev-1"), any(), eq(TransportOpcode.REAL_TIME_STATUS.code()), any()))
                .thenReturn(SyncDownlinkResult.writeFailed("设备当前未连接或写出失败：dev-1"));

        GroundStationLiveStatusDTO dto = query.read("dev-1");

        assertNull(dto.electricity());
    }

    @Test
    void keepsElectricityFromTheStatusReplyWhenMemoryIsStillEmpty() {
        GroundStationLiveStatusStore realStore = new GroundStationLiveStatusStore(new ObjectMapper());
        GroundStationLiveElectricityQuery realQuery = new GroundStationLiveElectricityQuery(
                realStore, new EnvelopeJsonCodec(new ObjectMapper()), downlinkService);
        when(downlinkService.sendSyncCommand(eq("dev-1"), any(), eq(TransportOpcode.REAL_TIME_STATUS.code()), any()))
                .thenReturn(SyncDownlinkResult.accepted(
                        "{\"opcode\":500101,\"code\":200,\"data\":{\"electricity\":\"82.9\"}}"));

        GroundStationLiveStatusDTO dto = realQuery.read("dev-1");

        assertEquals("82.9", dto.electricity());
        assertEquals("82.9", realStore.latest("dev-1").electricity());
    }
}
