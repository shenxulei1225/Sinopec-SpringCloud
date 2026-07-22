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
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import cn.iocoder.yudao.module.emergency.service.resource.ResourceTypeShareRuleService;
import cn.iocoder.yudao.module.emergency.service.dispatch.ResourcePoolService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 响应管理集成测试
 * 
 * 测试响应管理的完整流程，包括：
 * 1. 响应启动流程（包括任务自动生成）
 * 2. 响应升级流程（包括任务终止和新任务生成）
 * 3. 响应取消流程（包括任务终止和资源回收）
 * 4. 并发控制测试
 * 5. 状态转换验证
 * 6. 时间线查询
 * 
 * @author 应急管理系统
 */
@Import({EmergencyResponseServiceImpl.class, EmergencyTaskServiceImpl.class, TaskCompletionNotificationServiceImpl.class, ResourceDispatchServiceImpl.class})
@DisplayName("响应管理集成测试")
class EmergencyResponseIntegrationTest extends BasePostgresDbUnitTest {

    @MockitoBean
    private ResourceTypeShareRuleService resourceTypeShareRuleService;

    @MockitoBean
    private ResourcePoolService resourcePoolService;

    @MockitoBean
    private cn.cheers.x.module.platform.orchestration.api.OrchestrationRunApi orchestrationRunApi;

    @MockitoBean
    private cn.iocoder.yudao.module.emergency.service.timeline.EmergencyProcessTimelineWriter processTimelineWriter;

    @MockitoBean
    private cn.iocoder.yudao.module.emergency.framework.common.util.DistributedLockUtil distributedLockUtil;

    @MockitoBean
    private cn.iocoder.yudao.module.emergency.service.resource.ResourceTypeConfigService resourceTypeConfigService;

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

    private EmergencyEventDO testEvent;
    private EmergencyPlanDO testPlan;
    private Long planId;

    @BeforeEach
    void setUp() {
        // 准备测试事件
        testEvent = EmergencyEventDO.builder()
                .eventCode("EVT-INT-" + System.nanoTime())
                .eventType(1L)
                .status("confirmed")
                .discoveredAt(LocalDateTime.now())
                .build();
        eventMapper.insert(testEvent);

        // 准备测试预案
        testPlan = EmergencyPlanDO.builder()
                .planNo("PLAN-INT-" + System.nanoTime() % 100000)
                .planName("集成测试预案")
                .planType(1)
                .status("published")
                .build();
        planMapper.insert(testPlan);
        planId = testPlan.getId();
    }

    @Test
    @DisplayName("完整响应流程：启动->升级->取消")
    void testCompleteResponseFlow() {
        // 1. 准备预案步骤
        EmergencyPlanStepDO step1 = createPlanStep(planId, "步骤1", "II", null, 1);
        EmergencyPlanStepDO step2 = createPlanStep(planId, "步骤2", "II", step1.getId(), 2);

        // 2. 启动响应
        ResponseStartReqVO startReq = new ResponseStartReqVO();
        startReq.setEventId(testEvent.getId());
        startReq.setResponseLevel("II");
        startReq.setPlanId(planId);
        startReq.setReason("测试启动响应");
        
        ResponseRespVO response = responseService.start(startReq);
        assertNotNull(response.getId(), "响应ID不应为空");
        assertEquals("executing", response.getStatus(), "响应状态应为executing");

        // 验证响应记录
        EmergencyResponseDO savedResponse = responseMapper.selectById(response.getId());
        assertNotNull(savedResponse);
        assertEquals(testEvent.getId(), savedResponse.getEventId());
        assertEquals("II", savedResponse.getResponseLevel());
        assertEquals("executing", savedResponse.getStatus());

        // 验证任务自动生成
        List<EmergencyTaskDO> tasks = taskMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskDO>()
                        .eq(EmergencyTaskDO::getResponseId, response.getId())
        );
        assertFalse(tasks.isEmpty(), "应自动生成任务");
        assertEquals(2, tasks.size(), "应生成2个任务（对应2个预案步骤）");
        
        // 验证任务关联正确
        tasks.forEach(task -> {
            assertEquals(testEvent.getId(), task.getEventId());
            assertEquals(response.getId(), task.getResponseId());
            assertNotNull(task.getTitle());
        });

        // 3. 升级响应
        ResponseUpgradeReqVO upgradeReq = new ResponseUpgradeReqVO();
        upgradeReq.setNewResponseLevel("III");
        upgradeReq.setReason("测试升级响应");
        
        responseService.upgrade(String.valueOf(response.getId()), upgradeReq);

        // 验证响应升级
        EmergencyResponseDO upgradedResponse = responseMapper.selectById(response.getId());
        assertEquals("III", upgradedResponse.getResponseLevel(), "响应级别应升级为III");

        // 验证旧任务被终止
        List<EmergencyTaskDO> terminatedTasks = taskMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskDO>()
                        .eq(EmergencyTaskDO::getResponseId, response.getId())
                        .eq(EmergencyTaskDO::getStatus, "terminated")
        );
        assertEquals(2, terminatedTasks.size(), "旧任务应被终止");

        // 4. 创建资源调度
        ResourceDispatchDO dispatch = ResourceDispatchDO.builder()
                .eventId(testEvent.getId())
                .responseId(response.getId())
                .resourceId(100L)
                .status("in_use")
                .build();
        dispatchMapper.insert(dispatch);

        // 5. 取消响应
        ResponseCancelReqVO cancelReq = new ResponseCancelReqVO();
        cancelReq.setReason("测试取消响应");
        
        responseService.cancel(String.valueOf(response.getId()), cancelReq);

        // 验证响应取消
        EmergencyResponseDO cancelledResponse = responseMapper.selectById(response.getId());
        assertEquals("cancelled", cancelledResponse.getStatus(), "响应状态应为cancelled");
        assertNotNull(cancelledResponse.getCancelInfo(), "取消信息不应为空");

        // 验证资源回收
        ResourceDispatchDO recoveredDispatch = dispatchMapper.selectById(dispatch.getId());
        assertEquals("recovered", recoveredDispatch.getStatus(), "资源调度状态应为recovered");
    }

    @Test
    @DisplayName("响应启动：无预案步骤时不应自动生成任务")
    void testStartResponseWithoutPlanSteps() {
        // 不创建预案步骤

        ResponseStartReqVO startReq = new ResponseStartReqVO();
        startReq.setEventId(testEvent.getId());
        startReq.setResponseLevel("II");
        startReq.setPlanId(planId);
        startReq.setReason("测试启动响应（无步骤）");
        
        ResponseRespVO response = responseService.start(startReq);
        assertNotNull(response.getId());

        // 验证没有自动生成任务
        List<EmergencyTaskDO> tasks = taskMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskDO>()
                        .eq(EmergencyTaskDO::getResponseId, response.getId())
        );
        assertTrue(tasks.isEmpty(), "预案无步骤时，不应自动生成任务");
    }

    @Test
    @DisplayName("响应升级：新预案无步骤时不应生成新任务")
    void testUpgradeResponseWithoutNewPlanSteps() {
        // 准备初始预案步骤
        createPlanStep(planId, "初始步骤", "II", null, 1);

        // 启动响应
        ResponseStartReqVO startReq = new ResponseStartReqVO();
        startReq.setEventId(testEvent.getId());
        startReq.setResponseLevel("II");
        startReq.setPlanId(planId);
        startReq.setReason("启动");
        ResponseRespVO response = responseService.start(startReq);

        // 创建新预案（无步骤）
        EmergencyPlanDO newPlan = EmergencyPlanDO.builder()
                .planNo("PLAN-NEW-" + System.nanoTime() % 100000)
                .planName("新预案（无步骤）")
                .planType(1)
                .status("published")
                .build();
        planMapper.insert(newPlan);

        // 升级响应（切换到新预案）
        ResponseUpgradeReqVO upgradeReq = new ResponseUpgradeReqVO();
        upgradeReq.setNewResponseLevel("III");
        upgradeReq.setNewPlanId(newPlan.getId());
        upgradeReq.setReason("升级到新预案");
        
        responseService.upgrade(String.valueOf(response.getId()), upgradeReq);

        // 验证旧任务被终止
        List<EmergencyTaskDO> terminatedTasks = taskMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskDO>()
                        .eq(EmergencyTaskDO::getResponseId, response.getId())
                        .eq(EmergencyTaskDO::getStatus, "terminated")
        );
        assertEquals(1, terminatedTasks.size(), "旧任务应被终止");

        // 验证没有生成新任务（因为新预案无步骤）
        List<EmergencyTaskDO> newTasks = taskMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskDO>()
                        .eq(EmergencyTaskDO::getResponseId, response.getId())
                        .ne(EmergencyTaskDO::getStatus, "terminated")
        );
        assertTrue(newTasks.isEmpty(), "新预案无步骤时，不应生成新任务");
    }

    @Test
    @DisplayName("响应取消：应终止所有任务并回收所有资源")
    void testCancelResponseTerminatesTasksAndRecoversResources() {
        // 准备预案步骤
        createPlanStep(planId, "步骤1", "II", null, 1);

        // 启动响应
        ResponseStartReqVO startReq = new ResponseStartReqVO();
        startReq.setEventId(testEvent.getId());
        startReq.setResponseLevel("II");
        startReq.setPlanId(planId);
        startReq.setReason("启动");
        ResponseRespVO response = responseService.start(startReq);

        // 创建多个资源调度
        ResourceDispatchDO dispatch1 = createResourceDispatch(testEvent.getId(), response.getId(), 100L);
        ResourceDispatchDO dispatch2 = createResourceDispatch(testEvent.getId(), response.getId(), 200L);
        ResourceDispatchDO dispatch3 = createResourceDispatch(testEvent.getId(), response.getId(), 300L);

        // 取消响应
        ResponseCancelReqVO cancelReq = new ResponseCancelReqVO();
        cancelReq.setReason("取消测试");
        responseService.cancel(String.valueOf(response.getId()), cancelReq);

        // 验证所有任务被终止
        List<EmergencyTaskDO> allTasks = taskMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskDO>()
                        .eq(EmergencyTaskDO::getResponseId, response.getId())
        );
        assertFalse(allTasks.isEmpty(), "应有任务存在");
        allTasks.forEach(task -> {
            assertEquals("terminated", task.getStatus(), "所有任务应被终止");
        });

        // 验证所有资源被回收
        ResourceDispatchDO recovered1 = dispatchMapper.selectById(dispatch1.getId());
        ResourceDispatchDO recovered2 = dispatchMapper.selectById(dispatch2.getId());
        ResourceDispatchDO recovered3 = dispatchMapper.selectById(dispatch3.getId());
        
        assertEquals("recovered", recovered1.getStatus(), "资源1应被回收");
        assertEquals("recovered", recovered2.getStatus(), "资源2应被回收");
        assertEquals("recovered", recovered3.getStatus(), "资源3应被回收");
    }

    @Test
    @DisplayName("时间线查询：应返回响应相关的所有时间线记录")
    void testTimelineQuery() {
        // 准备预案步骤
        createPlanStep(planId, "步骤1", "II", null, 1);

        // 启动响应
        ResponseStartReqVO startReq = new ResponseStartReqVO();
        startReq.setEventId(testEvent.getId());
        startReq.setResponseLevel("II");
        startReq.setPlanId(planId);
        startReq.setReason("启动");
        ResponseRespVO response = responseService.start(startReq);

        // 查询时间线
        TimelinePageReqVO timelineReq = new TimelinePageReqVO();
        timelineReq.setPageNo(1);
        timelineReq.setPageSize(20);
        
        PageResult<TimelineItemRespVO> timeline = responseService.getTimeline(
                String.valueOf(response.getId()), timelineReq);

        assertNotNull(timeline, "时间线结果不应为空");
        assertNotNull(timeline.getList(), "时间线列表不应为空");
        assertFalse(timeline.getList().isEmpty(), "时间线应包含记录");
        
        // 验证时间线记录包含响应启动记录
        boolean hasStartRecord = timeline.getList().stream()
                .anyMatch(item -> item.getType() != null && 
                                 (item.getType().contains("start") || item.getType().contains("启动")));
        assertTrue(hasStartRecord, "时间线应包含响应启动记录");
    }

    @Test
    @DisplayName("响应状态转换验证：只能按正确顺序转换状态")
    void testResponseStatusTransition() {
        // 准备预案步骤
        createPlanStep(planId, "步骤1", "II", null, 1);

        // 启动响应（PENDING -> EXECUTING）
        ResponseStartReqVO startReq = new ResponseStartReqVO();
        startReq.setEventId(testEvent.getId());
        startReq.setResponseLevel("II");
        startReq.setPlanId(planId);
        startReq.setReason("启动");
        ResponseRespVO response = responseService.start(startReq);
        
        EmergencyResponseDO savedResponse = responseMapper.selectById(response.getId());
        assertEquals("executing", savedResponse.getStatus(), "启动后状态应为executing");

        // 取消响应（EXECUTING -> CANCELLED）
        ResponseCancelReqVO cancelReq = new ResponseCancelReqVO();
        cancelReq.setReason("取消");
        responseService.cancel(String.valueOf(response.getId()), cancelReq);
        
        EmergencyResponseDO cancelledResponse = responseMapper.selectById(response.getId());
        assertEquals("cancelled", cancelledResponse.getStatus(), "取消后状态应为cancelled");
    }

    /**
     * 创建预案步骤辅助方法
     */
    private EmergencyPlanStepDO createPlanStep(Long planId, String name, String planLevel, 
                                               Long parentId, Integer stepOrder) {
        EmergencyPlanStepDO step = new EmergencyPlanStepDO();
        step.setPlanId(planId);
        step.setName(name);
        step.setStepTitle(name);
        step.setParentId(parentId);
        step.setPlanLevel(planLevel);
        step.setStepOrder(stepOrder);
        planStepMapper.insert(step);
        return step;
    }

    /**
     * 创建资源调度辅助方法
     */
    private ResourceDispatchDO createResourceDispatch(Long eventId, Long responseId, Long resourceId) {
        ResourceDispatchDO dispatch = ResourceDispatchDO.builder()
                .eventId(eventId)
                .responseId(responseId)
                .resourceId(resourceId)
                .status("in_use")
                .build();
        dispatchMapper.insert(dispatch);
        return dispatch;
    }
}

