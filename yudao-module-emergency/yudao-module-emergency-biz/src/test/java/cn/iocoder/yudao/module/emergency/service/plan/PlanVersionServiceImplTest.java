package cn.iocoder.yudao.module.emergency.service.plan;

import cn.cheers.x.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanStepDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanStepMapper;
import cn.iocoder.yudao.module.emergency.framework.test.BasePostgresDbUnitTest;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 预案版本管理Service单元测试
 */
@Import({PlanVersionServiceImpl.class})
@DisplayName("预案版本管理Service测试")
class PlanVersionServiceImplTest extends BasePostgresDbUnitTest {

    @Resource
    private PlanVersionService planVersionService;

    @Resource
    private EmergencyPlanMapper emergencyPlanMapper;

    @Resource
    private EmergencyPlanStepMapper emergencyPlanStepMapper;

    private Long planId;

    @BeforeEach
    void setUp() {
        // 创建测试预案
        String uniquePlanNo = "P" + System.nanoTime() % 100000;
        EmergencyPlanDO plan = EmergencyPlanDO.builder()
                .planNo(uniquePlanNo)
                .planName("测试预案")
                .planType(1)
                .status("PUBLISHED")
                .isVersionLocked(false) // 初始未锁定
                .build();
        emergencyPlanMapper.insert(plan);
        planId = plan.getId();

        // 创建测试步骤
        EmergencyPlanStepDO step = EmergencyPlanStepDO.builder()
                .planId(planId)
                .stepOrder(1)
                .stepTitle("测试步骤")
                .stepDescription("测试步骤描述")
                .build();
        emergencyPlanStepMapper.insert(step);
    }

    @Test
    @DisplayName("发布版本并锁定-正常情况")
    void testPublishVersion_Success() {
        // Given
        String versionNumber = "v1.0.0";

        // When
        planVersionService.publishVersion(planId, versionNumber);

        // Then
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(planId);
        assertNotNull(plan);
        assertEquals(versionNumber, plan.getVersionNumber());
        assertTrue(plan.getIsVersionLocked());
    }

    @Test
    @DisplayName("发布版本并锁定-预案不存在")
    void testPublishVersion_PlanNotExists() {
        // When & Then
        assertThrows(Exception.class, () -> {
            planVersionService.publishVersion(999999L, "v1.0.0");
        });
    }

    @Test
    @DisplayName("解锁版本-正常情况")
    void testUnlockVersion_Success() {
        // Given: 先锁定版本
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(planId);
        plan.setVersionNumber("v1.0.0");
        plan.setIsVersionLocked(true);
        emergencyPlanMapper.updateById(plan);

        // When
        planVersionService.unlockVersion(planId);

        // Then
        plan = emergencyPlanMapper.selectById(planId);
        assertNotNull(plan);
        assertFalse(plan.getIsVersionLocked());
    }

    @Test
    @DisplayName("解锁版本-预案不存在")
    void testUnlockVersion_PlanNotExists() {
        // When & Then
        assertThrows(Exception.class, () -> {
            planVersionService.unlockVersion(999999L);
        });
    }

    @Test
    @DisplayName("获取预案快照-版本已锁定")
    void testGetPlanSnapshot_VersionLocked() {
        // Given: 锁定版本
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(planId);
        plan.setVersionNumber("v1.0.0");
        plan.setIsVersionLocked(true);
        emergencyPlanMapper.updateById(plan);

        // When
        String snapshot = planVersionService.getPlanSnapshot(planId);

        // Then
        assertNotNull(snapshot);
        // 验证快照包含预案基本信息
        // 使用 TypeReference 指定泛型类型，避免未检查的类型转换警告
        Map<String, Object> snapshotMap = JsonUtils.parseObject(snapshot, new TypeReference<Map<String, Object>>() {});
        assertNotNull(snapshotMap);
        assertEquals(planId, Long.valueOf(snapshotMap.get("planId").toString()));
        assertNotNull(snapshotMap.get("steps"));
    }

    @Test
    @DisplayName("获取预案快照-版本未锁定")
    void testGetPlanSnapshot_VersionNotLocked() {
        // Given: 版本未锁定（默认状态）

        // When
        String snapshot = planVersionService.getPlanSnapshot(planId);

        // Then
        assertNull(snapshot); // 版本未锁定，返回null（使用实时引用）
    }

    @Test
    @DisplayName("获取预案快照-预案不存在")
    void testGetPlanSnapshot_PlanNotExists() {
        // When & Then
        assertThrows(Exception.class, () -> {
            planVersionService.getPlanSnapshot(999999L);
        });
    }
}





