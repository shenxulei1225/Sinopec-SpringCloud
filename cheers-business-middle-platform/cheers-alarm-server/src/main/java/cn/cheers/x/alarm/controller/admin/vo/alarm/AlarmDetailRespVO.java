package cn.cheers.x.alarm.controller.admin.vo.alarm;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理后台 - 告警详情 Response VO
 */
@Schema(description = "管理后台 - 告警详情 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AlarmDetailRespVO extends AlarmRespVO {

    @Schema(description = "触发的告警规则ID", example = "1")
    private Long ruleId;

    @Schema(description = "升级时间")
    private LocalDateTime escalationTime;

    @Schema(description = "确认时间")
    private LocalDateTime acknowledgeTime;

    @Schema(description = "确认人ID", example = "1")
    private Long acknowledgeUserId;

    @Schema(description = "确认人姓名", example = "张三")
    private String acknowledgeUserName;

    @Schema(description = "确认备注", example = "已确认告警")
    private String acknowledgeRemark;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "处理人ID", example = "1")
    private Long handleUserId;

    @Schema(description = "处理人姓名", example = "李四")
    private String handleUserName;

    @Schema(description = "处理措施", example = "已启动潜水泵排水")
    private String handleMeasure;

    @Schema(description = "处理结果", example = "RESOLVED")
    private String handleResult;

    @Schema(description = "关闭时间")
    private LocalDateTime closeTime;

    @Schema(description = "关闭人ID", example = "1")
    private Long closeUserId;

    @Schema(description = "关闭人姓名", example = "王五")
    private String closeUserName;

    @Schema(description = "关闭原因", example = "HANDLED")
    private String closeReason;

    @Schema(description = "关闭备注", example = "问题已解决")
    private String closeRemark;

    @Schema(description = "附件列表")
    private List<AlarmAttachmentVO> attachments;

    @Schema(description = "联动执行记录")
    private List<LinkageExecutionSimpleVO> linkageExecutions;

    /**
     * 告警附件 VO
     */
    @Data
    @Schema(description = "告警附件")
    public static class AlarmAttachmentVO {

        @Schema(description = "附件ID", example = "1")
        private Long id;

        @Schema(description = "文件名", example = "现场照片.jpg")
        private String fileName;

        @Schema(description = "文件路径", example = "/upload/alarm/2026/01/04/xxx.jpg")
        private String filePath;

        @Schema(description = "文件类型", example = "IMAGE")
        private String fileType;

        @Schema(description = "文件大小（字节）", example = "1024000")
        private Long fileSize;

    }

    /**
     * 联动执行简要信息 VO
     */
    @Data
    @Schema(description = "联动执行简要信息")
    public static class LinkageExecutionSimpleVO {

        @Schema(description = "执行记录ID", example = "1")
        private Long id;

        @Schema(description = "动作类型", example = "DEVICE_CONTROL")
        private String actionType;

        @Schema(description = "目标设备名称", example = "潜水泵-B区2号")
        private String targetDeviceName;

        @Schema(description = "执行状态", example = "SUCCESS")
        private String executionStatus;

        @Schema(description = "执行结果", example = "设备启动成功")
        private String executionResult;

        @Schema(description = "开始时间")
        private LocalDateTime startTime;

        @Schema(description = "结束时间")
        private LocalDateTime endTime;

    }

}
