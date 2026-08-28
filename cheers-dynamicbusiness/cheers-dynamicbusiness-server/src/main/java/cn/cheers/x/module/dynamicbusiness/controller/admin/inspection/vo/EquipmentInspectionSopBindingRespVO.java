package cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "设备检查绑定")
@Data
public class EquipmentInspectionSopBindingRespVO {

    private Long id;
    private Long equipmentId;
    private Long inspectionItemId;
    private String executionMeans;
    private Long sopInstanceId;
}
