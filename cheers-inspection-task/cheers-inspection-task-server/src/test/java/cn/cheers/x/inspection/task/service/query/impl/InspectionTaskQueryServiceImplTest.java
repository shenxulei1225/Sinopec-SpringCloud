package cn.cheers.x.inspection.task.service.query.impl;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.service.enhance.ContentEnhancementService;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskRespVO;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.runtime.api.RuntimeQueryApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("InspectionTaskQueryServiceImpl 计划点回填")
class InspectionTaskQueryServiceImplTest {

    private PatrolTaskEntityStore store;
    private RuntimeQueryApi runtimeQueryApi;
    private InspectionTaskQueryServiceImpl service;

    @BeforeEach
    void setUp() {
        store = mock(PatrolTaskEntityStore.class);
        runtimeQueryApi = mock(RuntimeQueryApi.class);
        service = new InspectionTaskQueryServiceImpl(
                mock(ContentEnhancementService.class), store, runtimeQueryApi, new ObjectMapper());
    }

    @Test
    @DisplayName("有作业号时按 L4 带回已占窗计划点")
    void getTaskDetail_withRuntimeJob_enrichesSlots() {
        when(store.require(1L)).thenReturn(draft("job-1"));
        ScheduleSlotDTO slot = ScheduleSlotDTO.builder()
                .slotId("slot-1")
                .runtimeJobId("job-1")
                .plannedStart("2026-09-18T09:00:00+08:00")
                .plannedEnd("2026-09-18T10:00:00+08:00")
                .build();
        when(runtimeQueryApi.listSlotsByJobId("job-1")).thenReturn(CommonResult.success(List.of(slot)));

        InspectionTaskRespVO vo = service.getTaskDetail(1L);

        assertEquals(1, vo.getScheduleSlots().size());
        assertEquals("slot-1", vo.getScheduleSlots().get(0).getSlotId());
    }

    @Test
    @DisplayName("没有作业号就不查中台，计划点保持空")
    void getTaskDetail_withoutRuntimeJob_skipsRuntime() {
        when(store.require(1L)).thenReturn(draft(null));

        InspectionTaskRespVO vo = service.getTaskDetail(1L);

        assertTrue(vo.getScheduleSlots() == null || vo.getScheduleSlots().isEmpty());
        verify(runtimeQueryApi, never()).listSlotsByJobId(org.mockito.ArgumentMatchers.any());
    }

    private static PatrolTaskDraft draft(String runtimeJobId) {
        return new PatrolTaskDraft(
                1L, "1-5# 储罐每周巡检", "巡检", 1L, "UAV",
                null, null, null, null, null,
                "draft", 3, null, null, runtimeJobId, Boolean.FALSE, null, null, null, null);
    }
}
