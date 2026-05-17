package cn.iocoder.yudao.framework.trees.utils;

import cn.iocoder.yudao.framework.trees.core.TreeEntity;
import cn.iocoder.yudao.framework.trees.core.TreeVO;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 * 树形结构工具类
 * 提供树形结构的通用操作方法
 */
public class TreeUtils {

    /**
     * 构建树形结构
     *
     * @param list 平铺的节点列表
     * @param rootId 根节点ID
     * @param converter 转换器
     * @param <T> 实体类型
     * @param <V> VO类型
     * @return 树形结构
     */
    public static <T extends TreeEntity<Long>, V extends TreeVO<Long, V>> List<V> buildTree(
            List<T> list, Long rootId, TreeConverter<T, V> converter) {
        List<V> voList = list.stream()
                .map(converter::convert)
                .collect(Collectors.toList());
        return buildTreeRecursive(voList, rootId);
    }

    /**
     * 递归构建树形结构
     *
     * @param all 所有节点
     * @param parentId 父节点ID
     * @param <V> VO类型
     * @return 子节点列表
     */
    public static <V extends TreeVO<Long, V>> List<V> buildTreeRecursive(List<V> all, Long parentId) {
        List<V> children = new ArrayList<>();
        for (V vo : all) {
            if (Objects.equals(vo.getParentId(), parentId)) {
                vo.setChildren(buildTreeRecursive(all, vo.getId()));
                children.add(vo);
            }
        }
        return children;
    }

    /**
     * 针对动态业务的便捷方法 - 直接构建实体树形结构
     *
     * @param list 平铺的实体列表
     * @param rootId 根节点ID
     * @param <T> 实体类型
     * @return 树形结构
     */
    public static <T extends TreeEntity<Long>> List<T> buildDynamicTree(List<T> list, Long rootId) {
        return buildTreeRecursiveForEntity(list, rootId);
    }

    /**
     * 递归构建实体树形结构
     *
     * @param all 所有实体节点
     * @param parentId 父节点ID
     * @param <T> 实体类型
     * @return 子节点列表
     */
    public static <T extends TreeEntity<Long>> List<T> buildTreeRecursiveForEntity(List<T> all, Long parentId) {
        List<T> children = new ArrayList<>();
        for (T entity : all) {
            if (Objects.equals(entity.getParentId(), parentId)) {
                // 注意：这里需要实体类有setChildren方法，或者使用其他方式构建树形结构
                children.add(entity);
            }
        }
        return children;
    }

    /**
     * 支持动态字段的树形构建
     *
     * @param list 平铺的节点列表
     * @param rootId 根节点ID
     * @param fields 需要包含的字段列表
     * @param <T> 实体类型
     * @return 包含动态字段的树形结构
     */
    public static <T extends TreeEntity<Long>> List<Map<String, Object>> buildDynamicTreeWithFields(
            List<T> list, Long rootId, List<String> fields) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (T entity : list) {
            if (Objects.equals(entity.getParentId(), rootId)) {
                Map<String, Object> node = new HashMap<>();
                
                // 添加基础字段
                node.put("id", entity.getId());
                node.put("parentId", entity.getParentId());
                node.put("treePath", entity.getTreePath());
                node.put("level", entity.getLevel());
                node.put("sort", entity.getSort());
                node.put("name", entity.getName());
                node.put("code", entity.getCode());
                node.put("status", entity.getStatus());
                node.put("readonly", entity.getReadonly());
                
                // 添加动态字段（如果实体支持）
                if (entity instanceof DynamicTreeEntity) {
                    DynamicTreeEntity dynamicEntity = (DynamicTreeEntity) entity;
                    Map<String, Object> dynamicFields = dynamicEntity.getDynamicFields();
                    if (dynamicFields != null) {
                        for (String field : fields) {
                            if (dynamicFields.containsKey(field)) {
                                node.put(field, dynamicFields.get(field));
                            }
                        }
                    }
                }
                
                // 递归构建子节点
                List<Map<String, Object>> children = buildDynamicTreeWithFields(list, entity.getId(), fields);
                if (!children.isEmpty()) {
                    node.put("children", children);
                }
                
                result.add(node);
            }
        }
        
        return result;
    }

    /**
     * 获取节点路径（从根节点到当前节点）
     *
     * @param list 所有节点
     * @param nodeId 目标节点ID
     * @param converter 转换器
     * @param <T> 实体类型
     * @param <V> VO类型
     * @return 节点路径
     */
    public static <T extends TreeEntity<Long>, V extends TreeVO<Long, V>> List<V> getNodePath(
            List<T> list, Long nodeId, TreeConverter<T, V> converter) {
        Map<Long, V> nodeMap = list.stream()
                .map(converter::convert)
                .collect(Collectors.toMap(V::getId, v -> v));
        
        List<V> path = new ArrayList<>();
        V current = nodeMap.get(nodeId);
        while (current != null) {
            path.add(0, current);
            current = nodeMap.get(current.getParentId());
        }
        return path;
    }

    /**
     * 获取指定节点的所有子节点（包括子节点的子节点）
     *
     * @param list 所有节点
     * @param parentId 父节点ID
     * @param converter 转换器
     * @param <T> 实体类型
     * @param <V> VO类型
     * @return 所有子节点
     */
    public static <T extends TreeEntity<Long>, V extends TreeVO<Long, V>> List<V> getAllChildren(
            List<T> list, Long parentId, TreeConverter<T, V> converter) {
        Map<Long, V> nodeMap = list.stream()
                .map(converter::convert)
                .collect(Collectors.toMap(V::getId, v -> v));
        
        List<V> children = new ArrayList<>();
        getAllChildrenRecursive(nodeMap, parentId, children);
        return children;
    }

    /**
     * 递归获取所有子节点
     */
    private static <V extends TreeVO<Long, V>> void getAllChildrenRecursive(
            Map<Long, V> nodeMap, Long parentId, List<V> children) {
        for (V node : nodeMap.values()) {
            if (Objects.equals(node.getParentId(), parentId)) {
                children.add(node);
                getAllChildrenRecursive(nodeMap, node.getId(), children);
            }
        }
    }

    /**
     * 检查是否为子节点
     *
     * @param list 所有节点
     * @param parentId 父节点ID
     * @param childId 子节点ID
     * @param <T> 实体类型
     * @return 是否为子节点
     */
    public static <T extends TreeEntity<Long>> boolean isChildOf(List<T> list, Long parentId, Long childId) {
        Map<Long, T> nodeMap = list.stream()
                .collect(Collectors.toMap(T::getId, t -> t));
        
        T child = nodeMap.get(childId);
        if (child == null) {
            return false;
        }
        
        String childPath = child.getTreePath();
        T parent = nodeMap.get(parentId);
        if (parent == null) {
            return false;
        }
        
        String parentPath = parent.getTreePath();
        return childPath.startsWith(parentPath + "/" + parentId);
    }

    /**
     * 更新树路径
     *
     * @param entity 实体对象
     * @param parentEntity 父实体对象
     * @param <T> 实体类型
     */
    public static <T extends TreeEntity<Long>> void updateTreePath(T entity, T parentEntity) {
        if (parentEntity != null) {
            String parentPath = parentEntity.getTreePath();
            entity.setTreePath(parentPath + "/" + entity.getId());
            entity.setLevel(parentEntity.getLevel() + 1);
        } else {
            entity.setTreePath("/" + entity.getId());
            entity.setLevel(0);
        }
    }

    /**
     * 树形转换器接口
     */
    @FunctionalInterface
    public interface TreeConverter<T, V> {
        V convert(T entity);
    }

    /**
     * 动态树形实体接口
     * 用于支持动态字段的实体
     */
    public interface DynamicTreeEntity extends TreeEntity<Long> {
        /**
         * 获取动态字段
         *
         * @return 动态字段映射
         */
        Map<String, Object> getDynamicFields();
    }
}



