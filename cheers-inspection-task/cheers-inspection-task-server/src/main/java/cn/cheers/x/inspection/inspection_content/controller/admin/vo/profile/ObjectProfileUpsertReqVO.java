package cn.cheers.x.inspection.inspection_content.controller.admin.vo.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "对象巡检类型 upsert 请求")
public class ObjectProfileUpsertReqVO {

    @Schema(description = "设施 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "facilityId 不能为空")
    private Long facilityId;

    @Schema(description = "对象 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "objectId 不能为空")
    private Long objectId;

    @Schema(description = "巡检类型：HUMAN|GROUND_ROBOT|UAV", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "inspectionType 不能为空")
    private String inspectionType;

    @Schema(description = "默认作业时长（分钟）")
    private Integer defaultWorkMinutes;
}
