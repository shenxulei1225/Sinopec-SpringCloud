package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.route.InspectionRoutePlanDO;
import cn.cheers.x.inspection.inspection_content.dal.mysql.route.InspectionRoutePlanMapper;
import cn.cheers.x.inspection.inspection_content.service.orchestration.impl.PatrolConfirmServiceImpl;
import cn.cheers.x.inspection.orchestration.dto.PatrolConfirmReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolConfirmRespDTO;
import cn.cheers.x.inspection.task.dal.dataobject.task.InspectionTaskDO;
import cn.cheers.x.inspection.task.dal.mysql.task.InspectionTaskMapper;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatrolConfirmService 单元测试")
class PatrolConfirmServiceTest {

    private static final Long TASK_ID = 100L;
    private static final Long FACILITY_ID = 1L;

    @Mock
    private InspectionRoutePlanMapper routePlanMapper;
    @Mock
    private InspectionTaskMapper taskMapper;

    private PatrolConfirmServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PatrolConfirmServiceImpl(routePlanMapper, taskMapper, new ObjectMapper());
    }

    @Test
    @DisplayName("缺少 durationEstimateMinutes → 显式失败")
    void confirm_missingDuration_throws() {
        WorkItemDTO workItem = happyWorkItem();
        workItem.setDurationEstimateMinutes(null);

        PatrolConfirmReqDTO req = PatrolConfirmReqDTO.builder()
                .taskId(TASK_ID)
                .facilityId(FACILITY_ID)
                .workItems(List.of(workItem))
                .dryRun(false)
                .build();

        assertThrows(ServiceException.class, () -> service.confirm(req));
    }

    @Test
    @DisplayName("dryRun=true → 不写库")
    void confirm_dryRun_skipsPersist() {
        PatrolConfirmReqDTO req = happyReq(true);

        PatrolConfirmRespDTO resp = service.confirm(req);

        assertTrue(resp.getDryRun());
        verify(routePlanMapper, never()).insert(any(InspectionRoutePlanDO.class));
        verify(taskMapper, never()).updateById(any(InspectionTaskDO.class));
    }

    @Test
    @DisplayName("正常确认 → 插入路线方案并更新任务快照")
    void confirm_happyPath_insertsRoutePlanAndUpdatesTask() {
        when(taskMapper.selectById(TASK_ID)).thenReturn(task(TASK_ID));
        doAnswer(invocation -> {
            InspectionRoutePlanDO plan = invocation.getArgument(0);
            plan.setId(900L);
            return 1;
        }).when(routePlanMapper).insert(any(InspectionRoutePlanDO.class));

        PatrolConfirmRespDTO resp = service.confirm(happyReq(false));

        assertEquals(900L, resp.getRoutePlanId());
        assertEquals(TASK_ID, resp.getTaskId());

        ArgumentCaptor<InspectionRoutePlanDO> planCaptor = ArgumentCaptor.forClass(InspectionRoutePlanDO.class);
        verify(routePlanMapper).insert(planCaptor.capture());
        InspectionRoutePlanDO plan = planCaptor.getValue();
        assertEquals(FACILITY_ID, plan.getFacilityId());
        assertEquals("net-a", plan.getNetworkRef());
        assertEquals("HUMAN", plan.getInspectionType());
        assertEquals(45, plan.getDurationEstimateMinutes());
        assertEquals(TASK_ID, plan.getTaskId());

        ArgumentCaptor<InspectionTaskDO> taskCaptor = ArgumentCaptor.forClass(InspectionTaskDO.class);
        verify(taskMapper).updateById(taskCaptor.capture());
        InspectionTaskDO updated = taskCaptor.getValue();
        assertEquals(900L, updated.getRoutePlanId());
        assertEquals("net-a", updated.getNetworkRef());
        assertEquals(45, updated.getDurationEstimateMinutes());
        assertEquals("HUMAN", updated.getInspectionType());
    }

    private static PatrolConfirmReqDTO happyReq(boolean dryRun) {
        return PatrolConfirmReqDTO.builder()
                .taskId(TASK_ID)
                .facilityId(FACILITY_ID)
                .name("test-route")
                .workItems(List.of(happyWorkItem()))
                .dryRun(dryRun)
                .build();
    }

    private static WorkItemDTO happyWorkItem() {
        Map<String, Object> plannedRoute = new LinkedHashMap<>();
        plannedRoute.put("networkRef", "net-a");
        plannedRoute.put("totalDistanceMeters", 1200L);
        plannedRoute.put("stopIds", List.of("s1", "s2"));

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("networkRef", "net-a");
        payload.put("stopIds", List.of("s1", "s2"));
        payload.put("inspectionType", "HUMAN");
        payload.put("plannedRoute", plannedRoute);

        return WorkItemDTO.builder()
                .workId("work-1")
                .durationEstimateMinutes(45)
                .payload(payload)
                .build();
    }

    private static InspectionTaskDO task(Long id) {
        InspectionTaskDO task = new InspectionTaskDO();
        task.setId(id);
        task.setTaskName("task-" + id);
        return task;
    }
}
