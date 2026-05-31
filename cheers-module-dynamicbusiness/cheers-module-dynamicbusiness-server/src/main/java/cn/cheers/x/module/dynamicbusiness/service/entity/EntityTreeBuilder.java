package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity 树构建工具。
 *
 * <p>统一处理扁平节点 -> 树结构，并支持按不同来源的排序语义进行递归排序。</p>
 */
public final class EntityTreeBuilder {

    private EntityTreeBuilder() {
    }

    public enum SortMode {
        /** 同父节点局部序号排序（sort），id 兜底 */
        LOCAL_SIBLING_SORT,
        /** 全局ID排序 */
        GLOBAL_ID_SORT
    }

    public static List<EntityRespVO> buildTree(List<EntityRespVO> entities, SortMode sortMode) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        List<EntityRespVO> ordered = new ArrayList<>(entities);
        Map<Long, EntityRespVO> byId = new HashMap<>(ordered.size() * 2);
        for (EntityRespVO e : ordered) {
            if (e.getId() != null) {
                byId.put(e.getId(), e);
            }
            e.setChildren(null);
        }

        List<EntityRespVO> roots = new ArrayList<>();
        for (EntityRespVO e : ordered) {
            Long parentId = e.getParentId();
            if (parentId == null) {
                roots.add(e);
                continue;
            }
            EntityRespVO parent = byId.get(parentId);
            if (parent == null) {
                roots.add(e);
                continue;
            }
            List<EntityRespVO> children = parent.getChildren();
            if (children == null) {
                children = new ArrayList<>();
                parent.setChildren(children);
            }
            children.add(e);
        }

        Comparator<EntityRespVO> comparator = buildComparator(sortMode);
        roots.sort(comparator);
        sortChildrenRecursively(roots, comparator);
        return roots;
    }

    private static Comparator<EntityRespVO> buildComparator(SortMode mode) {
        if (mode == SortMode.LOCAL_SIBLING_SORT) {
            return Comparator
                    .comparing((EntityRespVO v) -> v.getSort() == null ? Integer.MAX_VALUE : v.getSort())
                    .thenComparing(v -> v.getId() == null ? Long.MAX_VALUE : v.getId());
        }
        return Comparator.comparing(v -> v.getId() == null ? Long.MAX_VALUE : v.getId());
    }

    private static void sortChildrenRecursively(List<EntityRespVO> nodes, Comparator<EntityRespVO> cmp) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        for (EntityRespVO n : nodes) {
            if (n.getChildren() != null && !n.getChildren().isEmpty()) {
                n.getChildren().sort(cmp);
                sortChildrenRecursively(n.getChildren(), cmp);
            }
        }
    }
}
