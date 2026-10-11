package cn.cheers.x.device.protocolgateway.service;

import cn.cheers.x.device.protocolgateway.api.dto.MissionDownlinkPieceDTO;
import cn.cheers.x.device.protocolgateway.api.dto.MissionDownlinkSnapshotMaps;
import cn.cheers.x.device.protocolgateway.api.dto.MissionTranslateCommitRespDTO;
import cn.cheers.x.device.protocolgateway.api.dto.MissionTranslatedDispatchReqDTO;
import cn.cheers.x.device.protocolgateway.api.message.CommandSendEnvelope;
import cn.cheers.x.device.protocolgateway.service.MissionDownlink.DirectCommand;
import cn.cheers.x.device.protocolgateway.service.MissionDownlink.DownlinkPiece;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 已翻译下行与待执行快照之间的结构转换。
 */
public final class MissionDownlinkSnapshotCodec {

    public static final String KIND_DIRECT = "direct";
    public static final String KIND_PACKAGE = "package";

    private MissionDownlinkSnapshotCodec() {
    }

    public static MissionTranslateCommitRespDTO toCommitResp(MissionDownlink downlink) {
        return new MissionTranslateCommitRespDTO(
                toPieceDtos(downlink),
                downlink.waylineFileId(),
                downlink.waylineFingerprint());
    }

    public static MissionDownlink fromDispatchReq(MissionTranslatedDispatchReqDTO req) {
        return fromPieceDtos(req.pieces(), req.waylineFileId(), req.waylineFingerprint());
    }

    public static MissionDownlink fromPieceMaps(
            List<Map<String, Object>> raw,
            String waylineFileId,
            String waylineFingerprint
    ) {
        return fromPieceDtos(fromMaps(raw), waylineFileId, waylineFingerprint);
    }

    public static List<Map<String, Object>> toMaps(List<MissionDownlinkPieceDTO> pieces) {
        return MissionDownlinkSnapshotMaps.toMaps(pieces);
    }

    private static List<MissionDownlinkPieceDTO> fromMaps(List<Map<String, Object>> raw) {
        List<MissionDownlinkPieceDTO> pieces = new ArrayList<>();
        if (raw == null) {
            return pieces;
        }
        for (Map<String, Object> map : raw) {
            if (map == null || map.isEmpty()) {
                continue;
            }
            Object opcode = map.get("opcode");
            pieces.add(new MissionDownlinkPieceDTO(
                    text(map.get("kind")),
                    opcode instanceof Number number ? number.intValue() : null,
                    text(map.get("msgId")),
                    text(map.get("wireJson")),
                    text(map.get("mqttTopic")),
                    text(map.get("deviceId")),
                    text(map.get("templateId")),
                    text(map.get("packagesJson"))));
        }
        return pieces;
    }

    private static List<MissionDownlinkPieceDTO> toPieceDtos(MissionDownlink downlink) {
        List<MissionDownlinkPieceDTO> dtos = new ArrayList<>();
        for (DownlinkPiece piece : downlink.pieces()) {
            if (piece.direct() != null) {
                DirectCommand direct = piece.direct();
                dtos.add(new MissionDownlinkPieceDTO(
                        KIND_DIRECT,
                        direct.opcode(),
                        direct.msgId(),
                        direct.wireJson(),
                        direct.mqttTopic(),
                        null,
                        null,
                        null));
            } else if (piece.commandPackage() != null) {
                CommandSendEnvelope envelope = piece.commandPackage();
                dtos.add(new MissionDownlinkPieceDTO(
                        KIND_PACKAGE,
                        null,
                        envelope.msgId(),
                        null,
                        null,
                        envelope.deviceId(),
                        envelope.templateId(),
                        envelope.packagesJson()));
            }
        }
        return dtos;
    }

    private static MissionDownlink fromPieceDtos(
            List<MissionDownlinkPieceDTO> dtos,
            String waylineFileId,
            String waylineFingerprint
    ) {
        List<DownlinkPiece> pieces = new ArrayList<>();
        if (dtos != null) {
            for (MissionDownlinkPieceDTO dto : dtos) {
                if (dto == null || dto.kind() == null) {
                    continue;
                }
                if (KIND_DIRECT.equalsIgnoreCase(dto.kind())) {
                    int opcode = dto.opcode() == null ? 0 : dto.opcode();
                    pieces.add(DownlinkPiece.direct(new DirectCommand(
                            opcode,
                            dto.msgId(),
                            dto.wireJson(),
                            dto.mqttTopic())));
                } else if (KIND_PACKAGE.equalsIgnoreCase(dto.kind())) {
                    pieces.add(DownlinkPiece.commandPackage(new CommandSendEnvelope(
                            dto.msgId(),
                            dto.deviceId(),
                            dto.templateId(),
                            dto.packagesJson())));
                }
            }
        }
        return new MissionDownlink(pieces, waylineFileId, waylineFingerprint);
    }

    private static String text(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
