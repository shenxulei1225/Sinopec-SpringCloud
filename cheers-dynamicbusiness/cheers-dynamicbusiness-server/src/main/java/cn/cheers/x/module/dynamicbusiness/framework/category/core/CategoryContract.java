package cn.cheers.x.module.dynamicbusiness.framework.category.core;

public interface CategoryContract<I> {

    I getId();
    void setId(I id);

    I getParentId();
    void setParentId(I parentId);

    /** 父分类编码（迁移用；默认实现不参与持久化） */
    default String getParentCode() {
        return null;
    }

    default void setParentCode(String parentCode) {
    }

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
