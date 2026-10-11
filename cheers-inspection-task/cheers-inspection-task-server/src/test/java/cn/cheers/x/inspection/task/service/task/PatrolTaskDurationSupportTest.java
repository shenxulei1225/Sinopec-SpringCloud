package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.inspection.task.model.task.InspectionContent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PatrolTaskDurationSupportTest {

    @Test
    void totalMinutes_addsItemActionAndTravel() {
        InspectionContent content = new InspectionContent();
        content.setItemActionDurationMinutes(BigDecimal.valueOf(720));
        assertEquals(57, PatrolTaskDurationSupport.totalMinutes(
                content, Map.of("estimatedTravelDuration", 2700), "ROBOT"));
    }

    @Test
    void totalMinutes_missingBoth_returnsNull() {
        assertNull(PatrolTaskDurationSupport.totalMinutes(new InspectionContent(), Map.of(), "ROBOT"));
    }

    @Test
    void totalMinutes_fractionRoundsUp() {
        InspectionContent content = new InspectionContent();
        content.setItemActionDurationMinutes(new BigDecimal("30"));
        assertEquals(8, PatrolTaskDurationSupport.totalMinutes(
                content, Map.of("estimatedTravelDuration", 420), "ROBOT"));
    }
}
