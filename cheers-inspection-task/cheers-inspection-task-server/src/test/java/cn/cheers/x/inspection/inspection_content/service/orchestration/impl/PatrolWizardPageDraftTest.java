package cn.cheers.x.inspection.inspection_content.service.orchestration.impl;

import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapReqDTO;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.task.PatrolItemActionDurationCalculator;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("向导排期用动作库现在的分钟数")
class PatrolWizardPageDraftTest {

    @Test
    @DisplayName("内容里留下的旧总分钟不拿来占这次的窗口")
    void overlay_replacesStaleItemMinutesWithLive() {
        InspectionContent content = new InspectionContent();
        content.setItemActionDurationMinutes(new BigDecimal("22"));
        InspectionContent.ObjectContent object = new InspectionContent.ObjectContent();
        object.setObjectId(10L);
        InspectionContent.ItemContent item = new InspectionContent.ItemContent();
        item.setItemId(1L);
        object.setItems(List.of(item));
        content.setCustomObjects(List.of(object));

        PatrolItemActionDurationCalculator calculator = mock(PatrolItemActionDurationCalculator.class);
        when(calculator.computeLiveSecondsForEquipment(any(), eq("ROBOT"), any()))
                .thenReturn(Optional.of(BigDecimal.ZERO));

        PatrolScheduleMapReqDTO request = new PatrolScheduleMapReqDTO();
        request.setPageInspectionContent(content);
        request.setPagePatrolExecutionMode("ROBOT");

        PatrolTaskDraft stored = new PatrolTaskDraft(
                99L, "task", "巡检", 7L, "ROBOT", null, null, null,
                null, null, "draft", 2, "sta-start", "sta-end", null, Boolean.TRUE,
                null, null, null, null, null);

        PatrolTaskDraft working = PatrolWizardPageDraft.overlay(stored, request, calculator);

        assertEquals(0, working.inspectionContent().getItemActionDurationMinutes().compareTo(BigDecimal.ZERO));
    }
}
