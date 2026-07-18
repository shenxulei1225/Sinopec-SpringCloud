package cn.cheers.x.twin.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - Twin 绑定请求")
@Data
public class TwinMappingBindReqVO {

    @Schema(description = "设施 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "facilityId 不能为空")
    private Long facilityId;

    @Schema(description = "ActorInstance ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "actorInstanceId 不能为空")
    private Long actorInstanceId;

    @Schema(description = "场景 ID")
    private Long sceneId;

    @Schema(description = "场景编码")
    private String sceneCode;

    @Schema(description = "绑定来源，例如 SCENE_EDITOR/FACILITY_PAGE/CONTEXT_MENU")
    private String bindSource;

    @Schema(description = "是否允许换绑")
    private Boolean forceRebind;

    @Schema(description = "备注")
    private String remark;
}
