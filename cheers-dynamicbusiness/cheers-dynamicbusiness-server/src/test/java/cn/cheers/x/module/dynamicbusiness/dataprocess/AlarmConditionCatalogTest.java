package cn.cheers.x.module.dynamicbusiness.dataprocess;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlarmConditionCatalogTest {

    @Test
    void samePointOnTwoModelsDropsTheModelNameOnTheShortLabel() {
        List<AlarmConditionCatalog.PointSentence> rows = List.of(
                new AlarmConditionCatalog.PointSentence(1, "水泵", "F-run", "0", "运行", "没有运行"),
                new AlarmConditionCatalog.PointSentence(2, "风机", "F-run", "0", "运行", "没有运行"));
        List<AlarmConditionCatalog.Published> published = AlarmConditionCatalog.conditions(rows);
        assertEquals(1, published.size());
        assertEquals("运行：没有运行", published.get(0).label());
        assertEquals("水泵、风机 · 运行：没有运行", published.get(0).wideLabel());
        assertEquals("bit:F-run:0", published.get(0).code());
        assertEquals(List.of("1", "2"), published.get(0).modelIds());
    }

    @Test
    void differentPointsKeepSeparateOptions() {
        List<AlarmConditionCatalog.PointSentence> rows = List.of(
                new AlarmConditionCatalog.PointSentence(1, "水泵", "F-pump", "1", "远程", "处于远程，平台可以控制"),
                new AlarmConditionCatalog.PointSentence(3, "液压井盖", "F-hatch", "0", "远程", "处于远程，平台可以控制"));
        List<AlarmConditionCatalog.Published> published = AlarmConditionCatalog.conditions(rows);
        assertEquals(2, published.size());
        assertTrue(published.stream().anyMatch(item -> item.label().equals("远程：处于远程，平台可以控制")
                && item.wideLabel().equals("水泵 · 远程：处于远程，平台可以控制")));
        assertTrue(published.stream().anyMatch(item -> item.wideLabel().startsWith("液压井盖 · ")));
    }

    @Test
    void sharedCodeResolvesEachModelsOwnPoint() {
        List<AlarmConditionCatalog.PointSentence> rows = List.of(
                new AlarmConditionCatalog.PointSentence(1, "水泵", "F-run", "0", "运行", "没有运行"),
                new AlarmConditionCatalog.PointSentence(2, "风机", "F-run", "0", "运行", "没有运行"));
        Map<Long, AlarmPolicy.Expect> resolved = AlarmConditionCatalog.resolve("bit:F-run:0", rows);
        assertEquals("F-run", resolved.get(1L).pointCode());
        assertEquals(0d, resolved.get(1L).expected());
        assertEquals("F-run", resolved.get(2L).pointCode());
        assertTrue(AlarmConditionCatalog.resolve("bit:F-run:0", rows).get(9L) == null);
    }
}
