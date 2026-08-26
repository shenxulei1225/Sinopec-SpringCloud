package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 型号设施覆盖范围响应。
 *
 * <p>设施数量只统计该型号实体行上的 {@code facility_id}；不读取型号的发起设施字段。</p>
 */
@Schema(description = "管理后台 - 型号设施覆盖范围响应")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelFacilityFootprintRespVO {

    @Schema(description = "型号编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long modelId;

    @Schema(description = "实体所属设施去重数", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Long facilityCount;

    @Schema(description = "覆盖分类", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"NONE", "SINGLE_FACILITY", "MULTI_FACILITY"})
    private String classification;
}
