package cn.cheers.x.module.dynamicbusiness.api.execution.dto;

import lombok.Data;

/**
 * 往执行账记过程后的结果：这本账上现在有几条过程。
 */
@Data
public class TaskExecutionAppendProcessRespDTO {

    private Long executionRecordId;

    private int processEntryCount;
}
