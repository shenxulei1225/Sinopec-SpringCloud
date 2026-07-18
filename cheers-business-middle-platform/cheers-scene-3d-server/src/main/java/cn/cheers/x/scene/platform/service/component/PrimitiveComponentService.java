package cn.cheers.x.scene.platform.service.component;

import cn.cheers.x.scene.platform.dal.dataobject.component.PrimitiveComponentDO;

import java.util.List;

public interface PrimitiveComponentService {

    List<PrimitiveComponentDO> getPrimitiveComponentList();

    PrimitiveComponentDO getPrimitiveComponent(Long id);

    void updatePrimitiveComponent(Long id, PrimitiveComponentDO primitiveComponentDO);

    void updatePrimitiveComponentDefaults(Long id, PrimitiveComponentDO primitiveComponentDO);

    void updatePrimitiveComponentSchema(Long id, PrimitiveComponentDO primitiveComponentDO);
}
