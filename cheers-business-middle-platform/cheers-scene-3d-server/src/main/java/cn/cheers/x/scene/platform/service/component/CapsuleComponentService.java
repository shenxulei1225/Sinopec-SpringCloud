package cn.cheers.x.scene.platform.service.component;

import cn.cheers.x.scene.platform.dal.dataobject.component.CapsuleComponentDO;

import java.util.List;

public interface CapsuleComponentService {

    List<CapsuleComponentDO> getCapsuleComponentList();

    CapsuleComponentDO getCapsuleComponent(Long id);

    void updateCapsuleComponent(Long id, CapsuleComponentDO capsuleComponentDO);

    void updateCapsuleComponentDefaults(Long id, CapsuleComponentDO capsuleComponentDO);

    void updateCapsuleComponentSchema(Long id, CapsuleComponentDO capsuleComponentDO);
}
