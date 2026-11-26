package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 应急事件列表和详情。编号用字符串，避免长整型在页面上丢精度。
 */
@Data
public class EmergencyCommandEventRespVO {

    @Schema(description = "应急事件编号")
    private String id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "告警编号")
    private String alertId;

    @Schema(description = "待研判、不成立、待启动、响应中")
    private String eventStatus;

    @Schema(description = "是否成立")
    private Boolean judgedReal;

    @Schema(description = "研判类型")
    private String eventType;

    @Schema(description = "研判级别")
    private String responseLevel;

    @Schema(description = "研判意见")
    private String judgeComment;

    @Schema(description = "提交研判的时间。还没研判时为空")
    private String assessTime;

    @Schema(description = "点触发应急事件的时间。还没触发时为空")
    private String triggerTime;

    @Schema(description = "记录创建时间")
    private String createTime;

    @Schema(description = "这起事件所属场站。接报时写入，决定谁能看到这起事件")
    private String facilityId;

    @Schema(description = "这起事件所属管线段。接报时写入，决定谁能看到这起事件")
    private String pipelineSegmentId;

    @Schema(description = "事件中心点经度。为空表示还没定位，看不了周边资源")
    private Double longitude;

    @Schema(description = "事件中心点纬度")
    private Double latitude;

    @Schema(description = "地点描述")
    private String locationText;

    @Schema(description = "事件发生时间")
    private String occurredAt;

    @Schema(description = "接报时写下的事故经过")
    private String intakeNote;

    @Schema(description = "接报照片地址")
    private List<String> intakePhotos;

    @Schema(description = "结案状态。处置中或已结案。空着表示还没结案")
    private String caseStatus;
}
