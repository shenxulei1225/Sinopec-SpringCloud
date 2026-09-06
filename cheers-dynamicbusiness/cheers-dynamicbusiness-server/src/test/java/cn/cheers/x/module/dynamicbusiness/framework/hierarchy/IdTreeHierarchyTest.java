package cn.cheers.x.module.dynamicbusiness.framework.hierarchy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdTreeHierarchyTest {

    @Test
    void buildPath_rootAndChild() {
        assertEquals("/10/", IdTreeHierarchy.buildPath(null, 10L));
        assertEquals("/10/20/", IdTreeHierarchy.buildPath("/10/", 20L));
    }

    @Test
    void wouldCreateCycle_detectsDescendantParent() {
        assertTrue(IdTreeHierarchy.wouldCreateCycle(10L, "/1/10/20/"));
        assertFalse(IdTreeHierarchy.wouldCreateCycle(10L, "/1/2/"));
        assertFalse(IdTreeHierarchy.wouldCreateCycle(10L, null));
    }

    @Test
    void replaceSubtreePrefix() {
        assertEquals(
                "/9/10/20/",
                IdTreeHierarchy.replaceSubtreePrefix("/1/10/20/", "/1/10/", "/9/10/"));
    }

    @Test
    void planMove_rootAndUnderParent() {
        var root = IdTreeHierarchy.planMove(5L, null, null);
        assertNull(root.newParentId());
        assertEquals("/5/", root.newTreePath());

        var child = IdTreeHierarchy.planMove(5L, 1L, "/1/");
        assertEquals(1L, child.newParentId());
        assertEquals("/1/5/", child.newTreePath());
    }
}
