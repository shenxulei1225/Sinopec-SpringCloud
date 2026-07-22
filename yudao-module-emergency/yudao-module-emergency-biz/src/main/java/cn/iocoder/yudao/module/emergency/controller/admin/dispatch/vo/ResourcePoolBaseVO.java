package cn.iocoder.yudao.module.emergency.controller.admin.dispatch.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 资源池 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class ResourcePoolBaseVO {

    @Schema(description = "资源名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "消防车001")
    @NotEmpty(message = "资源名称不能为空")
    private String name;

    @Schema(description = "资源类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "vehicle")
    @NotEmpty(message = "资源类型不能为空")
    private String type;

    @Schema(description = "描述", example = "一辆消防救援车")
    private String description;

    @Schema(description = "位置信息 JSON", example = "{\"longitude\": 120.123, \"latitude\": 30.456}")
    private String location;

    @Schema(description = "联系信息 JSON", example = "{\"phone\": \"13800138000\", \"email\": \"contact@example.com\"}")
    private String contactInfo;

    @Schema(description = "容量/数量", example = "5")
    @NotNull(message = "容量不能为空")
    private Integer capacity;

    @Schema(description = "单位", example = "人")
    private String unit;

    @Schema(description = "所属组织ID", example = "1")
    private Long organizationId;
}



