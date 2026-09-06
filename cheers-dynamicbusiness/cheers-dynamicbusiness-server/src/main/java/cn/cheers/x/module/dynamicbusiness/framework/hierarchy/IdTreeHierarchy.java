package cn.cheers.x.module.dynamicbusiness.framework.hierarchy;

import java.util.Objects;

/**
 * 按节点 id 拼接的树路径算法（实体树与分类树共用）。
 *
 * <p>管什么：路径格式 {@code /1/677/282/}、防挂到子孙、子树前缀替换。</p>
 * <p>不负责：查库、落库、分类↔实体 id 翻译。</p>
 * <p>禁止：业务侧再手写一套 path 拼接 / 环检测，避免与本类分叉导致不同步。</p>
 */
public final class IdTreeHierarchy {

    private IdTreeHierarchy() {
    }

    /**
     * 生成节点 tree_path。
     *
     * @param parentPath 父节点路径；根节点传 null/空
     * @param nodeId     当前节点 id
     */
    public static String buildPath(String parentPath, Long nodeId) {
        if (nodeId == null) {
            throw new IllegalArgumentException("nodeId is required");
        }
        String segment = nodeId + "/";
        if (parentPath == null || parentPath.isEmpty()) {
            return "/" + segment;
        }
        return parentPath.endsWith("/") ? parentPath + segment : parentPath + "/" + segment;
    }

    /**
     * 新父路径是否落在移动节点的子孙上（会成环）。
     *
     * @param movingNodeId     被移动节点 id
     * @param newParentTreePath 新父的 tree_path；根移动时传 null
     */
    public static boolean wouldCreateCycle(Long movingNodeId, String newParentTreePath) {
        if (movingNodeId == null || newParentTreePath == null || newParentTreePath.isBlank()) {
            return false;
        }
        return newParentTreePath.contains("/" + movingNodeId + "/");
    }

    /**
     * 将子节点路径从旧前缀换到新前缀（整棵子树批量重算用）。
     */
    public static String replaceSubtreePrefix(String childPath, String oldPrefix, String newPrefix) {
        if (childPath == null || oldPrefix == null || newPrefix == null) {
            return childPath;
        }
        if (!childPath.startsWith(oldPrefix)) {
            return childPath;
        }
        return newPrefix + childPath.substring(oldPrefix.length());
    }

    /**
     * 计算移动后的 parentId 与 treePath（不做环检测，调用方先 {@link #wouldCreateCycle}）。
     *
     * @param nodeId            被移动节点
     * @param newParentId       新父；null/0 表示根
     * @param newParentTreePath 新父路径；根时忽略
     */
    public static MovePlan planMove(Long nodeId, Long newParentId, String newParentTreePath) {
        Objects.requireNonNull(nodeId, "nodeId");
        Long normalizedParent = normalizeParentId(newParentId);
        if (normalizedParent == null) {
            return new MovePlan(null, buildPath(null, nodeId));
        }
        String parentPath = newParentTreePath;
        if (parentPath == null || parentPath.isBlank()) {
            parentPath = buildPath(null, normalizedParent);
        }
        return new MovePlan(normalizedParent, buildPath(parentPath, nodeId));
    }

    public static Long normalizeParentId(Long parentId) {
        if (parentId == null || parentId == 0L) {
            return null;
        }
        return parentId;
    }

    /** 一次移动算出的父 id 与新路径。 */
    public record MovePlan(Long newParentId, String newTreePath) {
    }
}
