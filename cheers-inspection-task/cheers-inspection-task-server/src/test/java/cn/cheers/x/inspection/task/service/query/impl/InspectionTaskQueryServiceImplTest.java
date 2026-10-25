package cn.cheers.x.inspection.task.service.query.impl;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskGeneratedInputReviewRespVO;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskPageReqVO;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskRespVO;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskSimpleRespVO;
import cn.cheers.x.inspection.task.service.task.GeneratedCheckInputReviewReadContext;
import cn.cheers.x.inspection.task.service.task.GeneratedCheckInputReviewService;
import cn.cheers.x.inspection.task.service.task.GeneratedCheckInputReviewReadSupport;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("InspectionTaskQueryServiceImpl 计划点回填")
class InspectionTaskQueryServiceImplTest {

    private PatrolTaskEntityStore store;
    private RuntimeQueryApi runtimeQueryApi;
    private GeneratedCheckInputReviewService reviewService;
    private GeneratedCheckInputReviewReadSupport readSupport;
    private InspectionTaskQueryServiceImpl service;

    @BeforeEach
    void setUp() {
        store = mock(PatrolTaskEntityStore.class);
        runtimeQueryApi = mock(RuntimeQueryApi.class);
        reviewService = mock(GeneratedCheckInputReviewService.class);
        readSupport = mock(GeneratedCheckInputReviewReadSupport.class);
        when(store.readArrangePolicy(1L)).thenReturn(new PatrolTaskEntityStore.ArrangePolicy(null, null, null, null));
        service = new InspectionTaskQueryServiceImpl(
                store,
                runtimeQueryApi,
                new ObjectMapper(),
                reviewService,
                readSupport);
    }

    @Test
    @DisplayName("任务列表把名称、管理状态和分页范围交给数据库")
    void getTaskPage_delegatesDatabasePageAndReviewsCurrentPageOnly() {
        PatrolTaskDraft generated = draft("job-1", "enabled", true);
        GeneratedCheckInputReviewReadContext context = mock(GeneratedCheckInputReviewReadContext.class);
        when(store.page("罐区", 1, 2, 20))
                .thenReturn(new PageResult<>(List.of(generated), 41L));
        when(readSupport.load(List.of(generated))).thenReturn(context);
        when(reviewService.review(generated, context))
                .thenReturn(new InspectionTaskGeneratedInputReviewRespVO());
        InspectionTaskPageReqVO req = new InspectionTaskPageReqVO();
        req.setTaskName("罐区");
        req.setStatus(1);
        req.setPageNo(2);
        req.setPageSize(20);

        PageResult<?> result = service.getTaskPage(req);

        assertEquals(41L, result.getTotal());
        verify(store).page("罐区", 1, 2, 20);
        verify(store, never()).listAll();
        verify(readSupport).load(List.of(generated));
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
    @DisplayName("没写下管理状态时列表仍返回这一条，状态留空")
    void getTaskPage_missingManageStatus_keepsRow() {
        PatrolTaskDraft missing = draft("job-1", null, false);
        when(store.page(null, null, 1, 10))
                .thenReturn(new PageResult<>(List.of(missing), 1L));
        when(readSupport.load(List.of())).thenReturn(mock(GeneratedCheckInputReviewReadContext.class));
        InspectionTaskPageReqVO req = new InspectionTaskPageReqVO();
        req.setPageNo(1);
        req.setPageSize(10);

        PageResult<InspectionTaskSimpleRespVO> result = service.getTaskPage(req);

        assertEquals(1L, result.getTotal());
        assertNull(result.getList().get(0).getStatus());
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
        return draft(runtimeJobId, "draft", false);
    }

    private static PatrolTaskDraft draft(
            String runtimeJobId,
            String manageStatus,
            boolean committed) {
        return new PatrolTaskDraft(
                1L, "1-5# 储罐每周巡检", "巡检", 1L, "UAV",
                null, null, null, null, null,
                manageStatus, 3, null, null, runtimeJobId, committed, null, null, null, null, null, null);
    }
}
