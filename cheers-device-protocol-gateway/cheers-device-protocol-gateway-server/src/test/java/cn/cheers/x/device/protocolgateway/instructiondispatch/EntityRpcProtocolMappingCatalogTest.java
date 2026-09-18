package cn.cheers.x.device.protocolgateway.instructiondispatch;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 对照目录从指令协议对接读；缺行或缺空包必须暴露，不得猜指令。
 */
class EntityRpcProtocolMappingCatalogTest {

    @Test
    void find_readsActionVersionAndSlots() {
        EntityRpcApi api = mock(EntityRpcApi.class);
        when(api.listEntities(eq("protocol_mapping"), isNull()))
                .thenReturn(CommonResult.success(List.of(mappingEntity())));
        EntityRpcProtocolMappingCatalog catalog = new EntityRpcProtocolMappingCatalog(api, new ObjectMapper());

        MappingRow row = catalog.find(11L, "robot-ws").orElseThrow();
        assertEquals(501L, row.instructionId());
        assertEquals("request.pointId", row.slots().get(0).path());
        assertEquals("FLD-PNT-002", row.slots().get(0).sourcePath());
    }

    @Test
    void find_missingRow_isEmpty() {
        EntityRpcApi api = mock(EntityRpcApi.class);
        when(api.listEntities(eq("protocol_mapping"), isNull()))
                .thenReturn(CommonResult.success(List.of(mappingEntity())));
        EntityRpcProtocolMappingCatalog catalog = new EntityRpcProtocolMappingCatalog(api, new ObjectMapper());

        assertTrue(catalog.find(99L, "robot-ws").isEmpty());
    }

    @Test
    void loadInstruction_readsOutboundEmptyPacket() {
        EntityRpcApi api = mock(EntityRpcApi.class);
        when(api.getEntity(eq(501L), eq("data_protocol")))
                .thenReturn(CommonResult.success(instructionEntity()));
        EntityRpcProtocolMappingCatalog catalog = new EntityRpcProtocolMappingCatalog(api, new ObjectMapper());

        InstructionTemplate template = catalog.loadInstruction(501L);
        assertEquals(200102, template.opcode());
        assertEquals("", ((Map<?, ?>) template.outbound().get("request")).get("pointId"));
    }

    @Test
    void listFail_exposesGap() {
        EntityRpcApi api = mock(EntityRpcApi.class);
        when(api.listEntities(eq("protocol_mapping"), isNull()))
                .thenReturn(CommonResult.error(500, "对照目录无响应"));
        EntityRpcProtocolMappingCatalog catalog = new EntityRpcProtocolMappingCatalog(api, new ObjectMapper());

        assertThrows(IllegalStateException.class, () -> catalog.find(11L, "robot-ws"));
    }

    private static EntityRespDTO mappingEntity() {
        EntityRespDTO dto = new EntityRespDTO();
        dto.setCustomFields(Map.of(
                "action_ref", Map.of("id", 11L),
                "mapped_protocol_version", "robot-ws",
                "protocol_instruction_ref", Map.of("id", 501L),
                "param_slot_mapping_json", List.of(Map.of(
                        "slot", "location_ref",
                        "sourcePath", "FLD-PNT-002",
                        "path", "request.pointId"
                ))
        ));
        return dto;
    }

    private static EntityRespDTO instructionEntity() {
        EntityRespDTO dto = new EntityRespDTO();
        dto.setCustomFields(Map.of(
                "opcode", 200102,
                "command_json", Map.of(
                        "outbound", Map.of(
                                "opcode", 200102,
                                "request", Map.of("pointId", "")
                        )
                )
        ));
        return dto;
    }
}
