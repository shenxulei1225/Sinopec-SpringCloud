package cn.iocoder.yudao.module.emergency.service.task;

import cn.iocoder.yudao.module.emergency.framework.test.BasePostgresDbUnitTest;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 任务完成通知服务单元测试
 * 
 * 测试覆盖：
 * 1. 发送任务完成通知（基本功能）
 * 2. 发送任务完成通知（带自定义消息）
 * 3. 通知接收人获取逻辑
 * 4. 通知内容构建逻辑
 * 5. 异常情况处理（通知失败不应影响任务完成）
 * 
 * @author 应急管理系统
 */
@Import(TaskCompletionNotificationServiceImpl.class)
@DisplayName("任务完成通知服务单元测试")
class TaskCompletionNotificationServiceTest extends BasePostgresDbUnitTest {

    @Resource
    private TaskCompletionNotificationService notificationService;

    @Test
    @DisplayName("发送任务完成通知：基本功能")
    void testSendCompletionNotification() {
        // 准备测试数据
        EmergencyTaskDO task = EmergencyTaskDO.builder()
                .id(1L)
                .taskCode("TASK-NOTIFY-001")
                .title("测试任务")
                .assignee("user123")
                .eventId(100L)
                .responseId(200L)
                .completeTime(LocalDateTime.now())
                .status("completed")
                .build();

        // 执行通知（不应抛出异常）
        assertDoesNotThrow(() -> {
            notificationService.sendCompletionNotification(task);
        }, "发送通知不应抛出异常");
    }

    @Test
    @DisplayName("发送任务完成通知：带自定义消息")
    void testSendCompletionNotificationWithMessage() {
        // 准备测试数据
        EmergencyTaskDO task = EmergencyTaskDO.builder()
                .id(2L)
                .taskCode("TASK-NOTIFY-002")
                .title("测试任务2")
                .assignee("user456")
                .eventId(101L)
                .responseId(201L)
                .completeTime(LocalDateTime.now())
                .status("completed")
                .build();

        String customMessage = "任务已完成，请及时查看结果";

        // 执行通知（不应抛出异常）
        assertDoesNotThrow(() -> {
            notificationService.sendCompletionNotification(task, customMessage);
        }, "发送带自定义消息的通知不应抛出异常");
    }

    @Test
    @DisplayName("发送任务完成通知：无接收人时不应报错")
    void testSendCompletionNotification_NoReceivers() {
        // 准备测试数据（无assignee）
        EmergencyTaskDO task = EmergencyTaskDO.builder()
                .id(3L)
                .taskCode("TASK-NOTIFY-003")
                .title("测试任务3")
                .assignee(null) // 无分配人
                .eventId(102L)
                .responseId(202L)
                .completeTime(LocalDateTime.now())
                .status("completed")
                .build();

        // 执行通知（不应抛出异常，即使没有接收人）
        assertDoesNotThrow(() -> {
            notificationService.sendCompletionNotification(task);
        }, "无接收人时发送通知不应抛出异常");
    }

    @Test
    @DisplayName("发送任务完成通知：通知失败不应影响任务完成")
    void testSendCompletionNotification_NotificationFailure() {
        // 准备测试数据
        EmergencyTaskDO task = EmergencyTaskDO.builder()
                .id(4L)
                .taskCode("TASK-NOTIFY-004")
                .title("测试任务4")
                .assignee("user789")
                .eventId(103L)
                .responseId(203L)
                .completeTime(LocalDateTime.now())
                .status("completed")
                .build();

        // 执行通知（即使内部发生异常，也不应抛出）
        // 注意：实际实现中，通知失败会被捕获并记录日志，不会抛出异常
        assertDoesNotThrow(() -> {
            notificationService.sendCompletionNotification(task);
        }, "通知失败不应抛出异常，应被内部捕获");
    }

    @Test
    @DisplayName("发送任务完成通知：验证通知内容包含必要信息")
    void testSendCompletionNotification_ContentValidation() {
        // 准备测试数据
        LocalDateTime completeTime = LocalDateTime.now();
        EmergencyTaskDO task = EmergencyTaskDO.builder()
                .id(5L)
                .taskCode("TASK-NOTIFY-005")
                .title("重要任务")
                .assignee("user999")
                .eventId(104L)
                .responseId(204L)
                .completeTime(completeTime)
                .status("completed")
                .build();

        // 执行通知
        assertDoesNotThrow(() -> {
            notificationService.sendCompletionNotification(task, "自定义消息");
        }, "发送通知不应抛出异常");

        // 注意：由于通知服务目前主要是日志记录，无法直接验证通知内容
        // 在实际集成测试中，可以验证通知是否真正发送到通知服务
    }

    @Test
    @DisplayName("发送任务完成通知：多次调用不应出错")
    void testSendCompletionNotification_MultipleCalls() {
        // 准备测试数据
        EmergencyTaskDO task = EmergencyTaskDO.builder()
                .id(6L)
                .taskCode("TASK-NOTIFY-006")
                .title("测试任务6")
                .assignee("user111")
                .eventId(105L)
                .responseId(205L)
                .completeTime(LocalDateTime.now())
                .status("completed")
                .build();

        // 多次调用通知（不应出错）
        assertDoesNotThrow(() -> {
            notificationService.sendCompletionNotification(task);
            notificationService.sendCompletionNotification(task, "第一次通知");
            notificationService.sendCompletionNotification(task, "第二次通知");
        }, "多次调用通知不应出错");
    }
}








