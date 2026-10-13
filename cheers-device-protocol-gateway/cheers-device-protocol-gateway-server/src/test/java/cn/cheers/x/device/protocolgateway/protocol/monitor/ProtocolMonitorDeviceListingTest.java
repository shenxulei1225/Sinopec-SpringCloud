package cn.cheers.x.device.protocolgateway.protocol.monitor;

import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorDeviceDTO;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.device.protocolgateway.datacollection.EntityRpcDeviceProtocolVersionCatalog.NamedEquipment;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProtocolMonitorDeviceListingTest {

    @Test
    void registeredDongguAndRobotAppearBeforeConnect() {
        Map<String, ProtocolMonitorDeviceDTO> byId = new LinkedHashMap<>();
        ProtocolMonitorDeviceListing.appendRegisteredNotYetSeen(byId, List.of(
                new NamedEquipment("uav-large", "1#东古大型无人机", ProtocolCodes.UAV_WS),
                new NamedEquipment("uav-small", "1#东古小型无人机", ProtocolCodes.UAV_WS),
                new NamedEquipment("robot-large", "大型机器人", ProtocolCodes.ROBOT_WS),
                new NamedEquipment("robot-small", "小型机器人", ProtocolCodes.ROBOT_WS),
                new NamedEquipment("dock-1", "金桥大疆机场无人机", ProtocolCodes.DJI_DOCK),
                new NamedEquipment("unknown-1", "未登记协议的设备", "vendor-x")
        ), deviceId -> "dock-1".equals(deviceId));

        assertEquals(5, byId.size());
        assertFalse(byId.get("uav-large").online());
        assertEquals("1#东古小型无人机", byId.get("uav-small").name());
        assertEquals(ProtocolCodes.ROBOT_WS, byId.get("robot-large").protocol());
        assertFalse(byId.get("robot-small").online());
        assertTrue(byId.get("dock-1").online());
        assertFalse(byId.containsKey("unknown-1"));
    }

    @Test
    void alreadySeenDeviceIsNotDuplicated() {
        Map<String, ProtocolMonitorDeviceDTO> byId = new LinkedHashMap<>();
        byId.put("uav-large", new ProtocolMonitorDeviceDTO(
                "uav-large", true, 10L, "1#东古大型无人机", ProtocolCodes.UAV_WS));
        ProtocolMonitorDeviceListing.appendRegisteredNotYetSeen(byId, List.of(
                new NamedEquipment("uav-large", "1#东古大型无人机", ProtocolCodes.UAV_WS)
        ), deviceId -> false);

        assertEquals(1, byId.size());
        assertTrue(byId.get("uav-large").online());
        assertEquals(10L, byId.get("uav-large").lastEventAt());
    }
}
