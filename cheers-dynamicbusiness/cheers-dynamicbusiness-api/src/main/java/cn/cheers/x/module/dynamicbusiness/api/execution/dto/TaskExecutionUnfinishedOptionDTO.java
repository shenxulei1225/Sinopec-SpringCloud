package cn.cheers.x.module.dynamicbusiness.api.execution.dto;

import lombok.Data;

/**
 * 协议监控任务下拉里的一条还没结束的执行账。
 * <p>只带名称、状态、计划时刻、生成时写下的收流地址、指令包上的模板编号。
 * <p>不带指令包正文。选定后再按执行记录编号读整本账。
 */
@Data
public class TaskExecutionUnfinishedOptionDTO {

    private Long executionRecordId;

    private String recordName;

    /** pending 待执行，in_progress 执行中。 */
    private String executionStatus;

    /** 计划时刻原文。没有时为空。 */
    private String plannedStart;

    /** 生成任务时写入的收流地址。没有时为空。 */
    private String mediaStreamUrl;

    /** 指令包上的模板编号。没有时为空。 */
    private String commandTemplateId;
}
