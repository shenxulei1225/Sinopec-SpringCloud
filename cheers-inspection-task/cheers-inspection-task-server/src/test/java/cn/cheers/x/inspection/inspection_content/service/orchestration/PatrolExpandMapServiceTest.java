package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.service.orchestration.impl.PatrolExpandMapServiceImpl;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandRespDTO;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.platform.topology.api.PathNetworkApi;
import cn.cheers.x.module.platform.topology.api.dto.PathNetworkSummaryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatrolExpandMapService 单元测试")
class PatrolExpandMapServiceTest {

    private static final Long FACILITY_ID = 1L;

    @Mock
    private PatrolTaskEntityStore patrolTaskEntityStore;
    @Mock
    private PathNetworkApi pathNetworkApi;

    private PatrolExpandMapServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PatrolExpandMapServiceImpl(patrolTaskEntityStore, pathNetworkApi);
    }

    @Test
    @DisplayName("0 条匹配路网 → 显式失败")
    void expand_zeroMatchingNetworks_fails() {
        when(pathNetworkApi.listPublished(FACILITY_ID)).thenReturn(CommonResult.success(List.of(
                network("net-uav", List.of("UAV"), "PUBLISHED"))));

        assertThrows(ServiceException.class, () -> service.expand(req(List.of(10L), List.of("sta-1"), null)));
    }

    @Test
    @DisplayName("1 条匹配路网 → 选用并展开请求里的 stopIds")
    void expand_oneMatchingNetwork_succeeds() {
        when(pathNetworkApi.listPublished(FACILITY_ID)).thenReturn(CommonResult.success(List.of(
                network("net-a", List.of("HUMAN"), "PUBLISHED"))));

        PatrolExpandRespDTO resp = service.expand(req(List.of(10L), List.of("sta-1"), null));

        assertEquals("net-a", resp.getNetworkRef());
        assertEquals(List.of("sta-1"), resp.getStopIds());
        assertEquals("HUMAN", resp.getInspectionType());
        assertNull(resp.getWorkItems().get(0).getPayload().get("workMinutes"));
    }

    @Test
    @DisplayName("任务页人工方式 MANUAL 按 HUMAN 选网")
    void expand_mapsManualToHumanNetwork() {
        when(pathNetworkApi.listPublished(FACILITY_ID)).thenReturn(CommonResult.success(List.of(
                network("net-a", List.of("HUMAN"), "PUBLISHED"))));

        PatrolExpandReqDTO request = req(List.of(10L), List.of("sta-1"), null);
        request.setInspectionType("MANUAL");
        PatrolExpandRespDTO resp = service.expand(request);

        assertEquals("HUMAN", resp.getInspectionType());
        assertEquals("net-a", resp.getNetworkRef());
    }

    @Test
    @DisplayName("expand：透传 startStopId / endStopId 到工作项 payload")
    void expand_forwardsStartStopId() {
        when(pathNetworkApi.listPublished(FACILITY_ID)).thenReturn(CommonResult.success(List.of(
                network("net-a", List.of("HUMAN"), "PUBLISHED"))));

        PatrolExpandReqDTO request = req(List.of(10L), List.of("sta-1"), null);
        request.setStartStopId("sta-depot");
        request.setEndStopId("sta-pad");
        request.setReturnToStart(Boolean.FALSE);

        PatrolExpandRespDTO resp = service.expand(request);

        Map<String, Object> payload = resp.getWorkItems().get(0).getPayload();
        assertEquals("sta-depot", payload.get("startStopId"));
        assertEquals("sta-pad", payload.get("endStopId"));
        assertEquals(Boolean.FALSE, payload.get("returnToStart"));
    }

    @Test
    @DisplayName("多条匹配路网且未指定 preferred → 显式失败")
    void expand_manyNetworksWithoutPreferred_fails() {
        when(pathNetworkApi.listPublished(FACILITY_ID)).thenReturn(CommonResult.success(List.of(
                network("net-a", List.of("HUMAN"), "PUBLISHED"),
                network("net-b", List.of("HUMAN"), "PUBLISHED"))));

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.expand(req(List.of(10L), List.of("sta-1"), null)));
        assertTrue(ex.getMessage() != null && ex.getMessage().contains("多条已发布路网"));
    }

    @Test
    @DisplayName("多条匹配路网且 preferred 命中 → 选用指定路网")
    void expand_manyNetworksWithPreferred_succeeds() {
        when(pathNetworkApi.listPublished(FACILITY_ID)).thenReturn(CommonResult.success(List.of(
                network("net-a", List.of("HUMAN"), "PUBLISHED"),
                network("net-b", List.of("HUMAN"), "PUBLISHED"))));

        PatrolExpandRespDTO resp = service.expand(req(List.of(10L), List.of("sta-1"), "net-b"));

        assertEquals("net-b", resp.getNetworkRef());
    }

    @Test
    @DisplayName("缺检查项停靠点 → 显式失败，不回读绑定表")
    void expand_missingStopIds_fails() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.expand(req(List.of(10L, 11L), List.of(), null)));
        assertTrue(ex.getMessage() != null && ex.getMessage().contains("停靠点"));
    }

    @Test
    @DisplayName("确认快照：从总任务 plannedRoute 展开 networkRef / stopIds")
    void expand_fromConfirmedSnapshot_readsEntityPlannedRoute() {
        Map<String, Object> planned = new java.util.LinkedHashMap<>();
        planned.put("networkRef", "net_44_draft");
        planned.put("stopIds", List.of("sta-1", "sta-2"));
        planned.put("totalDistanceMeters", 80L);
        when(patrolTaskEntityStore.require(9L)).thenReturn(new PatrolTaskDraft(
                9L, "task", "巡检", 1L, "ROBOT", null, null, null,
                null, planned, "draft", 2, "sta-start", "sta-end"));

        PatrolExpandReqDTO request = PatrolExpandReqDTO.builder()
                .taskId(9L)
                .fromConfirmedSnapshot(true)
                .build();

        PatrolExpandRespDTO resp = service.expand(request);

        assertEquals("net_44_draft", resp.getNetworkRef());
        assertEquals(List.of("sta-1", "sta-2"), resp.getStopIds());
        assertEquals("GROUND_ROBOT", resp.getInspectionType());
        assertEquals(2, resp.getWorkItems().get(0).getDurationEstimateMinutes());
    }

    @Test
    @DisplayName("缺巡检方式 → 显式失败，不查对象台账")
    void expand_missingInspectionType_fails() {
        PatrolExpandReqDTO request = req(List.of(10L, 11L), List.of("sta-1"), null);
        request.setInspectionType(null);
        ServiceException ex = assertThrows(ServiceException.class, () -> service.expand(request));
        assertTrue(ex.getMessage() != null && ex.getMessage().contains("巡检方式"));
    }

    private static PatrolExpandReqDTO req(List<Long> objectIds, List<String> stopIds, String preferredNetworkRef) {
        return PatrolExpandReqDTO.builder()
                .facilityId(FACILITY_ID)
                .objectIds(objectIds)
                .stopIds(stopIds)
                .inspectionType("HUMAN")
                .preferredNetworkRef(preferredNetworkRef)
                .build();
    }

    private static PathNetworkSummaryDTO network(String ref, List<String> types, String status) {
        return PathNetworkSummaryDTO.builder()
                .networkRef(ref)
                .facilityId(FACILITY_ID)
                .status(status)
                .applicableEquipmentTypes(types)
                .build();
    }
}
