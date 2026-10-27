package cn.cheers.x.inspection.task.controller.admin.vo.execution;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 电量走势上的一个点。minute 从这条线的起点算起。remainingPercent 是剩余电量。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatteryTrendPointVO {

    @Schema(description = "从起点算起的分钟")
    private int minute;

    @Schema(description = "剩余电量百分比")
    private double remainingPercent;
}
