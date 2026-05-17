package cn.iocoder.yudao.module.inspection.task.service.query.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 编排批次视图。
 */
@Data
public class InspectionTaskSchedulePlanView {

    private Long id;

    private String planCode;

    private Long taskId;

    private LocalDate horizonStartDate;

    private LocalDate horizonEndDate;

    private Integer scheduleCount;

    private Integer planStatus;

    private Integer triggerType;

    private String triggerBy;

    private String remark;

    private LocalDateTime createTime;
}
