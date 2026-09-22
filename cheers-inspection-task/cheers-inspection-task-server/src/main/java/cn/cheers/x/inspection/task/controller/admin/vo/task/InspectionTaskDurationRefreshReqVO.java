package cn.cheers.x.inspection.task.controller.admin.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 打开任务时现算时长：第 1 步动作耗时 + 第 2 步路径输入（当前到达位置）。
 */
@Data
@Schema(description = "打开任务现算时长")
public class InspectionTaskDurationRefreshReqVO {

    @Schema(description = "设备检查配置里当前到达位置对应的停靠点，用来对比已保存路线是否过期")
    private List<String> liveCheckItemStopIds = new ArrayList<>();
}
