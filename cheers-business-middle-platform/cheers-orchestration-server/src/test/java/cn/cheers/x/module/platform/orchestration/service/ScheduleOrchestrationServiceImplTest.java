package cn.cheers.x.module.platform.orchestration.service;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.capability.api.MappingProfileApi;
import cn.cheers.x.module.platform.capability.api.ProcessCapabilityBindingApi;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants;
import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.cheers.x.module.platform.policy.api.PolicyResolveApi;
import cn.cheers.x.module.platform.runtime.api.RuntimePersistApi;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimePersistReqDTO;
import cn.cheers.x.module.platform.scheduling.engine.SchedulingEngine;
import cn.cheers.x.workorder.api.WorkOrderApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ScheduleOrchestrationService 派工前置校验单元测试（Mockito，不启 Spring）
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ScheduleOrchestrationService 单元测试")
class ScheduleOrchestrationServiceImplTest {

    @Mock
    private SchedulingEngine schedulingEngine;
    @Mock
    private RuntimePersistApi runtimePersistApi;
    @Mock
    private PolicyResolveApi policyResolveApi;
    @Mock
    private ProcessCapabilityBindingApi processCapabilityBindingApi;
    @Mock
    private MappingProfileApi mappingProfileApi;
    @Mock
    private WorkOrderApi workOrderApi;

    @InjectMocks
    private ScheduleOrchestrationServiceImpl scheduleOrchestrationService;

    @Test
    @DisplayName("dispatch=true 且 fieldWorkStandardId=null：persist 前失败")
    void runSchedule_dispatchWithoutStandard_failsBeforePersist() {
        ScheduleRunRequest request = baseRequest();
        request.setDispatchWorkOrders(true);
        request.setFieldWorkStandardId(null);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> scheduleOrchestrationService.runSchedule(request, 1L));

        assertEquals(ErrorCodeConstants.SCHEDULE_DISPATCH_STANDARD_REQUIRED.getCode(), ex.getCode());
        verify(runtimePersistApi, never()).persist(any(RuntimePersistReqDTO.class));
        verify(workOrderApi, never()).create(any());
        verify(schedulingEngine, never()).solve(anyList(), any(), anyString());
    }

    @Test
    @DisplayName("dispatch=false：调用 persist，不调用 WorkOrderApi")
    void runSchedule_dispatchFalse_persistsWithoutWorkOrder() {
        ScheduleRunRequest request = baseRequest();
        request.setDispatchWorkOrders(false);

        when(schedulingEngine.solve(anyList(), any(), anyString()))
                .thenReturn(List.of(ScheduleSlotDTO.builder()
                        .slotId("slot-1")
                        .workId("work-1")
                        .build()));
        when(runtimePersistApi.persist(any(RuntimePersistReqDTO.class)))
                .thenReturn(CommonResult.success(null));

        ScheduleRunResponse response = scheduleOrchestrationService.runSchedule(request, 1L);

        assertEquals(1, response.getSlots().size());
        verify(runtimePersistApi).persist(any(RuntimePersistReqDTO.class));
        verify(workOrderApi, never()).create(any());
    }

    private static ScheduleRunRequest baseRequest() {
        return ScheduleRunRequest.builder()
                .orchestrationRef(OrchestrationRefs.STANDARD_EXPAND_SOLVE_PERSIST_V1)
                .schedulingSpec(SchedulingSpecDTO.builder()
                        .mode("once")
                        .conflictStrategy("none")
                        .build())
                .workItems(List.of(WorkItemDTO.builder()
                        .workId("work-1")
                        .build()))
                .build();
    }
}
