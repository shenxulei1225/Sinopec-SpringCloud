package cn.iocoder.yudao.module.emergency.controller.admin.command.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 管理后台 - 应急指令创建 Request VO
 */
@Schema(description = "管理后台 - 应急指令创建 Request VO")
@Data
public class EmergencyCommandCreateReqVO {

    @Schema(description = "指令编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "CMD20241218001")
    @NotEmpty(message = "指令编号不能为空")
    private String commandNo;

    @Schema(description = "指令标题", example = "紧急撤离指令")
    private String title;

    @Schema(description = "指令内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "请立即组织人员撤离现场")
    @NotEmpty(message = "指令内容不能为空")
    private String content;

    @Schema(description = "指令类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "evacuation")
    @NotEmpty(message = "指令类型不能为空")
    private String commandType;

    @Schema(description = "优先级", example = "HIGH")
    private String priority;

    @Schema(description = "截止时间", example = "2024-12-18 10:00:00")
    private LocalDateTime deadline;

    @Schema(description = "执行阶段", example = "response")
    private String stage;

    @Schema(description = "关联事件ID。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", example = "1")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long eventId;

    @Schema(description = "关联响应ID。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", example = "1")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long responseId;

    @Schema(description = "模板ID。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", example = "1")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long templateId;

    @Schema(description = "附件信息", example = "[{\"name\":\"现场照片.jpg\",\"url\":\"http://example.com/photo.jpg\"}]")
    private List<Map<String, Object>> attachments;

    @Schema(description = "发布信息", example = "{\"issuedAt\":\"2024-12-18T09:00:00\",\"publisher\":\"张三\"}")
    private Map<String, Object> issueInfo;

    @Schema(description = "执行信息", example = "{\"status\":\"pending\"}")
    private Map<String, Object> executionInfo;

    @Schema(description = "关联自定义配置表单ID（可选，仅创建时设置，更新时不允许修改）- 用于用户使用自定义表单上报数据。当设置时必须验证表单配置ID的有效性（存在且未删除）。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", example = "1024")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long formId;
}
