package cn.cheers.x.scene.platform.service.component;

import cn.cheers.x.scene.platform.dal.dataobject.component.ChildActorComponentDO;

import java.util.List;

public interface ChildActorComponentService {

    List<ChildActorComponentDO> getChildActorComponentList();

    ChildActorComponentDO getChildActorComponent(Long id);

    void updateChildActorComponent(Long id, ChildActorComponentDO childActorComponentDO);

    void updateChildActorComponentDefaults(Long id, ChildActorComponentDO childActorComponentDO);

    void updateChildActorComponentSchema(Long id, ChildActorComponentDO childActorComponentDO);
}
