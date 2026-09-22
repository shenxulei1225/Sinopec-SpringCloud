package cn.cheers.x.module.dynamicbusiness.convert.entity;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityFieldMapsSupportTest {

    @Test
    void applyWriteMaps_keepsIncomingStepTree_overOldEmptyBaseDefault() {
        EntityDO entity = new EntityDO();
        Map<String, Object> tree = Map.of("version", 1, "nodes", List.of(Map.of("nodeKey", "n1")));

        EntityFieldMapsSupport.applyWriteMapsToEntityDO(
                entity,
                Map.of(
                        "entityTypeCode", "task",
                        "modelId", 8L,
                        "name", "样例",
                        "status", 1,
                        "step_tree_json", List.of()),
                Map.of("step_tree_json", tree));

        assertEquals(tree, entity.getCustomFields().get("step_tree_json"));
    }

    @Test
    void applyWriteMaps_fillsBaseDedicatedWhenRequestOmitsKey() {
        EntityDO entity = new EntityDO();

        EntityFieldMapsSupport.applyWriteMapsToEntityDO(
                entity,
                Map.of(
                        "entityTypeCode", "task",
                        "modelId", 8L,
                        "name", "样例",
                        "status", 1,
                        "facility_id", 2L),
                Map.of("FLD-TSK-003", "巡检"));

        assertEquals(2L, entity.getCustomFields().get("facility_id"));
        assertEquals("巡检", entity.getCustomFields().get("FLD-TSK-003"));
        assertTrue(entity.getCustomFields().containsKey("facility_id"));
    }
}
