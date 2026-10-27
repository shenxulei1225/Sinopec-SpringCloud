package cn.cheers.x.inspection.task.controller.admin.vo.execution;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 场站实时监控左下角电量走势图。
 * <p>预计线来自电量检查秒数和设备耗电曲线。实际线来自步骤回传电量和回传时刻。
 * <p>缺权威字段时对应线是空的，并带上说明。不编直线。
 */
@Data
public class BatteryTrendRespVO {

    @Schema(description = "预计剩余电量")
    private List<BatteryTrendPointVO> estimated = new ArrayList<>();

    @Schema(description = "实际剩余电量")
    private List<BatteryTrendPointVO> actual = new ArrayList<>();

    @Schema(description = "故障或失败时实际线改成红色")
    private boolean abnormal;

    @Schema(description = "预计线空着时给人看的说明")
    private String estimatedMissingReason;

    @Schema(description = "实际线空着时给人看的说明")
    private String actualMissingReason;
}
