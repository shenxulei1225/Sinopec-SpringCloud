package cn.iocoder.yudao.framework.trees.core;

import java.util.List;

/**
 * 树形VO接口
 * 实现此接口的VO类将支持树形结构展示
 *
 * @param <ID> ID类型
 * @param <T> 当前类型
 */
public interface TreeVO<ID, T extends TreeVO<ID, T>> {
    
    /**
     * 获取节点ID
     */
    ID getId();

    /**
     * 获取父节点ID
     */
    ID getParentId();

    /**
     * 获取节点名称
     */
    String getName();

    /**
     * 获取节点编码
     */
    String getCode();

    /**
     * 获取树路径
     */
    String getTreePath();

    /**
     * 获取层级深度
     */
    Integer getLevel();

    /**
     * 获取排序号
     */
    Integer getSort();

    /**
     * 获取子节点列表
     */
    List<T> getChildren();

    /**
     * 设置子节点列表
     */
    T setChildren(List<T> children);
}



