package cn.cheers.x.module.dynamicbusiness.framework.tree.core;

public interface TreeContract<I> {

    I getId();

    void setId(I id);

    I getParentId();

    void setParentId(I parentId);

    String getName();

    default void setName(String name) {
    }

    String getTreePath();

    Integer getLevel();

    Integer getSort();

    void setTreePath(String treePath);

    void setLevel(Integer level);

    void setSort(Integer sort);
}
