package cn.iocoder.yudao.module.emergency.service.task;

import cn.iocoder.yudao.module.emergency.framework.test.BasePostgresDbUnitTest;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskHistoryDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskHistoryMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskMapper;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static cn.cheers.x.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 任务管理单元测试
 * 
 * 测试覆盖：
 * 1. 任务状态转换（启动、完成、终止）
 * 2. 状态转换边界情况
 * 3. 历史记录生成
 * 4. 执行记录管理
 * 5. 异常情况处理
 * 
 * @author 应急管理系统
 */
@Import({EmergencyTaskServiceImpl.class, TaskCompletionNotificationServiceImpl.class})
@DisplayName("任务管理单元测试")
class EmergencyTaskServiceImplTest extends BasePostgresDbUnitTest {

    @Resource
    private EmergencyTaskService taskService;
    
    @Resource
    private EmergencyTaskMapper taskMapper;
    
    @Resource
    private EmergencyTaskHistoryMapper historyMapper;

    @BeforeEach
    void setUp() {
        // 设置租户上下文
        TenantContextHolder.setTenantId(1L);
        TenantContextHolder.setIgnore(false);
    }

    @AfterEach
    void tearDown() {
        // 清理租户上下文
        TenantContextHolder.clear();
    }

    @Test
    @DisplayName("任务启动：pending -> in_progress")
    void testStartTask() {
        // 准备测试数据
        EmergencyTaskDO task = createTask("TASK-START-001", "pending", 1L, 1L);
        
        // 执行启动
        taskService.startTask(task.getId());
        
        // 验证状态更新
        EmergencyTaskDO started = taskMapper.selectById(task.getId());
        assertEquals("in_progress", started.getStatus(), "任务状态应为in_progress");
        assertNotNull(started.getStartTime(), "启动时间不应为空");
        
        // 验证历史记录
        List<EmergencyTaskHistoryDO> histories = historyMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskHistoryDO>()
                        .eq(EmergencyTaskHistoryDO::getTaskId, task.getId())
                        .orderByDesc(EmergencyTaskHistoryDO::getCreateTime)
        );
        assertFalse(histories.isEmpty(), "应生成历史记录");
        EmergencyTaskHistoryDO history = histories.get(0);
        assertEquals("status_change", history.getType(), "历史记录类型应为status_change");
        assertEquals("pending", history.getFromStatus(), "原状态应为pending");
        assertEquals("in_progress", history.getToStatus(), "新状态应为in_progress");
    }

    @Test
    @DisplayName("任务启动：非pending状态不应启动")
    void testStartTask_NonPendingStatus() {
        // 准备已启动的任务
        EmergencyTaskDO task = createTask("TASK-START-002", "in_progress", 1L, 1L);
        LocalDateTime originalStartTime = task.getStartTime();
        
        // 尝试再次启动
        taskService.startTask(task.getId());
        
        // 验证状态未改变
        EmergencyTaskDO unchanged = taskMapper.selectById(task.getId());
        assertEquals("in_progress", unchanged.getStatus(), "状态不应改变");
        assertEquals(originalStartTime, unchanged.getStartTime(), "启动时间不应改变");
    }

    @Test
    @DisplayName("任务完成：in_progress -> completed")
    void testCompleteTask() {
        // 准备进行中的任务
        EmergencyTaskDO task = createTask("TASK-COMPLETE-001", "in_progress", 1L, 1L);
        task.setStartTime(LocalDateTime.now().minusHours(1));
        taskMapper.updateById(task);
        
        // 执行完成
        taskService.completeTask(task.getId(), null);
        
        // 验证状态更新
        EmergencyTaskDO completed = taskMapper.selectById(task.getId());
        assertEquals("completed", completed.getStatus(), "任务状态应为completed");
        assertNotNull(completed.getCompleteTime(), "完成时间不应为空");
        
        // 验证执行记录
        assertNotNull(completed.getExecutionRecords(), "执行记录不应为空");
        assertTrue(completed.getExecutionRecords().containsKey("completeTime"), 
                "执行记录应包含completeTime");
        
        // 验证历史记录
        List<EmergencyTaskHistoryDO> histories = historyMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskHistoryDO>()
                        .eq(EmergencyTaskHistoryDO::getTaskId, task.getId())
                        .orderByDesc(EmergencyTaskHistoryDO::getCreateTime)
        );
        assertFalse(histories.isEmpty(), "应生成历史记录");
        EmergencyTaskHistoryDO history = histories.get(0);
        assertEquals("status_change", history.getType(), "历史记录类型应为status_change");
        assertEquals("in_progress", history.getFromStatus(), "原状态应为in_progress");
        assertEquals("completed", history.getToStatus(), "新状态应为completed");
    }

    @Test
    @DisplayName("任务完成：非in_progress状态不应完成")
    void testCompleteTask_NonInProgressStatus() {
        // 准备pending状态的任务
        EmergencyTaskDO task = createTask("TASK-COMPLETE-002", "pending", 1L, 1L);
        
        // 尝试完成
        taskService.completeTask(task.getId(), null);
        
        // 验证状态未改变
        EmergencyTaskDO unchanged = taskMapper.selectById(task.getId());
        assertEquals("pending", unchanged.getStatus(), "状态不应改变");
        assertNull(unchanged.getCompleteTime(), "完成时间应为空");
    }

    @Test
    @DisplayName("任务终止：任意非终态 -> terminated")
    void testTerminateTask() {
        // 准备进行中的任务
        EmergencyTaskDO task = createTask("TASK-TERMINATE-001", "in_progress", 1L, 1L);
        String reason = "测试终止原因";
        
        // 执行终止
        taskService.terminateTask(task.getId(), reason);
        
        // 验证状态更新
        EmergencyTaskDO terminated = taskMapper.selectById(task.getId());
        assertEquals("terminated", terminated.getStatus(), "任务状态应为terminated");
        
        // 验证历史记录
        List<EmergencyTaskHistoryDO> histories = historyMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskHistoryDO>()
                        .eq(EmergencyTaskHistoryDO::getTaskId, task.getId())
                        .orderByDesc(EmergencyTaskHistoryDO::getCreateTime)
        );
        assertFalse(histories.isEmpty(), "应生成历史记录");
        EmergencyTaskHistoryDO history = histories.get(0);
        assertEquals("terminate", history.getType(), "历史记录类型应为terminate");
        assertEquals("in_progress", history.getFromStatus(), "原状态应为in_progress");
        assertEquals("terminated", history.getToStatus(), "新状态应为terminated");
        assertEquals(reason, history.getReason(), "终止原因应正确记录");
    }

    @Test
    @DisplayName("任务终止：已终止状态不应重复终止")
    void testTerminateTask_AlreadyTerminated() {
        // 准备已终止的任务
        EmergencyTaskDO task = createTask("TASK-TERMINATE-002", "terminated", 1L, 1L);
        LocalDateTime originalUpdateTime = task.getUpdateTime();
        
        // 尝试再次终止
        taskService.terminateTask(task.getId(), "再次终止");
        
        // 验证状态未改变（虽然不会报错，但状态应该保持terminated）
        EmergencyTaskDO unchanged = taskMapper.selectById(task.getId());
        assertEquals("terminated", unchanged.getStatus(), "状态应保持terminated");
    }

    @Test
    @DisplayName("任务终止：从pending状态终止")
    void testTerminateTask_FromPending() {
        // 准备pending状态的任务
        EmergencyTaskDO task = createTask("TASK-TERMINATE-003", "pending", 1L, 1L);
        String reason = "从pending状态终止";
        
        // 执行终止
        taskService.terminateTask(task.getId(), reason);
        
        // 验证状态更新
        EmergencyTaskDO terminated = taskMapper.selectById(task.getId());
        assertEquals("terminated", terminated.getStatus(), "任务状态应为terminated");
        
        // 验证历史记录
        List<EmergencyTaskHistoryDO> histories = historyMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskHistoryDO>()
                        .eq(EmergencyTaskHistoryDO::getTaskId, task.getId())
                        .orderByDesc(EmergencyTaskHistoryDO::getCreateTime)
        );
        assertFalse(histories.isEmpty(), "应生成历史记录");
        EmergencyTaskHistoryDO history = histories.get(0);
        assertEquals("pending", history.getFromStatus(), "原状态应为pending");
        assertEquals("terminated", history.getToStatus(), "新状态应为terminated");
    }

    @Test
    @DisplayName("任务查询：正常查询")
    void testGetTask() {
        // 准备测试数据
        EmergencyTaskDO task = createTask("TASK-GET-001", "pending", 1L, 1L);
        
        // 查询任务
        EmergencyTaskDO found = taskService.get(task.getId());
        
        // 验证查询结果
        assertNotNull(found, "任务不应为空");
        assertEquals(task.getId(), found.getId(), "任务ID应匹配");
        assertEquals(task.getTaskCode(), found.getTaskCode(), "任务编号应匹配");
        assertEquals(task.getTitle(), found.getTitle(), "任务标题应匹配");
    }

    @Test
    @DisplayName("任务查询：不存在的任务返回null")
    void testGetTask_NotExists() {
        // 查询不存在的任务
        EmergencyTaskDO found = taskService.get(99999L);
        
        // 验证查询结果
        assertNull(found, "不存在的任务应返回null");
    }

    @Test
    @DisplayName("完整流程：启动->完成")
    void testCompleteFlow_StartToComplete() {
        // 准备测试数据
        EmergencyTaskDO task = createTask("TASK-FLOW-001", "pending", 1L, 1L);
        
        // 1. 启动任务
        taskService.startTask(task.getId());
        EmergencyTaskDO started = taskMapper.selectById(task.getId());
        assertEquals("in_progress", started.getStatus(), "启动后状态应为in_progress");
        assertNotNull(started.getStartTime(), "启动时间不应为空");
        
        // 2. 完成任务
        taskService.completeTask(task.getId(), null);
        // 重新查询数据库，确保获取最新数据（包括 JSONB 字段）
        EmergencyTaskDO completed = taskMapper.selectById(task.getId());
        assertEquals("completed", completed.getStatus());
        // actualEndTime 信息存储在 executionRecords 中
        // 注意：如果 executionRecords 为 null，可能是 MyBatis Plus 更新策略问题或 JSONB 字段反序列化问题
        // 先检查字段是否存在
        if (completed.getExecutionRecords() == null) {
            // 如果为 null，可能是更新时没有正确保存，或者读取时没有正确反序列化
            // 尝试直接查询数据库验证
            System.out.println("Warning: executionRecords is null after update, checking database...");
        }
        assertNotNull(completed.getExecutionRecords(), "executionRecords should not be null after completeTask");
        assertTrue(completed.getExecutionRecords().containsKey("actualEndTime"), 
                   "executionRecords should contain 'actualEndTime' key");
    }

    @Test
    @DisplayName("完整流程：启动->终止")
    void testCompleteFlow_StartToTerminate() {
        // 准备测试数据
        EmergencyTaskDO task = createTask("TASK-FLOW-002", "pending", 1L, 1L);
        
        // 1. 启动任务
        taskService.startTask(task.getId());
        EmergencyTaskDO started = taskMapper.selectById(task.getId());
        assertEquals("in_progress", started.getStatus(), "启动后状态应为in_progress");
        
        // 2. 终止任务
        String reason = "任务终止原因";
        taskService.terminateTask(task.getId(), reason);
        EmergencyTaskDO terminated = taskMapper.selectById(task.getId());
        assertEquals("terminated", terminated.getStatus(), "终止后状态应为terminated");
        
        // 验证历史记录
        List<EmergencyTaskHistoryDO> histories = historyMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskHistoryDO>()
                        .eq(EmergencyTaskHistoryDO::getTaskId, task.getId())
        );
        assertEquals(2, histories.size(), "应生成2条历史记录（启动和终止）");
        
        // 验证终止历史记录包含原因
        EmergencyTaskHistoryDO terminateHistory = histories.stream()
                .filter(h -> "terminate".equals(h.getType()))
                .findFirst()
                .orElse(null);
        assertNotNull(terminateHistory, "应存在终止历史记录");
        assertEquals(reason, terminateHistory.getReason(), "终止原因应正确记录");
    }

    @Test
    @DisplayName("异常情况：任务不存在")
    void testException_TaskNotExists() {
        // 尝试操作不存在的任务
        assertThrows(Exception.class, () -> {
            taskService.startTask(99999L);
        }, "操作不存在的任务应抛出异常");
    }

    /**
     * 创建测试任务辅助方法
     */
    private EmergencyTaskDO createTask(String taskCode, String status, Long eventId, Long responseId) {
        EmergencyTaskDO task = EmergencyTaskDO.builder()
                .eventId(eventId)
                .responseId(responseId)
                .taskCode(taskCode)
                .title("测试任务：" + taskCode)
                .status(status)
                .priority("NORMAL")
                .build();
        taskMapper.insert(task);
        return task;
    }

    // ========== 边界条件测试 ==========

    @Test
    void testStartTask_taskNotExists() {
        // 验证：任务不存在时应该抛出业务异常
        assertServiceException(() -> taskService.startTask(999L), TASK_NOT_EXISTS);
    }

    @Test
    void testCompleteTask_taskNotExists() {
        // 验证：任务不存在时应该抛出业务异常
        assertServiceException(() -> taskService.completeTask(999L, null), TASK_NOT_EXISTS);
    }

    @Test
    void testTerminateTask_taskNotExists() {
        // 验证：任务不存在时应该抛出业务异常
        assertServiceException(() -> taskService.terminateTask(999L, "reason"), TASK_NOT_EXISTS);
    }

    @Test
    void testStartTask_alreadyCompleted_noOp() {
        // 准备：创建一个已完成的任务
        EmergencyTaskDO task = EmergencyTaskDO.builder()
                .eventId(1L)
                .responseId(1L)
                .taskCode("TASK-COMPLETED-" + System.nanoTime())
                .title("completed_task")
                .status("completed")
                .build();
        taskMapper.insert(task);
        String originalStatus = task.getStatus();

        // 执行：尝试启动已完成的任务
        taskService.startTask(task.getId());

        // 验证：状态不应该改变（方法应该直接返回）
        EmergencyTaskDO after = taskMapper.selectById(task.getId());
        assertEquals(originalStatus, after.getStatus(), "已完成的任务不应该被重新启动");
    }

    @Test
    void testCompleteTask_notInProgress_noOp() {
        // 准备：创建一个待处理状态的任务
        EmergencyTaskDO task = EmergencyTaskDO.builder()
                .eventId(1L)
                .responseId(1L)
                .taskCode("TASK-PENDING-" + System.nanoTime())
                .title("pending_task")
                .status("pending")
                .build();
        taskMapper.insert(task);
        String originalStatus = task.getStatus();

        // 执行：尝试完成待处理状态的任务
        taskService.completeTask(task.getId(), null);

        // 验证：状态不应该改变（方法应该直接返回）
        EmergencyTaskDO after = taskMapper.selectById(task.getId());
        assertEquals(originalStatus, after.getStatus(), "待处理状态的任务不应该被直接完成");
    }

    @Test
    void testTerminateTask_alreadyTerminated_noOp() {
        // 准备：创建一个已终止的任务
        EmergencyTaskDO task = EmergencyTaskDO.builder()
                .eventId(1L)
                .responseId(1L)
                .taskCode("TASK-TERMINATED-" + System.nanoTime())
                .title("terminated_task")
                .status("terminated")
                .build();
        taskMapper.insert(task);
        String originalStatus = task.getStatus();

        // 执行：尝试终止已终止的任务
        taskService.terminateTask(task.getId(), "reason");

        // 验证：状态不应该改变（方法应该直接返回）
        EmergencyTaskDO after = taskMapper.selectById(task.getId());
        assertEquals(originalStatus, after.getStatus(), "已终止的任务不应该被再次终止");
    }
}
