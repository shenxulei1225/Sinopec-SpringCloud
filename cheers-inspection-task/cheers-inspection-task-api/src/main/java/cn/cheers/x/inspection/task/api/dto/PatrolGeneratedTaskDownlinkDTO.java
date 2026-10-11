package cn.cheers.x.inspection.task.api.dto;

import lombok.Data;

import java.util.List;

/**
 * 协议监控下发要用的那一次已生成任务。
 * <p>执行记录编号写进报文的任务编号和模板编号。动作清单的顺序就是包内序号。
 * <p>不负责：把上报写成完成。
 */
@Data
public class PatrolGeneratedTaskDownlinkDTO {

    private Long executionRecordId;

    private String recordName;

    private String protocolVersion;

    private String logicalDeviceId;

    private Long executorEquipmentId;

    private String mediaStreamUrl;

    /** 计划执行时刻，秒。启动指令的执行时间用这一格。 */
    private Long executionTimeEpochSeconds;

    private List<PatrolGeneratedTaskActionDTO> actions;
}
