package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskCreateReqVO;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskUpdateReqVO;

/**
 * 巡检任务写服务：创建/更新/删除总任务草稿。
 */
public interface InspectionTaskService {

    Long createTask(InspectionTaskCreateReqVO reqVO);

    void updateTask(InspectionTaskUpdateReqVO reqVO);

    void deleteTask(Long id);
}
