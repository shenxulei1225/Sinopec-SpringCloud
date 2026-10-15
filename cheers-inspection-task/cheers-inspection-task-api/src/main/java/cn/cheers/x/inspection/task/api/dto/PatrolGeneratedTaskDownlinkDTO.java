package cn.cheers.x.inspection.task.api.dto;

import lombok.Data;

import java.util.List;

/**
 * 协议监控下发要用的那一次已生成任务。
 * <p>执行记录编号写进启动、终止的任务编号。巡检指令包用生成时记下的正文，不再按动作重译。
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

    /** 生成任务时已经翻译好的 packages。监控下发原样写出。 */
    private String commandPackagesJson;

    /** 生成时写在指令包信封上的模板编号。 */
    private String commandTemplateId;
}
