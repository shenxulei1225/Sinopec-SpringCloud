package cn.cheers.x.module.platform.orchestration.api;

import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import cn.cheers.x.module.platform.contract.enums.RuntimeJobStatus;
import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.cheers.x.module.platform.orchestration.service.ScheduleOrchestrationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScheduleRunApiImpl 单元测试")
class ScheduleRunApiImplTest {

    @Mock
    private ScheduleOrchestrationService scheduleOrchestrationService;

    @InjectMocks
    private ScheduleRunApiImpl scheduleRunApiImpl;

    @Test
    @DisplayName("POST /platform/runtime/schedule/run 委托 ScheduleOrchestrationService.runSchedule")
    void run_delegatesToScheduleOrchestrationService() {
        ScheduleRunRequest request = ScheduleRunRequest.builder()
                .orchestrationRef(OrchestrationRefs.PATROL_ROUTE_PREVIEW_V1)
                .build();
        ScheduleRunResponse expected = ScheduleRunResponse.builder()
                .runtimeJobId("job-1")
                .status(RuntimeJobStatus.SCHEDULED)
                .build();
        when(scheduleOrchestrationService.runSchedule(request, null)).thenReturn(expected);

        var result = scheduleRunApiImpl.run(request);

        assertEquals(0, result.getCode());
        assertEquals(RuntimeJobStatus.SCHEDULED, result.getData().getStatus());
        verify(scheduleOrchestrationService).runSchedule(eq(request), isNull());
    }
}
