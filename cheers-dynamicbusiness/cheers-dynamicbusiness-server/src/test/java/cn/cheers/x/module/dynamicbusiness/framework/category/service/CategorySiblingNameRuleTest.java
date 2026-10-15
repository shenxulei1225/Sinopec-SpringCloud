package cn.cheers.x.module.dynamicbusiness.framework.category.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategorySiblingNameRuleTest {

    @Test
    void department_allowsTheSameNameUnderOneParent() {
        assertFalse(AbstractCategoryService.rejectsDuplicateSiblingName("department"));
    }

    @Test
    void equipmentCategoryTree_stillRejectsTheSameNameUnderOneParent() {
        assertTrue(AbstractCategoryService.rejectsDuplicateSiblingName("equipment"));
        assertTrue(AbstractCategoryService.rejectsDuplicateSiblingName("zone"));
    }
}
