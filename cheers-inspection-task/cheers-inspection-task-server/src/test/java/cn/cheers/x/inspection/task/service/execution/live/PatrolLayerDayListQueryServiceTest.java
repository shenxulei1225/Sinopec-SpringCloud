package cn.cheers.x.inspection.task.service.execution.live;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionDayAccountPageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PatrolLayerDayListQueryServiceTest {

    private TaskExecutionSessionApi taskExecutionSessionApi;
    private PatrolLayerDayListQueryService service;

    @BeforeEach
    void setUp() {
        taskExecutionSessionApi = mock(TaskExecutionSessionApi.class);
        service = new PatrolLayerDayListQueryService(taskExecutionSessionApi);
    }

    @Test
    void page_stopsWhenTheStationIsMissing() {
        assertThrows(ServiceException.class, () -> service.page(null, "2026-10-24", 1, 100));
        assertThrows(ServiceException.class, () -> service.page(0L, "2026-10-24", 1, 100));
        verifyNoInteractions(taskExecutionSessionApi);
    }

    @Test
    void page_stopsWhenTheDayIsNotACalendarDate() {
        assertThrows(ServiceException.class, () -> service.page(44L, "2026-02-31", 1, 100));
        verifyNoInteractions(taskExecutionSessionApi);
    }

    @Test
    void page_asksForThisStationAndTheSelectedDay() {
        TaskExecutionDayAccountPageDTO page = new TaskExecutionDayAccountPageDTO();
        page.setList(List.of());
        page.setTotal(0L);
        when(taskExecutionSessionApi.listPatrolLayerDay(44L, "2026-10-24", 1, 100))
                .thenReturn(CommonResult.success(page));

        TaskExecutionDayAccountPageDTO result = service.page(44L, "2026-10-24", null, null);

        assertEquals(0L, result.getTotal());
        assertEquals(List.of(), result.getList());
    }
}
