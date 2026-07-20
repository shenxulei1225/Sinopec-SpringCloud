package cn.iocoder.yudao.module.emergency.controller.admin.task.vo;

import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 根据事件ID查询任务列表响应")
@Data
public class TaskListByEventRespVO {

    @Schema(description = "所有任务列表（不分组）")
    private List<EmergencyTaskDO> allTasks;

    @Schema(description = "任务总数")
    private Long total;

    @Schema(description = "按阶段分组的任务列表（key为阶段名称：WARNING/RESPONSE，value为该阶段的任务列表）")
    private Map<String, List<EmergencyTaskDO>> tasksByStage;
}
