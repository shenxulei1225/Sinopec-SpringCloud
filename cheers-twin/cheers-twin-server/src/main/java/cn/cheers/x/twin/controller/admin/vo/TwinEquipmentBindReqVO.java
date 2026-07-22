package cn.cheers.x.twin.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - Twin 设备↔模型实例绑定请求")
@Data
public class TwinEquipmentBindReqVO {

    @Schema(description = "站场/设施作用域 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "facilityId 不能为空")
    private Long facilityId;

    @Schema(description = "动态业务设备实体 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "entityId 不能为空")
    private Long entityId;

    @Schema(description = "实体类型编码，默认 equipment")
    private String entityTypeCode;

    @Schema(description = "ActorInstance ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "actorInstanceId 不能为空")
    private Long actorInstanceId;

    @Schema(description = "场景 ID")
    private Long sceneId;

    @Schema(description = "场景编码")
    private String sceneCode;

    @Schema(description = "绑定来源，例如 SCENE_COMPOSITION")
    private String bindSource;

    @Schema(description = "是否允许换绑（覆盖该实例或该设备上的旧绑定）")
    private Boolean forceRebind;

    @Schema(description = "备注")
    private String remark;
}
