package cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 一条已经记入异常记录的分析告警。照片只给地址，不带文件内容。
 */
@Data
public class AnalysisAlarmItemRespVO {

    private Long id;

    @Schema(description = "分析能力名称。旧记录没写下时为空")
    private String capabilityName;

    @Schema(description = "这次告警的说明")
    private String note;

    @Schema(description = "任务里已有照片的地址")
    private String photoUrl;

    @Schema(description = "检测框。没有则为空")
    private Object detections;

    private Long facilityId;

    private String facilityName;

    private Long taskId;

    private String taskName;

    private Long executionRecordId;

    private String stepCode;

    private String stepTitle;

    @Schema(description = "记入异常记录的时间")
    private String occurredAt;
}
