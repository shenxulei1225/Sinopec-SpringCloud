package cn.cheers.x.device.protocolgateway.service;

import cn.cheers.x.device.protocolgateway.api.message.CommandSendEnvelope;

import java.util.List;

/**
 * 一次任务下发要按顺序写出的报文。
 * <p>对照选中的指令空包里有 request 时，收进巡检指令包；没有 request 时，按该指令自己发出。
 * <p>不负责按动作名称决定发哪条指令。
 * <p>notices 是生成时字段说明里不要求必填、这次没有值的项。必填缺失不会出现在这里，那种情况不产生下发。
 */
public record MissionDownlink(
        List<DownlinkPiece> pieces,
        String waylineFileId,
        String waylineFingerprint,
        List<String> notices
) {

    public MissionDownlink(List<DownlinkPiece> pieces) {
        this(pieces, null, null, List.of());
    }

    public MissionDownlink(List<DownlinkPiece> pieces, String waylineFileId, String waylineFingerprint) {
        this(pieces, waylineFileId, waylineFingerprint, List.of());
    }

    public MissionDownlink {
        pieces = pieces == null ? List.of() : List.copyOf(pieces);
        waylineFileId = waylineFileId == null || waylineFileId.isBlank() ? null : waylineFileId.trim();
        waylineFingerprint = waylineFingerprint == null || waylineFingerprint.isBlank() ? null : waylineFingerprint.trim();
        notices = notices == null ? List.of() : List.copyOf(notices);
    }

    /**
     * 一段下发。整包和单独指令二选一。
     */
    public record DownlinkPiece(DirectCommand direct, CommandSendEnvelope commandPackage) {

        public static DownlinkPiece direct(DirectCommand command) {
            return new DownlinkPiece(command, null);
        }

        public static DownlinkPiece commandPackage(CommandSendEnvelope envelope) {
            return new DownlinkPiece(null, envelope);
        }
    }

    /**
     * 对照选中的、自己就是一条完整指令的报文。
     * <p>mqttTopic 有值时，这是上云服务报文，不走地面站操作码。
     */
    public record DirectCommand(int opcode, String msgId, String wireJson, String mqttTopic) {

        public DirectCommand(int opcode, String msgId, String wireJson) {
            this(opcode, msgId, wireJson, null);
        }

        public boolean cloudService() {
            return mqttTopic != null && !mqttTopic.isBlank();
        }
    }
}
