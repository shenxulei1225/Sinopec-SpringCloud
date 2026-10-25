package cn.cheers.x.module.dynamicbusiness.api.execution.dto;

import lombok.Data;

/**
 * 巡检图层侧栏的一行。只含名称、任务、计划时刻和执行状态。
 * <p>点中一条之后再读步骤。不带步骤树，也不带整份扩展字段。
 */
@Data
public class TaskExecutionDayAccountDTO {

    private Long id;

    private String name;

    private String taskId;

    private String plannedStart;

    private String executionStatus;
}
