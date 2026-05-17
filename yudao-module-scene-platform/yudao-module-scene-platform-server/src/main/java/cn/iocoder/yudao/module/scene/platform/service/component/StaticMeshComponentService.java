package cn.iocoder.yudao.module.scene.platform.service.component;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.StaticMeshComponentDO;

import java.util.List;

public interface StaticMeshComponentService {

    List<StaticMeshComponentDO> getStaticMeshComponentList();

    StaticMeshComponentDO getStaticMeshComponent(Long id);

    void updateStaticMeshComponent(Long id, StaticMeshComponentDO staticMeshComponentDO);

    void updateStaticMeshComponentDefaults(Long id, StaticMeshComponentDO staticMeshComponentDO);

    void updateStaticMeshComponentSchema(Long id, StaticMeshComponentDO staticMeshComponentDO);
}
