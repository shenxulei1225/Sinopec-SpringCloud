package cn.iocoder.yudao.module.emergency.controller.admin.guarantee.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 应急保障创建 Request VO")
@Data
public class GuaranteeCreateReqVO {

    @Schema(description = "保障编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "GUA001")
    @NotBlank(message = "保障编号不能为空")
    private String guaranteeCode;

    @Schema(description = "保障名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "医疗救护保障")
    @NotBlank(message = "保障名称不能为空")
    private String guaranteeName;

    @Schema(description = "保障类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "medical")
    @NotBlank(message = "保障类型不能为空")
    private String guaranteeType;

    @Schema(description = "保障描述", example = "提供医疗救护服务")
    private String description;

    @Schema(description = "保障状态", example = "available")
    private String status;

    @Schema(description = "联系人", example = "张三")
    private String contactPerson;

    @Schema(description = "联系电话", example = "13800138000")
    private String contactPhone;

    @Schema(description = "保障位置", example = "XX医院")
    private String location;

    @Schema(description = "保障能力/容量", example = "可同时处理50名伤员")
    private String capacity;

    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled;
}



