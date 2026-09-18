package cn.cheers.x.inspection.task.service.task.impl;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskCreateProgressRespVO;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

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
        service = new PatrolTaskCreateProcessServiceImpl(store);
    }

    @Test
    void advance_objectsToRoute_writesUnlockedOne() {
        when(store.require(8L)).thenReturn(draft(0, "UAV", selectedTank(), null));

        InspectionTaskCreateProgressRespVO progress = service.advance(8L, 1);

        assertEquals(1, progress.getUnlockedStep());
        verify(store).mergeDraftFields(8L, Map.of(PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED, 1));
    }

    @Test
    void advance_skipAhead_throws() {
        when(store.require(8L)).thenReturn(draft(0, "UAV", selectedTank(), null));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.advance(8L, 2));
        assertTrue(ex.getMessage().contains("不能跳过"));
        verify(store, never()).mergeDraftFields(any(), any());
    }

    @Test
    void advance_withoutObjects_throws() {
        when(store.require(8L)).thenReturn(draft(0, "UAV", null, null));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.advance(8L, 1));
        assertTrue(ex.getMessage().contains("巡检对象"));
    }

    @Test
    void advance_routeWithoutSavedPath_throws() {
        when(store.require(8L)).thenReturn(draft(1, "UAV", selectedTank(), null));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.advance(8L, 2));
        assertTrue(ex.getMessage().contains("保存路线"));
    }

    @Test
    void advance_fixedCameraSkipsRoute() {
        when(store.require(8L)).thenReturn(draft(1, "FIXED_CAMERA", selectedTank(), null));

        InspectionTaskCreateProgressRespVO progress = service.advance(8L, 2);

        assertEquals(2, progress.getUnlockedStep());
        verify(store).mergeDraftFields(8L, Map.of(PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED, 2));
    }

    @Test
    void getProgress_clampsOversizedStepToLast() {
        when(store.require(8L)).thenReturn(draft(9, "UAV", selectedTank(), Map.of("stopIds", List.of("a"))));

        InspectionTaskCreateProgressRespVO progress = service.getProgress(8L);

        assertEquals(3, progress.getUnlockedStep());
        assertEquals(3, progress.getLastStep());
    }

    @Test
    void advance_scheduleToOrchestrationConfirm() {
        when(store.require(8L)).thenReturn(draft(2, "UAV", selectedTank(), Map.of("stopIds", List.of("a"))));

        InspectionTaskCreateProgressRespVO progress = service.advance(8L, 3);

        assertEquals(3, progress.getUnlockedStep());
        assertEquals(3, progress.getLastStep());
        verify(store).mergeDraftFields(8L, Map.of(PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED, 3));
    }

    @Test
    void advance_beyondLastStep_throws() {
        when(store.require(8L)).thenReturn(draft(3, "UAV", selectedTank(), Map.of("stopIds", List.of("a"))));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.advance(8L, 4));
        assertTrue(ex.getMessage().contains("0 到 3"));
        verify(store, never()).mergeDraftFields(any(), any());
    }

    @Test
    void invalidate_pullsBackAndClearsRoute() {
        when(store.require(8L)).thenReturn(draft(2, "UAV", selectedTank(), Map.of("stopIds", List.of("a"))));

        InspectionTaskCreateProgressRespVO progress = service.invalidate(8L, 1);

        assertEquals(1, progress.getUnlockedStep());
        verify(store).clearPlannedRoute(8L);
        verify(store).mergeDraftFields(eq(8L), eq(Map.of(PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED, 1)));
    }

    private static PatrolTaskDraft draft(
            int unlocked,
            String mode,
            InspectionContent content,
            Object plannedRoute
    ) {
        return new PatrolTaskDraft(
                8L, "样例", "巡检", 2L, mode, content, null, null, null, plannedRoute, "draft", unlocked, null, null);
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
