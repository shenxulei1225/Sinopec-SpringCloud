package cn.iocoder.yudao.module.scene.platform.service.component.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.CameraComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.component.CameraComponentMapper;
import cn.iocoder.yudao.module.scene.platform.service.component.CameraComponentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class CameraComponentServiceImpl implements CameraComponentService {

    @Resource
    private CameraComponentMapper cameraComponentMapper;

    @Override
    public List<CameraComponentDO> getCameraComponentList() {
        return cameraComponentMapper.selectList();
    }

    @Override
    public CameraComponentDO getCameraComponent(Long id) {
        CameraComponentDO item = cameraComponentMapper.selectById(id);
        if (item == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Camera 组件不存在");
        }
        return item;
    }

    @Override
    public void updateCameraComponent(Long id, CameraComponentDO cameraComponentDO) {
        CameraComponentDO db = getCameraComponent(id);
        BeanUtils.copyProperties(cameraComponentDO, db);
        cameraComponentMapper.updateById(db);
    }

    @Override
    public void updateCameraComponentDefaults(Long id, CameraComponentDO cameraComponentDO) {
        CameraComponentDO db = getCameraComponent(id);
        db.setFieldOfView(cameraComponentDO.getFieldOfView());
        db.setAspectRatio(cameraComponentDO.getAspectRatio());
        db.setNearClipPlane(cameraComponentDO.getNearClipPlane());
        db.setFarClipPlane(cameraComponentDO.getFarClipPlane());
        db.setConstrainAspectRatio(cameraComponentDO.getConstrainAspectRatio());
        db.setAutoActivate(cameraComponentDO.getAutoActivate());
        db.setMetadataJson(cameraComponentDO.getMetadataJson());
        cameraComponentMapper.updateById(db);
    }

    @Override
    public void updateCameraComponentSchema(Long id, CameraComponentDO cameraComponentDO) {
        CameraComponentDO db = getCameraComponent(id);
        db.setMetadataJson(cameraComponentDO.getMetadataJson());
        cameraComponentMapper.updateById(db);
    }
}
