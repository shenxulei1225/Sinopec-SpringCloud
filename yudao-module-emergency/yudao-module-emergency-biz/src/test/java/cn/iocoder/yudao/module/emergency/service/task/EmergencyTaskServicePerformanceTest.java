package cn.iocoder.yudao.module.emergency.service.task;

import cn.iocoder.yudao.module.emergency.framework.test.BasePostgresDbUnitTest;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 应急任务Service性能测试
 * 
 * 性能指标要求：
 * - 启动任务：< 50ms
 * - 完成任务：< 50ms
 * - 终止任务：< 50ms
 * - 批量启动任务（100个）：< 500ms
 * - 批量完成任务（100个）：< 500ms
 */
@Import({EmergencyTaskServiceImpl.class, TaskCompletionNotificationServiceImpl.class})
class EmergencyTaskServicePerformanceTest extends BasePostgresDbUnitTest {

    @Resource
    private EmergencyTaskService taskService;
    @Resource
    private EmergencyTaskMapper taskMapper;

    /**
     * 性能测试：启动任务
     * 目标：< 50ms
     */
    @Test
    void testStartTask_performance() {
        // 准备任务
        EmergencyTaskDO task = EmergencyTaskDO.builder()
                .eventId(1L)
                .responseId(1L)
                .title("性能测试任务")
                .status("pending")
                .build();
        taskMapper.insert(task);

        // 执行性能测试
        long startTime = System.currentTimeMillis();
        taskService.startTask(task.getId());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证结果
        EmergencyTaskDO started = taskMapper.selectById(task.getId());
        assertEquals("in_progress", started.getStatus());
        assertNotNull(started.getStartTime());
        assertTrue(duration < 50, 
                String.format("启动任务耗时 %dms，超过50ms阈值", duration));
        
        System.out.println(String.format("✓ 启动任务性能测试通过，耗时: %dms", duration));
    }

    /**
     * 性能测试：完成任务
     * 目标：< 50ms
     */
    @Test
    void testCompleteTask_performance() {
        // 准备任务
        EmergencyTaskDO task = EmergencyTaskDO.builder()
                .eventId(1L)
                .responseId(1L)
                .title("性能测试任务")
                .status("in_progress")
                .build();
        taskMapper.insert(task);

        // 执行性能测试
        long startTime = System.currentTimeMillis();
        taskService.completeTask(task.getId(), null);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证结果
        EmergencyTaskDO completed = taskMapper.selectById(task.getId());
        assertEquals("completed", completed.getStatus());
        assertNotNull(completed.getExecutionRecords());
        assertTrue(completed.getExecutionRecords().containsKey("actualEndTime"));
        assertTrue(duration < 50, 
                String.format("完成任务耗时 %dms，超过50ms阈值", duration));
        
        System.out.println(String.format("✓ 完成任务性能测试通过，耗时: %dms", duration));
    }

    /**
     * 性能测试：终止任务
     * 目标：< 50ms
     */
    @Test
    void testTerminateTask_performance() {
        // 准备任务
        EmergencyTaskDO task = EmergencyTaskDO.builder()
                .eventId(1L)
                .responseId(1L)
                .title("性能测试任务")
                .status("in_progress")
                .build();
        taskMapper.insert(task);

        // 执行性能测试
        long startTime = System.currentTimeMillis();
        taskService.terminateTask(task.getId(), "性能测试-终止");
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证结果
        EmergencyTaskDO terminated = taskMapper.selectById(task.getId());
        assertEquals("terminated", terminated.getStatus());
        // 任务已终止，状态为 "terminated"
        assertTrue(duration < 50, 
                String.format("终止任务耗时 %dms，超过50ms阈值", duration));
        
        System.out.println(String.format("✓ 终止任务性能测试通过，耗时: %dms", duration));
    }

    /**
     * 性能测试：批量启动任务
     * 目标：< 500ms（100个任务）
     */
    @Test
    void testBatchStartTask_performance() {
        // 准备100个任务
        List<Long> taskIds = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            EmergencyTaskDO task = EmergencyTaskDO.builder()
                    .eventId(1L)
                    .responseId(1L)
                    .title("批量任务-" + i)
                    .status("pending")
                    .build();
            taskMapper.insert(task);
            taskIds.add(task.getId());
        }

        // 执行性能测试
        long startTime = System.currentTimeMillis();
        for (Long taskId : taskIds) {
            taskService.startTask(taskId);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证结果
        for (Long taskId : taskIds) {
            EmergencyTaskDO task = taskMapper.selectById(taskId);
            assertEquals("in_progress", task.getStatus());
        }
        assertTrue(duration < 500, 
                String.format("批量启动任务（100个）耗时 %dms，超过500ms阈值", duration));
        
        System.out.println(String.format("✓ 批量启动任务性能测试通过，耗时: %dms", duration));
    }

    /**
     * 性能测试：批量完成任务
     * 目标：< 500ms（100个任务）
     */
    @Test
    void testBatchCompleteTask_performance() {
        // 准备100个进行中的任务
        List<Long> taskIds = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            EmergencyTaskDO task = EmergencyTaskDO.builder()
                    .eventId(1L)
                    .responseId(1L)
                    .title("批量任务-" + i)
                    .status("in_progress")
                    .build();
            taskMapper.insert(task);
            taskIds.add(task.getId());
        }

        // 执行性能测试
        long startTime = System.currentTimeMillis();
        for (Long taskId : taskIds) {
            taskService.completeTask(taskId, null);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证结果
        for (Long taskId : taskIds) {
            EmergencyTaskDO task = taskMapper.selectById(taskId);
            assertEquals("completed", task.getStatus());
            assertNotNull(task.getExecutionRecords());
        }
        assertTrue(duration < 500, 
                String.format("批量完成任务（100个）耗时 %dms，超过500ms阈值", duration));
        
        System.out.println(String.format("✓ 批量完成任务性能测试通过，耗时: %dms", duration));
    }
}

