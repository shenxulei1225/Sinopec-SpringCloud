package cn.cheers.x.scene.platform.service.component.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.scene.platform.dal.dataobject.component.SphereComponentDO;
import cn.cheers.x.scene.platform.dal.mysql.component.SphereComponentMapper;
import cn.cheers.x.scene.platform.service.component.SphereComponentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class SphereComponentServiceImpl implements SphereComponentService {

    @Resource
    private SphereComponentMapper sphereComponentMapper;

    @Override
    public List<SphereComponentDO> getSphereComponentList() {
        return sphereComponentMapper.selectList();
    }

    @Override
    public SphereComponentDO getSphereComponent(Long id) {
        SphereComponentDO item = sphereComponentMapper.selectById(id);
        if (item == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Sphere 组件不存在");
        }
        return item;
    }

    @Override
    public void updateSphereComponent(Long id, SphereComponentDO sphereComponentDO) {
        SphereComponentDO db = getSphereComponent(id);
        BeanUtils.copyProperties(sphereComponentDO, db);
        sphereComponentMapper.updateById(db);
    }

    @Override
    public void updateSphereComponentDefaults(Long id, SphereComponentDO sphereComponentDO) {
        SphereComponentDO db = getSphereComponent(id);
        db.setSphereRadius(sphereComponentDO.getSphereRadius());
        db.setCollisionEnabled(sphereComponentDO.getCollisionEnabled());
        db.setGenerateOverlapEvents(sphereComponentDO.getGenerateOverlapEvents());
        db.setMetadataJson(sphereComponentDO.getMetadataJson());
        sphereComponentMapper.updateById(db);
    }

    @Override
    public void updateSphereComponentSchema(Long id, SphereComponentDO sphereComponentDO) {
        SphereComponentDO db = getSphereComponent(id);
        db.setMetadataJson(sphereComponentDO.getMetadataJson());
        sphereComponentMapper.updateById(db);
    }
}
