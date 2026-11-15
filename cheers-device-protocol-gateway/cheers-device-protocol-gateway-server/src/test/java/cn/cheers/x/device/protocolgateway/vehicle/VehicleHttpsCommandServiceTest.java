package cn.cheers.x.device.protocolgateway.vehicle;

import cn.cheers.x.device.protocolgateway.api.vehicle.VehicleGateActions;
import cn.cheers.x.device.protocolgateway.api.vehicle.VehicleGateCommandResp;
import cn.cheers.x.device.protocolgateway.datacollection.DeviceProtocolVersionCatalog;
import cn.cheers.x.device.protocolgateway.instructiondispatch.InstructionTemplate;
import cn.cheers.x.device.protocolgateway.instructiondispatch.MappingRow;
import cn.cheers.x.device.protocolgateway.instructiondispatch.ProtocolMappingCatalog;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VehicleHttpsCommandServiceTest {

    @Test
    void relativePath_isNotSent() {
        AtomicInteger calls = new AtomicInteger();
        VehicleHttpsCommandService service = service(httpsPacket("/ISAPI/Parking/barrierGate"), (method, address, body) -> {
            calls.incrementAndGet();
            return 200;
        });
        VehicleGateCommandResp result = service.send("gate-1", VehicleGateActions.LIFT, Map.of("车牌", "鄂A1"));
        assertFalse(result.sent());
        assertEquals("这条指令还没有服务地址", result.message());
        assertEquals(0, calls.get());
    }

    @Test
    void absoluteAddress_isSent() {
        AtomicInteger calls = new AtomicInteger();
        VehicleHttpsCommandService service = service(
                httpsPacket("https://park.example/barrier"),
                (method, address, body) -> {
                    calls.incrementAndGet();
                    assertEquals("https://park.example/barrier", address);
                    assertEquals("POST", method);
                    return 200;
                });
        VehicleGateCommandResp result = service.send("gate-1", VehicleGateActions.LIFT, Map.of());
        assertTrue(result.sent());
        assertEquals(1, calls.get());
    }

    private static VehicleHttpsCommandService service(
            Map<String, Object> outbound,
            VehicleHttpsCommandService.HttpExchange exchange) {
        DeviceProtocolVersionCatalog versions = mock(DeviceProtocolVersionCatalog.class);
        when(versions.firstVersion("gate-1")).thenReturn(Optional.of("parking-https"));
        EntityRpcApi entities = mock(EntityRpcApi.class);
        EntityRespDTO action = new EntityRespDTO();
        action.setId(7L);
        when(entities.getEntityByCode(VehicleGateActions.LIFT, "action")).thenReturn(CommonResult.success(action));
        ProtocolMappingCatalog mappings = new ProtocolMappingCatalog() {
            @Override
            public Optional<MappingRow> find(long actionId, String protocolVersion) {
                return Optional.of(new MappingRow(actionId, protocolVersion, 3L, List.of()));
            }

            @Override
            public InstructionTemplate loadInstruction(long instructionId) {
                return new InstructionTemplate(0, outbound, "https", "");
            }
        };
        return new VehicleHttpsCommandService(versions, mappings, entities, new ObjectMapper(), exchange);
    }

    private static Map<String, Object> httpsPacket(String path) {
        return Map.of("httpMethod", "POST", "path", path);
    }
}
