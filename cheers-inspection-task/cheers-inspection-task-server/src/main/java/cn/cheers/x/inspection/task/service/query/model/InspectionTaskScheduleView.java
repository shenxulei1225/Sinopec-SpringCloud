package cn.cheers.x.inspection.task.service.query.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务排期视图。
 */
@Data
public class InspectionTaskScheduleView {

    private Long id;

    private Long planId;

    private String planCode;

    private Long taskId;

    private String taskCode;

    private String taskName;

    private LocalDateTime scheduledTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;

    private Long executorId;

    private String executorName;

    private String remark;

    private LocalDateTime createTime;
}
