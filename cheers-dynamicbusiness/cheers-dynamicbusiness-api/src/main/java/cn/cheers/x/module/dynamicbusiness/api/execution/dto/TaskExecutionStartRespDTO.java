package cn.cheers.x.module.dynamicbusiness.api.execution.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskExecutionStartRespDTO {

    private Long executionRecordId;

    private List<Long> stepIds;
}
