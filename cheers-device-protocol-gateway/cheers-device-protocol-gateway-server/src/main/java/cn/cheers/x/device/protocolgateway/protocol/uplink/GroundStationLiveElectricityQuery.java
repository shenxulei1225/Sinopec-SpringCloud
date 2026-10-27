package cn.cheers.x.device.protocolgateway.protocol.uplink;

import cn.cheers.x.device.protocolgateway.api.dto.GroundStationLiveStatusDTO;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.service.DeviceProtocolDownlinkService;
import cn.cheers.x.device.protocolgateway.service.SyncDownlinkResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 读这台地面站设备的当前电量。
 * <p>管什么：内存里已经有最新一帧就用这一帧。还没有时，向设备要一次「获取设备当前实时状态」，等答卷后再读。
 * <p>不管什么：不读巡检账，不把电量换成剩余可飞秒数，不画线。
 * <p>禁止：没报到时写成 100。设备没连上仍假装有电量。
 */
@Service
public class GroundStationLiveElectricityQuery {

    private final GroundStationLiveStatusStore store;
    private final EnvelopeJsonCodec envelopeJsonCodec;
    private final DeviceProtocolDownlinkService downlinkService;

    public GroundStationLiveElectricityQuery(
            GroundStationLiveStatusStore store,
            EnvelopeJsonCodec envelopeJsonCodec,
            DeviceProtocolDownlinkService downlinkService) {
        this.store = store;
        this.envelopeJsonCodec = envelopeJsonCodec;
        this.downlinkService = downlinkService;
    }

    /**
     * 读到电量就返回。设备没连上、答卷没有电量、或还没报过，电量空着。
     */
    public GroundStationLiveStatusDTO read(String deviceId) {
        String id = deviceId == null ? "" : deviceId.trim();
        GroundStationLiveStatusStore.Frame cached = store.latest(id);
        if (hasElectricity(cached)) {
            return toDto(cached);
        }
        if (!StringUtils.hasText(id)) {
            return empty();
        }
        askCurrentStatus(id);
        GroundStationLiveStatusStore.Frame after = store.latest(id);
        return hasElectricity(after) ? toDto(after) : empty();
    }

    private void askCurrentStatus(String deviceId) {
        String msgId = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> packet = new LinkedHashMap<>();
        packet.put("opcode", TransportOpcode.REAL_TIME_STATUS.code());
        packet.put("msgId", msgId);
        packet.put("deviceId", deviceId);
        SyncDownlinkResult result = downlinkService.sendSyncCommand(
                deviceId,
                envelopeJsonCodec.encodeObject(packet),
                TransportOpcode.REAL_TIME_STATUS.code(),
                msgId);
        if (result != null && StringUtils.hasText(result.replyJson())) {
            store.remember(deviceId, TransportOpcode.REAL_TIME_STATUS.code(), result.replyJson());
        }
    }

    private static boolean hasElectricity(GroundStationLiveStatusStore.Frame frame) {
        return frame != null && StringUtils.hasText(frame.electricity());
    }

    private static GroundStationLiveStatusDTO toDto(GroundStationLiveStatusStore.Frame frame) {
        return new GroundStationLiveStatusDTO(frame.electricity(), frame.receivedAtEpochMs());
    }

    private static GroundStationLiveStatusDTO empty() {
        return new GroundStationLiveStatusDTO(null, null);
    }

}
