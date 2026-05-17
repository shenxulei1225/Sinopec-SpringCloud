package cn.iocoder.yudao.module.scene.platform.service.component;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.SphereComponentDO;

import java.util.List;

public interface SphereComponentService {

    List<SphereComponentDO> getSphereComponentList();

    SphereComponentDO getSphereComponent(Long id);

    void updateSphereComponent(Long id, SphereComponentDO sphereComponentDO);

    void updateSphereComponentDefaults(Long id, SphereComponentDO sphereComponentDO);

    void updateSphereComponentSchema(Long id, SphereComponentDO sphereComponentDO);
}
