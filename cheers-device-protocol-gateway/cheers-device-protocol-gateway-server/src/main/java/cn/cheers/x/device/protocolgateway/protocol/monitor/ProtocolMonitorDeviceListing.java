package cn.cheers.x.device.protocolgateway.protocol.monitor;

import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorDeviceDTO;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.device.protocolgateway.datacollection.EntityRpcDeviceProtocolVersionCatalog;
import cn.cheers.x.device.protocolgateway.datacollection.EntityRpcDeviceProtocolVersionCatalog.NamedEquipment;
import cn.cheers.x.device.protocolgateway.djicloud.DjiCloudOnlineRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * 监控页设备名单。名称来自巡检设备台账。
 * <p>负责：给已连接的设备补名称；把已登记默认协议的设备放进下拉，不必先连上。
 * 东古无人机、机器人和大疆都走这一条。大疆是否在线看上云通道。东古和机器人是否在线看地面站会话。
 * <p>不负责：地面站收发；把大疆设备改接到 WebSocket；给没登记默认协议的设备猜一份协议。
 */
@Component
@RequiredArgsConstructor
public class ProtocolMonitorDeviceListing {

    private final ProtocolMonitorHub monitorHub;
    private final EntityRpcDeviceProtocolVersionCatalog equipment;
    private final DjiCloudOnlineRegistry cloudOnline;

    public List<ProtocolMonitorDeviceDTO> list() {
        Map<String, ProtocolMonitorDeviceDTO> byId = new LinkedHashMap<>();
        for (ProtocolMonitorDeviceDTO row : monitorHub.listDevices()) {
            Optional<NamedEquipment> known = equipment.findEquipment(row.deviceId());
            String name = known.map(NamedEquipment::name).orElse("");
            String protocol = known.map(NamedEquipment::protocol).orElse("");
            boolean online = row.online() || (ProtocolCodes.isDjiCloud(protocol) && cloudOnline.isOnline(row.deviceId()));
            byId.put(row.deviceId(), new ProtocolMonitorDeviceDTO(
                    row.deviceId(), online, row.lastEventAt(), name, protocol));
        }
        appendRegisteredNotYetSeen(byId, equipment.equipmentWithProtocol(), cloudOnline::isOnline);
        List<ProtocolMonitorDeviceDTO> rows = new ArrayList<>(byId.values());
        rows.sort(Comparator
                .comparing((ProtocolMonitorDeviceDTO row) -> row.name() == null || row.name().isBlank())
                .thenComparing(row -> row.name() == null ? "" : row.name())
                .thenComparing(ProtocolMonitorDeviceDTO::deviceId));
        return List.copyOf(rows);
    }

    /**
     * 台账里已登记默认协议、但这台还没在窗口里出现过的，也放进下拉。
     * 东古无人机和机器人没连上时标离线。大疆没开会话时改看上云是否在线。
     * 协议码不在已知四份里的不放。已经在窗口里的不重复放。
     */
    static void appendRegisteredNotYetSeen(
            Map<String, ProtocolMonitorDeviceDTO> byId,
            List<NamedEquipment> registered,
            Predicate<String> djiCloudOnline) {
        for (NamedEquipment known : registered) {
            if (!ProtocolCodes.isKnown(known.protocol()) || byId.containsKey(known.deviceId())) {
                continue;
            }
            boolean online = ProtocolCodes.isDjiCloud(known.protocol()) && djiCloudOnline.test(known.deviceId());
            byId.put(known.deviceId(), new ProtocolMonitorDeviceDTO(
                    known.deviceId(),
                    online,
                    null,
                    known.name(),
                    known.protocol()
            ));
        }
    }
}
