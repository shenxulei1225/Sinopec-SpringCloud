package cn.cheers.x.module.dynamicbusiness.service.entity.refcategory;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityRefCategoryProjectionServiceImplTest {

    @Test
    void extractTargetEntityId_fromRefMap_anyType() {
        assertEquals(6L, EntityRefCategoryProjectionServiceImpl.extractTargetEntityId(
                Map.of("entityTypeCode", "region", "id", 6)));
        assertEquals(9L, EntityRefCategoryProjectionServiceImpl.extractTargetEntityId(
                Map.of("entityTypeCode", "zone", "id", 9)));
        assertEquals(6L, EntityRefCategoryProjectionServiceImpl.extractTargetEntityId(
                Map.of("entityTypeCode", "facility", "id", 6)));
    }

    @Test
    void extractTargetEntityId_fromNumber() {
        assertEquals(6L, EntityRefCategoryProjectionServiceImpl.extractTargetEntityId(6));
        assertNull(EntityRefCategoryProjectionServiceImpl.extractTargetEntityId(0));
        assertNull(EntityRefCategoryProjectionServiceImpl.extractTargetEntityId(null));
    }

    @Test
    void extractTargets_multiRefList() {
        Set<EntityRefCategoryProjectionServiceImpl.TargetRef> targets =
                EntityRefCategoryProjectionServiceImpl.extractTargets(
                        List.of(
                                Map.of("entityTypeCode", "zone", "id", 1),
                                Map.of("entityTypeCode", "zone", "id", 2),
                                Map.of("entityTypeCode", "zone", "id", 1)),
                        "zone");
        assertEquals(2, targets.size());
        assertTrue(targets.stream().anyMatch(t -> ObjectsEqualsId(t, 1L)));
        assertTrue(targets.stream().anyMatch(t -> ObjectsEqualsId(t, 2L)));
    }

    private static boolean ObjectsEqualsId(EntityRefCategoryProjectionServiceImpl.TargetRef t, Long id) {
        return t != null && id.equals(t.id());
    }
}
