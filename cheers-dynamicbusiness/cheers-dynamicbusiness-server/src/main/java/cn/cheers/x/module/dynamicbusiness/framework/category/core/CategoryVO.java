package cn.cheers.x.module.dynamicbusiness.framework.category.core;

import java.util.List;

public interface CategoryVO<I, T extends CategoryVO<I, T>> extends CategoryContract<I> {

    List<T> getChildren();

    void setChildren(List<T> children);
}
