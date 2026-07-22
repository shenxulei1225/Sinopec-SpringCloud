package cn.iocoder.yudao.module.emergency.service.dispatch;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyResourceDispatchExpandRespDTO;
import cn.iocoder.yudao.module.emergency.dal.mysql.dispatch.ResourceDispatchMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.dispatch.ResourcePoolMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventMapper;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import cn.iocoder.yudao.module.emergency.framework.common.util.DistributedLockUtil;
import cn.iocoder.yudao.module.emergency.service.resource.ResourceTypeConfigService;
import cn.iocoder.yudao.module.emergency.service.resource.ResourceTypeShareRuleService;
import cn.iocoder.yudao.module.emergency.service.timeline.EmergencyProcessTimelineWriter;
import cn.cheers.x.module.platform.orchestration.api.OrchestrationRunApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ResourceDispatchOrchestrationSolveTest {

    @InjectMocks
    private ResourceDispatchServiceImpl dispatchService;

    @Mock private ResourceDispatchMapper dispatchMapper;
    @Mock private ResourceTypeShareRuleService shareRuleService;
    @Mock private ResourcePoolService resourcePoolService;
    @Mock private ResourcePoolMapper resourcePoolMapper;
    @Mock private DistributedLockUtil distributedLockUtil;
    @Mock private ResourceTypeConfigService resourceTypeConfigService;
    @Mock private EmergencyEventMapper eventMapper;
    @Mock private OrchestrationRunApi orchestrationRunApi;
    @Mock private EmergencyProcessTimelineWriter processTimelineWriter;

    @Test
    void solve_noWindow_setsSolveSkipped() {
        EmergencyResourceDispatchExpandRespDTO expand = EmergencyResourceDispatchExpandRespDTO.builder()
                .eventId(1L)
                .resourceId(2L)
                .build();

        EmergencyResourceDispatchExpandRespDTO out = dispatchService.solveDispatchForOrchestration(expand);
        assertTrue(Boolean.TRUE.equals(out.getSolveSkipped()));
    }

    @Test
    void solve_withWindow_failsExplicitly() {
        EmergencyResourceDispatchExpandRespDTO expand = EmergencyResourceDispatchExpandRespDTO.builder()
                .eventId(1L)
                .resourceId(2L)
                .windowStart("2026-07-21T00:00:00Z")
                .build();

        ServiceException ex = assertThrows(ServiceException.class,
                () -> dispatchService.solveDispatchForOrchestration(expand));
        assertEquals(ErrorCodeConstants.DISPATCH_SCHEDULE_WINDOW_UNSUPPORTED.getCode(), ex.getCode());
    }
}
