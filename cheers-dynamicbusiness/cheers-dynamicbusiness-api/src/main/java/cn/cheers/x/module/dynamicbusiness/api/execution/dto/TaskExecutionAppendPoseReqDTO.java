package cn.cheers.x.module.dynamicbusiness.api.execution.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 往这次执行另存一个回放位置点。不写入过程列表。
 * <p>x、y、z 的含义由 frameRef 决定。调用方必须已经选定这次执行。
 */
@Data
public class TaskExecutionAppendPoseReqDTO {

    @NotNull
    private Long executionRecordId;

    /** 账记在哪个目录；巡检传 task_record_patrol */
    private String entityTypeCode;

    @NotNull
    private Long t;

    @NotNull
    private Double x;

    @NotNull
    private Double y;

    @NotNull
    private Double z;

    private Double yaw;

    private String frameRef;
}
