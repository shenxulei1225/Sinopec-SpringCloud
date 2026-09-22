package cn.cheers.x.inspection.task.service.task.impl;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskCreateProgressRespVO;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskDurationRefreshReqVO;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskDurationRefreshRespVO;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.task.CreateWizardInvalidation;
import cn.cheers.x.inspection.task.service.task.PatrolItemActionDurationCalculator;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PatrolTaskCreateProcessServiceImplTest {

    private PatrolTaskEntityStore store;
    private PatrolTaskCreateProcessServiceImpl service;

    @BeforeEach
    void setUp() {
        store = mock(PatrolTaskEntityStore.class);
        PatrolItemActionDurationCalculator calculator = mock(PatrolItemActionDurationCalculator.class);
        when(calculator.computeLiveMinutes(any(), any())).thenReturn(Optional.empty());
        service = new PatrolTaskCreateProcessServiceImpl(store, calculator);
    }

    @Test
    void advance_objectsToRoute_requiresObjectsItemsAndMode() {
        PatrolTaskDraft draft = draft(0, "UAV", selectedTank(), null, null, null, null);
        when(store.require(8L)).thenReturn(draft);

        InspectionTaskCreateProgressRespVO progress = service.advance(8L, 1);

        assertEquals(1, progress.getUnlockedStep());
        verify(store).mergeDraftFields(8L, Map.of(PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED, 1));
    }

    @Test
    void advance_skipAhead_throws() {
        when(store.require(8L)).thenReturn(draft(0, "UAV", selectedTank(), null, null, null, null));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.advance(8L, 2));
        assertTrue(ex.getMessage().contains("不能跳过"));
        verify(store, never()).mergeDraftFields(any(), any());
    }

    @Test
    void advance_withoutObjects_throws() {
        when(store.require(8L)).thenReturn(draft(0, "UAV", null, null, null, null, null));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.advance(8L, 1));
        assertTrue(ex.getMessage().contains("巡检对象"));
    }

    @Test
    void advance_routeWithoutSavedPath_throws() {
        when(store.require(8L)).thenReturn(draft(1, "UAV", selectedTank(), null, null, null, null));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.advance(8L, 2));
        assertTrue(ex.getMessage().contains("保存路线"));
    }

    @Test
    void advance_fixedCameraSkipsRoute() {
        when(store.require(8L)).thenReturn(draft(1, "FIXED_CAMERA", selectedTank(), null, null, null, null));

        InspectionTaskCreateProgressRespVO progress = service.advance(8L, 2);

        assertEquals(2, progress.getUnlockedStep());
        verify(store).mergeDraftFields(8L, Map.of(PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED, 2));
    }

    @Test
    void getProgress_clampsOversizedStepToLast() {
        when(store.require(8L)).thenReturn(draft(9, "UAV", selectedTank(), Map.of("stopIds", List.of("a")), null, null, null));

        InspectionTaskCreateProgressRespVO progress = service.getProgress(8L);

        assertEquals(3, progress.getUnlockedStep());
        assertEquals(3, progress.getLastStep());
    }

    @Test
    void advance_scheduleToConfirm_withoutPreview_throws() {
        when(store.require(8L)).thenReturn(draft(2, "UAV", selectedTank(), Map.of("stopIds", List.of("a")), null, Boolean.FALSE, null));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.advance(8L, 3));
        assertTrue(ex.getMessage().contains("还没有试排计划"));
        verify(store, never()).mergeDraftFields(any(), any());
    }

    @Test
    void advance_scheduleToConfirm_withPreview() {
        when(store.require(8L)).thenReturn(draft(
                2, "UAV", selectedTank(), Map.of("stopIds", List.of("a")), null, Boolean.FALSE,
                List.of(Map.of("slotId", "slot-1"))));

        InspectionTaskCreateProgressRespVO progress = service.advance(8L, 3);

        assertEquals(3, progress.getUnlockedStep());
        assertEquals(3, progress.getLastStep());
        verify(store).mergeDraftFields(8L, Map.of(PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED, 3));
    }

    @Test
    void advance_beyondLastStep_throws() {
        when(store.require(8L)).thenReturn(draft(3, "UAV", selectedTank(), Map.of("stopIds", List.of("a")), "job-1", Boolean.TRUE, null));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.advance(8L, 4));
        assertTrue(ex.getMessage().contains("0 到 3"));
        verify(store, never()).mergeDraftFields(any(), any());
    }

    @Test
    void invalidate_pullsBackAndClearsRoute() {
        when(store.require(8L)).thenReturn(draft(2, "UAV", selectedTank(), Map.of("stopIds", List.of("a")), null, null, null));

        InspectionTaskCreateProgressRespVO progress = service.invalidate(8L, 1, true);

        assertEquals(1, progress.getUnlockedStep());
        verify(store).clearPlannedRoute(8L);
        verify(store).clearLaterComputedResults(8L);
        verify(store).mergeDraftFields(eq(8L), eq(Map.of(PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED, 1)));
    }

    @Test
    void invalidate_executionMeansChanged_pullsBackToObjects() {
        when(store.require(8L)).thenReturn(draft(3, "ROBOT", selectedTank(), Map.of("stopIds", List.of("a")), null, Boolean.FALSE, List.of(Map.of("slotId", "s"))));

        InspectionTaskCreateProgressRespVO progress = service.invalidate(8L, CreateWizardInvalidation.EXECUTION_MEANS_CHANGED);

        assertEquals(0, progress.getUnlockedStep());
        verify(store).clearPlannedRoute(8L);
        verify(store).clearLaterComputedResults(8L);
        verify(store).mergeDraftFields(eq(8L), eq(Map.of(PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED, 0)));
    }

    @Test
    void invalidate_saveRouteKeepsPlannedRoute() {
        when(store.require(8L)).thenReturn(draft(3, "UAV", selectedTank(), Map.of("stopIds", List.of("a")), null, null, List.of(Map.of("slotId", "s"))));

        InspectionTaskCreateProgressRespVO progress = service.invalidate(8L, 1, false);

        assertEquals(1, progress.getUnlockedStep());
        verify(store, never()).clearPlannedRoute(8L);
        verify(store).clearLaterComputedResults(8L);
    }

    @Test
    void refreshDurations_liveStopsDiffer_pullsBackDraft() {
        InspectionContent content = selectedTank();
        content.setItemActionDurationMinutes(10);
        when(store.require(8L)).thenReturn(draft(
                3, "UAV", content, Map.of("stopIds", List.of("start", "a", "end"), "startStopId", "start", "endStopId", "end"),
                null, Boolean.FALSE, List.of(Map.of("slotId", "s"))));

        InspectionTaskDurationRefreshReqVO req = new InspectionTaskDurationRefreshReqVO();
        req.setLiveCheckItemStopIds(List.of("b"));

        InspectionTaskDurationRefreshRespVO resp = service.refreshDurations(8L, req);

        assertEquals(Boolean.TRUE, resp.getPathChanged());
        assertEquals(1, resp.getUnlockedStep());
        verify(store).clearPlannedRoute(8L);
    }

    private static PatrolTaskDraft draft(
            int unlocked,
            String mode,
            InspectionContent content,
            Object plannedRoute,
            String runtimeJobId,
            Boolean orchestrationCommitted,
            Object orchestrationPreviewSlots
    ) {
        return new PatrolTaskDraft(
                8L, "样例", "巡检", 2L, mode, content, null, null, null, plannedRoute, "draft", unlocked,
                null, null, runtimeJobId, orchestrationCommitted, null, null, orchestrationPreviewSlots, null);
    }

    private static InspectionContent selectedTank() {
        InspectionContent.ItemContent item = new InspectionContent.ItemContent();
        item.setItemId(7L);
        InspectionContent.ObjectContent object = new InspectionContent.ObjectContent();
        object.setObjectId(900104L);
        object.setItems(List.of(item));
        InspectionContent content = new InspectionContent();
        content.setCustomObjects(List.of(object));
        return content;
    }
}
