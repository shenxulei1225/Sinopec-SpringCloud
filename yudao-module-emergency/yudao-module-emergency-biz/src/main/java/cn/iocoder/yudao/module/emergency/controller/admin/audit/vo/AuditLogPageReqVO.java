package cn.iocoder.yudao.module.emergency.controller.admin.audit.vo;

import cn.cheers.x.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.cheers.x.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 审计日志分页查询请求VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AuditLogPageReqVO extends PageParam {

    /**
     * 事件ID
     */
    private Long eventId;

    /**
     * 响应ID
     */
    private Long responseId;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 操作模块
     */
    private String operationModule;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime endTime;
}



