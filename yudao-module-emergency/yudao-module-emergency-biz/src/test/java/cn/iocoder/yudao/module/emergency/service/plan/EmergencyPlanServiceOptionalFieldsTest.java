package cn.iocoder.yudao.module.emergency.service.plan;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.framework.test.BasePostgresDbUnitTest;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanPageReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanRespVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 预案管理（可选字段）集成测试
 */
@Import({
    EmergencyPlanServiceImpl.class,
    cn.iocoder.yudao.module.emergency.convert.plan.EmergencyPlanConvertImpl.class  // MapStruct 生成的实现类，需要显式导入
})
class EmergencyPlanServiceOptionalFieldsTest extends BasePostgresDbUnitTest {

    @Resource
    private EmergencyPlanService emergencyPlanService;

    @Resource
    private EmergencyPlanMapper emergencyPlanMapper;

    @Test
    void testCreateBasicPlanWithoutLevelsAndSteps() {
        // 准备参数：基础预案（无级别、无步骤）
        EmergencyPlanCreateReqVO createReqVO = new EmergencyPlanCreateReqVO();
        createReqVO.setPlanNo("PLAN-BASIC-001");
        createReqVO.setPlanName("基础预案");
        createReqVO.setPlanType(1);
        createReqVO.setStatus("draft");
        // planGroupId, planLevels, steps 均为 null

        // 调用
        Long planId = emergencyPlanService.createEmergencyPlan(createReqVO);

        // 断言
        assertNotNull(planId);
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(planId);
        assertNotNull(plan);
        assertEquals("PLAN-BASIC-001", plan.getPlanNo());
        assertNull(plan.getPlanGroupId());
        assertNull(plan.getPlanLevels());
    }

    @Test
    void testCreatePlanWithGroupAndLevels() {
        // 准备参数：包含分组和级别
        EmergencyPlanCreateReqVO createReqVO = new EmergencyPlanCreateReqVO();
        createReqVO.setPlanNo("PLAN-FULL-001");
        createReqVO.setPlanName("完整预案");
        createReqVO.setPlanType(1);
        createReqVO.setStatus("published");
        createReqVO.setPlanGroupId(1L);
        createReqVO.setPlanLevels(Arrays.asList("I", "II", "III"));

        // 调用
        Long planId = emergencyPlanService.createEmergencyPlan(createReqVO);

        // 断言
        assertNotNull(planId);
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(planId);
        assertNotNull(plan);
        assertEquals(1L, plan.getPlanGroupId());
        assertNotNull(plan.getPlanLevels());
        assertEquals(3, plan.getPlanLevels().size());
        assertTrue(plan.getPlanLevels().contains("I"));
        assertTrue(plan.getPlanLevels().contains("II"));
        assertTrue(plan.getPlanLevels().contains("III"));
    }

    @Test
    void testFilterByPlanGroup() {
        // 1. 创建分组
        // 假设分组ID为1（实际测试中需要先创建分组）

        // 2. 创建有分组的预案
        EmergencyPlanCreateReqVO plan1ReqVO = new EmergencyPlanCreateReqVO();
        plan1ReqVO.setPlanNo("PLAN-GROUP-001");
        plan1ReqVO.setPlanName("分组预案1");
        plan1ReqVO.setPlanType(1);
        plan1ReqVO.setStatus("published");
        plan1ReqVO.setPlanGroupId(1L);
        Long plan1Id = emergencyPlanService.createEmergencyPlan(plan1ReqVO);

        // 3. 创建未分组的预案
        EmergencyPlanCreateReqVO plan2ReqVO = new EmergencyPlanCreateReqVO();
        plan2ReqVO.setPlanNo("PLAN-UNGROUP-001");
        plan2ReqVO.setPlanName("未分组预案");
        plan2ReqVO.setPlanType(1);
        plan2ReqVO.setStatus("published");
        // planGroupId 为 null
        Long plan2Id = emergencyPlanService.createEmergencyPlan(plan2ReqVO);

        // 4. 查询指定分组的预案
        EmergencyPlanPageReqVO pageReqVO1 = new EmergencyPlanPageReqVO();
        pageReqVO1.setPageNo(1);
        pageReqVO1.setPageSize(10);
        pageReqVO1.setPlanGroupId(1L);
        PageResult<EmergencyPlanRespVO> result1 = emergencyPlanService.getEmergencyPlanPage(pageReqVO1);
        assertNotNull(result1);
        assertTrue(result1.getList().stream().anyMatch(p -> p.getId().equals(plan1Id)));

        // 5. 查询未分组的预案（planGroupId=-1）
        EmergencyPlanPageReqVO pageReqVO2 = new EmergencyPlanPageReqVO();
        pageReqVO2.setPageNo(1);
        pageReqVO2.setPageSize(10);
        pageReqVO2.setPlanGroupId(-1L); // -1 表示未分组
        PageResult<EmergencyPlanRespVO> result2 = emergencyPlanService.getEmergencyPlanPage(pageReqVO2);
        assertNotNull(result2);
        assertTrue(result2.getList().stream().anyMatch(p -> p.getId().equals(plan2Id)));
    }

    @Test
    void testRecommendPlans() {
        // 1. 创建有级别的预案
        EmergencyPlanCreateReqVO plan1ReqVO = new EmergencyPlanCreateReqVO();
        plan1ReqVO.setPlanNo("PLAN-LEVEL-I");
        plan1ReqVO.setPlanName("I级预案");
        plan1ReqVO.setPlanType(1);
        plan1ReqVO.setStatus("published");
        plan1ReqVO.setPlanLevels(Arrays.asList("I"));
        Long plan1Id = emergencyPlanService.createEmergencyPlan(plan1ReqVO);

        // 2. 创建多级别的预案
        EmergencyPlanCreateReqVO plan2ReqVO = new EmergencyPlanCreateReqVO();
        plan2ReqVO.setPlanNo("PLAN-LEVEL-II-III");
        plan2ReqVO.setPlanName("II/III级预案");
        plan2ReqVO.setPlanType(1);
        plan2ReqVO.setStatus("published");
        plan2ReqVO.setPlanLevels(Arrays.asList("II", "III"));
        Long plan2Id = emergencyPlanService.createEmergencyPlan(plan2ReqVO);

        // 3. 创建没有级别的预案（不应被推荐）
        EmergencyPlanCreateReqVO plan3ReqVO = new EmergencyPlanCreateReqVO();
        plan3ReqVO.setPlanNo("PLAN-NO-LEVEL");
        plan3ReqVO.setPlanName("无级别预案");
        plan3ReqVO.setPlanType(1);
        plan3ReqVO.setStatus("published");
        // planLevels 为 null
        Long plan3Id = emergencyPlanService.createEmergencyPlan(plan3ReqVO);

        // 4. 推荐II级预案
        List<EmergencyPlanRespVO> recommended = emergencyPlanService.recommendPlans("II", 1);

        // 5. 断言
        assertNotNull(recommended);
        assertTrue(recommended.stream().anyMatch(p -> p.getId().equals(plan2Id)));
        assertFalse(recommended.stream().anyMatch(p -> p.getId().equals(plan1Id))); // I级预案不应被推荐
        assertFalse(recommended.stream().anyMatch(p -> p.getId().equals(plan3Id))); // 无级别预案不应被推荐
    }
}

