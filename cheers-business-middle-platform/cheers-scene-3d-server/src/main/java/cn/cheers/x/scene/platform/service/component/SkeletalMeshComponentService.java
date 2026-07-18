package cn.cheers.x.scene.platform.service.component;

import cn.cheers.x.scene.platform.dal.dataobject.component.SkeletalMeshComponentDO;

import java.util.List;

public interface SkeletalMeshComponentService {

    List<SkeletalMeshComponentDO> getSkeletalMeshComponentList();

    SkeletalMeshComponentDO getSkeletalMeshComponent(Long id);

    void updateSkeletalMeshComponent(Long id, SkeletalMeshComponentDO skeletalMeshComponentDO);

    void updateSkeletalMeshComponentDefaults(Long id, SkeletalMeshComponentDO skeletalMeshComponentDO);

    void updateSkeletalMeshComponentSchema(Long id, SkeletalMeshComponentDO skeletalMeshComponentDO);
}
