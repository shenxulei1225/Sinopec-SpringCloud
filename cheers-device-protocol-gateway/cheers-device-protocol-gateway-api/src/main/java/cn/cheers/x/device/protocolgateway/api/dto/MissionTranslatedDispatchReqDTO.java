package cn.cheers.x.device.protocolgateway.api.dto;

import java.util.List;

/**
 * 开跑下发：使用生成任务时已翻译并落盘的报文，不再 translate。
 */
public record MissionTranslatedDispatchReqDTO(
        String protocolVersion,
        String deviceId,
        String taskId,
        String templateId,
        List<MissionDownlinkPieceDTO> pieces,
        String waylineFileId,
        String waylineFingerprint
) {
}
