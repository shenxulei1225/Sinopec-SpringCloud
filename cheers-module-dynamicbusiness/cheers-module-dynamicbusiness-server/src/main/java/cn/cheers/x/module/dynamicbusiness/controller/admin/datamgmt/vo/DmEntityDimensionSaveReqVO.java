package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Schema(description = "数据管理 - 批量保存实体浏览维度")
@Data
public class DmEntityDimensionSaveReqVO {

    @Schema(description = "数据类型编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "entityTypeCode 不能为空")
    private String entityTypeCode;

    @Schema(description = "维度行列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "dimensions 不能为空")
    @Valid
    private List<DmEntityDimensionSaveItemVO> dimensions;
}
