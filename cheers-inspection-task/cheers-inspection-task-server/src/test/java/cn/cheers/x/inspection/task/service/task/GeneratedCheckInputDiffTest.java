package cn.cheers.x.inspection.task.service.task;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratedCheckInputDiffTest {

    @Test
    void paramValueChange_usesSlotsTheStepDeclares() {
        GeneratedCheckInputDiff.StepView live = new GeneratedCheckInputDiff.StepView(
                "n1", "photo", List.of("yaw", "focus"),
                Map.of("yaw", "10", "focus", "2"));
        GeneratedCheckInputDiff.StepView saved = new GeneratedCheckInputDiff.StepView(
                "n1", "photo", List.of("yaw", "focus"),
                Map.of("yaw", "10", "focus", "4"));

        List<GeneratedCheckInputDiff.Change> changes = GeneratedCheckInputDiff.diffItem(List.of(live), List.of(saved));

        assertEquals(1, changes.size());
        assertEquals("参数", changes.get(0).aspect());
        assertTrue(changes.get(0).detail().contains("focus"));
        assertFalse(changes.get(0).durationInput());
        assertEquals(List.of("focus"), changes.get(0).slots());
    }

    @Test
    void actionDurationChange_isDurationInput_andUnknownCodeIsNotRouteInput() {
        String customPoint = "F-custom-point";
        GeneratedCheckInputDiff.StepView live = new GeneratedCheckInputDiff.StepView(
                "n1", "photo", List.of("action_duration", customPoint, "yaw"),
                Map.of("action_duration", "3", customPoint, "9", "yaw", "1"));
        GeneratedCheckInputDiff.StepView saved = new GeneratedCheckInputDiff.StepView(
                "n1", "photo", List.of("action_duration", customPoint, "yaw"),
                Map.of("action_duration", "1", customPoint, "8", "yaw", "1"));

        List<GeneratedCheckInputDiff.Change> changes = GeneratedCheckInputDiff.diffItem(List.of(live), List.of(saved));

        assertEquals(1, changes.size());
        assertTrue(changes.get(0).durationInput());
        assertFalse(changes.get(0).routeInput());
        assertEquals(List.of("action_duration", customPoint), changes.get(0).slots());
    }

    @Test
    void stepListChange_doesNotRequireDurationOrStopFields() {
        GeneratedCheckInputDiff.StepView live = new GeneratedCheckInputDiff.StepView(
                "n2", "record", List.of("dwell"), Map.of("dwell", "3"));
        GeneratedCheckInputDiff.StepView saved = new GeneratedCheckInputDiff.StepView(
                "n1", "photo", List.of("yaw"), Map.of("yaw", "1"));

        List<GeneratedCheckInputDiff.Change> changes = GeneratedCheckInputDiff.diffItem(List.of(live), List.of(saved));

        assertEquals(1, changes.size());
        assertEquals("步骤", changes.get(0).aspect());
    }

    @Test
    void removedApplicableRelation_isAChangeEvenWhenStepsRemain() {
        List<GeneratedCheckInputDiff.Change> changes = GeneratedCheckInputDiff.noLongerApplicable();

        assertEquals(1, changes.size());
        assertEquals("适用关系", changes.get(0).aspect());
        assertTrue(changes.get(0).detail().contains("不再适用"));
    }

    @Test
    void locationIdAndSavedName_areTheSamePoint() {
        Map<String, String> pointNames = Map.of("78", "南罐组东罐9顶");
        String slot = "F-e49f76bdca3e4e1393e8e2f1cc0e7867";
        GeneratedCheckInputDiff.StepView live = new GeneratedCheckInputDiff.StepView(
                "n1", "arrive", List.of(slot),
                GeneratedCheckInputDiff.valuesOf(List.of(slot), Map.of(slot, 78), pointNames));
        GeneratedCheckInputDiff.StepView saved = new GeneratedCheckInputDiff.StepView(
                "n1", "arrive", List.of(slot),
                GeneratedCheckInputDiff.valuesOf(List.of(slot), Map.of(slot, "南罐组东罐9顶"), pointNames));

        assertTrue(GeneratedCheckInputDiff.diffItem(List.of(live), List.of(saved)).isEmpty());
    }

    @Test
    void locationObjectAndId_areTheSamePoint() {
        String slot = "F-e49f76bdca3e4e1393e8e2f1cc0e7867";
        Map<String, Object> place = Map.of("id", 78, "code", "tank-9", "name", "南罐组东罐9顶");
        GeneratedCheckInputDiff.StepView live = new GeneratedCheckInputDiff.StepView(
                "n1", "arrive", List.of(slot),
                GeneratedCheckInputDiff.valuesOf(List.of(slot), Map.of(slot, 78), Map.of()));
        GeneratedCheckInputDiff.StepView saved = new GeneratedCheckInputDiff.StepView(
                "n1", "arrive", List.of(slot),
                GeneratedCheckInputDiff.valuesOf(List.of(slot), Map.of(slot, place), Map.of()));

        assertTrue(GeneratedCheckInputDiff.diffItem(List.of(live), List.of(saved)).isEmpty());
    }

    @Test
    void savedArrivalPointWithoutEquipmentPoint_isNotAChange() {
        String slot = "F-e49f76bdca3e4e1393e8e2f1cc0e7867";
        GeneratedCheckInputDiff.StepView live = new GeneratedCheckInputDiff.StepView(
                "n1", "arrive", List.of(slot), Map.of(slot, ""));
        GeneratedCheckInputDiff.StepView saved = new GeneratedCheckInputDiff.StepView(
                "n1", "arrive", List.of(slot), Map.of(slot, "94"));

        assertTrue(GeneratedCheckInputDiff.diffItem(List.of(live), List.of(saved)).isEmpty());
    }

    @Test
    void differentLocationIds_areAParameterChange() {
        String slot = "F-e49f76bdca3e4e1393e8e2f1cc0e7867";
        GeneratedCheckInputDiff.StepView live = new GeneratedCheckInputDiff.StepView(
                "n1", "arrive", List.of(slot),
                GeneratedCheckInputDiff.valuesOf(List.of(slot), Map.of(slot, 78), Map.of()));
        GeneratedCheckInputDiff.StepView saved = new GeneratedCheckInputDiff.StepView(
                "n1", "arrive", List.of(slot),
                GeneratedCheckInputDiff.valuesOf(List.of(slot), Map.of(slot, 70), Map.of()));

        List<GeneratedCheckInputDiff.Change> changes = GeneratedCheckInputDiff.diffItem(List.of(live), List.of(saved));

        assertEquals(1, changes.size());
        assertEquals("参数", changes.get(0).aspect());
    }

    @Test
    void sameActionDurationWithTrailingZero_isNotAChange() {
        GeneratedCheckInputDiff.StepView live = new GeneratedCheckInputDiff.StepView(
                "n2", "hold", List.of("action_duration"), Map.of("action_duration", "1.666667"));
        GeneratedCheckInputDiff.StepView saved = new GeneratedCheckInputDiff.StepView(
                "n2", "hold", List.of("action_duration"), Map.of("action_duration", "1.6666670"));

        assertTrue(GeneratedCheckInputDiff.diffItem(List.of(live), List.of(saved)).isEmpty());
    }

    @Test
    void noSnapshot_isNotAChange() {
        GeneratedCheckInputDiff.StepView live = new GeneratedCheckInputDiff.StepView(
                "n1", "photo", List.of("yaw"), Map.of("yaw", "1"));

        assertTrue(GeneratedCheckInputDiff.diffItem(List.of(live), List.of()).isEmpty());
    }
}
