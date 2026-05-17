package cn.iocoder.yudao.module.scene.platform.service.component.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.LightComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.component.LightComponentMapper;
import cn.iocoder.yudao.module.scene.platform.service.component.LightComponentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class LightComponentServiceImpl implements LightComponentService {

    @Resource
    private LightComponentMapper lightComponentMapper;

    @Override
    public List<LightComponentDO> getLightComponentList() {
        return lightComponentMapper.selectList();
    }

    @Override
    public LightComponentDO getLightComponent(Long id) {
        LightComponentDO item = lightComponentMapper.selectById(id);
        if (item == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Light 组件不存在");
        }
        return item;
    }

    @Override
    public void updateLightComponent(Long id, LightComponentDO lightComponentDO) {
        LightComponentDO db = getLightComponent(id);
        BeanUtils.copyProperties(lightComponentDO, db);
        lightComponentMapper.updateById(db);
    }

    @Override
    public void updateLightComponentDefaults(Long id, LightComponentDO lightComponentDO) {
        LightComponentDO db = getLightComponent(id);
        db.setIntensity(lightComponentDO.getIntensity());
        db.setLightColor(lightComponentDO.getLightColor());
        db.setCastShadows(lightComponentDO.getCastShadows());
        db.setMobility(lightComponentDO.getMobility());
        db.setMetadataJson(lightComponentDO.getMetadataJson());
        lightComponentMapper.updateById(db);
    }

    @Override
    public void updateLightComponentSchema(Long id, LightComponentDO lightComponentDO) {
        LightComponentDO db = getLightComponent(id);
        db.setMetadataJson(lightComponentDO.getMetadataJson());
        lightComponentMapper.updateById(db);
    }
}
