package cn.iocoder.yudao.module.emergency.service.response;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.framework.test.BasePostgresDbUnitTest;
import cn.iocoder.yudao.module.emergency.controller.admin.response.vo.*;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.response.EmergencyResponseDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanStepDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.response.EmergencyResponseMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanStepMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskMapper;
import cn.iocoder.yudao.module.emergency.service.task.EmergencyTaskServiceImpl;
import cn.iocoder.yudao.module.emergency.service.task.TaskCompletionNotificationServiceImpl;
import cn.iocoder.yudao.module.emergency.service.dispatch.ResourceDispatchServiceImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import cn.iocoder.yudao.module.emergency.service.resource.ResourceTypeShareRuleService;
import cn.iocoder.yudao.module.emergency.service.dispatch.ResourcePoolService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 应急响应Service性能测试
 * 
 * 性能指标要求：
 * - 启动响应：< 100ms（包含少量步骤）
 * - 启动响应（大量步骤）：< 500ms（包含100个步骤）
 * - 升级响应：< 100ms
 * - 取消响应：< 100ms
 * - 时间线分页查询：< 50ms（每页10条）
 * - 响应分页查询：< 50ms（每页10条）
 */
@Import({EmergencyResponseServiceImpl.class, EmergencyTaskServiceImpl.class, TaskCompletionNotificationServiceImpl.class, ResourceDispatchServiceImpl.class})
class EmergencyResponseServicePerformanceTest extends BasePostgresDbUnitTest {

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

    private EmergencyEventDO testEvent;
    private EmergencyPlanDO testPlan;

    @BeforeEach
    void setUp() {
        // 准备测试事件
        testEvent = EmergencyEventDO.builder()
                .eventCode("EVT-PERF-" + System.nanoTime())
                .eventType(null) // 事件分类ID（可选）
                .status("confirmed")
                .discoveredAt(LocalDateTime.now())
                .build();
        eventMapper.insert(testEvent);

        // 准备测试预案
        testPlan = EmergencyPlanDO.builder()
                .planNo("P-PERF-" + System.nanoTime() % 100000)
                .planName("性能测试预案")
                .planType(1)
                .status("published")
                .build();
        planMapper.insert(testPlan);
    }

    /**
     * 性能测试：启动响应（少量步骤）
     * 目标：< 100ms
     */
    @Test
    void testStartResponse_performance_smallSteps() {
        // 准备少量步骤（5个）
        createPlanSteps(testPlan.getId(), 5, "II");

        // 执行性能测试
        long startTime = System.currentTimeMillis();
        
        ResponseStartReqVO req = new ResponseStartReqVO();
        req.setEventId(testEvent.getId());
        req.setResponseLevel("II");
        req.setPlanId(testPlan.getId());
        req.setReason("性能测试");
        ResponseRespVO resp = responseService.start(req);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证结果
        assertNotNull(resp.getId());
        assertTrue(duration < 100, 
                String.format("启动响应（少量步骤）耗时 %dms，超过100ms阈值", duration));
        
        // 验证任务已创建
        var tasks = taskMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskDO>()
                        .eq(EmergencyTaskDO::getResponseId, resp.getId()));
        assertEquals(5, tasks.size());
        
        System.out.println(String.format("✓ 启动响应（少量步骤）性能测试通过，耗时: %dms", duration));
    }

    /**
     * 性能测试：启动响应（大量步骤）
     * 目标：< 500ms（包含100个步骤）
     */
    @Test
    void testStartResponse_performance_largeSteps() {
        // 准备大量步骤（100个）
        createPlanSteps(testPlan.getId(), 100, "II");

        // 执行性能测试
        long startTime = System.currentTimeMillis();
        
        ResponseStartReqVO req = new ResponseStartReqVO();
        req.setEventId(testEvent.getId());
        req.setResponseLevel("II");
        req.setPlanId(testPlan.getId());
        req.setReason("性能测试-大量步骤");
        ResponseRespVO resp = responseService.start(req);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证结果
        assertNotNull(resp.getId());
        assertTrue(duration < 500, 
                String.format("启动响应（大量步骤）耗时 %dms，超过500ms阈值", duration));
        
        // 验证任务已创建
        var tasks = taskMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskDO>()
                        .eq(EmergencyTaskDO::getResponseId, resp.getId()));
        assertEquals(100, tasks.size());
        
        System.out.println(String.format("✓ 启动响应（大量步骤）性能测试通过，耗时: %dms", duration));
    }

    /**
     * 性能测试：升级响应
     * 目标：< 100ms
     */
    @Test
    void testUpgradeResponse_performance() {
        // 准备响应和任务
        EmergencyResponseDO response = new EmergencyResponseDO();
        response.setEventId(testEvent.getId());
        response.setResponseLevel("II");
        response.setPlanId(testPlan.getId());
        response.setStatus("executing");
        responseMapper.insert(response);

        // 创建10个任务
        for (int i = 0; i < 10; i++) {
            EmergencyTaskDO task = EmergencyTaskDO.builder()
                    .eventId(testEvent.getId())
                    .responseId(response.getId())
                    .title("task-" + i)
                    .status("in_progress")
                    .build();
            taskMapper.insert(task);
        }

        // 准备新预案
        EmergencyPlanDO newPlan = EmergencyPlanDO.builder()
                .planNo("P-NEW-" + System.nanoTime() % 100000)
                .planName("新预案")
                .planType(1)
                .status("published")
                .build();
        planMapper.insert(newPlan);
        createPlanSteps(newPlan.getId(), 5, "III");

        // 执行性能测试
        long startTime = System.currentTimeMillis();
        
        ResponseUpgradeReqVO req = new ResponseUpgradeReqVO();
        req.setNewResponseLevel("III");
        req.setNewPlanId(newPlan.getId());
        req.setReason("性能测试-升级");
        responseService.upgrade(String.valueOf(response.getId()), req);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证结果
        EmergencyResponseDO db = responseMapper.selectById(response.getId());
        assertEquals("III", db.getResponseLevel());
        assertTrue(duration < 100, 
                String.format("升级响应耗时 %dms，超过100ms阈值", duration));
        
        System.out.println(String.format("✓ 升级响应性能测试通过，耗时: %dms", duration));
    }

    /**
     * 性能测试：取消响应
     * 目标：< 100ms
     */
    @Test
    void testCancelResponse_performance() {
        // 准备响应和任务
        EmergencyResponseDO response = new EmergencyResponseDO();
        response.setEventId(testEvent.getId());
        response.setResponseLevel("II");
        response.setPlanId(testPlan.getId());
        response.setStatus("executing");
        responseMapper.insert(response);

        // 创建10个任务
        for (int i = 0; i < 10; i++) {
            EmergencyTaskDO task = EmergencyTaskDO.builder()
                    .eventId(testEvent.getId())
                    .responseId(response.getId())
                    .title("task-" + i)
                    .status("in_progress")
                    .build();
            taskMapper.insert(task);
        }

        // 执行性能测试
        long startTime = System.currentTimeMillis();
        
        ResponseCancelReqVO req = new ResponseCancelReqVO();
        req.setReason("性能测试-取消");
        responseService.cancel(String.valueOf(response.getId()), req);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证结果
        EmergencyResponseDO db = responseMapper.selectById(response.getId());
        assertEquals("cancelled", db.getStatus());
        assertTrue(duration < 100, 
                String.format("取消响应耗时 %dms，超过100ms阈值", duration));
        
        System.out.println(String.format("✓ 取消响应性能测试通过，耗时: %dms", duration));
    }

    /**
     * 性能测试：时间线分页查询
     * 目标：< 50ms（每页10条）
     */
    @Test
    void testGetTimeline_performance() {
        // 准备响应
        EmergencyResponseDO response = new EmergencyResponseDO();
        response.setEventId(testEvent.getId());
        response.setResponseLevel("II");
        response.setPlanId(testPlan.getId());
        response.setStatus("executing");
        responseMapper.insert(response);

        // 执行性能测试
        long startTime = System.currentTimeMillis();
        
        TimelinePageReqVO req = new TimelinePageReqVO();
        req.setPageNo(1);
        req.setPageSize(10);
        PageResult<TimelineItemRespVO> page = responseService.getTimeline(String.valueOf(response.getId()), req);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证结果
        assertNotNull(page);
        assertNotNull(page.getList());
        assertTrue(duration < 50, 
                String.format("时间线分页查询耗时 %dms，超过50ms阈值", duration));
        
        System.out.println(String.format("✓ 时间线分页查询性能测试通过，耗时: %dms", duration));
    }

    /**
     * 性能测试：响应分页查询（大数据量）
     * 目标：< 50ms（每页10条，总数据量100条）
     */
    @Test
    void testGetResponsePage_performance() {
        // 准备大量响应数据（100条）
        for (int i = 0; i < 100; i++) {
            EmergencyResponseDO response = new EmergencyResponseDO();
            response.setEventId(testEvent.getId());
            response.setResponseLevel("II");
            response.setPlanId(testPlan.getId());
            response.setStatus("executing");
            responseMapper.insert(response);
        }

        // 执行性能测试
        long startTime = System.currentTimeMillis();
        
        cn.cheers.x.framework.common.pojo.PageParam pageParam = new cn.cheers.x.framework.common.pojo.PageParam();
        pageParam.setPageNo(1);
        pageParam.setPageSize(10);
        PageResult<ResponseRespVO> page = responseService.getResponsePage(pageParam);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证结果
        assertNotNull(page);
        assertTrue(page.getTotal() >= 100);
        assertEquals(10, page.getList().size());
        assertTrue(duration < 50, 
                String.format("响应分页查询耗时 %dms，超过50ms阈值", duration));
        
        System.out.println(String.format("✓ 响应分页查询性能测试通过，耗时: %dms", duration));
    }

    /**
     * 辅助方法：创建预案步骤
     */
    private void createPlanSteps(Long planId, int count, String level) {
        for (int i = 0; i < count; i++) {
            EmergencyPlanStepDO step = new EmergencyPlanStepDO();
            step.setPlanId(planId);
            step.setName("step-" + i);
            step.setParentId(null);
            step.setPlanLevel(level);
            planStepMapper.insert(step);
        }
    }
}

