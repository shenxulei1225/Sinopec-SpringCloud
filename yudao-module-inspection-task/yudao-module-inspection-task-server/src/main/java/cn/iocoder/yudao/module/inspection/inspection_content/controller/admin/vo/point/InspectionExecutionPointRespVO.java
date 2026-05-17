package cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.point;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 执行点位 Response VO")
@Data
public class InspectionExecutionPointRespVO {

    @Schema(description = "点位 ID", example = "1")
    private Long id;

    @Schema(description = "点位编码")
    private String pointCode;

    @Schema(description = "点位名称")
    private String pointName;

    @Schema(description = "站场 ID")
    private Long siteId;

    @Schema(description = "巡检对象 ID")
    private Long objectId;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "高度")
    private BigDecimal height;

    @Schema(description = "偏航角")
    private BigDecimal yaw;

    @Schema(description = "俯仰角")
    private BigDecimal pitch;

    @Schema(description = "滚转角")
    private BigDecimal roll;

    @Schema(description = "适用设备类型")
    private String deviceType;

    @Schema(description = "排序权重")
    private Integer weight;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
