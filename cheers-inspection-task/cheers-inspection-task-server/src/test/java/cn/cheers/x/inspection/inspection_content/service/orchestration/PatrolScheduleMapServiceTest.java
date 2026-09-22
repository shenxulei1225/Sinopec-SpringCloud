package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.service.orchestration.impl.PatrolScheduleMapServiceImpl;
import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapRespDTO;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.platform.orchestration.route.RoutePayloadKeys;
import cn.cheers.x.module.platform.topology.api.PathNetworkApi;
import cn.cheers.x.module.platform.topology.api.dto.PathNetworkSummaryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatrolScheduleMapService 单元测试")
class PatrolScheduleMapServiceTest {

    private static final Long FACILITY_ID = 1L;

    @Mock
    private PatrolTaskEntityStore patrolTaskEntityStore;
    @Mock
    private PathNetworkApi pathNetworkApi;

    private PatrolScheduleMapServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PatrolScheduleMapServiceImpl(patrolTaskEntityStore, pathNetworkApi);
    }

    @Test
    @DisplayName("0 条匹配路网 → 显式失败")
    void expand_zeroMatchingNetworks_fails() {
        when(pathNetworkApi.listPublished(FACILITY_ID)).thenReturn(CommonResult.success(List.of(
                network("net-uav", List.of("UAV"), "PUBLISHED"))));

        assertThrows(ServiceException.class, () -> service.expandPatrolWorkItems(req(List.of(10L), List.of("sta-1"), null)));
    }

    @Test
    @DisplayName("1 条匹配路网 → 选用并展开请求里的 stopIds")
    void expand_oneMatchingNetwork_succeeds() {
        when(pathNetworkApi.listPublished(FACILITY_ID)).thenReturn(CommonResult.success(List.of(
                network("net-a", List.of("HUMAN"), "PUBLISHED"))));

        PatrolScheduleMapRespDTO resp = service.expandPatrolWorkItems(req(List.of(10L), List.of("sta-1"), null));

        assertEquals("net-a", resp.getNetworkRef());
        assertEquals(List.of("sta-1"), resp.getStopIds());
        assertEquals("HUMAN", resp.getInspectionType());
        assertNull(resp.getWorkItems().get(0).getPayload().get("estimatedActionDuration"));
    }

    @Test
    @DisplayName("任务页人工方式 MANUAL 按 HUMAN 选网")
    void expand_mapsManualToHumanNetwork() {
        when(pathNetworkApi.listPublished(FACILITY_ID)).thenReturn(CommonResult.success(List.of(
                network("net-a", List.of("HUMAN"), "PUBLISHED"))));

        PatrolScheduleMapReqDTO request = req(List.of(10L), List.of("sta-1"), null);
        request.setInspectionType("MANUAL");
        PatrolScheduleMapRespDTO resp = service.expandPatrolWorkItems(request);

        assertEquals("HUMAN", resp.getInspectionType());
        assertEquals("net-a", resp.getNetworkRef());
    }

    @Test
    @DisplayName("expand：透传 startStopId / endStopId 到工作项 payload")
    void expand_forwardsStartStopId() {
        when(pathNetworkApi.listPublished(FACILITY_ID)).thenReturn(CommonResult.success(List.of(
                network("net-a", List.of("HUMAN"), "PUBLISHED"))));

        PatrolScheduleMapReqDTO request = req(List.of(10L), List.of("sta-1"), null);
        request.setStartStopId("sta-depot");
        request.setEndStopId("sta-pad");
        request.setReturnToStart(Boolean.FALSE);

        PatrolScheduleMapRespDTO resp = service.expandPatrolWorkItems(request);

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
                () -> service.expandPatrolWorkItems(req(List.of(10L), List.of("sta-1"), null)));
        assertTrue(ex.getMessage() != null && ex.getMessage().contains("多条已发布路网"));
    }

    @Test
    @DisplayName("多条匹配路网且 preferred 命中 → 选用指定路网")
    void expand_manyNetworksWithPreferred_succeeds() {
        when(pathNetworkApi.listPublished(FACILITY_ID)).thenReturn(CommonResult.success(List.of(
                network("net-a", List.of("HUMAN"), "PUBLISHED"),
                network("net-b", List.of("HUMAN"), "PUBLISHED"))));

        PatrolScheduleMapRespDTO resp = service.expandPatrolWorkItems(req(List.of(10L), List.of("sta-1"), "net-b"));

        assertEquals("net-b", resp.getNetworkRef());
    }

    @Test
    @DisplayName("缺检查项停靠点 → 显式失败，不回读绑定表")
    void expand_missingStopIds_fails() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.expandPatrolWorkItems(req(List.of(10L, 11L), List.of(), null)));
        assertTrue(ex.getMessage() != null && ex.getMessage().contains("停靠点"));
    }

    @Test
    @DisplayName("确认快照：从总任务 plannedRoute 展开 networkRef / stopIds")
    void expand_fromSavedRouteSnapshot_readsEntityPlannedRoute() {
        Map<String, Object> planned = new java.util.LinkedHashMap<>();
        planned.put("networkRef", "net_44_draft");
        planned.put("stopIds", List.of("sta-1", "sta-2"));
        planned.put("totalDistanceMeters", 80L);
        when(patrolTaskEntityStore.require(9L)).thenReturn(new PatrolTaskDraft(
                9L, "task", "巡检", 1L, "ROBOT", null, null, null,
                null, planned, "draft", 2, "sta-start", "sta-end", null, null, null, null, null, null));

        PatrolScheduleMapReqDTO request = PatrolScheduleMapReqDTO.builder()
                .taskId(9L)
                .fromSavedRouteSnapshot(true)
                .build();

        PatrolScheduleMapRespDTO resp = service.expandPatrolWorkItems(request);

        assertEquals("net_44_draft", resp.getNetworkRef());
        assertEquals(List.of("sta-1", "sta-2"), resp.getStopIds());
        assertEquals("GROUND_ROBOT", resp.getInspectionType());
        assertEquals(2, resp.getWorkItems().get(0).getEstimatedDuration());
        assertEquals("task", resp.getWorkItems().get(0).getEntityTypeCode());
        assertEquals("patrol_task", resp.getWorkItems().get(0).getSourceModelCode());
        assertEquals("9", resp.getWorkItems().get(0).getSourceInstanceId());
    }

    @Test
    @DisplayName("确认快照：有执行设备时写入 resourceRequirements 供 SOLVE 占窗")
    void expand_fromSavedRouteSnapshot_setsResourceRequirements() {
        ExecutionDeviceBinding binding = new ExecutionDeviceBinding();
        binding.setEquipmentId(42L);
        Map<String, Object> planned = new java.util.LinkedHashMap<>();
        planned.put("stopIds", List.of("sta-1", "sta-2"));
        planned.put("totalDistanceMeters", 80L);
        when(patrolTaskEntityStore.require(9L)).thenReturn(new PatrolTaskDraft(
                9L, "task", "巡检", 1L, "ROBOT", null, binding, null,
                null, planned, "draft", 2, "sta-start", "sta-end", null, null, null, null, null, null));

        PatrolScheduleMapRespDTO resp = service.expandPatrolWorkItems(PatrolScheduleMapReqDTO.builder()
                .taskId(9L)
                .fromSavedRouteSnapshot(true)
                .build());

        assertEquals(1, resp.getWorkItems().get(0).getResourceRequirements().size());
        assertEquals("GROUND_ROBOT", resp.getWorkItems().get(0).getResourceRequirements().get(0).getResourceType());
        assertEquals("42", resp.getWorkItems().get(0).getResourceRequirements().get(0).getFixedResourceId());
        assertEquals(1, resp.getWorkItems().get(0).getResourceRequirements().get(0).getQuantity());
    }

    @Test
    @DisplayName("previewOrchestration 试排：按已保存模板复制多条带计划时刻的工作项")
    void expand_fromSavedRouteSnapshot_clonesByScheduleOccurrences() {
        Map<String, Object> planned = new java.util.LinkedHashMap<>();
        planned.put("stopIds", List.of("sta-1", "sta-2"));
        planned.put("totalDistanceMeters", 80L);
        when(patrolTaskEntityStore.require(9L)).thenReturn(new PatrolTaskDraft(
                9L, "task", "巡检", 1L, "ROBOT", null, null, null,
                null, planned, "draft", 2, "sta-start", "sta-end", null, null,
                dailySchedule("2026-09-01", "2026-09-02", List.of("09:00", "18:00")), null, null, null));

        PatrolScheduleMapRespDTO resp = service.expandPatrolWorkItems(PatrolScheduleMapReqDTO.builder()
                .taskId(9L)
                .fromSavedRouteSnapshot(true)
                .expandWorkItemsFromScheduleTemplate(true)
                .build());

        assertEquals(4, resp.getWorkItems().size());
        assertEquals("2026-09-01T09:00:00+08:00",
                resp.getWorkItems().get(0).getTimePreferences().getAllowedWindows().get(0).getStart());
        assertEquals("2026-09-02T18:00:00+08:00",
                resp.getWorkItems().get(3).getTimePreferences().getAllowedWindows().get(0).getStart());
        assertEquals(2, resp.getWorkItems().get(0).getEstimatedDuration());
    }

    @Test
    @DisplayName("previewOrchestration 试排：模板展不开计划时刻则显式失败")
    void expand_fromSavedRouteSnapshot_missingSchedule_fails() {
        Map<String, Object> planned = new java.util.LinkedHashMap<>();
        planned.put("stopIds", List.of("sta-1"));
        planned.put("totalDistanceMeters", 80L);
        when(patrolTaskEntityStore.require(9L)).thenReturn(new PatrolTaskDraft(
                9L, "task", "巡检", 1L, "ROBOT", null, null, null,
                null, planned, "draft", 2, "sta-start", "sta-end", null, null, null, null, null, null));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.expandPatrolWorkItems(
                PatrolScheduleMapReqDTO.builder()
                        .taskId(9L)
                        .fromSavedRouteSnapshot(true)
                        .expandWorkItemsFromScheduleTemplate(true)
                        .build()));
        assertTrue(ex.getMessage() != null && ex.getMessage().contains("排期计划时刻"));
    }

    @Test
    @DisplayName("确认快照无 networkRef 仍可排期展开")
    void expand_fromSavedRouteSnapshot_withoutNetworkRef_succeeds() {
        Map<String, Object> planned = new java.util.LinkedHashMap<>();
        planned.put("stopIds", List.of("sta-1", "sta-2"));
        planned.put("totalDistanceMeters", 80L);
        when(patrolTaskEntityStore.require(9L)).thenReturn(new PatrolTaskDraft(
                9L, "task", "巡检", 1L, "ROBOT", null, null, null,
                null, planned, "draft", 2, "sta-start", "sta-end", null, null, null, null, null, null));

        PatrolScheduleMapReqDTO request = PatrolScheduleMapReqDTO.builder()
                .taskId(9L)
                .fromSavedRouteSnapshot(true)
                .build();

        PatrolScheduleMapRespDTO resp = service.expandPatrolWorkItems(request);

        assertNull(resp.getNetworkRef());
        assertEquals(List.of("sta-1", "sta-2"), resp.getStopIds());
        assertNull(resp.getWorkItems().get(0).getPayload().get("networkRef"));
    }

    @Test
    @DisplayName("缺巡检方式 → 显式失败，不查对象台账")
    void expand_missingInspectionType_fails() {
        PatrolScheduleMapReqDTO request = req(List.of(10L, 11L), List.of("sta-1"), null);
        request.setInspectionType(null);
        ServiceException ex = assertThrows(ServiceException.class, () -> service.expandPatrolWorkItems(request));
        assertTrue(ex.getMessage() != null && ex.getMessage().contains("巡检方式"));
    }

    private static PatrolScheduleMapReqDTO req(List<Long> objectIds, List<String> stopIds, String preferredNetworkRef) {
        return PatrolScheduleMapReqDTO.builder()
                .facilityId(FACILITY_ID)
                .objectIds(objectIds)
                .stopIds(stopIds)
                .inspectionType("HUMAN")
                .preferredNetworkRef(preferredNetworkRef)
                .build();
    }

    private static Map<String, Object> dailySchedule(String start, String end, List<String> timePoints) {
        Map<String, Object> range = new java.util.LinkedHashMap<>();
        range.put("startDate", start);
        range.put("endDate", end);
        Map<String, Object> values = new java.util.LinkedHashMap<>();
        values.put("scheduleMode", 1);
        values.put("repeatMode", 2);
        values.put("timePoints", timePoints);
        values.put("repeatRange", range);
        Map<String, Object> item = new java.util.LinkedHashMap<>();
        item.put("enabled", true);
        item.put("values", values);
        Map<String, Object> config = new java.util.LinkedHashMap<>();
        config.put("items", List.of(item));
        return config;
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
