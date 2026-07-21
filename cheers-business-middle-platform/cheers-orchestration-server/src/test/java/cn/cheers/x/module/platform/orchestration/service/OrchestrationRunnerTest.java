package cn.cheers.x.module.platform.orchestration.service;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.maintenance.api.MaintenanceApi;
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
import cn.cheers.x.module.platform.runtime.api.dto.RuntimePersistReqDTO;
import cn.cheers.x.module.platform.scheduling.engine.SchedulingEngine;
import cn.cheers.x.workorder.api.WorkOrderApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrchestrationRunner 单元测试")
class OrchestrationRunnerTest {

    @Mock private SchedulingEngine schedulingEngine;
    @Mock private RuntimePersistApi runtimePersistApi;
    @Mock private PolicyResolveApi policyResolveApi;
    @Mock private ProcessCapabilityBindingApi processCapabilityBindingApi;
    @Mock private MappingProfileApi mappingProfileApi;
    @Mock private WorkOrderApi workOrderApi;
    @Mock private MaintenanceApi maintenanceApi;

    private OrchestrationRunner runner;

    @BeforeEach
    void setUp() {
        runner = new OrchestrationRunner();
        ReflectionTestUtils.setField(runner, "schedulingEngine", schedulingEngine);
        ReflectionTestUtils.setField(runner, "runtimePersistApi", runtimePersistApi);
        ReflectionTestUtils.setField(runner, "policyResolveApi", policyResolveApi);
        ReflectionTestUtils.setField(runner, "processCapabilityBindingApi", processCapabilityBindingApi);
        ReflectionTestUtils.setField(runner, "mappingProfileApi", mappingProfileApi);
        ReflectionTestUtils.setField(runner, "workOrderApi", workOrderApi);
        ReflectionTestUtils.setField(runner, "maintenanceApi", maintenanceApi);
        ReflectionTestUtils.setField(runner, "templateRegistry", new OrchestrationTemplateRegistry());
        ReflectionTestUtils.setField(runner, "phaseHandlerRegistry", new PhaseHandlerRegistry(List.of()));
    }

    @Test
    @DisplayName("dryRun=true：求解但不 persist、不派工")
    void run_dryRun_neverPersistsOrDispatches() {
        ScheduleRunRequest request = baseRequest();
        request.setDryRun(true);
        request.setDispatchWorkOrders(true);
        request.setFieldWorkStandardId(3L);

        when(schedulingEngine.solve(anyList(), any(), anyString(), anyList()))
                .thenReturn(List.of(ScheduleSlotDTO.builder().slotId("slot-1").workId("work-1").build()));

        ScheduleRunResponse response = runner.run(request, 1L);

        assertEquals(1, response.getSlots().size());
        assertEquals(1, response.getWorkItems().size());
        assertEquals("work-1", response.getWorkItems().get(0).getWorkId());
        verify(runtimePersistApi, never()).persist(any(RuntimePersistReqDTO.class));
        verify(workOrderApi, never()).create(any());
    }

    @Test
    @DisplayName("未知 orchestrationRef：失败")
    void run_unknownOrchestrationRef_fails() {
        ScheduleRunRequest request = baseRequest();
        request.setOrchestrationRef("orch.unknown.not_registered");

        ServiceException ex = assertThrows(ServiceException.class, () -> runner.run(request, 1L));

        assertEquals(ErrorCodeConstants.SCHEDULE_RUN_ORCHESTRATION_UNKNOWN.getCode(), ex.getCode());
        verify(schedulingEngine, never()).solve(anyList(), any(), anyString());
        verify(runtimePersistApi, never()).persist(any(RuntimePersistReqDTO.class));
    }

    private static ScheduleRunRequest baseRequest() {
        return ScheduleRunRequest.builder()
                .orchestrationRef(OrchestrationRefs.STANDARD_EXPAND_SOLVE_PERSIST_V1)
                .schedulingSpec(SchedulingSpecDTO.builder().mode("once").conflictStrategy("none").build())
                .workItems(List.of(WorkItemDTO.builder().workId("work-1").durationEstimateMinutes(30).build()))
                .build();
    }
}
