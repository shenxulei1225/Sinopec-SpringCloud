package cn.cheers.x.module.dynamicbusiness.framework.tree.core;

import java.util.List;

public interface TreeVO<I, T extends TreeVO<I, T>> extends TreeContract<I> {

    List<T> getChildren();

    void setChildren(List<T> children);
}
