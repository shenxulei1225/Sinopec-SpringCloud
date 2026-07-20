package cn.cheers.x.module.platform.topology.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PathNetworkCreateReqVO {

    @NotNull
    @Schema(description = "设施编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long facilityId;

    @NotBlank
    @Schema(description = "路网名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String displayName;

    @Schema(description = "说明")
    private String description;

    @Schema(description = "适用设备类型：HUMAN / GROUND_ROBOT / UAV")
    private List<String> applicableEquipmentTypes;
}
