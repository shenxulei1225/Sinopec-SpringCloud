package cn.cheers.x.module.dynamicbusiness.framework.tree.utils;

import cn.cheers.x.module.dynamicbusiness.framework.tree.core.TreeContract;
import cn.cheers.x.module.dynamicbusiness.framework.tree.core.TreeVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class TreeUtils {

    private TreeUtils() {
    }

    public static <T extends TreeContract<Long>> List<T> buildTree(List<T> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, T> nodeMap = nodes.stream()
                .filter(item -> item.getId() != null)
                .collect(Collectors.toMap(TreeContract::getId, item -> item));

        List<T> roots = new ArrayList<>();
        for (T node : nodes) {
            Long parentId = node.getParentId();
            if (parentId == null || parentId == 0L || !nodeMap.containsKey(parentId)) {
                roots.add(node);
                continue;
            }
            T parent = nodeMap.get(parentId);
            if (parent instanceof TreeVO<?, ?> parentVO) {
                List<T> children = (List<T>) parentVO.getChildren();
                if (children == null) {
                    children = new ArrayList<>();
                    TreeVO rawVO = (TreeVO) parentVO;
                    rawVO.setChildren(children);
                }
                children.add(node);
            }
        }
        Comparator<T> comparator = Comparator.comparing(TreeContract::getSort, Comparator.nullsLast(Integer::compareTo));
        roots.sort(comparator);
        roots.forEach(root -> sortChildren(root, comparator));
        return roots;
    }

    private static <T extends TreeContract<Long>> void sortChildren(T node, Comparator<? super T> comparator) {
        if (!(node instanceof TreeVO<?, ?> vo)) {
            return;
        }
        List<T> children = (List<T>) vo.getChildren();
        if (children == null || children.isEmpty()) {
            return;
        }
        children.sort(comparator);
        children.forEach(child -> sortChildren(child, comparator));
    }

    public static <T extends TreeVO<Long, T>> List<T> buildTreeByVO(List<T> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, T> nodeMap = nodes.stream()
                .filter(item -> item.getId() != null)
                .collect(Collectors.toMap(TreeVO::getId, item -> item));

        List<T> roots = new ArrayList<>();
        for (T node : nodes) {
            Long parentId = node.getParentId();
            if (parentId == null || parentId == 0L || !nodeMap.containsKey(parentId)) {
                roots.add(node);
                continue;
            }
            T parent = nodeMap.get(parentId);
            List<T> children = parent.getChildren();
            if (children == null) {
                children = new ArrayList<>();
                parent.setChildren(children);
            }
            children.add(node);
        }
        Comparator<T> comparator = Comparator.comparing(TreeVO::getSort, Comparator.nullsLast(Integer::compareTo));
        roots.sort(comparator);
        roots.forEach(root -> sortChildrenByVO(root, comparator));
        return roots;
    }

    private static <T extends TreeVO<Long, T>> void sortChildrenByVO(T node, Comparator<? super T> comparator) {
        List<T> children = node.getChildren();
        if (children == null || children.isEmpty()) {
            return;
        }
        children.sort(comparator);
        children.forEach(child -> sortChildrenByVO(child, comparator));
    }

    public static String generateTreePath(String parentPath, String name) {
        if (parentPath == null || parentPath.isEmpty()) {
            return name;
        }
        return parentPath + "/" + name;
    }
}
