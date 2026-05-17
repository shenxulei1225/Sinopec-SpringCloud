package cn.iocoder.yudao.module.alarm.controller.admin.vo.alarm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 人工上报告警 Request VO
 */
@Schema(description = "管理后台 - 人工上报告警 Request VO")
@Data
public class AlarmReportReqVO {

    @Schema(description = "告警类型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "告警类型不能为空")
    private Long alarmTypeId;

    @Schema(description = "告警级别", requiredMode = Schema.RequiredMode.REQUIRED, example = "WARNING")
    @NotBlank(message = "告警级别不能为空")
    private String alarmLevel;

    @Schema(description = "告警内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "发现设备异常，需要检修")
    @NotBlank(message = "告警内容不能为空")
    @Size(min = 10, max = 500, message = "告警内容长度必须在10-500字之间")
    private String alarmContent;

    @Schema(description = "关联设备ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "关联设备不能为空")
    private Long deviceId;

    @Schema(description = "位置ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "位置信息不能为空")
    private Long locationId;

    @Schema(description = "附件URL列表", example = "[\"http://xxx/1.jpg\", \"http://xxx/2.jpg\"]")
    @Size(max = 5, message = "附件最多5个")
    private List<AttachmentVO> attachments;

    /**
     * 附件信息
     */
    @Data
    @Schema(description = "附件信息")
    public static class AttachmentVO {

        @Schema(description = "文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "现场照片.jpg")
        @NotBlank(message = "文件名不能为空")
        private String fileName;

        @Schema(description = "文件路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "/upload/alarm/2026/01/04/xxx.jpg")
        @NotBlank(message = "文件路径不能为空")
        private String filePath;

        @Schema(description = "文件类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "IMAGE")
        @NotBlank(message = "文件类型不能为空")
        private String fileType;

        @Schema(description = "文件大小（字节）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024000")
        @NotNull(message = "文件大小不能为空")
        private Long fileSize;

    }

}
