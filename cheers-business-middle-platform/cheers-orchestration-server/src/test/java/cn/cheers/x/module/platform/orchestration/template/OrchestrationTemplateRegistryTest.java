package cn.cheers.x.module.platform.orchestration.template;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.cheers.x.module.platform.orchestration.phase.OrchestrationPhase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("OrchestrationTemplateRegistry 单元测试")
class OrchestrationTemplateRegistryTest {

    private OrchestrationTemplateRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new OrchestrationTemplateRegistry();
    }

    @Test
    @DisplayName("未知 ref 抛错")
    void require_unknownRef_throws() {
        assertThrows(ServiceException.class, () -> registry.require("orch.unknown"));
    }

    @Test
    @DisplayName("标准模板阶段顺序 EXPAND→SOLVE→PERSIST")
    void standardTemplate_phaseOrder() {
        OrchestrationTemplate t = registry.require(OrchestrationRefs.STANDARD_EXPAND_SOLVE_PERSIST_V1);
        assertEquals(List.of(
                OrchestrationPhase.EXPAND,
                OrchestrationPhase.SOLVE,
                OrchestrationPhase.PERSIST), t.getPhases());
    }

    @Test
    @DisplayName("巡检路线预览模板 EXPAND→ROUTE 且挂载 expand handler")
    void patrolRoutePreview_phaseOrder() {
        OrchestrationTemplate t = registry.require(OrchestrationRefs.PATROL_ROUTE_PREVIEW_V1);
        assertEquals(List.of(
                OrchestrationPhase.EXPAND,
                OrchestrationPhase.ROUTE), t.getPhases());
        assertEquals("patrol.expand_map_v1", t.getHandlerIds().get(OrchestrationPhase.EXPAND));
        assertEquals("platform.route.plan_v1", t.getHandlerIds().get(OrchestrationPhase.ROUTE));
    }

    @Test
    @DisplayName("巡检路线确认模板阶段顺序 EXPAND→ROUTE→CONFIRM")
    void patrolRouteConfirm_phaseOrder() {
        OrchestrationTemplate t = registry.require(OrchestrationRefs.PATROL_ROUTE_CONFIRM_V1);
        assertEquals(List.of(
                OrchestrationPhase.EXPAND,
                OrchestrationPhase.ROUTE,
                OrchestrationPhase.CONFIRM), t.getPhases());
    }

    @Test
    @DisplayName("巡检启用模板阶段顺序 EXPAND→SOLVE→PERSIST")
    void patrolScheduleEnable_phaseOrder() {
        OrchestrationTemplate t = registry.require(OrchestrationRefs.PATROL_SCHEDULE_ENABLE_V1);
        assertEquals(List.of(
                OrchestrationPhase.EXPAND,
                OrchestrationPhase.SOLVE,
                OrchestrationPhase.PERSIST), t.getPhases());
    }

    @Test
    @DisplayName("巡检重排模板阶段顺序 EXPAND→ROUTE→SOLVE→PERSIST")
    void patrolReplan_phaseOrder() {
        OrchestrationTemplate t = registry.require(OrchestrationRefs.PATROL_REPLAN_V1);
        assertEquals(List.of(
                OrchestrationPhase.EXPAND,
                OrchestrationPhase.ROUTE,
                OrchestrationPhase.SOLVE,
                OrchestrationPhase.PERSIST), t.getPhases());
    }
}
