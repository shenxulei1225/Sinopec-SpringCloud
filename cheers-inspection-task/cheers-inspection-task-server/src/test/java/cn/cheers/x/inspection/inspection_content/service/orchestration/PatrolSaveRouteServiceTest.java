package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.inspection.inspection_content.service.orchestration.impl.PatrolSaveRouteServiceImpl;
import cn.cheers.x.inspection.orchestration.dto.PatrolSaveRouteReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolSaveRouteRespDTO;
import cn.cheers.x.inspection.task.service.task.PatrolPlannedRouteSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.orchestration.route.RoutePayloadKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatrolSaveRouteService 单元测试")
class PatrolSaveRouteServiceTest {

    private static final Long TASK_ID = 100L;
    private static final Long FACILITY_ID = 1L;

    @Mock
    private PatrolTaskEntityStore patrolTaskEntityStore;

    private PatrolSaveRouteServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PatrolSaveRouteServiceImpl(patrolTaskEntityStore);
    }

    @Test
    @DisplayName("plannedRoute 缺动作时长仍写入预览结果")
    void saveRoute_missingActionDuration_stillWrites() {
        WorkItemDTO workItem = happyWorkItem();
        Map<String, Object> payload = workItem.getPayload();
        Map<String, Object> planned = PatrolPlannedRouteSupport.asPlannedMap(payload.get(RoutePayloadKeys.PLANNED_ROUTE));
        planned.remove(RoutePayloadKeys.ESTIMATED_ACTION_DURATION);
        payload.put(RoutePayloadKeys.PLANNED_ROUTE, planned);

        PatrolSaveRouteReqDTO req = PatrolSaveRouteReqDTO.builder()
                .taskId(TASK_ID)
                .facilityId(FACILITY_ID)
                .workItems(List.of(workItem))
                .dryRun(false)
                .build();

        service.saveRoute(req);

        ArgumentCaptor<Map<String, Object>> plannedCaptor = ArgumentCaptor.forClass(Map.class);
        verify(patrolTaskEntityStore).writePlannedRoute(eq(TASK_ID), plannedCaptor.capture());
        assertTrue(!plannedCaptor.getValue().containsKey(RoutePayloadKeys.ESTIMATED_ACTION_DURATION));
        assertEquals(List.of("s1", "s2"), plannedCaptor.getValue().get("stopIds"));
    }

    @Test
    @DisplayName("dryRun=true → 不写库")
    void saveRoute_dryRun_skipsPersist() {
        PatrolSaveRouteRespDTO resp = service.saveRoute(happyReq(true));

        assertTrue(resp.getDryRun());
        verify(patrolTaskEntityStore, never()).writePlannedRoute(any(), any());
    }

    @Test
    @DisplayName("saveRoute → 写入工作项 plannedRoute")
    void saveRoute_writesPlannedRoute() {
        PatrolSaveRouteRespDTO resp = service.saveRoute(happyReq(false));

        assertEquals(TASK_ID, resp.getTaskId());

        ArgumentCaptor<Map<String, Object>> plannedCaptor = ArgumentCaptor.forClass(Map.class);
        verify(patrolTaskEntityStore).writePlannedRoute(eq(TASK_ID), plannedCaptor.capture());
        assertTrue(!plannedCaptor.getValue().containsKey(RoutePayloadKeys.ESTIMATED_ACTION_DURATION));
        assertEquals(45, plannedCaptor.getValue().get(RoutePayloadKeys.ESTIMATED_TRAVEL_DURATION));
    }

    private static PatrolSaveRouteReqDTO happyReq(boolean dryRun) {
        return PatrolSaveRouteReqDTO.builder()
                .taskId(TASK_ID)
                .facilityId(FACILITY_ID)
                .workItems(List.of(happyWorkItem()))
                .dryRun(dryRun)
                .build();
    }

    private static WorkItemDTO happyWorkItem() {
        Map<String, Object> plannedRoute = new LinkedHashMap<>();
        plannedRoute.put("networkRef", "net-a");
        plannedRoute.put("stopIds", List.of("s1", "s2"));
        plannedRoute.put(RoutePayloadKeys.ESTIMATED_ACTION_DURATION, 12);
        plannedRoute.put(RoutePayloadKeys.ESTIMATED_TRAVEL_DURATION, 45);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(RoutePayloadKeys.PLANNED_ROUTE, plannedRoute);

        return WorkItemDTO.builder().workId("work-1").payload(payload).build();
    }
}
