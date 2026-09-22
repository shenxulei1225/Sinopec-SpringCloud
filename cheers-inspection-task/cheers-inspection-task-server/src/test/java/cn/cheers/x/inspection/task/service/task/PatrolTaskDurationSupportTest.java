package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.inspection.task.model.task.InspectionContent;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PatrolTaskDurationSupportTest {

    @Test
    void totalMinutes_addsItemActionAndTravel() {
        InspectionContent content = new InspectionContent();
        content.setItemActionDurationMinutes(12);
        assertEquals(57, PatrolTaskDurationSupport.totalMinutes(
                content, Map.of("estimatedTravelDuration", 45), "ROBOT"));
    }

    @Test
    void totalMinutes_missingBoth_returnsNull() {
        assertNull(PatrolTaskDurationSupport.totalMinutes(new InspectionContent(), Map.of(), "ROBOT"));
    }
}
