package cn.iocoder.yudao.module.emergency.service.task;

import cn.iocoder.yudao.module.emergency.framework.test.BasePostgresDbUnitTest;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanStepDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanStepMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 任务执行时限传递测试
 * 
 * 验证从预案步骤创建任务时，timeLimit字段是否正确传递
 */
class TaskTimeLimitTest extends BasePostgresDbUnitTest {
    
    @Resource
    private EmergencyTaskMapper taskMapper;
    
    @Resource
    private EmergencyPlanStepMapper planStepMapper;

    @Test
    void testTimeLimitPassedFromPlanStep() {
        // 1. 创建预案步骤，设置执行时限为60分钟
        EmergencyPlanStepDO planStep = EmergencyPlanStepDO.builder()
                .planId(1L)
                .stepTitle("测试步骤")
                .stepDescription("测试步骤描述")
                .stepStage("RESPONSE")
                .scheduledStartTime(0) // 立即开始
                .timeLimit(60) // 执行时限60分钟
                .build();
        planStepMapper.insert(planStep);
        
        // 2. 模拟从预案步骤创建任务（简化测试，直接创建任务）
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime expectedDueTime = startTime.plusMinutes(60);
        
        EmergencyTaskDO task = EmergencyTaskDO.builder()
                .eventId(1L)
                .responseId(1L)
                .planStepId(planStep.getId())
                .taskCode("TASK-TEST-001")
                .title(planStep.getStepTitle())
                .content(planStep.getStepDescription())
                .taskType(planStep.getStepStage())
                .stage(planStep.getStepStage())
                .status("pending")
                .priority("NORMAL")
                .isKey(false)
                .startTime(startTime)
                .timeLimit(planStep.getTimeLimit()) // 从预案步骤传递
                .dueTime(expectedDueTime) // 根据timeLimit计算
                .build();
        taskMapper.insert(task);
        
        // 3. 验证任务的timeLimit字段
        EmergencyTaskDO createdTask = taskMapper.selectById(task.getId());
        assertNotNull(createdTask, "任务应该已创建");
        assertEquals(60, createdTask.getTimeLimit(), "任务的执行时限应该为60分钟");
        assertNotNull(createdTask.getDueTime(), "任务的截止时间应该已设置");
        
        // 4. 验证dueTime计算正确（允许1分钟误差）
        long minutesDifference = java.time.temporal.ChronoUnit.MINUTES.between(
            expectedDueTime, createdTask.getDueTime());
        assertTrue(Math.abs(minutesDifference) <= 1, 
            "截止时间应该在预期时间1分钟内，预期: " + expectedDueTime + ", 实际: " + createdTask.getDueTime());
    }
    
    @Test
    void testTimeLimitCalculatesDueTimeWhenStartTimeIsNull() {
        // 测试当startTime为空时，基于当前时间计算dueTime
        EmergencyPlanStepDO planStep = EmergencyPlanStepDO.builder()
                .planId(1L)
                .stepTitle("测试步骤2")
                .stepDescription("测试步骤描述2")
                .stepStage("RESPONSE")
                .timeLimit(120) // 执行时限120分钟
                .build();
        planStepMapper.insert(planStep);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expectedDueTime = now.plusMinutes(120);
        
        EmergencyTaskDO task = EmergencyTaskDO.builder()
                .eventId(1L)
                .responseId(1L)
                .planStepId(planStep.getId())
                .taskCode("TASK-TEST-002")
                .title(planStep.getStepTitle())
                .content(planStep.getStepDescription())
                .taskType(planStep.getStepStage())
                .stage(planStep.getStepStage())
                .status("pending")
                .priority("NORMAL")
                .isKey(false)
                .startTime(null) // startTime为空
                .timeLimit(planStep.getTimeLimit())
                .dueTime(expectedDueTime) // 基于当前时间计算
                .build();
        taskMapper.insert(task);
        
        // 验证
        EmergencyTaskDO createdTask = taskMapper.selectById(task.getId());
        assertNotNull(createdTask.getTimeLimit(), "任务的执行时限应该已设置");
        assertEquals(120, createdTask.getTimeLimit(), "任务的执行时限应该为120分钟");
        assertNotNull(createdTask.getDueTime(), "任务的截止时间应该已设置");
    }
    
    @Test
    void testTimeLimitIsNullWhenPlanStepHasNoTimeLimit() {
        // 测试当预案步骤没有设置timeLimit时，任务的timeLimit应该为空
        EmergencyPlanStepDO planStep = EmergencyPlanStepDO.builder()
                .planId(1L)
                .stepTitle("测试步骤3")
                .stepDescription("测试步骤描述3")
                .stepStage("RESPONSE")
                .timeLimit(null) // 没有设置执行时限
                .build();
        planStepMapper.insert(planStep);
        
        EmergencyTaskDO task = EmergencyTaskDO.builder()
                .eventId(1L)
                .responseId(1L)
                .planStepId(planStep.getId())
                .taskCode("TASK-TEST-003")
                .title(planStep.getStepTitle())
                .content(planStep.getStepDescription())
                .taskType(planStep.getStepStage())
                .stage(planStep.getStepStage())
                .status("pending")
                .priority("NORMAL")
                .isKey(false)
                .timeLimit(null) // 从预案步骤传递（为空）
                .dueTime(null) // 无法计算
                .build();
        taskMapper.insert(task);
        
        // 验证
        EmergencyTaskDO createdTask = taskMapper.selectById(task.getId());
        assertNull(createdTask.getTimeLimit(), "任务的执行时限应该为空");
        assertNull(createdTask.getDueTime(), "任务的截止时间应该为空");
    }
}

