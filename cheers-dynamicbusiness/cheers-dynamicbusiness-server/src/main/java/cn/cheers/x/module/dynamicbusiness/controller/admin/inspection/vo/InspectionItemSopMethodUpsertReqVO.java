package cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "检查项方法 upsert")
@Data
public class InspectionItemSopMethodUpsertReqVO {

    @NotNull
    private Long inspectionItemId;

    @NotBlank
    @Schema(description = "执行手段 MANUAL/UAV/ROBOT/FIXED_CAMERA")
    private String executionMeans;

    @NotNull
    @Schema(description = "SOP 模板实体 id")
    private Long sopTemplateId;

    private Integer sort;
}
