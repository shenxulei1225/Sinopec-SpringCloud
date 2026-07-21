package cn.cheers.x.module.platform.orchestration.service;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.maintenance.api.MaintenanceApi;
import cn.cheers.x.maintenance.api.dto.BindingResolveReqDTO;
import cn.cheers.x.maintenance.api.dto.BindingResolveRespDTO;
import cn.cheers.x.module.platform.capability.api.MappingProfileApi;
import cn.cheers.x.module.platform.capability.api.ProcessCapabilityBindingApi;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants;
import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandlerRegistry;
import cn.cheers.x.module.platform.orchestration.template.OrchestrationTemplateRegistry;
import cn.cheers.x.module.platform.policy.api.PolicyResolveApi;
import cn.cheers.x.module.platform.runtime.api.RuntimePersistApi;
import cn.cheers.x.module.platform.runtime.api.RuntimeQueryApi;
import cn.cheers.x.module.platform.runtime.api.RuntimeSlotWriteApi;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimePersistReqDTO;
import cn.cheers.x.module.platform.scheduling.engine.SchedulingEngine;
import cn.cheers.x.workorder.api.WorkOrderApi;
import cn.cheers.x.workorder.api.dto.WorkOrderCreateReqDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScheduleOrchestrationService 单元测试")
class ScheduleOrchestrationServiceImplTest {

    @Mock private SchedulingEngine schedulingEngine;
    @Mock private RuntimePersistApi runtimePersistApi;
    @Mock private RuntimeQueryApi runtimeQueryApi;
    @Mock private RuntimeSlotWriteApi runtimeSlotWriteApi;
    @Mock private PolicyResolveApi policyResolveApi;
    @Mock private ProcessCapabilityBindingApi processCapabilityBindingApi;
    @Mock private MappingProfileApi mappingProfileApi;
    @Mock private WorkOrderApi workOrderApi;
    @Mock private MaintenanceApi maintenanceApi;

    private ScheduleOrchestrationServiceImpl scheduleOrchestrationService;

    @BeforeEach
    void setUp() {
        OrchestrationRunner runner = new OrchestrationRunner();
        ReflectionTestUtils.setField(runner, "schedulingEngine", schedulingEngine);
        ReflectionTestUtils.setField(runner, "runtimePersistApi", runtimePersistApi);
        ReflectionTestUtils.setField(runner, "runtimeQueryApi", runtimeQueryApi);
        ReflectionTestUtils.setField(runner, "runtimeSlotWriteApi", runtimeSlotWriteApi);
        ReflectionTestUtils.setField(runner, "policyResolveApi", policyResolveApi);
        ReflectionTestUtils.setField(runner, "processCapabilityBindingApi", processCapabilityBindingApi);
        ReflectionTestUtils.setField(runner, "mappingProfileApi", mappingProfileApi);
        ReflectionTestUtils.setField(runner, "workOrderApi", workOrderApi);
        ReflectionTestUtils.setField(runner, "maintenanceApi", maintenanceApi);
        ReflectionTestUtils.setField(runner, "templateRegistry", new OrchestrationTemplateRegistry());
        ReflectionTestUtils.setField(runner, "phaseHandlerRegistry", new PhaseHandlerRegistry(List.of()));

        scheduleOrchestrationService = new ScheduleOrchestrationServiceImpl();
        ReflectionTestUtils.setField(scheduleOrchestrationService, "orchestrationRunner", runner);
        ReflectionTestUtils.setField(scheduleOrchestrationService, "maintenanceApi", maintenanceApi);

        lenient().when(runtimeQueryApi.listSlots(any(), any(), any(), any(), any(), anyList()))
                .thenReturn(CommonResult.success(List.of()));
    }

    @Test
    @DisplayName("dispatch=true 无显式标准且无 scope：persist 前失败")
    void runSchedule_dispatchWithoutStandardAndScope_failsBeforePersist() {
        ScheduleRunRequest request = baseRequest();
        request.setDispatchWorkOrders(true);
        request.setFieldWorkStandardId(null);
        request.setScope(null);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> scheduleOrchestrationService.runSchedule(request, 1L));

        assertEquals(ErrorCodeConstants.SCHEDULE_DISPATCH_STANDARD_REQUIRED.getCode(), ex.getCode());
        verify(runtimePersistApi, never()).persist(any(RuntimePersistReqDTO.class));
        verify(workOrderApi, never()).create(any());
        verify(schedulingEngine, never()).solve(anyList(), any(), anyString());
    }

    @Test
    @DisplayName("dispatch=true 显式标准：不调 resolveBinding，standardId=3")
    void runSchedule_explicitStandard_skipsBinding() {
        ScheduleRunRequest request = baseRequest();
        request.setDispatchWorkOrders(true);
        request.setFieldWorkStandardId(3L);
        request.setScope("inspection");

        when(schedulingEngine.solve(anyList(), any(), anyString(), anyList()))
                .thenReturn(List.of(ScheduleSlotDTO.builder().slotId("slot-1").workId("work-1").build()));
        when(runtimePersistApi.persist(any(RuntimePersistReqDTO.class))).thenReturn(CommonResult.success(null));
        when(workOrderApi.create(any())).thenReturn(CommonResult.success(100L));

        ScheduleRunResponse response = scheduleOrchestrationService.runSchedule(request, 1L);

        assertEquals(List.of(100L), response.getWorkOrderIds());
        verify(maintenanceApi, never()).resolveBinding(any());
        ArgumentCaptor<WorkOrderCreateReqDTO> captor = ArgumentCaptor.forClass(WorkOrderCreateReqDTO.class);
        verify(workOrderApi).create(captor.capture());
        assertEquals(3L, captor.getValue().getStandardId());
    }

    @Test
    @DisplayName("dispatch=true 无显式标准：绑定返回 9")
    void runSchedule_bindingResolve_usesResolvedStandard() {
        ScheduleRunRequest request = baseRequest();
        request.setDispatchWorkOrders(true);
        request.setFieldWorkStandardId(null);
        request.setScope("inspection");
        request.setAssetTypeCode("PUMP");
        request.setFrequencyCode("M");

        when(maintenanceApi.resolveBinding(any(BindingResolveReqDTO.class)))
                .thenReturn(CommonResult.success(BindingResolveRespDTO.builder().fieldStandardId(9L).build()));
        when(schedulingEngine.solve(anyList(), any(), anyString(), anyList()))
                .thenReturn(List.of(ScheduleSlotDTO.builder().slotId("slot-1").workId("work-1").build()));
        when(runtimePersistApi.persist(any(RuntimePersistReqDTO.class))).thenReturn(CommonResult.success(null));
        when(workOrderApi.create(any())).thenReturn(CommonResult.success(200L));

        scheduleOrchestrationService.runSchedule(request, 1L);

        ArgumentCaptor<WorkOrderCreateReqDTO> captor = ArgumentCaptor.forClass(WorkOrderCreateReqDTO.class);
        verify(workOrderApi).create(captor.capture());
        assertEquals(9L, captor.getValue().getStandardId());
    }

    @Test
    @DisplayName("dispatch=false：调用 persist，不调用 WorkOrderApi")
    void runSchedule_dispatchFalse_persistsWithoutWorkOrder() {
        ScheduleRunRequest request = baseRequest();
        request.setDispatchWorkOrders(false);

        when(schedulingEngine.solve(anyList(), any(), anyString(), anyList()))
                .thenReturn(List.of(ScheduleSlotDTO.builder().slotId("slot-1").workId("work-1").build()));
        when(runtimePersistApi.persist(any(RuntimePersistReqDTO.class))).thenReturn(CommonResult.success(null));

        ScheduleRunResponse response = scheduleOrchestrationService.runSchedule(request, 1L);

        assertEquals(1, response.getSlots().size());
        verify(runtimePersistApi).persist(any(RuntimePersistReqDTO.class));
        verify(workOrderApi, never()).create(any());
    }

    private static ScheduleRunRequest baseRequest() {
        return ScheduleRunRequest.builder()
                .orchestrationRef(OrchestrationRefs.STANDARD_EXPAND_SOLVE_PERSIST_V1)
                .schedulingSpec(SchedulingSpecDTO.builder().mode("once").conflictStrategy("none").build())
                .workItems(List.of(WorkItemDTO.builder().workId("work-1").durationEstimateMinutes(30).build()))
                .build();
    }
}
