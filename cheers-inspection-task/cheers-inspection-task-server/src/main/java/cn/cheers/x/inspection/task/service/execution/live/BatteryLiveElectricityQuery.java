package cn.cheers.x.inspection.task.service.execution.live;

import cn.cheers.x.device.protocolgateway.api.DjiCloudAircraftStatusApi;
import cn.cheers.x.device.protocolgateway.api.GroundStationLiveStatusApi;
import cn.cheers.x.device.protocolgateway.api.dto.AircraftFlightStatusDTO;
import cn.cheers.x.device.protocolgateway.api.dto.GroundStationLiveStatusDTO;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.service.execution.openrun.DongguBatteryRemain;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 读设备刚报上的当前电量百分比，给预计线当起点。
 * <p>管什么：大疆机场和大疆 Pilot 先读飞机最新一帧电量百分比。这一帧只有剩余可飞秒数时，按最长飞行时间和耗电曲线反查百分比。
 * 地面站这一路，或大疆这一帧还没有电量时，读「获取设备当前实时状态」答卷里的电量。
 * <p>不管什么：不堆历史点，不写电量，不画线。
 * <p>禁止：没报到时写成 100。
 */
@Service
public class BatteryLiveElectricityQuery {

    private final GroundStationLiveStatusApi groundStationLiveStatusApi;
    private final DjiCloudAircraftStatusApi aircraftStatusApi;

    public BatteryLiveElectricityQuery(
            GroundStationLiveStatusApi groundStationLiveStatusApi,
            DjiCloudAircraftStatusApi aircraftStatusApi) {
        this.groundStationLiveStatusApi = groundStationLiveStatusApi;
        this.aircraftStatusApi = aircraftStatusApi;
    }

    /**
     * 读到当前电量就返回百分比字。还没报到、没有设备编号、或这一路不是东古/大疆，返回空。
     */
    public String currentPercent(ExecutionDeviceBinding binding, Integer fullBatterySeconds, Object dischargeCurve) {
        if (binding == null) {
            return null;
        }
        String protocol = text(binding.getProtocolCode());
        String deviceId = text(binding.getLogicalDeviceId());
        if (!StringUtils.hasText(deviceId)) {
            return null;
        }
        if (ProtocolCodes.isDjiCloud(protocol)) {
            String dji = fromDji(deviceId, fullBatterySeconds, dischargeCurve);
            if (StringUtils.hasText(dji)) {
                return dji;
            }
        }
        if (ProtocolCodes.ROBOT_WS.equals(protocol)
                || ProtocolCodes.UAV_WS.equals(protocol)
                || ProtocolCodes.isDjiCloud(protocol)) {
            return fromGroundStation(deviceId);
        }
        return null;
    }

    private String fromGroundStation(String deviceId) {
        CommonResult<GroundStationLiveStatusDTO> rpc = groundStationLiveStatusApi.latest(deviceId);
        String electricity = rpc == null || !rpc.isSuccess() || rpc.getData() == null
                ? null
                : rpc.getData().electricity();
        return StringUtils.hasText(electricity) ? electricity.trim() : null;
    }

    private String fromDji(String gatewaySn, Integer fullBatterySeconds, Object dischargeCurve) {
        CommonResult<AircraftFlightStatusDTO> rpc = aircraftStatusApi.status(gatewaySn);
        if (rpc == null || !rpc.isSuccess() || rpc.getData() == null || StringUtils.hasText(rpc.getData().gap())) {
            return null;
        }
        AircraftFlightStatusDTO frame = rpc.getData();
        if (frame.batteryPercent() != null) {
            return String.valueOf(frame.batteryPercent());
        }
        BigDecimal percent = DongguBatteryRemain.percentFromRemainSeconds(
                frame.remainFlightTimeSeconds(), fullBatterySeconds, dischargeCurve);
        return percent == null ? null : percent.stripTrailingZeros().toPlainString();
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

}
