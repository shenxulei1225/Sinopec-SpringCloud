package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskDurationRefreshReqVO;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskDurationRefreshRespVO;

/**
 * 打开巡检任务时现算动作与路线时长。
 * <p>不负责：保存向导步骤、路线、计划点或步骤图；页面修改是否失效由页面比较权威输入后决定。</p>
 */
public interface PatrolTaskCreateProcessService {

    InspectionTaskDurationRefreshRespVO refreshDurations(Long taskId, InspectionTaskDurationRefreshReqVO reqVO);
}
