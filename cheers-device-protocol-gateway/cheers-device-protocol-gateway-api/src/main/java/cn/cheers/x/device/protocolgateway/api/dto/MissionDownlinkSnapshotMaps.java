package cn.cheers.x.device.protocolgateway.api.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 待执行快照里存已翻译下行的 Map 结构（与网关 {@code MissionDownlinkSnapshotCodec} 字段一致）。
 */
public final class MissionDownlinkSnapshotMaps {

    private MissionDownlinkSnapshotMaps() {
    }

    public static List<Map<String, Object>> toMaps(List<MissionDownlinkPieceDTO> pieces) {
        List<Map<String, Object>> maps = new ArrayList<>();
        if (pieces == null) {
            return maps;
        }
        for (MissionDownlinkPieceDTO piece : pieces) {
            if (piece == null) {
                continue;
            }
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("kind", piece.kind());
            map.put("opcode", piece.opcode());
            map.put("msgId", piece.msgId());
            map.put("wireJson", piece.wireJson());
            map.put("mqttTopic", piece.mqttTopic());
            map.put("deviceId", piece.deviceId());
            map.put("templateId", piece.templateId());
            map.put("packagesJson", piece.packagesJson());
            maps.add(map);
        }
        return maps;
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> readPieces(Object standardSnapshot) {
        if (!(standardSnapshot instanceof Map<?, ?> snapshot)) {
            return List.of();
        }
        Object raw = snapshot.get(PatrolOpenRunSnapshotKeys.DISPATCH_DOWNLINK_PIECES);
        if (!(raw instanceof List<?> list) || list.isEmpty()) {
            return List.of();
        }
        List<Map<String, Object>> maps = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Map<?, ?> map) {
                maps.add(new LinkedHashMap<>((Map<String, Object>) map));
            }
        }
        return maps;
    }
}
