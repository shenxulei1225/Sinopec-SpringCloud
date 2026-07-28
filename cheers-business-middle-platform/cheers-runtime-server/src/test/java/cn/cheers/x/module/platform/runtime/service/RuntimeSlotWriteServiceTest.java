package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionAppendReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotReleaseReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotStatusUpdateReqDTO;
import cn.cheers.x.module.platform.runtime.dal.dataobject.ScheduleSlotDO;
import cn.cheers.x.module.platform.runtime.dal.mysql.ScheduleSlotMapper;
import cn.cheers.x.module.platform.runtime.enums.ErrorCodeConstants;
import cn.cheers.x.module.platform.runtime.enums.RuntimeSlotReleaseMode;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuntimeSlotWriteServiceTest {

    @Mock
    private ScheduleSlotMapper scheduleSlotMapper;
    @Mock
    private ProcessTimelineService processTimelineService;

    @InjectMocks
    private RuntimeSlotWriteServiceImpl runtimeSlotWriteService;

    @Test
    void update_missingSlot_throws() {
        when(scheduleSlotMapper.selectById("missing-slot")).thenReturn(null);

        RuntimeSlotStatusUpdateReqDTO req = RuntimeSlotStatusUpdateReqDTO.builder()
                .slotId("missing-slot")
                .slotStatus(SlotStatus.COMPLETED)
                .build();

        ServiceException ex = assertThrows(ServiceException.class,
                () -> runtimeSlotWriteService.updateSlotStatus(req));
        assertEquals(ErrorCodeConstants.SCHEDULE_SLOT_NOT_EXISTS.getCode(), ex.getCode());
        verify(scheduleSlotMapper, never()).updateById(any(ScheduleSlotDO.class));
        verify(processTimelineService, never()).append(any(ProcessTimelineActionAppendReqDTO.class));
    }

    @Test
    void update_completed_appendsTimeline() {
        OffsetDateTime actualEnd = OffsetDateTime.parse("2026-07-21T10:00:00+08:00");
        ScheduleSlotDO existing = ScheduleSlotDO.builder()
                .id("slot-1")
                .runtimeJobId("job-1")
                .workId("work-1")
                .entityTypeCode("patrol_task")
                .slotStatus(SlotStatus.PLANNED.name())
                .facilityId(100L)
                .build();
        when(scheduleSlotMapper.selectById("slot-1")).thenReturn(existing);
        when(processTimelineService.append(any(ProcessTimelineActionAppendReqDTO.class))).thenReturn(42L);

        RuntimeSlotStatusUpdateReqDTO req = RuntimeSlotStatusUpdateReqDTO.builder()
                .slotId("slot-1")
                .slotStatus(SlotStatus.COMPLETED)
                .actualEnd(actualEnd)
                .reason("巡检完成")
                .build();

        runtimeSlotWriteService.updateSlotStatus(req);

        ArgumentCaptor<ScheduleSlotDO> slotCaptor = ArgumentCaptor.forClass(ScheduleSlotDO.class);
        verify(scheduleSlotMapper).updateById(slotCaptor.capture());
        assertEquals(SlotStatus.COMPLETED.name(), slotCaptor.getValue().getSlotStatus());
        assertEquals(actualEnd, slotCaptor.getValue().getActualEnd());

        ArgumentCaptor<ProcessTimelineActionAppendReqDTO> timelineCaptor =
                ArgumentCaptor.forClass(ProcessTimelineActionAppendReqDTO.class);
        verify(processTimelineService).append(timelineCaptor.capture());
        ProcessTimelineActionAppendReqDTO timeline = timelineCaptor.getValue();
        assertEquals("schedule_slot", timeline.getTargetType());
        assertEquals("slot-1", timeline.getTargetId());
        assertEquals("slot.status_update", timeline.getActionCode());
        assertEquals(actualEnd, timeline.getOccurredAt());
        assertEquals(100L, timeline.getFacilityId());
    }

    @Test
    void release_yield_keepsCompleted_cancelsPlanned() {
        ScheduleSlotDO completed = ScheduleSlotDO.builder()
                .id("slot-done")
                .runtimeJobId("job-1")
                .workId("work-1")
                .slotStatus(SlotStatus.COMPLETED.name())
                .facilityId(100L)
                .build();
        ScheduleSlotDO planned = ScheduleSlotDO.builder()
                .id("slot-planned")
                .runtimeJobId("job-1")
                .workId("work-1")
                .slotStatus(SlotStatus.PLANNED.name())
                .facilityId(100L)
                .build();
        ScheduleSlotDO inProgress = ScheduleSlotDO.builder()
                .id("slot-running")
                .runtimeJobId("job-1")
                .workId("work-1")
                .slotStatus(SlotStatus.IN_PROGRESS.name())
                .facilityId(100L)
                .build();
        when(scheduleSlotMapper.selectList(any(LambdaQueryWrapperX.class)))
                .thenReturn(List.of(completed, planned, inProgress));

        RuntimeSlotReleaseReqDTO req = RuntimeSlotReleaseReqDTO.builder()
                .runtimeJobId("job-1")
                .mode(RuntimeSlotReleaseMode.YIELD_PAUSE)
                .reason("让路给其他任务")
                .build();

        runtimeSlotWriteService.releaseUnfinished(req);

        ArgumentCaptor<ScheduleSlotDO> slotCaptor = ArgumentCaptor.forClass(ScheduleSlotDO.class);
        verify(scheduleSlotMapper, times(2)).updateById(slotCaptor.capture());
        List<ScheduleSlotDO> updated = slotCaptor.getAllValues();
        assertEquals(SlotStatus.CANCELLED.name(), updated.get(0).getSlotStatus());
        assertEquals(SlotStatus.CANCELLED.name(), updated.get(1).getSlotStatus());

        ArgumentCaptor<ProcessTimelineActionAppendReqDTO> timelineCaptor =
                ArgumentCaptor.forClass(ProcessTimelineActionAppendReqDTO.class);
        verify(processTimelineService).append(timelineCaptor.capture());
        ProcessTimelineActionAppendReqDTO timeline = timelineCaptor.getValue();
        assertEquals("runtime_job", timeline.getTargetType());
        assertEquals("job-1", timeline.getTargetId());
        assertEquals("slot.release_unfinished", timeline.getActionCode());
        assertEquals(100L, timeline.getFacilityId());
    }
}
