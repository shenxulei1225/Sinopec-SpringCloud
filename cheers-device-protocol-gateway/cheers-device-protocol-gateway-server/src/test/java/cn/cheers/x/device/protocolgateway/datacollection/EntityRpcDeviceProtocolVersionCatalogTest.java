package cn.cheers.x.device.protocolgateway.datacollection;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
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
 * 设备台账取第一种协议版本；对不上或未登记不得猜。
 */
class EntityRpcDeviceProtocolVersionCatalogTest {

    @Test
    void firstVersion_readsFirstRegistered() {
        EntityRpcDeviceProtocolVersionCatalog catalog = catalogWith(equipment(
                "SN-001", "EQ-1", List.of("robot-ws", "uav-ws")));
        assertEquals("robot-ws", catalog.firstVersion("SN-001").orElseThrow());
        assertEquals("robot-ws", catalog.firstVersion("EQ-1").orElseThrow());
    }

    @Test
    void missingDevice_isEmpty() {
        EntityRpcDeviceProtocolVersionCatalog catalog = catalogWith(equipment(
                "SN-001", "EQ-1", List.of("robot-ws")));
        assertTrue(catalog.firstVersion("SN-999").isEmpty());
    }

    @Test
    void emptyVersions_isEmpty() {
        EntityRpcDeviceProtocolVersionCatalog catalog = catalogWith(equipment(
                "SN-001", "EQ-1", List.of()));
        assertTrue(catalog.firstVersion("SN-001").isEmpty());
    }

    @Test
    void duplicateLogicalId_isEmpty() {
        EntityRpcDeviceProtocolVersionCatalog catalog = catalogWith(
                equipment("SN-001", "A", List.of("robot-ws")),
                equipment("SN-001", "B", List.of("uav-ws"))
        );
        assertTrue(catalog.firstVersion("SN-001").isEmpty());
    }

    private static EntityRpcDeviceProtocolVersionCatalog catalogWith(EntityRespDTO... entities) {
        EntityRpcApi api = mock(EntityRpcApi.class);
        when(api.listEntities(eq("equipment"), isNull()))
                .thenReturn(CommonResult.success(List.of(entities)));
        return new EntityRpcDeviceProtocolVersionCatalog(api);
    }

    private static EntityRespDTO equipment(String deviceCode, String code, List<String> versions) {
        EntityRespDTO dto = new EntityRespDTO();
        dto.setBaseFields(Map.of(
                "device_code", deviceCode,
                "code", code,
                "protocol_versions", versions
        ));
        return dto;
    }
}
