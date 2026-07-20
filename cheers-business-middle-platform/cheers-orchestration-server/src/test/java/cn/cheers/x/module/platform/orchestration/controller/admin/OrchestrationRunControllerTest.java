package cn.cheers.x.module.platform.orchestration.controller.admin;

import cn.cheers.x.module.platform.orchestration.api.dto.OrchestrationRunRequest;
import cn.cheers.x.module.platform.orchestration.api.dto.OrchestrationRunResponse;
import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.cheers.x.module.platform.orchestration.service.OrchestrationRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrchestrationRunController 单元测试")
class OrchestrationRunControllerTest {

    @Mock
    private OrchestrationRunner orchestrationRunner;

    @InjectMocks
    private OrchestrationRunController controller;

    @Test
    @DisplayName("POST /run 委托 OrchestrationRunner.run")
    void run_delegatesToRunner() {
        OrchestrationRunRequest request = OrchestrationRunRequest.builder()
                .orchestrationRef(OrchestrationRefs.EMERGENCY_START_RESPONSE_V1)
                .scope("emergency")
                .payload(Map.of("eventId", 1L))
                .build();
        OrchestrationRunResponse expected = OrchestrationRunResponse.builder()
                .orchestrationRef(OrchestrationRefs.EMERGENCY_START_RESPONSE_V1)
                .status("COMPLETED")
                .build();
        when(orchestrationRunner.run(any())).thenReturn(expected);

        var result = controller.run(request);

        assertEquals(0, result.getCode());
        assertEquals("COMPLETED", result.getData().getStatus());
        verify(orchestrationRunner).run(request);
    }
}
