package cn.cheers.x.inspection.inspection_content.controller.admin.vo.point;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 执行点位查询 Request VO")
@Data
public class InspectionExecutionPointPageReqVO {

    @Schema(description = "点位名称")
    private String pointName;

    @Schema(description = "点位编码")
    private String pointCode;

    @Schema(description = "站场 ID")
    private Long siteId;

    @Schema(description = "巡检对象 ID")
    private Long objectId;

    @Schema(description = "适用设备类型")
    private String deviceType;

    @Schema(description = "状态")
    private String status;
}
