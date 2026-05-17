package cn.iocoder.yudao.framework.trees.service;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.trees.core.TreeEntity;

import org.springframework.transaction.annotation.Transactional;

/**
 * 抽象树形服务基类
 * 提供通用的树形结构操作功能
 *
 * @param <M> Mapper类型
 * @param <T> 实体类型，必须实现TreeEntity接口
 * @param <ID> 主键类型
 * @param <BASE> 基础DO类型
 */
public abstract class AbstractTreeService<M extends BaseMapperX<T>, T extends TreeEntity<ID>, ID extends Serializable, BASE extends BaseDO> {

    /**
     * 获取Mapper实例
     * 子类需要实现此方法返回具体的Mapper
     */
    protected abstract M getMapper();

    /**
     * 获取表名
     * 用于某些特殊查询场景
     */
    protected abstract String getTableName();

    /**
     * 创建树形节点
     */
    @Transactional(rollbackFor = Exception.class)
    public ID createNode(T node) {
        validateNode(node);
        buildTreePath(node);
        getMapper().insert(node);
        return node.getId();
    }

    /**
     * 更新树形节点
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateNode(T node) {
        validateNode(node);
        
        // 检查是否需要重建树路径
        T existingNode = getMapper().selectById(node.getId());
        if (existingNode != null && !Objects.equals(existingNode.getParentId(), node.getParentId())) {
            buildTreePath(node);
            // 如果父节点发生变化，需要更新所有子节点的树路径
            updateChildrenTreePath(node);
        }
        
        getMapper().updateById(node);
    }

    /**
     * 删除树形节点
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteNode(ID id) {
        T node = getMapper().selectById(id);
        if (node == null) {
            throw new RuntimeException("节点不存在");
        }
        
        // 检查是否有子节点
        List<T> children = getChildrenByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("存在子节点，无法删除");
        }
        
        getMapper().deleteById(id);
    }

    /**
     * 获取节点详情
     */
    public T getNode(ID id) {
        return getMapper().selectById(id);
    }

    /**
     * 获取子节点列表
     */
    public List<T> getChildren(ID parentId) {
        return getChildrenByParentId(parentId);
    }

    /**
     * 构建树路径
     */
    protected void buildTreePath(T node) {
        if (node.getParentId() == null || node.getParentId().equals(getDefaultRootId())) {
            node.setTreePath("/");
            node.setLevel(1);
        } else {
            T parent = getMapper().selectById(node.getParentId());
            if (parent == null) {
                throw new RuntimeException("父节点不存在");
            }
            
            String parentPath = parent.getTreePath();
            if (!parentPath.endsWith("/")) {
                parentPath += "/";
            }
            node.setTreePath(parentPath + parent.getId() + "/");
            node.setLevel(parent.getLevel() + 1);
        }
    }

    /**
     * 更新子节点的树路径
     */
    protected void updateChildrenTreePath(T parentNode) {
        List<T> children = getChildrenByParentId(parentNode.getId());
        for (T child : children) {
            buildTreePath(child);
            getMapper().updateById(child);
            // 递归更新子节点的子节点
            updateChildrenTreePath(child);
        }
    }

    /**
     * 验证节点数据
     */
    protected void validateNode(T node) {
        if (node.getName() == null || node.getName().trim().isEmpty()) {
            throw new RuntimeException("节点名称不能为空");
        }
        
        // 验证父节点存在性
        if (node.getParentId() != null && !node.getParentId().equals(getDefaultRootId())) {
            T parent = getMapper().selectById(node.getParentId());
            if (parent == null) {
                throw new RuntimeException("父节点不存在");
            }
        }
    }

    /**
     * 根据父节点ID查询子节点（需要子类实现）
     */
    protected abstract List<T> getChildrenByParentId(ID parentId);

    /**
     * 获取默认根节点ID
     * 子类可以重写此方法定义自己的根节点ID
     */
    @SuppressWarnings("unchecked")
    protected ID getDefaultRootId() {
        return (ID) Long.valueOf(0L);
    }
}


