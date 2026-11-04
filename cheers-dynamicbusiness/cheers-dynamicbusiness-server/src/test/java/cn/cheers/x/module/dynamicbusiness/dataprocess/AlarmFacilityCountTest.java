package cn.cheers.x.module.dynamicbusiness.dataprocess;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlarmFacilityCountTest {

    @Test
    void parentAddsTheLevelsUnderIt() {
        Map<Long, Long> parentOf = new LinkedHashMap<>();
        parentOf.put(1L, null);
        parentOf.put(2L, 1L);
        parentOf.put(3L, 1L);
        Map<Long, Map<String, AlarmFacilityCount.Cell>> direct = new LinkedHashMap<>();
        direct.put(2L, cell("紧急", "未处理"));
        direct.put(3L, cell("紧急", "已处理"));
        direct.get(3L).get("一般").addStatus("已处理");

        Map<Long, Map<String, AlarmFacilityCount.Cell>> rolled = AlarmFacilityCount.rollup(parentOf, direct);
        AlarmFacilityCount.Cell urgent = rolled.get(1L).get("紧急");
        assertEquals(2, urgent.getAll());
        assertEquals(1, urgent.getOpen());
        assertEquals(1, urgent.getCleared());
        assertEquals(1, rolled.get(1L).get("一般").getCleared());
        assertEquals(0, rolled.get(2L).get("一般").getAll());
    }

    @Test
    void aRecordLinkedToParentAndChildCountsOnceOnTheChild() {
        Map<Long, Long> parentOf = Map.of(1L, 0L, 2L, 1L);
        assertEquals(List.of(2L), AlarmFacilityCount.deepest(List.of(1L, 2L), parentOf));
    }

    @Test
    void reviewRepairAndEmergencyStayOpen() {
        AlarmFacilityCount.Cell cell = AlarmFacilityCount.emptyLevels().get("紧急");
        cell.addStatus("复核中");
        cell.addStatus("维修中");
        cell.addStatus("应急事件处理");
        assertEquals(3, cell.getAll());
        assertEquals(3, cell.getOpen());
        assertEquals(0, cell.getCleared());
    }

    @Test
    void ignoredCountsOnlyInAll() {
        AlarmFacilityCount.Cell cell = new AlarmFacilityCount.Cell();
        cell.addStatus("已忽略");
        assertEquals(1, cell.getAll());
        assertEquals(0, cell.getOpen());
        assertEquals(0, cell.getCleared());
    }

    private static Map<String, AlarmFacilityCount.Cell> cell(String level, String status) {
        Map<String, AlarmFacilityCount.Cell> levels = AlarmFacilityCount.emptyLevels();
        levels.get(level).addStatus(status);
        return levels;
    }
}
