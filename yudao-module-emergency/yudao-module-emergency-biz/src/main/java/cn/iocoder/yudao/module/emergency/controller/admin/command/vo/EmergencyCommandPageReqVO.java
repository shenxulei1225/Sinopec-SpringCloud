package cn.iocoder.yudao.module.emergency.controller.admin.command.vo;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 管理后台 - 应急指令分页 Request VO
 */
@Schema(description = "管理后台 - 应急指令分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmergencyCommandPageReqVO extends PageParam {

    @Schema(description = "指令编号", example = "CMD20241218001")
    private String commandNo;

    @Schema(description = "指令标题", example = "紧急撤离指令")
    private String title;

    @Schema(description = "指令类型", example = "evacuation")
    private String commandType;

    @Schema(description = "优先级", example = "HIGH")
    private String priority;

    @Schema(description = "指令状态", example = "issued")
    private String status;

    @Schema(description = "执行阶段", example = "response")
    private String stage;

    @Schema(description = "关联事件ID", example = "1")
    private Long eventId;

    @Schema(description = "关联响应ID", example = "1")
    private Long responseId;

    @Schema(description = "模板ID", example = "1")
    private Long templateId;

    @Schema(description = "截止时间开始", example = "2024-12-18 00:00:00")
    private LocalDateTime deadlineStart;

    @Schema(description = "截止时间结束", example = "2024-12-18 23:59:59")
    private LocalDateTime deadlineEnd;
}
