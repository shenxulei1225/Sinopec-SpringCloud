package cn.cheers.x.module.dynamicbusiness.service.entity.relation;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityCategoryRelationModelSyncTest {

    @Test
    void skipsFacilityCategoryWhenHangingPoint() {
        CategoryDO jinqiao = new CategoryDO();
        jinqiao.setId(2104756L);
        jinqiao.setCategoryTypeCode("facility");
        assertFalse(EntityCategoryRelationServiceImpl.categoryKindMatchesEntityType(jinqiao, "point"));
    }

    @Test
    void keepsPointCategoryWhenHangingPoint() {
        CategoryDO traversal = new CategoryDO();
        traversal.setId(2104750L);
        traversal.setCategoryTypeCode("point");
        assertTrue(EntityCategoryRelationServiceImpl.categoryKindMatchesEntityType(traversal, "point"));
    }

    @Test
    void rejectsBlankOrMissingCategory() {
        assertFalse(EntityCategoryRelationServiceImpl.categoryKindMatchesEntityType(null, "point"));
        CategoryDO blank = new CategoryDO();
        blank.setCategoryTypeCode("  ");
        assertFalse(EntityCategoryRelationServiceImpl.categoryKindMatchesEntityType(blank, "point"));
    }
}
