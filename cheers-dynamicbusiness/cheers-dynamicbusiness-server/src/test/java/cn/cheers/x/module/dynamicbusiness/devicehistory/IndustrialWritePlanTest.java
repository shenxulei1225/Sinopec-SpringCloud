package cn.cheers.x.module.dynamicbusiness.devicehistory;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IndustrialWritePlanTest {

    @Test
    void plansTheMatchedWritePoint() {
        IndustrialWriteRecipe recipe = recipe("MODEL-PUMP", "F-switch", 1);
        IndustrialWritePlan.Plan plan = IndustrialWritePlan.plan(
                recipe, "MODEL-PUMP", List.of(row("F-switch", "Z1/SW")), Map.of("Z1/SW", "ns=2;s=SW"));

        assertNull(plan.refusal());
        assertEquals(1, plan.writes().size());
        assertEquals("ns=2;s=SW", plan.writes().get(0).nodeId());
        assertTrue(plan.writes().get(0).on());
    }

    @Test
    void refusesWhenTheNodeWasNotBrowsed() {
        IndustrialWriteRecipe recipe = recipe("MODEL-PUMP", "F-switch", 0);
        IndustrialWritePlan.Plan plan = IndustrialWritePlan.plan(
                recipe, "MODEL-PUMP", List.of(row("F-switch", "Z1/SW")), Map.of());

        assertEquals("这个写点还没有节点标识", plan.refusal());
        assertTrue(plan.writes().isEmpty());
    }

    @Test
    void refusesWhenTheEquipmentModelDoesNotMatch() {
        IndustrialWriteRecipe recipe = recipe("MODEL-PUMP", "F-switch", 1);
        IndustrialWritePlan.Plan plan = IndustrialWritePlan.plan(
                recipe, "MODEL-FAN", List.of(row("F-switch", "Z1/SW")), Map.of("Z1/SW", "ns=2;s=SW"));

        assertEquals("这台设备不是这个动作管的型号", plan.refusal());
    }

    @Test
    void refusesWhenTheWritePointIsMissingOrDuplicated() {
        IndustrialWriteRecipe recipe = recipe("MODEL-PUMP", "F-switch", 1);

        IndustrialWritePlan.Plan missing = IndustrialWritePlan.plan(recipe, "MODEL-PUMP", List.of(), Map.of());
        IndustrialWritePlan.Plan duplicated = IndustrialWritePlan.plan(
                recipe, "MODEL-PUMP", List.of(row("F-switch", "A"), row("F-switch", "B")), Map.of());

        assertEquals("点位对照里对不上这个写点", missing.refusal());
        assertEquals("点位对照里对不上这个写点", duplicated.refusal());
    }

    @Test
    void plansBothCoverTagsInOrder() {
        IndustrialWriteRecipe recipe = new IndustrialWriteRecipe(List.of("MODEL-COVER"), List.of(
                new IndustrialWriteRecipe.Write("F-open", 1),
                new IndustrialWriteRecipe.Write("F-close", 0)));
        IndustrialWritePlan.Plan plan = IndustrialWritePlan.plan(recipe, "MODEL-COVER", List.of(
                row("F-open", "Z1/OPEN"),
                row("F-close", "Z1/CLOSE")), Map.of("Z1/OPEN", "ns=2;s=OPEN", "Z1/CLOSE", "ns=2;s=CLOSE"));

        assertNull(plan.refusal());
        assertEquals("ns=2;s=OPEN", plan.writes().get(0).nodeId());
        assertTrue(plan.writes().get(0).on());
        assertEquals("ns=2;s=CLOSE", plan.writes().get(1).nodeId());
        assertTrue(!plan.writes().get(1).on());
    }

    @Test
    void refusesAnActionThatHasNoWriteBook() {
        IndustrialWritePlan.Plan plan = IndustrialWritePlan.plan(null, "MODEL-PUMP", List.of(), Map.of());

        assertEquals("这条动作还没对上写点", plan.refusal());
    }

    @Test
    void readsGallerySingleModelAndInspectionModelList() {
        IndustrialWriteRecipe gallery = IndustrialWriteRecipe.parse("""
                {"industrialEquipment":{"equipmentModelCode":"MODEL-PUMP","writes":[{"fieldCode":"F-switch","value":1}]}}
                """);
        IndustrialWriteRecipe valve = IndustrialWriteRecipe.parse("""
                {"industrialEquipment":{"equipmentModelCodes":["MODEL-VALVE","MODEL-DN400"],"writes":[]}}
                """);

        assertTrue(gallery.appliesTo("MODEL-PUMP"));
        assertEquals(1, gallery.writes().size());
        assertTrue(valve.appliesTo("MODEL-DN400"));
        assertTrue(valve.writes().isEmpty());
    }

    @Test
    void refusesWhenTheValveActionHasNoWritePointYet() {
        IndustrialWriteRecipe recipe = new IndustrialWriteRecipe(List.of("MODEL-VALVE", "MODEL-DN400"), List.of());

        IndustrialWritePlan.Plan valve = IndustrialWritePlan.plan(recipe, "MODEL-VALVE", List.of(), Map.of());
        IndustrialWritePlan.Plan other = IndustrialWritePlan.plan(recipe, "MODEL-PUMP", List.of(), Map.of());

        assertEquals("这条动作还没对上写点", valve.refusal());
        assertEquals("这台设备不是这个动作管的型号", other.refusal());
    }

    private static IndustrialWriteRecipe recipe(String model, String field, int value) {
        return new IndustrialWriteRecipe(List.of(model), List.of(new IndustrialWriteRecipe.Write(field, value)));
    }

    private static IndustrialWritePlan.BoundWrite row(String field, String binding) {
        return new IndustrialWritePlan.BoundWrite(field, binding);
    }
}
