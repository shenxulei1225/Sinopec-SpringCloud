package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatrolLayerDayAccountQueryTest {

    @Test
    void sql_readsSidebarFieldsForThisFacilityAndDay() {
        String select = PatrolLayerDayAccountQuery.selectSql("ent_task_excution_record_t1");
        String count = PatrolLayerDayAccountQuery.countSql("ent_task_excution_record_t1");
        assertTrue(select.contains("custom_fields->>'planned_start' AS planned_start"));
        assertTrue(select.contains("custom_fields->>'execution_status' AS execution_status"));
        assertTrue(select.contains("custom_fields->>'task_id' AS task_id"));
        assertTrue(select.contains("facility_id = ?"));
        assertTrue(select.contains("planned_start' >= ?"));
        assertTrue(select.contains("planned_start' < ?"));
        assertTrue(select.contains("'pending', 'in_progress', 'completed', 'fault', 'failed'"));
        assertTrue(select.contains("ORDER BY custom_fields->>'planned_start' ASC, id ASC"));
        assertTrue(select.contains("LIMIT ? OFFSET ?"));
        assertFalse(select.contains("SELECT custom_fields"));
        assertTrue(count.contains("facility_id = ?"));
        assertFalse(count.contains("LIMIT"));
    }

    @Test
    void beijingDayRange_keepsTheStoredClockInsideThatDay() {
        PatrolLayerDayAccountQuery.DayRange range = PatrolLayerDayAccountQuery.beijingDayRange("2026-10-24");
        assertEquals("2026-10-24T00:00:00+08:00", range.inclusiveStart());
        assertEquals("2026-10-25T00:00:00+08:00", range.exclusiveEnd());
        assertTrue(PatrolLayerDayAccountQuery.includesStoredClock(range, "2026-10-24T00:00:00+08:00"));
        assertTrue(PatrolLayerDayAccountQuery.includesStoredClock(range, "2026-10-24T09:00:00+08:00"));
        assertFalse(PatrolLayerDayAccountQuery.includesStoredClock(range, "2026-10-23T23:59:59+08:00"));
        assertFalse(PatrolLayerDayAccountQuery.includesStoredClock(range, "2026-10-25T00:00:00+08:00"));
        assertFalse(PatrolLayerDayAccountQuery.includesStoredClock(range, null));
        assertFalse(PatrolLayerDayAccountQuery.includesStoredClock(range, " "));
    }

    @Test
    void beijingDayRange_rejectsADayThatIsNotACalendarDate() {
        assertThrows(ServiceException.class, () -> PatrolLayerDayAccountQuery.beijingDayRange("2026-02-31"));
        assertThrows(ServiceException.class, () -> PatrolLayerDayAccountQuery.beijingDayRange(null));
    }
}
