package cn.cheers.x.facility.management.controller.admin.vo.facility;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 设施详情 VO（legacy接口使用）
 */
@Schema(description = "管理后台 - 设施详情 VO")
@Data
public class FacilityDetailVO extends FacilityListItemVO {

    @Schema(description = "选点状态")
    @JsonProperty("selectionState")
    private String selectionState;

    @Schema(description = "图片")
    private String picture;

    @Schema(description = "巡检时间")
    @JsonProperty("inspectionTime")
    private String inspectionTime;

    @Schema(description = "巡检结果")
    @JsonProperty("inspectionResult")
    private String inspectionResult;

    @Schema(description = "告警ID")
    @JsonProperty("alarmId")
    private String alarmId;

    @Schema(description = "操作列表")
    private String[] operation;
}
