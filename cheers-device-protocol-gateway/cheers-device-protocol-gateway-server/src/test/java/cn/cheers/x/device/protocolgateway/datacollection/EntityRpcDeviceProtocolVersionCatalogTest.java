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
    void firstVersion_readsEquipmentCode() {
        EntityRpcDeviceProtocolVersionCatalog catalog = catalogWith(equipment(
                "SN-001", "EQ-1", List.of("robot-ws", "uav-ws")));
        assertEquals("robot-ws", catalog.firstVersion("EQ-1").orElseThrow());
        assertTrue(catalog.firstVersion("SN-001").isEmpty());
    }

    @Test
    void missingDevice_isEmpty() {
        EntityRpcDeviceProtocolVersionCatalog catalog = catalogWith(equipment(
                "SN-001", "EQ-1", List.of("robot-ws")));
        assertTrue(catalog.firstVersion("EQ-999").isEmpty());
    }

    @Test
    void emptyVersions_isEmpty() {
        EntityRpcDeviceProtocolVersionCatalog catalog = catalogWith(equipment(
                "SN-001", "EQ-1", List.of()));
        assertTrue(catalog.firstVersion("EQ-1").isEmpty());
    }

    @Test
    void registeredProtocol_usesDefaultNotSupportList() {
        EntityRespDTO row = equipment("SN-001", "EQ-1", List.of("robot-ws"));
        row.setCustomFields(Map.of("platform_protocol", "uav-ws"));
        EntityRpcDeviceProtocolVersionCatalog catalog = catalogWith(row);
        assertEquals("uav-ws", catalog.registrationOf("EQ-1").protocol());
        assertEquals("robot-ws", catalog.firstVersion("EQ-1").orElseThrow());
    }

    @Test
    void missingDefaultProtocol_explains() {
        EntityRpcDeviceProtocolVersionCatalog catalog = catalogWith(equipment(
                "SN-001", "EQ-1", List.of("robot-ws")));
        assertEquals("这台设备没有登记默认使用的协议", catalog.registrationOf("EQ-1").reason());
    }

    @Test
    void duplicateEquipmentCode_isEmpty() {
        EntityRpcDeviceProtocolVersionCatalog catalog = catalogWith(
                equipment("SN-001", "EQ-1", List.of("robot-ws")),
                equipment("SN-002", "EQ-1", List.of("uav-ws"))
        );
        assertTrue(catalog.firstVersion("EQ-1").isEmpty());
    }

    @Test
    void monitorId_isEquipmentCode() {
        EntityRespDTO row = equipment("SN-001", "equipment-abc", List.of("uav-ws"));
        row.setCustomFields(Map.of("platform_protocol", "uav-ws", "name", "1#东古大型无人机"));
        EntityRpcDeviceProtocolVersionCatalog catalog = catalogWith(row);
        assertEquals("equipment-abc", catalog.equipmentWithProtocol().get(0).deviceId());
        assertEquals("1#东古大型无人机", catalog.findEquipment("equipment-abc").orElseThrow().name());
        assertTrue(catalog.findEquipment("SN-001").isEmpty());
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
