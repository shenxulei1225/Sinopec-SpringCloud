package cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "实例能力摘要")
public class InstanceCapabilitySummaryRespVO {

    @Schema(description = "实例键", example = "dynamic-model:equipment")
    private String instanceKey;

    @Schema(description = "展示名称")
    private String label;

    @Schema(description = "能力域")
    private String domain;

    @Schema(description = "业务类型编码")
    private String businessTypeCode;

    @Schema(description = "模型 ID")
    private Long modelId;

    @Schema(description = "系统资源编码")
    private String resourceCode;

    @Schema(description = "契约版本")
    private Integer version;
}
