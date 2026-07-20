package cn.iocoder.yudao.module.emergency.service.plan;

import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanMapper;
import cn.iocoder.yudao.module.emergency.framework.test.BasePostgresDbUnitTest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 预案状态管理Service单元测试
 */
@Import({PlanStatusServiceImpl.class})
@DisplayName("预案状态管理Service测试")
class PlanStatusServiceImplTest extends BasePostgresDbUnitTest {

    @Resource
    private PlanStatusService planStatusService;

    @Resource
    private EmergencyPlanMapper emergencyPlanMapper;

    private Long planId;

    @BeforeEach
    void setUp() {
        // 创建测试预案
        String uniquePlanNo = "P" + System.nanoTime() % 100000;
        EmergencyPlanDO plan = EmergencyPlanDO.builder()
                .planNo(uniquePlanNo)
                .planName("测试预案")
                .planType(1)
                .status("DRAFT") // 初始状态为草稿
                .build();
        emergencyPlanMapper.insert(plan);
        planId = plan.getId();
    }

    @Test
    @DisplayName("发布预案-正常情况")
    void testPublishPlan_Success() {
        // When
        planStatusService.publishPlan(planId);

        // Then
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(planId);
        assertNotNull(plan);
        assertEquals("PUBLISHED", plan.getStatus());
    }

    @Test
    @DisplayName("发布预案-预案不存在")
    void testPublishPlan_PlanNotExists() {
        // When & Then
        assertThrows(Exception.class, () -> {
            planStatusService.publishPlan(999999L);
        });
    }

    @Test
    @DisplayName("发布预案-状态转换无效（非草稿状态）")
    void testPublishPlan_InvalidStatusTransition() {
        // Given: 将预案状态设置为已发布
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(planId);
        plan.setStatus("PUBLISHED");
        emergencyPlanMapper.updateById(plan);

        // When & Then
        assertThrows(Exception.class, () -> {
            planStatusService.publishPlan(planId);
        });
    }

    @Test
    @DisplayName("停用预案-正常情况")
    void testDisablePlan_Success() {
        // Given: 先将预案发布
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(planId);
        plan.setStatus("PUBLISHED");
        emergencyPlanMapper.updateById(plan);

        // When
        planStatusService.disablePlan(planId);

        // Then
        plan = emergencyPlanMapper.selectById(planId);
        assertNotNull(plan);
        assertEquals("DISABLED", plan.getStatus());
    }

    @Test
    @DisplayName("停用预案-状态转换无效（非已发布状态）")
    void testDisablePlan_InvalidStatusTransition() {
        // Given: 预案状态为草稿
        // plan状态已经是DRAFT，无需修改

        // When & Then
        assertThrows(Exception.class, () -> {
            planStatusService.disablePlan(planId);
        });
    }

    @Test
    @DisplayName("验证预案可用性-已发布状态")
    void testIsPlanAvailable_Published() {
        // Given: 将预案状态设置为已发布
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(planId);
        plan.setStatus("PUBLISHED");
        emergencyPlanMapper.updateById(plan);

        // When
        boolean available = planStatusService.isPlanAvailable(planId);

        // Then
        assertTrue(available);
    }

    @Test
    @DisplayName("验证预案可用性-草稿状态")
    void testIsPlanAvailable_Draft() {
        // Given: 预案状态为草稿（默认状态）

        // When
        boolean available = planStatusService.isPlanAvailable(planId);

        // Then
        assertFalse(available);
    }

    @Test
    @DisplayName("验证预案可用性-已停用状态")
    void testIsPlanAvailable_Disabled() {
        // Given: 将预案状态设置为已停用
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(planId);
        plan.setStatus("DISABLED");
        emergencyPlanMapper.updateById(plan);

        // When
        boolean available = planStatusService.isPlanAvailable(planId);

        // Then
        assertFalse(available);
    }

    @Test
    @DisplayName("验证预案可用性-预案不存在")
    void testIsPlanAvailable_PlanNotExists() {
        // When
        boolean available = planStatusService.isPlanAvailable(999999L);

        // Then
        assertFalse(available);
    }
}






