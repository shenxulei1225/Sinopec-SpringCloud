package cn.cheers.x.inspection.task.service.task.impl;

import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskDurationRefreshReqVO;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskDurationRefreshRespVO;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.task.PatrolItemActionDurationCalculator;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
        when(calculator.computeLiveSecondsForEquipment(any(), any(), any())).thenReturn(Optional.empty());
        service = new PatrolTaskCreateProcessServiceImpl(store, calculator);
    }

    @Test
    void refreshDurations_liveStopsDiffer_pullsBackDraft() {
        InspectionContent content = selectedTank();
        content.setItemActionDurationMinutes(BigDecimal.valueOf(10));
        when(store.require(8L)).thenReturn(draft(
                3, "UAV", content, Map.of("stopIds", List.of("start", "a", "end"), "startStopId", "start", "endStopId", "end"),
                null, Boolean.FALSE, List.of(Map.of("slotId", "s"))));

        InspectionTaskDurationRefreshReqVO req = new InspectionTaskDurationRefreshReqVO();
        req.setLiveCheckItemStopIds(List.of("b"));

        InspectionTaskDurationRefreshRespVO resp = service.refreshDurations(8L, req);

        assertEquals(Boolean.FALSE, resp.getPathChanged());
        assertEquals(3, resp.getUnlockedStep());
        verify(store, never()).clearPlannedRoute(8L);
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
                null, null, runtimeJobId, orchestrationCommitted, null, null, orchestrationPreviewSlots, null, null);
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
