package cn.cheers.x.module.dynamicbusiness.framework.category.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryUtilsTest {

    @Test
    void buildIdTreePath_withParentPrefix() {
        assertEquals("/1/281/282/283/", CategoryUtils.buildIdTreePath("/1/281/282/", 283L));
        assertEquals("/283/", CategoryUtils.buildIdTreePath(null, 283L));
        assertEquals("/283/", CategoryUtils.buildIdTreePath("", 283L));
    }

    @Test
    void treePathMatchesNode_requiresTrailingIdSegment() {
        assertTrue(CategoryUtils.treePathMatchesNode("/1/281/282/283/", 283L));
        assertTrue(CategoryUtils.treePathMatchesNode("/283/", 283L));
        assertFalse(CategoryUtils.treePathMatchesNode("/1/281/282/", 283L));
        assertFalse(CategoryUtils.treePathMatchesNode("283", 283L));
    }
}
