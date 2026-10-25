package cn.cheers.x.module.dynamicbusiness.service.organization;

import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTreeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationTreeNodeRespVO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * 把场站挂到运营区域树上。只生成这一次查询的结果，不写分类。
 * 关联到的区域节点存在：场站成为该节点的子节点。
 * 没有唯一关联，或节点不在这棵树上：场站单独列在树外。
 */
public final class OrganizationTreeComposer {

    public static final String KIND_REGION = "region";
    public static final String KIND_STATION = "station";

    private OrganizationTreeComposer() {
    }

    /**
     * 一座场站只认一个宿主。零个或多个宿主都返回空，调用方不得再挑一个。
     */
    public static Long singleRegionHost(LinkedHashSet<Long> hostCategoryIds) {
        if (hostCategoryIds == null || hostCategoryIds.size() != 1) {
            return null;
        }
        Long hostId = hostCategoryIds.iterator().next();
        if (hostId == null || hostId <= 0) {
            return null;
        }
        return hostId;
    }

    public static List<OrganizationTreeNodeRespVO> compose(List<CategoryTreeRespVO> regionTree,
                                                           List<OrganizationStationPlacement> stations) {
        List<CategoryTreeRespVO> source = regionTree == null ? List.of() : regionTree;
        List<OrganizationTreeNodeRespVO> cloned = new ArrayList<>();
        Map<Long, OrganizationTreeNodeRespVO> byId = new LinkedHashMap<>();
        for (CategoryTreeRespVO node : source) {
            if (node != null) {
                cloned.add(cloneRegion(node, byId));
            }
        }
        List<OrganizationTreeNodeRespVO> unmatched = new ArrayList<>();
        for (OrganizationStationPlacement station : stations == null ? List.<OrganizationStationPlacement>of() : stations) {
            OrganizationTreeNodeRespVO stationNode = stationNode(station);
            OrganizationTreeNodeRespVO parent = station.regionCategoryId() == null
                    ? null
                    : byId.get(station.regionCategoryId());
            if (parent == null) {
                unmatched.add(stationNode);
                continue;
            }
            parent.getChildren().add(stationNode);
        }
        List<OrganizationTreeNodeRespVO> roots = unwrapStructuralRoot(source, cloned);
        roots.addAll(unmatched);
        return roots;
    }

    private static OrganizationTreeNodeRespVO cloneRegion(CategoryTreeRespVO source,
                                                          Map<Long, OrganizationTreeNodeRespVO> byId) {
        OrganizationTreeNodeRespVO node = new OrganizationTreeNodeRespVO();
        node.setId(source.getId());
        node.setName(source.getName());
        node.setKind(KIND_REGION);
        node.setEntityId(source.getEntityId());
        List<OrganizationTreeNodeRespVO> children = new ArrayList<>();
        if (source.getChildren() != null) {
            for (CategoryTreeRespVO child : source.getChildren()) {
                if (child != null) {
                    children.add(cloneRegion(child, byId));
                }
            }
        }
        node.setChildren(children);
        if (source.getId() != null) {
            byId.put(source.getId(), node);
        }
        return node;
    }

    private static OrganizationTreeNodeRespVO stationNode(OrganizationStationPlacement station) {
        OrganizationTreeNodeRespVO node = new OrganizationTreeNodeRespVO();
        node.setId(station.categoryId());
        node.setName(station.name());
        node.setKind(KIND_STATION);
        node.setEntityId(station.entityId());
        node.setChildren(List.of());
        return node;
    }

    private static List<OrganizationTreeNodeRespVO> unwrapStructuralRoot(List<CategoryTreeRespVO> source,
                                                                         List<OrganizationTreeNodeRespVO> cloned) {
        if (source.size() == 1 && cloned.size() == 1 && !Boolean.TRUE.equals(source.get(0).getIsEntity())) {
            List<OrganizationTreeNodeRespVO> children = cloned.get(0).getChildren();
            return children == null ? new ArrayList<>() : new ArrayList<>(children);
        }
        return new ArrayList<>(cloned);
    }
}
