package cn.cheers.x.module.dynamicbusiness.service.organization;

import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTreeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationTreeNodeRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryCategoryRelationDO;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrganizationTreeComposerTest {

    @Test
    void stationHangsUnderTheLinkedRegionAndStructuralRootIsOmitted() {
        CategoryTreeRespVO workArea = region(3L, "福州作业区", 100205L);
        CategoryTreeRespVO company = region(2L, "福建省管网", 100024L, workArea);
        CategoryTreeRespVO root = folder(1L, "运营区域", company);

        List<OrganizationTreeNodeRespVO> nodes = OrganizationTreeComposer.compose(List.of(root), List.of(
                new OrganizationStationPlacement(20L, "福州末站", 3L, 3L)));

        assertEquals(List.of("福建省管网"), names(nodes));
        assertEquals(List.of("福州末站"), names(nodes.get(0).getChildren().get(0).getChildren()));
        assertEquals("station", nodes.get(0).getChildren().get(0).getChildren().get(0).getKind());
    }

    @Test
    void stationWithoutRegionStaysOutsideTheTree() {
        CategoryTreeRespVO company = region(2L, "福建省管网", 100024L);
        CategoryTreeRespVO root = folder(1L, "运营区域", company);

        List<OrganizationTreeNodeRespVO> nodes = OrganizationTreeComposer.compose(List.of(root), List.of(
                new OrganizationStationPlacement(21L, "金桥厂区", 44L, null)));

        assertEquals(List.of("福建省管网", "金桥厂区"), names(nodes));
    }

    @Test
    void moreThanOneHostIsNotChosen() {
        assertNull(OrganizationTreeComposer.singleRegionHost(new LinkedHashSet<>(List.of(1L, 2L))));
        assertEquals(3L, OrganizationTreeComposer.singleRegionHost(new LinkedHashSet<>(List.of(3L))));
    }

    @Test
    void duplicateHostRowStillCountsAsOne() {
        CategoryCategoryRelationDO first = relation(20L, 3L);
        CategoryCategoryRelationDO duplicate = relation(20L, 3L);
        CategoryCategoryRelationDO other = relation(21L, 4L);
        CategoryCategoryRelationDO secondHost = relation(21L, 5L);

        var single = OrganizationTreeQueryServiceImpl.singleHostByStation(List.of(first, duplicate, other, secondHost));

        assertEquals(3L, single.get(20L));
        assertNull(single.get(21L));
    }

    private static List<String> names(List<OrganizationTreeNodeRespVO> nodes) {
        return nodes.stream().map(OrganizationTreeNodeRespVO::getName).toList();
    }

    private static CategoryTreeRespVO folder(Long id, String name, CategoryTreeRespVO... children) {
        CategoryTreeRespVO node = region(id, name, null, children);
        node.setIsEntity(false);
        node.setEntityId(null);
        return node;
    }

    private static CategoryTreeRespVO region(Long id, String name, Long entityId, CategoryTreeRespVO... children) {
        CategoryTreeRespVO node = new CategoryTreeRespVO();
        node.setId(id);
        node.setName(name);
        node.setIsEntity(entityId != null);
        node.setEntityId(entityId);
        node.setChildren(List.of(children));
        return node;
    }

    private static CategoryCategoryRelationDO relation(Long memberId, Long hostId) {
        return CategoryCategoryRelationDO.builder()
                .memberCategoryId(memberId)
                .hostCategoryId(hostId)
                .build();
    }
}
