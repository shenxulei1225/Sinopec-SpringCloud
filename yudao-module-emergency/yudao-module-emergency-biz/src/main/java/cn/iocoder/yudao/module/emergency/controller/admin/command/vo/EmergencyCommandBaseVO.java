package cn.iocoder.yudao.module.emergency.controller.admin.command.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 应急指令基础 VO
 */
@Schema(description = "应急指令基础 VO")
@Data
public class EmergencyCommandBaseVO {

    @Schema(description = "指令编号", example = "1")
    private Long id;

    @Schema(description = "指令编号（唯一）", example = "CMD20241218001")
    private String commandNo;

    @Schema(description = "指令标题", example = "紧急撤离指令")
    private String title;

    @Schema(description = "指令内容", example = "请立即组织人员撤离现场")
    private String content;

    @Schema(description = "指令类型", example = "evacuation")
    private String commandType;

    @Schema(description = "优先级", example = "HIGH")
    private String priority;

    @Schema(description = "截止时间", example = "2024-12-18 10:00:00")
    private LocalDateTime deadline;

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

    @Schema(description = "附件信息", example = "[{\"name\":\"现场照片.jpg\",\"url\":\"http://example.com/photo.jpg\",\"type\":\"image\"}]")
    private List<Map<String, Object>> attachments;

    @Schema(description = "发布信息", example = "{\"issuedAt\":\"2024-12-18T09:00:00\",\"publisher\":\"张三\",\"publisherId\":1}")
    private Map<String, Object> issueInfo;

    @Schema(description = "执行信息", example = "{\"startedAt\":\"2024-12-18T09:30:00\",\"executor\":\"李四\",\"progress\":50}")
    private Map<String, Object> executionInfo;

    @Schema(description = "关联自定义配置表单ID（可选，仅创建时设置，更新时不允许修改）- 用于用户使用自定义表单上报数据", example = "1024")
    private Long formId;
}
