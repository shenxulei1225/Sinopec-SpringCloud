package cn.iocoder.yudao.framework.category.core;

public interface CategoryContract<I> {

    I getId();
    void setId(I id);

    I getParentId();
    void setParentId(I parentId);

    String getName();
    default void setName(String name) {
    }

    String getCode();
    default void setCode(String code) {
    }

    String getCategoryTypeCode();
    default void setCategoryTypeCode(String categoryTypeCode) {
    }

    String getTreePath();

    Integer getLevel();

    Integer getSort();

    Integer getStatus();
    default void setStatus(Integer status) {
    }

    void setTreePath(String treePath);

    void setLevel(Integer level);

    void setSort(Integer sort);
}
