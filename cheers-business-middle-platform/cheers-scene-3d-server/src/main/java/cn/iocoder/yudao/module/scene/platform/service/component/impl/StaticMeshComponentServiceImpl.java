package cn.iocoder.yudao.module.scene.platform.service.component.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.StaticMeshComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.component.StaticMeshComponentMapper;
import cn.iocoder.yudao.module.scene.platform.service.component.StaticMeshComponentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class StaticMeshComponentServiceImpl implements StaticMeshComponentService {

    @Resource
    private StaticMeshComponentMapper staticMeshComponentMapper;

    @Override
    public List<StaticMeshComponentDO> getStaticMeshComponentList() {
        return staticMeshComponentMapper.selectList();
    }

    @Override
    public StaticMeshComponentDO getStaticMeshComponent(Long id) {
        StaticMeshComponentDO item = staticMeshComponentMapper.selectById(id);
        if (item == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Static Mesh 组件不存在");
        }
        return item;
    }

    @Override
    public void updateStaticMeshComponent(Long id, StaticMeshComponentDO staticMeshComponentDO) {
        StaticMeshComponentDO db = getStaticMeshComponent(id);
        BeanUtils.copyProperties(staticMeshComponentDO, db);
        staticMeshComponentMapper.updateById(db);
    }

    @Override
    public void updateStaticMeshComponentDefaults(Long id, StaticMeshComponentDO staticMeshComponentDO) {
        StaticMeshComponentDO db = getStaticMeshComponent(id);
        db.setMeshCode(staticMeshComponentDO.getMeshCode());
        db.setMaterialsJson(staticMeshComponentDO.getMaterialsJson());
        db.setCastShadow(staticMeshComponentDO.getCastShadow());
        db.setReceiveShadow(staticMeshComponentDO.getReceiveShadow());
        db.setGenerateOverlapEvents(staticMeshComponentDO.getGenerateOverlapEvents());
        db.setMobility(staticMeshComponentDO.getMobility());
        db.setMetadataJson(staticMeshComponentDO.getMetadataJson());
        staticMeshComponentMapper.updateById(db);
    }

    @Override
    public void updateStaticMeshComponentSchema(Long id, StaticMeshComponentDO staticMeshComponentDO) {
        StaticMeshComponentDO db = getStaticMeshComponent(id);
        db.setMetadataJson(staticMeshComponentDO.getMetadataJson());
        staticMeshComponentMapper.updateById(db);
    }
}
