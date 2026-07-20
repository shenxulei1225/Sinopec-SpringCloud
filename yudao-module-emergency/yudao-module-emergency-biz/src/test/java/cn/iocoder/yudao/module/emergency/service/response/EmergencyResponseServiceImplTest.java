package cn.iocoder.yudao.module.emergency.service.response;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.framework.test.BasePostgresDbUnitTest;
import cn.iocoder.yudao.module.emergency.controller.admin.response.vo.*;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.response.EmergencyResponseDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.dispatch.ResourceDispatchDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanStepDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.response.EmergencyResponseMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.dispatch.ResourceDispatchMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanStepMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskMapper;
import cn.iocoder.yudao.module.emergency.service.task.EmergencyTaskServiceImpl;
import cn.iocoder.yudao.module.emergency.service.task.TaskCompletionNotificationServiceImpl;
import cn.iocoder.yudao.module.emergency.service.dispatch.ResourceDispatchServiceImpl;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import cn.iocoder.yudao.module.emergency.service.resource.ResourceTypeShareRuleService;
import cn.iocoder.yudao.module.emergency.service.dispatch.ResourcePoolService;

import static cn.cheers.x.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 基础单测示例：验证 start/upgrade/cancel 以及 timeline 占位
 */
@Import({EmergencyResponseServiceImpl.class, EmergencyTaskServiceImpl.class, TaskCompletionNotificationServiceImpl.class, ResourceDispatchServiceImpl.class})
class EmergencyResponseServiceImplTest extends BasePostgresDbUnitTest {

    @MockitoBean
    private ResourceTypeShareRuleService resourceTypeShareRuleService;

    @MockitoBean
    private ResourcePoolService resourcePoolService;

    @Resource
    private EmergencyResponseServiceImpl responseService;
    @Resource
    private EmergencyEventMapper eventMapper;
    @Resource
    private EmergencyResponseMapper responseMapper;
    @Resource
    private EmergencyPlanMapper planMapper;
    @Resource
    private EmergencyPlanStepMapper planStepMapper;
    @Resource
    private EmergencyTaskMapper taskMapper;
    @Resource
    private ResourceDispatchMapper dispatchMapper;

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
    void testStart_ok() {
        // 准备事件
        EmergencyEventDO event = EmergencyEventDO.builder()
                .eventCode("EVT-TEST-" + System.nanoTime())
                .eventType(1L)
                .status("confirmed")
                .discoveredAt(java.time.LocalDateTime.now())
                .build();
        eventMapper.insert(event);

        // 准备预案
        EmergencyPlanDO plan = EmergencyPlanDO.builder()
                .planNo("P" + System.nanoTime() % 100000)
                .planName("测试预案")
                .planType(1)
                .status("published")
                .build();
        planMapper.insert(plan);
        Long planId = plan.getId();

        // 准备预案步骤（用于克隆任务）
        EmergencyPlanStepDO step = new EmergencyPlanStepDO();
        step.setPlanId(planId); // 使用前面创建的预案ID
        step.setName("step1");
        step.setParentId(null);
        step.setPlanLevel("II");
        planStepMapper.insert(step);

        ResponseStartReqVO req = new ResponseStartReqVO();
        req.setEventId(event.getId());
        req.setResponseLevel("II");
        req.setPlanId(planId); // 使用前面创建的预案ID
        req.setReason("start");
        ResponseRespVO resp = responseService.start(req);
        assertNotNull(resp.getId());

        EmergencyResponseDO db = responseMapper.selectById(resp.getId());
        assertEquals("executing", db.getStatus());

        // 克隆的任务存在
        var tasks = taskMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskDO>()
                .eq(EmergencyTaskDO::getResponseId, resp.getId()));
        assertFalse(tasks.isEmpty());
    }

    @Test
    void testUpgrade_ok() {
        EmergencyResponseDO response = new EmergencyResponseDO();
        response.setEventId(1L);
        response.setResponseLevel("II");
        response.setPlanId(1L);
        response.setStatus("executing");
        responseMapper.insert(response);

        // 旧任务
        EmergencyTaskDO task = EmergencyTaskDO.builder()
                .eventId(1L)
                .responseId(response.getId())
                .title("oldTask")
                .status("in_progress")
                .build();
        taskMapper.insert(task);

        ResponseUpgradeReqVO req = new ResponseUpgradeReqVO();
        req.setNewResponseLevel("III");
        req.setReason("upgrade");
        responseService.upgrade(String.valueOf(response.getId()), req);

        EmergencyResponseDO db = responseMapper.selectById(response.getId());
        assertEquals("III", db.getResponseLevel());

        // 旧任务应终止
        EmergencyTaskDO terminated = taskMapper.selectById(task.getId());
        assertEquals("terminated", terminated.getStatus());
    }

    @Test
    void testCancel_ok() {
        EmergencyResponseDO response = new EmergencyResponseDO();
        response.setEventId(1L);
        response.setResponseLevel("II");
        response.setPlanId(1L);
        response.setStatus("executing");
        responseMapper.insert(response);

        // 准备调度
        ResourceDispatchDO dispatch = ResourceDispatchDO.builder()
                .eventId(1L)
                .responseId(response.getId())
                .resourceId(1L)
                .status("in_use")
                .build();
        dispatchMapper.insert(dispatch);

        ResponseCancelReqVO req = new ResponseCancelReqVO();
        req.setReason("cancel");
        responseService.cancel(String.valueOf(response.getId()), req);

        EmergencyResponseDO db = responseMapper.selectById(response.getId());
        assertEquals("cancelled", db.getStatus());
        assertNotNull(db.getCancelInfo());

        // 调度回收
        ResourceDispatchDO recovered = dispatchMapper.selectById(dispatch.getId());
        assertEquals("recovered", recovered.getStatus());
    }

    @Test
    void testTimeline_placeholder() {
        EmergencyResponseDO response = new EmergencyResponseDO();
        response.setEventId(1L);
        response.setResponseLevel("II");
        response.setPlanId(1L);
        response.setStatus("executing");
        responseMapper.insert(response);

        TimelinePageReqVO req = new TimelinePageReqVO();
        req.setPageNo(1);
        req.setPageSize(10);
        PageResult<TimelineItemRespVO> page = responseService.getTimeline(String.valueOf(response.getId()), req);
        assertNotNull(page);
        assertNotNull(page.getList());
    }

    @Test
    void testStart_withoutSteps() {
        // 准备事件
        EmergencyEventDO event = EmergencyEventDO.builder()
                .eventCode("EVT-TEST-003")
                .eventType(1L)
                .status("confirmed")
                .discoveredAt(java.time.LocalDateTime.now())
                .build();
        eventMapper.insert(event);

        // 预案没有步骤（不插入步骤数据）

        ResponseStartReqVO req = new ResponseStartReqVO();
        req.setEventId(event.getId());
        req.setResponseLevel("II");
        req.setPlanId(999L); // 不存在的预案ID或没有步骤的预案
        req.setReason("start");
        ResponseRespVO resp = responseService.start(req);
        assertNotNull(resp.getId());

        EmergencyResponseDO db = responseMapper.selectById(resp.getId());
        assertEquals("executing", db.getStatus());

        // 没有步骤，所以没有自动生成任务
        var tasks = taskMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskDO>()
                .eq(EmergencyTaskDO::getResponseId, resp.getId()));
        assertTrue(tasks.isEmpty(), "预案没有步骤时，不应自动生成任务，允许手动创建任务");
    }

    @Test
    void testUpgrade_withoutSteps() {
        EmergencyResponseDO response = new EmergencyResponseDO();
        response.setEventId(1L);
        response.setResponseLevel("II");
        response.setPlanId(1L);
        response.setStatus("executing");
        responseMapper.insert(response);

        // 旧任务
        EmergencyTaskDO task = EmergencyTaskDO.builder()
                .eventId(1L)
                .responseId(response.getId())
                .title("oldTask")
                .status("in_progress")
                .build();
        taskMapper.insert(task);

        ResponseUpgradeReqVO req = new ResponseUpgradeReqVO();
        req.setNewResponseLevel("III");
        req.setNewPlanId(999L); // 新预案没有步骤
        req.setReason("upgrade");
        responseService.upgrade(String.valueOf(response.getId()), req);

        EmergencyResponseDO db = responseMapper.selectById(response.getId());
        assertEquals("III", db.getResponseLevel());
        assertEquals(999L, db.getPlanId());

        // 旧任务应终止
        EmergencyTaskDO terminated = taskMapper.selectById(task.getId());
        assertEquals("terminated", terminated.getStatus());

        // 新预案没有步骤，所以没有自动生成新任务
        var newTasks = taskMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskDO>()
                .eq(EmergencyTaskDO::getResponseId, response.getId()));
        assertEquals(1, newTasks.size(), "只有旧任务被终止，新预案没有步骤时不应自动生成新任务");
        assertEquals("terminated", newTasks.get(0).getStatus());
    }

    // ========== 边界条件测试 ==========

    @Test
    void testStart_eventNotExists() {
        // 准备参数：事件不存在
        ResponseStartReqVO req = new ResponseStartReqVO();
        req.setEventId(999L); // 不存在的事件ID
        req.setResponseLevel("II");
        req.setPlanId(1L);
        req.setReason("start");

        // 验证：应该抛出业务异常
        assertServiceException(() -> responseService.start(req), EVENT_NOT_EXISTS);
    }

    @Test
    void testUpgrade_responseNotExists() {
        // 准备参数：响应不存在
        ResponseUpgradeReqVO req = new ResponseUpgradeReqVO();
        req.setNewResponseLevel("III");
        req.setReason("upgrade");

        // 验证：应该抛出业务异常
        assertServiceException(() -> responseService.upgrade("999", req), RESPONSE_NOT_EXISTS);
    }

    @Test
    void testUpgrade_invalidResponseNo() {
        // 准备参数：无效的响应编号（非数字）
        ResponseUpgradeReqVO req = new ResponseUpgradeReqVO();
        req.setNewResponseLevel("III");
        req.setReason("upgrade");

        // 验证：应该抛出NumberFormatException（运行时异常）
        assertThrows(NumberFormatException.class, () -> responseService.upgrade("invalid", req));
    }

    @Test
    void testCancel_responseNotExists() {
        // 准备参数：响应不存在
        ResponseCancelReqVO req = new ResponseCancelReqVO();
        req.setReason("cancel");

        // 验证：应该抛出业务异常
        assertServiceException(() -> responseService.cancel("999", req), RESPONSE_NOT_EXISTS);
    }

    @Test
    void testCancel_invalidResponseNo() {
        // 准备参数：无效的响应编号（非数字）
        ResponseCancelReqVO req = new ResponseCancelReqVO();
        req.setReason("cancel");

        // 验证：应该抛出NumberFormatException（运行时异常）
        assertThrows(NumberFormatException.class, () -> responseService.cancel("invalid", req));
    }

    @Test
    void testGetTimeline_responseNotExists() {
        // 准备参数：响应不存在
        TimelinePageReqVO req = new TimelinePageReqVO();
        req.setPageNo(1);
        req.setPageSize(10);

        // 验证：应该抛出业务异常
        assertServiceException(() -> responseService.getTimeline("999", req), RESPONSE_NOT_EXISTS);
    }

    @Test
    void testGetTimeline_invalidResponseNo() {
        // 准备参数：无效的响应编号（非数字）
        TimelinePageReqVO req = new TimelinePageReqVO();
        req.setPageNo(1);
        req.setPageSize(10);

        // 验证：应该抛出NumberFormatException（运行时异常）
        assertThrows(NumberFormatException.class, () -> responseService.getTimeline("invalid", req));
    }
}
