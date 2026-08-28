package cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "设备检查绑定 upsert")
@Data
public class EquipmentInspectionSopUpsertReqVO {

    @NotNull
    private Long equipmentId;

    @NotNull
    private Long inspectionItemId;

    @NotBlank
    private String executionMeans;

    @NotNull
    @Schema(description = "SOP 实例 id（is_template=false）")
    private Long sopInstanceId;
}
