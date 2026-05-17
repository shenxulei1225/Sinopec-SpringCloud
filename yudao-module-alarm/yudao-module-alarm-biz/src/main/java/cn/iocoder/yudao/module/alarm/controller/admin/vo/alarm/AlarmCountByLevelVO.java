package cn.iocoder.yudao.module.alarm.controller.admin.vo.alarm;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 告警级别统计 VO
 *
 * @author 告警管理模块
 */
@Schema(description = "管理后台 - 告警级别统计 Response VO")
@Data
public class AlarmCountByLevelVO {

    @Schema(description = "紧急告警数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    private Integer criticalCount;

    @Schema(description = "重要告警数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer majorCount;

    @Schema(description = "一般告警数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Integer minorCount;

    @Schema(description = "提示告警数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "15")
    private Integer warningCount;

    @Schema(description = "总数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "50")
    private Integer totalCount;

}
