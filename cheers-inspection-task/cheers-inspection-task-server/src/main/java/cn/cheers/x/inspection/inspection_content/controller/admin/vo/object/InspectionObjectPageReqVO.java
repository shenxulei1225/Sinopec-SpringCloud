package cn.cheers.x.inspection.inspection_content.controller.admin.vo.object;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 巡检对象查询 Request VO")
@Data
public class InspectionObjectPageReqVO {

    @Schema(description = "对象名称")
    private String objectName;

    @Schema(description = "对象编码")
    private String objectCode;

    @Schema(description = "站场 ID")
    private Long facilityId;

    @Schema(description = "对象类型")
    private String objectType;

    @Schema(description = "状态")
    private String status;
}
