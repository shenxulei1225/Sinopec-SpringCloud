package cn.cheers.x.module.dynamicbusiness.service.organization;

import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationTreeNodeRespVO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrganizationTreeComposerTest {

    @Test
    void organizationsNestByParent() {
        List<OrganizationTreeNodeRespVO> nodes = OrganizationTreeComposer.compose(List.of(
                new OrganizationNodeRow(68L, "国家管网集团", 0L, 2970L),
                new OrganizationNodeRow(82L, "福建省管网", 68L, 2971L),
                new OrganizationNodeRow(86L, "厦门作业区", 82L, 2973L)));

        assertEquals(List.of("国家管网集团"), names(nodes));
        OrganizationTreeNodeRespVO fujian = nodes.get(0).getChildren().get(0);
        assertEquals("organization:82", fujian.getKey());
        assertEquals(2971L, fujian.getModelId());
        assertEquals(List.of("厦门作业区"), names(fujian.getChildren()));
        assertEquals(List.of(), fujian.getChildren().get(0).getChildren());
    }

    @Test
    void missingParentBecomesRoot() {
        List<OrganizationTreeNodeRespVO> nodes = OrganizationTreeComposer.compose(List.of(
                new OrganizationNodeRow(68L, "国家管网集团", null, 2970L),
                new OrganizationNodeRow(95L, "金桥作业区", 999L, 2973L)));

        assertEquals(List.of("国家管网集团", "金桥作业区"), names(nodes));
    }

    private static List<String> names(List<OrganizationTreeNodeRespVO> nodes) {
        return nodes.stream().map(OrganizationTreeNodeRespVO::getName).toList();
    }
}
