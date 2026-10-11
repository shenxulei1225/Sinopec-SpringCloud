package cn.cheers.x.device.protocolgateway.djicloud;

import cn.cheers.x.device.protocolgateway.api.dto.AircraftFlightStatusDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 按网关取它下面唯一一架飞机的最新飞行状态。
 * <p>网关编号来自调用方。本类不读巡检账，也不在多架飞机里挑第一架。
 */
@Service
public class DjiCloudAircraftStatusService {

    private final DjiCloudOnlineRegistry onlineRegistry;
    private final DjiCloudAircraftStatusRegistry statusRegistry;

    public DjiCloudAircraftStatusService(
            DjiCloudOnlineRegistry onlineRegistry,
            DjiCloudAircraftStatusRegistry statusRegistry) {
        this.onlineRegistry = onlineRegistry;
        this.statusRegistry = statusRegistry;
    }

    public AircraftFlightStatusDTO forGateway(String gatewaySn) {
        if (gatewaySn == null || gatewaySn.isBlank()) {
            return AircraftFlightStatusDTO.gap("没有网关编号，不能看飞机状态", null);
        }
        List<String> aircraftSns = onlineRegistry.aircraftSns(gatewaySn);
        if (aircraftSns.isEmpty()) {
            return AircraftFlightStatusDTO.gap("这台网关还没有报上挂着的飞机", null);
        }
        if (aircraftSns.size() > 1) {
            return AircraftFlightStatusDTO.gap("这台网关下有多架飞机，没有写明看哪一架", null);
        }
        String aircraftSn = aircraftSns.get(0);
        AircraftFlightStatusDTO found = statusRegistry.get(aircraftSn);
        if (found == null) {
            return AircraftFlightStatusDTO.gap("还没有这架飞机的状态上报", aircraftSn);
        }
        return found;
    }
}
