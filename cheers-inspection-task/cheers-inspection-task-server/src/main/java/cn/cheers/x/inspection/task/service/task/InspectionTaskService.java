package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskCreateReqVO;

import java.util.Map;

/**
 * 巡检任务写服务：创建、保存未发布修改、删除总任务。
 */
public interface InspectionTaskService {

    Long createTask(InspectionTaskCreateReqVO reqVO);

    void saveUnpublishedModification(Long id, Map<String, Object> snapshot);

    void deleteTask(Long id);
}
