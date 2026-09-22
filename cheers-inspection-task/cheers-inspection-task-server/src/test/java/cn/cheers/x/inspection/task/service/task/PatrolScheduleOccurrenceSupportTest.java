package cn.cheers.x.inspection.task.service.task;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PatrolScheduleOccurrenceSupport")
class PatrolScheduleOccurrenceSupportTest {

    @Test
    @DisplayName("每日固定 09:00/18:00 在范围内逐日展开")
    void expand_dailyFixedTimes_coversRange() {
        List<OffsetDateTime> starts = PatrolScheduleOccurrenceSupport.expandPlannedStarts(dailyConfig(
                "2026-09-01", "2026-09-02", List.of("09:00", "18:00")));

        assertEquals(4, starts.size());
        assertEquals(OffsetDateTime.parse("2026-09-01T09:00:00+08:00"), starts.get(0));
        assertEquals(OffsetDateTime.parse("2026-09-01T18:00:00+08:00"), starts.get(1));
        assertEquals(OffsetDateTime.parse("2026-09-02T09:00:00+08:00"), starts.get(2));
        assertEquals(OffsetDateTime.parse("2026-09-02T18:00:00+08:00"), starts.get(3));
    }

    @Test
    @DisplayName("未启用模板不产出计划时刻")
    void expand_disabledItem_empty() {
        Map<String, Object> config = dailyConfig("2026-09-01", "2026-09-03", List.of("09:00"));
        @SuppressWarnings("unchecked")
        Map<String, Object> item = (Map<String, Object>) ((List<?>) config.get("items")).get(0);
        item.put("enabled", false);

        assertTrue(PatrolScheduleOccurrenceSupport.expandPlannedStarts(config).isEmpty());
    }

    @Test
    @DisplayName("缺重复范围不产出计划时刻")
    void expand_missingRange_empty() {
        Map<String, Object> config = dailyConfig("2026-09-01", "2026-09-03", List.of("09:00"));
        @SuppressWarnings("unchecked")
        Map<String, Object> item = (Map<String, Object>) ((List<?>) config.get("items")).get(0);
        @SuppressWarnings("unchecked")
        Map<String, Object> values = (Map<String, Object>) item.get("values");
        values.remove("repeatRange");

        assertTrue(PatrolScheduleOccurrenceSupport.expandPlannedStarts(config).isEmpty());
    }

    private static Map<String, Object> dailyConfig(String start, String end, List<String> timePoints) {
        Map<String, Object> range = new LinkedHashMap<>();
        range.put("startDate", start);
        range.put("endDate", end);
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("scheduleMode", 1);
        values.put("repeatMode", 2);
        values.put("timePoints", timePoints);
        values.put("repeatRange", range);
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("enabled", true);
        item.put("values", values);
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("items", List.of(item));
        config.put("version", 1);
        return config;
    }
}
