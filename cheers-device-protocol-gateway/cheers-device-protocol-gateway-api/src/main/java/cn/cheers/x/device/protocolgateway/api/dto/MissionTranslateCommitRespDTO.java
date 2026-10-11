package cn.cheers.x.device.protocolgateway.api.dto;

import java.util.List;

/**
 * 生成任务时正式翻译结果：航线已落盘（若适用），下行报文可写入待执行快照。
 */
public record MissionTranslateCommitRespDTO(
        List<MissionDownlinkPieceDTO> pieces,
        String waylineFileId,
        String waylineFingerprint
) {
}
