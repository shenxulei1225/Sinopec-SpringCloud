package cn.cheers.x.inspection.inspection_content.controller.admin.vo.point;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 点位检查项 Response VO")
@Data
public class InspectionExecutionPointItemRespVO {

    @Schema(description = "关联 ID", example = "1")
    private Long id;

    @Schema(description = "站场 ID")
    private Long siteId;

    @Schema(description = "点位 ID")
    private Long pointId;

    @Schema(description = "检查项 ID")
    private Long itemId;

    @Schema(description = "执行动作参数 JSON")
    private String paramsJson;

    @Schema(description = "排序号")
    private Integer sortNo;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
