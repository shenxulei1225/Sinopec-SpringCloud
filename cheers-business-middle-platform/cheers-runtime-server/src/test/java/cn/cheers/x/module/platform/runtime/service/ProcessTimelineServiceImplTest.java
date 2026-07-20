package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionAppendReqDTO;
import cn.cheers.x.module.platform.runtime.dal.dataobject.ProcessTimelineActionDO;`r`nimport cn.cheers.x.module.platform.runtime.dal.mysql.ProcessTimelineActionMapper;
import cn.cheers.x.module.platform.runtime.enums.ErrorCodeConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessTimelineServiceImplTest {

    @Mock
    private ProcessTimelineActionMapper processTimelineActionMapper;

    @InjectMocks
    private ProcessTimelineServiceImpl processTimelineService;

    @Test
    void append_missingHowSummary_rejects() {
        ProcessTimelineActionAppendReqDTO req = ProcessTimelineActionAppendReqDTO.builder()
                .targetType("emergency_event")
                .targetId("1")
                .occurredAt(OffsetDateTime.now())
                .actionCode("event.confirm")
                .howSummary("  ")
                .build();

        ServiceException ex = assertThrows(ServiceException.class, () -> processTimelineService.append(req));
        assertEquals(ErrorCodeConstants.PROCESS_TIMELINE_APPEND_INVALID.getCode(), ex.getCode());
        verify(processTimelineActionMapper, never()).insert(any(ProcessTimelineActionDO.class));
    }

    @Test
    void append_missingActionCode_rejects() {
        ProcessTimelineActionAppendReqDTO req = ProcessTimelineActionAppendReqDTO.builder()
                .targetType("emergency_event")
                .targetId("1")
                .occurredAt(OffsetDateTime.now())
                .howSummary("确认为真实事件")
                .build();

        assertThrows(ServiceException.class, () -> processTimelineService.append(req));
        verify(processTimelineActionMapper, never()).insert(any(ProcessTimelineActionDO.class));
    }
}
