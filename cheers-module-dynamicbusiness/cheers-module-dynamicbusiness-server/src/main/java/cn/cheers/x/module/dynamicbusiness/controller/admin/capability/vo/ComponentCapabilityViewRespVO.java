package cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "按组件类型投影后的能力视图（非完整 registry 契约）")
public class ComponentCapabilityViewRespVO {

    @Schema(description = "实例键", example = "dynamic-model:equipment")
    private String instanceKey;

    @Schema(description = "与 instanceKey 相同，兼容前端 queryContractKey")
    private String queryContractKey;

    @Schema(description = "组件编码：list / tree / table / card")
    private String componentCode;

    @Schema(description = "展示名称")
    private String label;

    @Schema(description = "能力域：system / dynamic-model / dynamic-entity")
    private String domain;

    @Schema(description = "契约版本")
    private Integer version;

    @Schema(description = "读能力")
    private ComponentCapabilityReadRespVO read;

    @Schema(description = "写能力（list/table 有；tree 通常为空）")
    private ComponentCapabilityWriteRespVO write;
}
