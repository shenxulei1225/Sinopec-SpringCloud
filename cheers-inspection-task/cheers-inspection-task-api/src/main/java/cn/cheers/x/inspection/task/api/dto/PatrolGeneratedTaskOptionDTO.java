package cn.cheers.x.inspection.task.api.dto;

import lombok.Data;

/**
 * 协议监控里可选的一条已生成任务。
 * <p>只带选择时要看的名称、状态、计划时刻和已经分配的收流地址。
 * <p>不负责：动作清单和翻译后的指令包。选定后再按执行记录编号读取。
 */
@Data
public class PatrolGeneratedTaskOptionDTO {

    private Long executionRecordId;

    private String recordName;

    /** pending 待执行，in_progress 执行中。 */
    private String executionStatus;

    /** 计划时刻原文，例如 2026-10-31T18:00:00+08:00。没有时为空。 */
    private String plannedStart;

    /** 生成任务时写入的收流地址。没有时为空。 */
    private String mediaStreamUrl;
}
