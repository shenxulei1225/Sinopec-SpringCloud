package cn.cheers.x.scene.platform.service.component;

import cn.cheers.x.scene.platform.dal.dataobject.component.SplineMeshComponentDO;

import java.util.List;

public interface SplineMeshComponentService {

    List<SplineMeshComponentDO> getSplineMeshComponentList();

    SplineMeshComponentDO getSplineMeshComponent(Long id);

    void updateSplineMeshComponent(Long id, SplineMeshComponentDO splineMeshComponentDO);

    void updateSplineMeshComponentDefaults(Long id, SplineMeshComponentDO splineMeshComponentDO);

    void updateSplineMeshComponentSchema(Long id, SplineMeshComponentDO splineMeshComponentDO);
}
