package cn.iocoder.yudao.module.emergency.service.task;

import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;
import cn.iocoder.yudao.module.emergency.controller.admin.task.vo.TaskCompleteReqVO;

public interface EmergencyTaskService {

    /**
     * 启动任务：pending -> in_progress
     */
    void startTask(Long taskId);

    /**
     * 完成任务：in_progress -> completed
     * @param taskId 任务ID
     * @param reqVO 完成任务请求对象（包含comment、attachments、timelyReport）
     */
    void completeTask(Long taskId, TaskCompleteReqVO reqVO);

    /**
     * 终止任务：任意非终态 -> terminated
     */
    void terminateTask(Long taskId, String reason);

    EmergencyTaskDO get(Long id);

    /**
     * 根据事件ID查询所有关联任务
     * 支持按阶段分组显示和阶段过滤
     *
     * @param eventId 事件ID
     * @param stage 阶段过滤（WARNING预警/RESPONSE响应，为空则显示所有阶段）
     * @param groupByStage 是否按阶段分组显示
     * @return 任务列表（按阶段分组或全部）
     */
    cn.iocoder.yudao.module.emergency.controller.admin.task.vo.TaskListByEventRespVO getTasksByEventId(Long eventId, String stage, Boolean groupByStage);

    /**
     * 根据父任务ID查询子任务列表
     *
     * @param parentTaskId 父任务ID
     * @return 子任务列表
     */
    java.util.List<EmergencyTaskDO> getSubTasks(Long parentTaskId);
}

















