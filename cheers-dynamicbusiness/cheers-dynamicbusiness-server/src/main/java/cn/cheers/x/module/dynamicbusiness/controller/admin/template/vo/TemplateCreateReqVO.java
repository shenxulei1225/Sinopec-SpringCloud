package cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 字段模板创建请求 VO
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 字段模板创建请求")
@Data
public class TemplateCreateReqVO {

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "设备基础模板")
    @NotBlank(message = "模板名称不能为空")
    private String name;

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "业务类型编码不能为空")
    private String entityTypeCode;

    @Schema(description = "模板描述", example = "包含设备管理的基础字段")
    private String description;

    @Schema(description = "模板状态（1-启用，0-禁用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模板状态不能为空")
    private Integer status;

    @Schema(description = "是否为系统预设模板", example = "false")
    private Boolean isSystem;
}
