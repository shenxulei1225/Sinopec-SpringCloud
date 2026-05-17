package cn.iocoder.yudao.framework.category.core;

import java.util.List;

public interface CategoryVO<ID, T extends CategoryVO<ID, T>> {

    ID getId();

    ID getParentId();

    String getName();

    String getCode();

    String getCategoryTypeCode();

    String getTreePath();

    Integer getLevel();

    Integer getSort();

    Integer getStatus();

    List<T> getChildren();

    T setChildren(List<T> children);
}
