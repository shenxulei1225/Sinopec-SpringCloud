package cn.iocoder.yudao.framework.trees.core;

/**
 * 树形实体接口
 * 实现此接口的实体类将获得树形结构支持
 *
 * @param <ID> ID类型
 */
public interface TreeEntity<ID> {
    
    /**
     * 获取节点ID
     */
    ID getId();

    /**
     * 获取父节点ID
     */
    ID getParentId();

    /**
     * 设置父节点ID
     */
    TreeEntity<ID> setParentId(ID parentId);

    /**
     * 获取树路径
     */
    String getTreePath();

    /**
     * 设置树路径
     */
    TreeEntity<ID> setTreePath(String treePath);

    /**
     * 获取层级深度
     */
    Integer getLevel();

    /**
     * 设置层级深度
     */
    TreeEntity<ID> setLevel(Integer level);

    /**
     * 获取排序号
     */
    Integer getSort();

    /**
     * 设置排序号
     */
    TreeEntity<ID> setSort(Integer sort);

    /**
     * 获取节点名称（用于显示）
     */
    String getName();

    /**
     * 获取节点编码
     */
    String getCode();

    /**
     * 获取节点状态
     */
    Integer getStatus();

    /**
     * 是否只读
     */
    Boolean getReadonly();
}



