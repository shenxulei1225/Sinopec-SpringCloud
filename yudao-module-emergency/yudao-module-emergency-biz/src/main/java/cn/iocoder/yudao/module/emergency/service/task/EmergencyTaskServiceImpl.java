package cn.iocoder.yudao.module.emergency.service.task;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskHistoryDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.response.EmergencyResponseDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskHistoryMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.response.EmergencyResponseMapper;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import cn.iocoder.yudao.module.emergency.controller.admin.task.vo.TaskListByEventRespVO;
import cn.iocoder.yudao.module.emergency.controller.admin.task.vo.TaskCompleteReqVO;
import cn.iocoder.yudao.module.emergency.service.timeline.EmergencyProcessTimelineWriter;
import cn.cheers.x.framework.common.util.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@Slf4j
@Service
public class EmergencyTaskServiceImpl implements EmergencyTaskService {

    private final EmergencyTaskMapper taskMapper;
    private final EmergencyTaskHistoryMapper historyMapper;
    private final EmergencyResponseMapper responseMapper;
    private final TaskCompletionNotificationService notificationService;
    private final EmergencyProcessTimelineWriter processTimelineWriter;

    public EmergencyTaskServiceImpl(EmergencyTaskMapper taskMapper,
                                    EmergencyTaskHistoryMapper historyMapper,
                                    EmergencyResponseMapper responseMapper,
                                    TaskCompletionNotificationService notificationService,
                                    EmergencyProcessTimelineWriter processTimelineWriter) {
        this.taskMapper = taskMapper;
        this.historyMapper = historyMapper;
        this.responseMapper = responseMapper;
        this.notificationService = notificationService;
        this.processTimelineWriter = processTimelineWriter;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public void startTask(Long taskId) {
        EmergencyTaskDO task = getOrThrow(taskId);
        if (!"pending".equals(task.getStatus())) {
            return;
        }
        String from = task.getStatus();
        task.setStatus("in_progress");
        task.setStartTime(LocalDateTime.now());
        taskMapper.updateById(task);
        Long responseId = getResponseIdOrThrow(task);
        historyMapper.insert(EmergencyTaskHistoryDO.builder()
                .responseId(responseId)
                .taskId(taskId)
                .type("status_change")
                .fromStatus(from)
                .toStatus("in_progress")
                .build());
        writeTaskTimeline(task, "task.start", "启动任务 taskId=" + taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public void completeTask(Long taskId, TaskCompleteReqVO reqVO) {
        EmergencyTaskDO task = getOrThrow(taskId);
        if (!"in_progress".equals(task.getStatus())) {
            return;
        }
        
        // 检查是否有未完成的子任务
        List<EmergencyTaskDO> subTasks = taskMapper.selectList(
                new LambdaQueryWrapper<EmergencyTaskDO>()
                        .eq(EmergencyTaskDO::getParentId, taskId)
                        .ne(EmergencyTaskDO::getStatus, "completed")
                        .ne(EmergencyTaskDO::getStatus, "terminated"));
        
        if (subTasks != null && !subTasks.isEmpty()) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.TASK_HAS_INCOMPLETE_SUBTASKS);
        }
        
        String from = task.getStatus();
        task.setStatus("completed");
        LocalDateTime completeTime = LocalDateTime.now();
        task.setCompleteTime(completeTime);
        // 结束时间信息也存储在 executionRecords 中
        java.util.Map<String, Object> executionRecords = task.getExecutionRecords();
        if (executionRecords == null) {
            executionRecords = new java.util.HashMap<>();
        }
        executionRecords.put("actualEndTime", completeTime.toString());
        executionRecords.put("completeTime", completeTime.toString());
        task.setExecutionRecords(executionRecords);
        // 使用 updateById 更新，MyBatis Plus 会自动使用实体类上定义的 TypeHandler
        taskMapper.updateById(task);
        Long responseId = getResponseIdOrThrow(task);
        
        // 构建完整的数据对象，包含 comment、attachments、timelyReport
        java.util.Map<String, Object> completeData = new java.util.HashMap<>();
        if (reqVO.getComment() != null && !reqVO.getComment().trim().isEmpty()) {
            completeData.put("comment", reqVO.getComment());
        }
        if (reqVO.getAttachments() != null && !reqVO.getAttachments().isEmpty()) {
            completeData.put("attachments", reqVO.getAttachments());
        }
        if (reqVO.getTimelyReport() != null && !reqVO.getTimelyReport().trim().isEmpty()) {
            completeData.put("timelyReport", reqVO.getTimelyReport());
        }
        
        // 将完整数据以 JSON 格式存储到 reason 字段
        String reasonJson = completeData.isEmpty() ? null : JsonUtils.toJsonString(completeData);
        
        historyMapper.insert(EmergencyTaskHistoryDO.builder()
                .responseId(responseId)
                .taskId(taskId)
                .type("status_change")
                .fromStatus(from)
                .toStatus("completed")
                .reason(reasonJson)  // 保存完整数据（comment、attachments、timelyReport）的 JSON 到 reason 字段
                .build());
        
        // 发送任务完成通知
        try {
            notificationService.sendCompletionNotification(task);
        } catch (Exception e) {
            // 通知失败不应影响任务完成流程，只记录日志
            log.error("发送任务完成通知失败: taskId={}", taskId, e);
        }
        writeTaskTimeline(task, "task.complete", "完成任务 taskId=" + taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public void terminateTask(Long taskId, String reason) {
        EmergencyTaskDO task = getOrThrow(taskId);
        if ("terminated".equals(task.getStatus())) {
            return;
        }
        String from = task.getStatus();
        task.setStatus("terminated");
        taskMapper.updateById(task);
        Long responseId = getResponseIdOrThrow(task);
        historyMapper.insert(EmergencyTaskHistoryDO.builder()
                .responseId(responseId)
                .taskId(taskId)
                .type("terminate")
                .fromStatus(from)
                .toStatus("terminated")
                .reason(reason)
                .build());
        writeTaskTimeline(task, "task.terminate",
                "终止任务 taskId=" + taskId + (reason != null && !reason.isBlank() ? ": " + reason : ""));
    }

    private void writeTaskTimeline(EmergencyTaskDO task, String actionCode, String howSummary) {
        Long eventId = task.getEventId();
        if (eventId == null && task.getResponseId() != null) {
            EmergencyResponseDO response = responseMapper.selectById(task.getResponseId());
            if (response != null) {
                eventId = response.getEventId();
            }
        }
        if (eventId == null) {
            log.error("过程时间线双写跳过：任务无 eventId, taskId={}", task.getId());
            return;
        }
        processTimelineWriter.appendEventAction(eventId, actionCode, howSummary);
    }

    @Override
    public EmergencyTaskDO get(Long id) {
        return taskMapper.selectById(id);
    }

    @Override
    public TaskListByEventRespVO getTasksByEventId(Long eventId, String stage, Boolean groupByStage) {
        // 构建查询条件
        LambdaQueryWrapper<EmergencyTaskDO> queryWrapper = new LambdaQueryWrapper<EmergencyTaskDO>()
                .eq(EmergencyTaskDO::getEventId, eventId)
                .ne(EmergencyTaskDO::getStatus, "terminated") // 排除已终止的任务
                .orderByAsc(EmergencyTaskDO::getCreateTime); // 按创建时间排序（时间轴顺序）

        // 如果指定了阶段过滤，添加阶段条件
        if (stage != null && !stage.trim().isEmpty()) {
            queryWrapper.eq(EmergencyTaskDO::getStage, stage);
        }

        // 查询所有符合条件的任务
        List<EmergencyTaskDO> allTasks = taskMapper.selectList(queryWrapper);

        // 构建响应对象
        TaskListByEventRespVO respVO = new TaskListByEventRespVO();
        respVO.setAllTasks(allTasks);
        respVO.setTotal((long) allTasks.size());

        // 如果要求按阶段分组，进行分组
        if (groupByStage != null && groupByStage) {
            Map<String, List<EmergencyTaskDO>> tasksByStage = allTasks.stream()
                    .collect(Collectors.groupingBy(
                            task -> task.getStage() != null ? task.getStage() : "UNKNOWN",
                            Collectors.toList()
                    ));
            respVO.setTasksByStage(tasksByStage);
        }

        return respVO;
    }

    private EmergencyTaskDO getOrThrow(Long id) {
        EmergencyTaskDO task = taskMapper.selectById(id);
        if (task == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.TASK_NOT_EXISTS);
        }
        return task;
    }

    /**
     * 获取任务的响应ID，如果任务没有响应ID，则通过事件ID查询响应
     * 预警阶段的任务可能没有响应，此时返回null（允许预警阶段任务历史记录的response_id为null）
     *
     * @param task 任务对象
     * @return 响应ID，预警阶段可能为null
     */
    private Long getResponseIdOrThrow(EmergencyTaskDO task) {
        // 如果任务已有响应ID，直接返回
        if (task.getResponseId() != null) {
            return task.getResponseId();
        }
        
        // 如果任务没有响应ID，通过事件ID查询响应
        if (task.getEventId() == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESPONSE_NOT_EXISTS, 
                    "任务缺少事件ID，无法关联响应");
        }
        
        EmergencyResponseDO response = responseMapper.selectByEventId(task.getEventId());
        if (response == null || response.getId() == null) {
            // 预警阶段的任务可能没有响应，允许返回null
            // 预警阶段的任务历史记录 response_id 可以为 null
            log.debug("任务 {} 关联的事件 {} 没有对应的响应，这是预警阶段任务，允许 response_id 为 null", 
                    task.getId(), task.getEventId());
            return null;
        }
        
        return response.getId();
    }

    @Override
    public List<EmergencyTaskDO> getSubTasks(Long parentTaskId) {
        return taskMapper.selectList(
                new LambdaQueryWrapper<EmergencyTaskDO>()
                        .eq(EmergencyTaskDO::getParentId, parentTaskId)
                        .orderByAsc(EmergencyTaskDO::getCreateTime));
    }
}

