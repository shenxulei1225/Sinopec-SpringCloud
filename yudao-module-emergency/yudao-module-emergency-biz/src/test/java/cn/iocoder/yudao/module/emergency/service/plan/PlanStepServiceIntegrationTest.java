package cn.iocoder.yudao.module.emergency.service.plan;

import cn.iocoder.yudao.module.emergency.framework.test.BasePostgresDbUnitTest;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.PlanStepCopyReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.PlanStepCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.PlanStepRespVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanStepDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanStepMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 预案步骤管理集成测试
 */
@Import(PlanStepServiceImpl.class)
class PlanStepServiceIntegrationTest extends BasePostgresDbUnitTest {

    @Resource
    private PlanStepService planStepService;

    @Resource
    private EmergencyPlanStepMapper planStepMapper;

    @Resource
    private EmergencyPlanMapper emergencyPlanMapper;

    private Long planId;

    @BeforeEach
    void setUp() {
        // 创建测试预案 - 使用唯一plan_no避免冲突
        String uniquePlanNo = "P" + System.nanoTime() % 100000; // 保持在50字符以内
        EmergencyPlanDO plan = EmergencyPlanDO.builder()
                .planNo(uniquePlanNo)
                .planName("测试预案")
                .planType(1)
                .status("published")
                .build();
        emergencyPlanMapper.insert(plan);
        planId = plan.getId();
    }

    @Test
    void testCreateStepWithTreeStructure() {
        // 1. 创建根步骤
        PlanStepCreateReqVO rootStepReqVO = new PlanStepCreateReqVO();
        rootStepReqVO.setPlanId(planId);
        rootStepReqVO.setPlanLevel("II");
        rootStepReqVO.setParentId(null);
        rootStepReqVO.setStepOrder(1);
        rootStepReqVO.setStepTitle("响应启动");
        rootStepReqVO.setStepStage("response");
        rootStepReqVO.setScheduledStartTime(0);
        Long rootStepId = planStepService.createStep(rootStepReqVO);

        // 2. 创建子步骤
        PlanStepCreateReqVO childStepReqVO = new PlanStepCreateReqVO();
        childStepReqVO.setPlanId(planId);
        childStepReqVO.setPlanLevel("II");
        childStepReqVO.setParentId(rootStepId);
        childStepReqVO.setStepOrder(1);
        childStepReqVO.setStepTitle("通知相关人员");
        childStepReqVO.setScheduledStartTime(5);
        Long childStepId = planStepService.createStep(childStepReqVO);

        // 3. 查询步骤树
        List<PlanStepRespVO> stepTree = planStepService.getStepTree(planId, "II");

        // 4. 断言
        assertNotNull(stepTree);
        assertFalse(stepTree.isEmpty());
        PlanStepRespVO root = stepTree.stream()
                .filter(s -> s.getId().equals(rootStepId))
                .findFirst()
                .orElse(null);
        assertNotNull(root);
        assertNotNull(root.getChildren());
        assertFalse(root.getChildren().isEmpty());
        assertEquals(childStepId, root.getChildren().get(0).getId());
    }

    @Test
    void testCopyStepWithSubtree() {
        // 1. 创建源步骤树
        PlanStepCreateReqVO rootStepReqVO = new PlanStepCreateReqVO();
        rootStepReqVO.setPlanId(planId);
        rootStepReqVO.setPlanLevel("II");
        rootStepReqVO.setParentId(null);
        rootStepReqVO.setStepOrder(1);
        rootStepReqVO.setStepTitle("响应启动");
        rootStepReqVO.setScheduledStartTime(0);
        Long sourceRootStepId = planStepService.createStep(rootStepReqVO);

        PlanStepCreateReqVO childStepReqVO = new PlanStepCreateReqVO();
        childStepReqVO.setPlanId(planId);
        childStepReqVO.setPlanLevel("II");
        childStepReqVO.setParentId(sourceRootStepId);
        childStepReqVO.setStepOrder(1);
        childStepReqVO.setStepTitle("通知相关人员");
        childStepReqVO.setScheduledStartTime(5);
        planStepService.createStep(childStepReqVO);

        // 2. 创建目标预案
        EmergencyPlanDO targetPlan = EmergencyPlanDO.builder()
                .planNo("P" + (System.nanoTime() % 100000 + 1))
                .planName("目标预案")
                .planType(1)
                .status("published")
                .build();
        emergencyPlanMapper.insert(targetPlan);
        Long targetPlanId = targetPlan.getId();

        // 3. 复制步骤（包括子树）
        PlanStepCopyReqVO copyReqVO = new PlanStepCopyReqVO();
        copyReqVO.setSourceStepId(sourceRootStepId);
        copyReqVO.setTargetPlanId(targetPlanId);
        copyReqVO.setTargetParentId(null);
        copyReqVO.setTimeOffsetMinutes(10); // 时间偏移10分钟
        copyReqVO.setTargetPlanLevel("III");
        Long newRootStepId = planStepService.copyStep(copyReqVO);

        // 4. 断言：新根步骤已创建
        assertNotNull(newRootStepId);
        EmergencyPlanStepDO newRootStep = planStepMapper.selectById(newRootStepId);
        assertNotNull(newRootStep);
        assertEquals(targetPlanId, newRootStep.getPlanId());
        assertEquals("III", newRootStep.getPlanLevel());
        assertEquals(10, newRootStep.getScheduledStartTime()); // 时间已偏移

        // 5. 断言：子步骤也被复制
        List<EmergencyPlanStepDO> newChildSteps = planStepMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyPlanStepDO>()
                        .eq(EmergencyPlanStepDO::getParentId, newRootStepId)
        );
        assertFalse(newChildSteps.isEmpty());
        assertEquals(15, newChildSteps.get(0).getScheduledStartTime()); // 子步骤时间也偏移
    }

    @Test
    void testDeleteStepWithCascade() {
        // 1. 创建步骤树
        PlanStepCreateReqVO rootStepReqVO = new PlanStepCreateReqVO();
        rootStepReqVO.setPlanId(planId);
        rootStepReqVO.setPlanLevel("II");
        rootStepReqVO.setParentId(null);
        rootStepReqVO.setStepOrder(1);
        rootStepReqVO.setStepTitle("响应启动");
        Long rootStepId = planStepService.createStep(rootStepReqVO);

        PlanStepCreateReqVO childStepReqVO = new PlanStepCreateReqVO();
        childStepReqVO.setPlanId(planId);
        childStepReqVO.setPlanLevel("II");
        childStepReqVO.setParentId(rootStepId);
        childStepReqVO.setStepOrder(1);
        childStepReqVO.setStepTitle("通知相关人员");
        Long childStepId = planStepService.createStep(childStepReqVO);

        // 2. 级联删除根步骤
        planStepService.deleteStep(rootStepId, true);

        // 3. 断言：根步骤和子步骤都被删除
        assertNull(planStepMapper.selectById(rootStepId));
        assertNull(planStepMapper.selectById(childStepId));
    }

    @Test
    void testDeleteStepWithPromoteChildren() {
        // 1. 创建步骤树
        PlanStepCreateReqVO rootStepReqVO = new PlanStepCreateReqVO();
        rootStepReqVO.setPlanId(planId);
        rootStepReqVO.setPlanLevel("II");
        rootStepReqVO.setParentId(null);
        rootStepReqVO.setStepOrder(1);
        rootStepReqVO.setStepTitle("响应启动");
        Long rootStepId = planStepService.createStep(rootStepReqVO);

        PlanStepCreateReqVO childStepReqVO = new PlanStepCreateReqVO();
        childStepReqVO.setPlanId(planId);
        childStepReqVO.setPlanLevel("II");
        childStepReqVO.setParentId(rootStepId);
        childStepReqVO.setStepOrder(1);
        childStepReqVO.setStepTitle("通知相关人员");
        Long childStepId = planStepService.createStep(childStepReqVO);

        // 2. 删除根步骤（提升子步骤）
        planStepService.deleteStep(rootStepId, false);

        // 3. 断言：根步骤被删除
        assertNull(planStepMapper.selectById(rootStepId));

        // 4. 断言：子步骤的parentId被设为null（提升为根步骤）
        EmergencyPlanStepDO childStep = planStepMapper.selectById(childStepId);
        assertNotNull(childStep);
        assertNull(childStep.getParentId());
    }

    @Test
    void testMoveStep() {
        // 1. 创建两个根步骤
        PlanStepCreateReqVO root1ReqVO = new PlanStepCreateReqVO();
        root1ReqVO.setPlanId(planId);
        root1ReqVO.setPlanLevel("II");
        root1ReqVO.setParentId(null);
        root1ReqVO.setStepOrder(1);
        root1ReqVO.setStepTitle("步骤1");
        Long root1Id = planStepService.createStep(root1ReqVO);

        PlanStepCreateReqVO root2ReqVO = new PlanStepCreateReqVO();
        root2ReqVO.setPlanId(planId);
        root2ReqVO.setPlanLevel("II");
        root2ReqVO.setParentId(null);
        root2ReqVO.setStepOrder(2);
        root2ReqVO.setStepTitle("步骤2");
        Long root2Id = planStepService.createStep(root2ReqVO);

        // 2. 创建子步骤
        PlanStepCreateReqVO childReqVO = new PlanStepCreateReqVO();
        childReqVO.setPlanId(planId);
        childReqVO.setPlanLevel("II");
        childReqVO.setParentId(root1Id);
        childReqVO.setStepOrder(1);
        childReqVO.setStepTitle("子步骤");
        Long childId = planStepService.createStep(childReqVO);

        // 3. 移动子步骤到根步骤2
        planStepService.moveStep(childId, root2Id);

        // 4. 断言
        EmergencyPlanStepDO childStep = planStepMapper.selectById(childId);
        assertNotNull(childStep);
        assertEquals(root2Id, childStep.getParentId());
    }

    @Test
    void testGetStepTreeFilteredByLevel() {
        // 1. 创建不同级别的步骤
        PlanStepCreateReqVO step1ReqVO = new PlanStepCreateReqVO();
        step1ReqVO.setPlanId(planId);
        step1ReqVO.setPlanLevel("I");
        step1ReqVO.setParentId(null);
        step1ReqVO.setStepOrder(1);
        step1ReqVO.setStepTitle("I级步骤");
        Long step1Id = planStepService.createStep(step1ReqVO);

        PlanStepCreateReqVO step2ReqVO = new PlanStepCreateReqVO();
        step2ReqVO.setPlanId(planId);
        step2ReqVO.setPlanLevel("II");
        step2ReqVO.setParentId(null);
        step2ReqVO.setStepOrder(1);
        step2ReqVO.setStepTitle("II级步骤");
        Long step2Id = planStepService.createStep(step2ReqVO);

        // 2. 查询II级步骤树
        List<PlanStepRespVO> stepTree = planStepService.getStepTree(planId, "II");

        // 3. 断言：只返回II级步骤
        assertNotNull(stepTree);
        assertTrue(stepTree.stream().anyMatch(s -> s.getId().equals(step2Id)));
        assertFalse(stepTree.stream().anyMatch(s -> s.getId().equals(step1Id)));
    }
}

