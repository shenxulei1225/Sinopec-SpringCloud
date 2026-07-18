package cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.point;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 点位检查项查询 Request VO")
@Data
public class InspectionExecutionPointItemPageReqVO {

    @Schema(description = "站场 ID")
    private Long siteId;

    @Schema(description = "点位 ID")
    private Long pointId;

    @Schema(description = "检查项 ID")
    private Long itemId;

    @Schema(description = "是否启用")
    private Boolean enabled;
}
