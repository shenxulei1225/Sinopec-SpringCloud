package cn.cheers.x.twin.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Twin 设备↔模型实例解绑请求")
@Data
public class TwinEquipmentUnbindReqVO {

    @Schema(description = "映射 ID")
    private Long mappingId;

    @Schema(description = "站场作用域（与 entityId 联用）")
    private Long facilityId;

    @Schema(description = "设备实体 ID")
    private Long entityId;

    @Schema(description = "实体类型编码，默认 equipment")
    private String entityTypeCode;

    @Schema(description = "ActorInstance ID")
    private Long actorInstanceId;

    @Schema(description = "备注")
    private String remark;
}
