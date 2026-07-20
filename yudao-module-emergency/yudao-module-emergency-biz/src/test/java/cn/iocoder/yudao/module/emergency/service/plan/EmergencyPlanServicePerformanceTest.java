package cn.iocoder.yudao.module.emergency.service.plan;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.framework.test.BasePostgresDbUnitTest;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.*;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanStepUpdateVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanStepDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanStepMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 应急预案Service性能测试
 * 
 * 性能指标要求：
 * - 创建预案（少量步骤）：< 100ms（5个步骤）
 * - 创建预案（大量步骤）：< 500ms（100个步骤）
 * - 更新预案：< 200ms
 * - 查询预案详情：< 50ms
 * - 分页查询预案：< 50ms（每页10条，总数据量100条）
 * - 推荐预案：< 50ms
 */
@Import({
    EmergencyPlanServiceImpl.class,
    cn.iocoder.yudao.module.emergency.convert.plan.EmergencyPlanConvertImpl.class
})
class EmergencyPlanServicePerformanceTest extends BasePostgresDbUnitTest {

    @Resource
    private EmergencyPlanServiceImpl emergencyPlanService;
    @Resource
    private EmergencyPlanMapper emergencyPlanMapper;
    @Resource
    private EmergencyPlanStepMapper emergencyPlanStepMapper;

    /**
     * 性能测试：创建预案（少量步骤）
     * 目标：< 100ms（5个步骤）
     */
    @Test
    void testCreateEmergencyPlan_performance_smallSteps() {
        // 准备参数
        String uniquePlanNo = "P-PERF-" + System.nanoTime() % 100000;
        EmergencyPlanCreateReqVO reqVO = new EmergencyPlanCreateReqVO();
        reqVO.setPlanNo(uniquePlanNo);
        reqVO.setPlanName("性能测试预案-少量步骤");
        reqVO.setPlanType(1);
        reqVO.setPlanLevels(java.util.Arrays.asList("I", "II"));

        // 准备5个步骤
        List<EmergencyPlanStepBaseVO> steps = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            EmergencyPlanStepBaseVO step = new EmergencyPlanStepBaseVO();
            step.setStepTitle("step-" + i);
            step.setStepOrder(i + 1);
            steps.add(step);
        }
        reqVO.setSteps(steps);

        // 执行性能测试
        long startTime = System.currentTimeMillis();
        Long planId = emergencyPlanService.createEmergencyPlan(reqVO);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证结果
        assertNotNull(planId);
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(planId);
        assertNotNull(plan);
        assertEquals(uniquePlanNo, plan.getPlanNo());
        
        // 验证步骤已创建
        List<EmergencyPlanStepDO> createdSteps = emergencyPlanStepMapper.selectListByPlanId(planId);
        assertEquals(5, createdSteps.size());
        
        assertTrue(duration < 100, 
                String.format("创建预案（少量步骤）耗时 %dms，超过100ms阈值", duration));
        
        System.out.println(String.format("✓ 创建预案（少量步骤）性能测试通过，耗时: %dms", duration));
    }

    /**
     * 性能测试：创建预案（大量步骤）
     * 目标：< 500ms（100个步骤）
     */
    @Test
    void testCreateEmergencyPlan_performance_largeSteps() {
        // 准备参数
        String uniquePlanNo = "P-PERF-LARGE-" + System.nanoTime() % 100000;
        EmergencyPlanCreateReqVO reqVO = new EmergencyPlanCreateReqVO();
        reqVO.setPlanNo(uniquePlanNo);
        reqVO.setPlanName("性能测试预案-大量步骤");
        reqVO.setPlanType(1);
        reqVO.setPlanLevels(java.util.Arrays.asList("I", "II", "III"));

        // 准备100个步骤
        List<EmergencyPlanStepBaseVO> steps = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            EmergencyPlanStepBaseVO step = new EmergencyPlanStepBaseVO();
            step.setStepTitle("step-" + i);
            step.setStepOrder(i + 1);
            steps.add(step);
        }
        reqVO.setSteps(steps);

        // 执行性能测试
        long startTime = System.currentTimeMillis();
        Long planId = emergencyPlanService.createEmergencyPlan(reqVO);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证结果
        assertNotNull(planId);
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(planId);
        assertNotNull(plan);
        assertEquals(uniquePlanNo, plan.getPlanNo());
        
        // 验证步骤已创建
        List<EmergencyPlanStepDO> createdSteps = emergencyPlanStepMapper.selectListByPlanId(planId);
        assertEquals(100, createdSteps.size());
        
        assertTrue(duration < 500, 
                String.format("创建预案（大量步骤）耗时 %dms，超过500ms阈值", duration));
        
        System.out.println(String.format("✓ 创建预案（大量步骤）性能测试通过，耗时: %dms", duration));
    }

    /**
     * 性能测试：更新预案
     * 目标：< 200ms
     */
    @Test
    void testUpdateEmergencyPlan_performance() {
        // 准备预案
        EmergencyPlanDO plan = EmergencyPlanDO.builder()
                .planNo("P-UPDATE-" + System.nanoTime() % 100000)
                .planName("更新测试预案")
                .planType(1)
                .status("published")
                .build();
        emergencyPlanMapper.insert(plan);

        // 准备更新参数
        EmergencyPlanUpdateReqVO updateReqVO = new EmergencyPlanUpdateReqVO();
        updateReqVO.setId(plan.getId());
        updateReqVO.setPlanName("更新后的预案名称");
        updateReqVO.setPlanType(1);

        // 准备5个新步骤
        List<EmergencyPlanStepUpdateVO> steps = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            EmergencyPlanStepUpdateVO step = new EmergencyPlanStepUpdateVO();
            step.setStepTitle("updated-step-" + i);
            step.setStepOrder(i + 1);
            steps.add(step);
        }
        updateReqVO.setSteps(steps);

        // 执行性能测试
        long startTime = System.currentTimeMillis();
        emergencyPlanService.updateEmergencyPlan(updateReqVO);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证结果
        EmergencyPlanDO updatedPlan = emergencyPlanMapper.selectById(plan.getId());
        assertEquals("更新后的预案名称", updatedPlan.getPlanName());
        
        // 验证步骤已更新
        List<EmergencyPlanStepDO> updatedSteps = emergencyPlanStepMapper.selectListByPlanId(plan.getId());
        assertEquals(5, updatedSteps.size());
        
        assertTrue(duration < 200, 
                String.format("更新预案耗时 %dms，超过200ms阈值", duration));
        
        System.out.println(String.format("✓ 更新预案性能测试通过，耗时: %dms", duration));
    }

    /**
     * 性能测试：查询预案详情
     * 目标：< 50ms
     */
    @Test
    void testGetEmergencyPlan_performance() {
        // 准备预案和步骤
        EmergencyPlanDO plan = EmergencyPlanDO.builder()
                .planNo("P-GET-" + System.nanoTime() % 100000)
                .planName("查询测试预案")
                .planType(1)
                .status("published")
                .build();
        emergencyPlanMapper.insert(plan);

        // 创建10个步骤
        for (int i = 0; i < 10; i++) {
            EmergencyPlanStepDO step = new EmergencyPlanStepDO();
            step.setPlanId(plan.getId());
            step.setName("step-" + i);
            step.setPlanLevel("II");
            emergencyPlanStepMapper.insert(step);
        }

        // 执行性能测试
        long startTime = System.currentTimeMillis();
        EmergencyPlanRespVO result = emergencyPlanService.getEmergencyPlan(plan.getId());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证结果
        assertNotNull(result);
        assertEquals(plan.getPlanNo(), result.getPlanNo());
        assertNotNull(result.getSteps());
        assertEquals(10, result.getSteps().size());
        
        assertTrue(duration < 50, 
                String.format("查询预案详情耗时 %dms，超过50ms阈值", duration));
        
        System.out.println(String.format("✓ 查询预案详情性能测试通过，耗时: %dms", duration));
    }

    /**
     * 性能测试：分页查询预案（大数据量）
     * 目标：< 50ms（每页10条，总数据量100条）
     */
    @Test
    void testGetEmergencyPlanPage_performance() {
        // 准备大量预案数据（100条）
        for (int i = 0; i < 100; i++) {
            EmergencyPlanDO plan = EmergencyPlanDO.builder()
                    .planNo("P-PAGE-" + System.nanoTime() % 100000 + "-" + i)
                    .planName("分页测试预案-" + i)
                    .planType(1)
                    .status("published")
                    .build();
            emergencyPlanMapper.insert(plan);
        }

        // 执行性能测试
        long startTime = System.currentTimeMillis();
        
        EmergencyPlanPageReqVO pageReqVO = new EmergencyPlanPageReqVO();
        pageReqVO.setPageNo(1);
        pageReqVO.setPageSize(10);
        PageResult<EmergencyPlanRespVO> page = emergencyPlanService.getEmergencyPlanPage(pageReqVO);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证结果
        assertNotNull(page);
        assertTrue(page.getTotal() >= 100);
        assertEquals(10, page.getList().size());
        
        assertTrue(duration < 50, 
                String.format("分页查询预案耗时 %dms，超过50ms阈值", duration));
        
        System.out.println(String.format("✓ 分页查询预案性能测试通过，耗时: %dms", duration));
    }

    /**
     * 性能测试：推荐预案
     * 目标：< 50ms
     */
    @Test
    void testRecommendPlans_performance() {
        // 准备预案数据（包含不同级别）
        for (int i = 0; i < 20; i++) {
            EmergencyPlanDO plan = EmergencyPlanDO.builder()
                    .planNo("P-REC-" + System.nanoTime() % 100000 + "-" + i)
                    .planName("推荐测试预案-" + i)
                    .planType(1)
                    .status("published")
                    .planLevels(java.util.Arrays.asList("I", "II"))
                    .build();
            emergencyPlanMapper.insert(plan);
        }

        // 执行性能测试
        long startTime = System.currentTimeMillis();
        List<EmergencyPlanRespVO> recommended = emergencyPlanService.recommendPlans("II", 1);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证结果
        assertNotNull(recommended);
        assertTrue(duration < 50, 
                String.format("推荐预案耗时 %dms，超过50ms阈值", duration));
        
        System.out.println(String.format("✓ 推荐预案性能测试通过，耗时: %dms", duration));
    }
}

