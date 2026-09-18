package cn.cheers.x.device.protocolgateway.datacollection;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 说明书从协议目录读；同一操作码不同说明不猜。
 */
class EntityRpcProtocolInstructionCatalogTest {

    @Test
    void sameOpcodeSameSpec_returnsRows() {
        EntityRpcProtocolInstructionCatalog catalog = catalogWith(
                entity(500202, List.of(row("opcode", "integer", true))),
                entity(500202, List.of(row("opcode", "integer", true)))
        );
        CatalogLookup lookup = catalog.findMainPacket(500202, null);
        assertTrue(lookup.ok());
        assertEquals("opcode", lookup.rows().get(0).path());
    }

    @Test
    void sameOpcodeDifferentSpec_isAmbiguous() {
        EntityRpcProtocolInstructionCatalog catalog = catalogWith(
                entity(200501, List.of(row("opcode", "integer", true))),
                entity(200501, List.of(
                        row("opcode", "integer", true),
                        row("distance", "string", false)
                ))
        );
        CatalogLookup lookup = catalog.findMainPacket(200501, null);
        assertTrue(lookup.error().contains("协议版本"));
    }

    @Test
    void missingOpcode_exposesGap() {
        EntityRpcProtocolInstructionCatalog catalog = catalogWith(
                entity(500202, List.of(row("opcode", "integer", true)))
        );
        assertEquals("找不到该指令的字段说明", catalog.findMainPacket(500103, null).error());
    }

    @Test
    void sameOpcodeDifferentSpec_withDeviceVersion_picksThatBook() {
        EntityRpcProtocolInstructionCatalog catalog = catalogWith(
                entity(200501, List.of("robot-ws"), List.of(row("opcode", "integer", true))),
                entity(200501, List.of("uav-ws"), List.of(
                        row("opcode", "integer", true),
                        row("distance", "string", false)
                ))
        );
        CatalogLookup lookup = catalog.findMainPacket(200501, "uav-ws");
        assertTrue(lookup.ok());
        assertEquals(2, lookup.rows().size());
    }

    @Test
    void versionNotOnAnyBook_exposesGap() {
        EntityRpcProtocolInstructionCatalog catalog = catalogWith(
                entity(200501, List.of("robot-ws"), List.of(row("opcode", "integer", true)))
        );
        assertEquals("找不到该协议版本的字段说明", catalog.findMainPacket(200501, "uav-ws").error());
    }

    private static EntityRpcProtocolInstructionCatalog catalogWith(EntityRespDTO... entities) {
        EntityRpcApi api = mock(EntityRpcApi.class);
        when(api.listEntities(eq("data_protocol"), isNull()))
                .thenReturn(CommonResult.success(List.of(entities)));
        return new EntityRpcProtocolInstructionCatalog(api, new ObjectMapper());
    }

    private static EntityRespDTO entity(int opcode, List<Map<String, Object>> outbound) {
        return entity(opcode, List.of(), outbound);
    }

    private static EntityRespDTO entity(
            int opcode,
            List<String> protocolVersions,
            List<Map<String, Object>> outbound
    ) {
        EntityRespDTO dto = new EntityRespDTO();
        dto.setCustomFields(Map.of(
                "opcode", opcode,
                "protocol_version", protocolVersions,
                "field_description_json", Map.of("outbound", outbound)
        ));
        return dto;
    }

    private static Map<String, Object> row(String path, String type, boolean required) {
        return Map.of("path", path, "type", type, "label", path, "required", required);
    }
}
