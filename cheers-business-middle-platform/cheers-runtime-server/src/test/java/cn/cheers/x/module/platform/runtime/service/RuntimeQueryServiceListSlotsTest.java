package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.runtime.dal.dataobject.ScheduleSlotDO;
import cn.cheers.x.module.platform.runtime.dal.mysql.ScheduleSlotMapper;
import cn.cheers.x.module.platform.runtime.enums.ErrorCodeConstants;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuntimeQueryServiceListSlotsTest {

    private static final OffsetDateTime FROM = OffsetDateTime.parse("2026-07-21T08:00:00+08:00");
    private static final OffsetDateTime TO = OffsetDateTime.parse("2026-07-21T18:00:00+08:00");

    @Mock
    private ScheduleSlotMapper scheduleSlotMapper;

    @InjectMocks
    private RuntimeQueryServiceImpl runtimeQueryService;

    @Test
    void listSlots_missingFrom_throws() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> runtimeQueryService.listSlots(null, TO, null, null, null, null));
        assertEquals(ErrorCodeConstants.SCHEDULE_SLOT_QUERY_TIME_RANGE_REQUIRED.getCode(), ex.getCode());
    }

    @Test
    void listSlots_missingTo_throws() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> runtimeQueryService.listSlots(FROM, null, null, null, null, null));
        assertEquals(ErrorCodeConstants.SCHEDULE_SLOT_QUERY_TIME_RANGE_REQUIRED.getCode(), ex.getCode());
    }

    @Test
    void listSlots_filtersBySite_entityType_andStatuses() {
        ScheduleSlotDO matching = slot("slot-1", "patrol_task", 100L, SlotStatus.PLANNED,
                "2026-07-21T09:00:00+08:00", "2026-07-21T10:00:00+08:00", null);
        when(scheduleSlotMapper.selectList(any(LambdaQueryWrapperX.class))).thenReturn(List.of(matching));

        List<ScheduleSlotDTO> result = runtimeQueryService.listSlots(
                FROM, TO, null, "patrol_task", 100L, List.of(SlotStatus.PLANNED));

        assertEquals(1, result.size());
        assertEquals("slot-1", result.get(0).getSlotId());
        verify(scheduleSlotMapper).selectList(any(LambdaQueryWrapperX.class));
    }

    @Test
    void listSlots_filtersByResourceId_inApplicationLayer() {
        ScheduleSlotDO robotSlot = slot("slot-robot", "patrol_task", 100L, SlotStatus.PLANNED,
                "2026-07-21T09:00:00+08:00", "2026-07-21T10:00:00+08:00",
                "[{\"resourceType\":\"GROUND_ROBOT\",\"resourceId\":\"robot-1\"}]");
        ScheduleSlotDO otherSlot = slot("slot-other", "patrol_task", 100L, SlotStatus.PLANNED,
                "2026-07-21T11:00:00+08:00", "2026-07-21T12:00:00+08:00",
                "[{\"resourceType\":\"GROUND_ROBOT\",\"resourceId\":\"robot-2\"}]");
        when(scheduleSlotMapper.selectList(any(LambdaQueryWrapperX.class)))
                .thenReturn(List.of(robotSlot, otherSlot));

        List<ScheduleSlotDTO> result = runtimeQueryService.listSlots(
                FROM, TO, "robot-1", null, null, null);

        assertEquals(1, result.size());
        assertEquals("slot-robot", result.get(0).getSlotId());
        assertTrue(result.get(0).getAssignedResources().stream()
                .anyMatch(r -> "robot-1".equals(r.getResourceId())));
    }

    @Test
    void listSlots_excludesCancelledWhenStatusesProvided() {
        ScheduleSlotDO planned = slot("slot-planned", "patrol_task", 100L, SlotStatus.PLANNED,
                "2026-07-21T09:00:00+08:00", "2026-07-21T10:00:00+08:00", null);
        when(scheduleSlotMapper.selectList(any(LambdaQueryWrapperX.class))).thenReturn(List.of(planned));

        List<ScheduleSlotDTO> occupied = runtimeQueryService.listSlots(
                FROM, TO, null, null, null,
                List.of(SlotStatus.PLANNED, SlotStatus.IN_PROGRESS, SlotStatus.COMPLETED));

        assertEquals(1, occupied.size());
        assertEquals(SlotStatus.PLANNED, occupied.get(0).getSlotStatus());

        ArgumentCaptor<LambdaQueryWrapperX<ScheduleSlotDO>> captor =
                ArgumentCaptor.forClass(LambdaQueryWrapperX.class);
        verify(scheduleSlotMapper).selectList(captor.capture());
    }

    private static ScheduleSlotDO slot(String id, String entityTypeCode, Long siteId, SlotStatus status,
                                       String plannedStart, String plannedEnd, String assignedResources) {
        return ScheduleSlotDO.builder()
                .id(id)
                .runtimeJobId("job-1")
                .workId("work-1")
                .entityTypeCode(entityTypeCode)
                .siteId(siteId)
                .slotStatus(status.name())
                .plannedStart(OffsetDateTime.parse(plannedStart))
                .plannedEnd(OffsetDateTime.parse(plannedEnd))
                .assignedResources(assignedResources)
                .build();
    }
}
