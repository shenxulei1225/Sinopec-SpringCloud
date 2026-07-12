package cn.cheers.x.module.dynamicbusiness.framework.category.utils;

import cn.cheers.x.module.dynamicbusiness.framework.category.core.CategoryContract;
import cn.cheers.x.module.dynamicbusiness.framework.category.core.CategoryVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class CategoryUtils {

    private CategoryUtils() {
    }

    public static <T extends CategoryContract<Long>> List<T> buildTree(List<T> categories) {
        if (categories == null || categories.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, T> nodeMap = categories.stream()
                .filter(item -> item.getId() != null)
                .collect(Collectors.toMap(CategoryContract::getId, item -> item));

        List<T> roots = new ArrayList<>();
        for (T node : categories) {
            Long parentId = node.getParentId();
            if (parentId == null || parentId == 0L || !nodeMap.containsKey(parentId)) {
                roots.add(node);
                continue;
            }
            T parent = nodeMap.get(parentId);
            if (parent instanceof CategoryVO<?, ?> parentVO) {
                List<T> children = (List<T>) parentVO.getChildren();
                if (children == null) {
                    children = new ArrayList<>();
                    CategoryVO rawVO = (CategoryVO) parentVO;
                    rawVO.setChildren(children);
                }
                children.add(node);
            }
        }
        Comparator<T> comparator = Comparator.comparing(CategoryContract::getSort, Comparator.nullsLast(Integer::compareTo));
        roots.sort(comparator);
        roots.forEach(root -> sortChildren(root, comparator));
        return roots;
    }

    private static <T extends CategoryContract<Long>> void sortChildren(T node, Comparator<? super T> comparator) {
        if (!(node instanceof CategoryVO<?, ?> vo)) {
            return;
        }
        List<T> children = (List<T>) vo.getChildren();
        if (children == null || children.isEmpty()) {
            return;
        }
        children.sort(comparator);
        children.forEach(child -> sortChildren(child, comparator));
    }

    public static <T extends CategoryVO<Long, T>> List<T> buildTreeByVO(List<T> categories) {
        if (categories == null || categories.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, T> nodeMap = categories.stream()
                .filter(item -> item.getId() != null)
                .collect(Collectors.toMap(CategoryVO::getId, item -> item));

        List<T> roots = new ArrayList<>();
        for (T node : categories) {
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
        Comparator<T> comparator = Comparator.comparing(CategoryVO::getSort, Comparator.nullsLast(Integer::compareTo));
        roots.sort(comparator);
        roots.forEach(root -> sortChildrenByVO(root, comparator));
        return roots;
    }

    private static <T extends CategoryVO<Long, T>> void sortChildrenByVO(T node, Comparator<? super T> comparator) {
        List<T> children = node.getChildren();
        if (children == null || children.isEmpty()) {
            return;
        }
        children.sort(comparator);
        children.forEach(child -> sortChildrenByVO(child, comparator));
    }

    /**
     * 按分类 id 生成 tree_path（格式 {@code /1/677/282/}），与改名无关。
     */
    public static String buildIdTreePath(String parentPath, Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("categoryId is required");
        }
        String segment = categoryId + "/";
        if (parentPath == null || parentPath.isEmpty()) {
            return "/" + segment;
        }
        return parentPath.endsWith("/") ? parentPath + segment : parentPath + "/" + segment;
    }

    /**
     * 判断 tree_path 是否为按分类 id 拼接的路径，且末段与当前节点 id 一致。
     */
    public static boolean treePathMatchesNode(String treePath, Long categoryId) {
        if (categoryId == null || treePath == null || treePath.isEmpty()) {
            return false;
        }
        if (!treePath.startsWith("/") || !treePath.endsWith("/")) {
            return false;
        }
        return treePath.endsWith("/" + categoryId + "/");
    }

    /**
     * @deprecated 旧实现按名称拼接，改名会导致子树前缀失效；请用 {@link #buildIdTreePath(String, Long)}。
     */
    @Deprecated
    public static String generateTreePath(String parentPath, String name) {
        if (parentPath == null || parentPath.isEmpty()) {
            return name;
        }
        return parentPath + "/" + name;
    }
}
