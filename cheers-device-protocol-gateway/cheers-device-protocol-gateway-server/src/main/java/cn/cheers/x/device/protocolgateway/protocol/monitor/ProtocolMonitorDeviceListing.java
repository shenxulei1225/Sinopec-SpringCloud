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

/**
 * 监控页设备名单。名称来自巡检设备台账。
 * <p>负责：给已连接的设备补名称；把已登记大疆协议的设备放进名单，即使用户还没从地面站连上。
 * <p>不负责：地面站收发；把大疆设备改接到 WebSocket。
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
        for (NamedEquipment known : equipment.equipmentWithProtocol()) {
            if (!ProtocolCodes.isDjiCloud(known.protocol()) || byId.containsKey(known.deviceId())) {
                continue;
            }
            byId.put(known.deviceId(), new ProtocolMonitorDeviceDTO(
                    known.deviceId(),
                    cloudOnline.isOnline(known.deviceId()),
                    null,
                    known.name(),
                    known.protocol()
            ));
        }
        List<ProtocolMonitorDeviceDTO> rows = new ArrayList<>(byId.values());
        rows.sort(Comparator
                .comparing((ProtocolMonitorDeviceDTO row) -> row.name() == null || row.name().isBlank())
                .thenComparing(row -> row.name() == null ? "" : row.name())
                .thenComparing(ProtocolMonitorDeviceDTO::deviceId));
        return List.copyOf(rows);
    }
}
