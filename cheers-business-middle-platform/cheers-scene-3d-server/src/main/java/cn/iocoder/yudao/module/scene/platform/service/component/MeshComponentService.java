package cn.iocoder.yudao.module.scene.platform.service.component;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.MeshComponentDO;

import java.util.List;

public interface MeshComponentService {

    List<MeshComponentDO> getMeshComponentList();

    MeshComponentDO getMeshComponent(Long id);

    void updateMeshComponent(Long id, MeshComponentDO meshComponentDO);

    void updateMeshComponentDefaults(Long id, MeshComponentDO meshComponentDO);

    void updateMeshComponentSchema(Long id, MeshComponentDO meshComponentDO);
}
