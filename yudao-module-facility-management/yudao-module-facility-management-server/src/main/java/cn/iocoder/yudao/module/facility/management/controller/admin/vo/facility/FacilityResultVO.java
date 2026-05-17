package cn.iocoder.yudao.module.facility.management.controller.admin.vo.facility;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 设施巡检结果 VO（legacy接口使用）
 */
@Schema(description = "管理后台 - 设施巡检结果 VO")
@Data
public class FacilityResultVO {

    @Schema(description = "正常数量")
    private Integer normal;

    @Schema(description = "异常告警数量")
    private Integer abnormalAlert;

    @Schema(description = "一般告警数量")
    private Integer generalAlert;

    @Schema(description = "严重告警数量")
    private Integer criticalAlert;
}
