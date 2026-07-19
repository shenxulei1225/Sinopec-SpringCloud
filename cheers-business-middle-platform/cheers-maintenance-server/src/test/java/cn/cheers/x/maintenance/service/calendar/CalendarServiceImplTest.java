package cn.cheers.x.maintenance.service.calendar;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.maintenance.api.ScheduleRunApi;
import cn.cheers.x.maintenance.controller.admin.vo.calendar.CalendarExpandReqVO;
import cn.cheers.x.maintenance.dal.dataobject.CalendarEntryDO;
import cn.cheers.x.maintenance.dal.dataobject.HandbookDO;
import cn.cheers.x.maintenance.dal.mysql.CalendarEntryMapper;
import cn.cheers.x.maintenance.dal.mysql.HandbookMapper;
import cn.cheers.x.maintenance.enums.HandbookStatusEnum;
import cn.cheers.x.maintenance.framework.config.MaintenanceCalendarProperties;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarServiceImplTest {

    @Mock private CalendarEntryMapper calendarEntryMapper;
    @Mock private HandbookMapper handbookMapper;
    @Mock private ScheduleRunApi scheduleRunApi;
    @Mock private MaintenanceCalendarProperties calendarProperties;
    @InjectMocks private CalendarServiceImpl calendarService;

    @Test
    @DisplayName("expand M：含两个月初 → 两条")
    void expand_monthly_twoMonths() {
        when(handbookMapper.selectById(1L)).thenReturn(HandbookDO.builder()
                .id(1L).scope("inspection").frequencyCode("M")
                .status(HandbookStatusEnum.PUBLISHED.getStatus()).build());
        when(calendarEntryMapper.selectByUnique(any(), any(), any())).thenReturn(null);
        doAnswer(inv -> { inv.getArgument(0, CalendarEntryDO.class).setId(1L); return 1; })
                .when(calendarEntryMapper).insert(any(CalendarEntryDO.class));

        CalendarExpandReqVO req = new CalendarExpandReqVO();
        req.setHandbookId(1L);
        req.setFrom(LocalDate.of(2026, 1, 1));
        req.setTo(LocalDate.of(2026, 2, 20));
        req.setAssetIds(List.of(100L));
        assertEquals(2, calendarService.expand(req));
        verify(calendarEntryMapper, times(2)).insert(any(CalendarEntryDO.class));
    }

    @Test
    @DisplayName("trigger 成功 → TRIGGERED")
    void trigger_ok() {
        when(calendarEntryMapper.selectById(5L)).thenReturn(CalendarEntryDO.builder()
                .id(5L).handbookId(1L).assetId(100L).scope("inspection")
                .status(CalendarServiceImpl.STATUS_PLANNED).build());
        when(calendarProperties.getSchedulingMode()).thenReturn("once");
        when(calendarProperties.getConflictStrategy()).thenReturn("none");
        when(calendarProperties.getOrchestrationRef()).thenReturn("orch.standard_expand_solve_persist_v1");
        when(handbookMapper.selectById(1L)).thenReturn(HandbookDO.builder()
                .id(1L).assetTypeCode("PUMP").frequencyCode("M").scope("inspection").build());
        when(scheduleRunApi.run(any())).thenReturn(CommonResult.success(ScheduleRunResponse.builder()
                .runtimeJobId("job-1").workOrderIds(List.of(9L)).build()));

        calendarService.trigger(5L);
        verify(calendarEntryMapper).updateById(argThat((CalendarEntryDO u) ->
                CalendarServiceImpl.STATUS_TRIGGERED.equals(u.getStatus()) && "job-1".equals(u.getRuntimeJobId())));
    }

    @Test
    @DisplayName("trigger 缺 schedulingMode → 显式失败")
    void trigger_missingSpec() {
        when(calendarEntryMapper.selectById(5L)).thenReturn(CalendarEntryDO.builder()
                .id(5L).status(CalendarServiceImpl.STATUS_PLANNED).build());
        when(calendarProperties.getSchedulingMode()).thenReturn(null);
        assertThrows(ServiceException.class, () -> calendarService.trigger(5L));
    }
}
