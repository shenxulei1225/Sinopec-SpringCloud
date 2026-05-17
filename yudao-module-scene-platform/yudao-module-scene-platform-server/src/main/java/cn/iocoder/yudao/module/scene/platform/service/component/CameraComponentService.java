package cn.iocoder.yudao.module.scene.platform.service.component;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.CameraComponentDO;

import java.util.List;

public interface CameraComponentService {

    List<CameraComponentDO> getCameraComponentList();

    CameraComponentDO getCameraComponent(Long id);

    void updateCameraComponent(Long id, CameraComponentDO cameraComponentDO);

    void updateCameraComponentDefaults(Long id, CameraComponentDO cameraComponentDO);

    void updateCameraComponentSchema(Long id, CameraComponentDO cameraComponentDO);
}
