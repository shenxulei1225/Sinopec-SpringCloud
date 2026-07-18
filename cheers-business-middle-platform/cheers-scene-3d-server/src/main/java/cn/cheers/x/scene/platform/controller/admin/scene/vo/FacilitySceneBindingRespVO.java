package cn.cheers.x.scene.platform.controller.admin.scene.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 设施场景绑定 Response VO")
@Data
public class FacilitySceneBindingRespVO {

    @Schema(description = "设施编号", example = "45")
    private Long facilityId;

    @Schema(description = "场景编码", example = "SCENE-LUOYANG-SHENGRUI")
    private String sceneCode;

    @Schema(description = "场景编号", example = "10003")
    private Long sceneId;
}
