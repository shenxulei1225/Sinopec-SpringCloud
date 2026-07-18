package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 业务实体导出请求 VO
 */
@Schema(description = "管理后台 - 业务实体导出请求 VO")
@Data
public class EntityExportReqVO {

    @Schema(description = "业务类型编码（必填）", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @jakarta.validation.constraints.NotBlank(message = "业务类型编码不能为空")
    private String entityTypeCode;

    @Schema(description = "模型ID", example = "1")
    private Long modelId;

    @Schema(description = "状态（0-禁用，1-启用）", example = "1")
    private Integer status;

    @Schema(description = "关键词（模糊匹配实体名称）", example = "设备")
    private String keyword;

    @Schema(description = "导出格式（excel/csv/json）", example = "excel")
    private String format;
}
