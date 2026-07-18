package cn.iocoder.yudao.module.scene.platform.service.component.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.PrimitiveComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.component.PrimitiveComponentMapper;
import cn.iocoder.yudao.module.scene.platform.service.component.PrimitiveComponentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class PrimitiveComponentServiceImpl implements PrimitiveComponentService {

    @Resource
    private PrimitiveComponentMapper primitiveComponentMapper;

    @Override
    public List<PrimitiveComponentDO> getPrimitiveComponentList() {
        return primitiveComponentMapper.selectList();
    }

    @Override
    public PrimitiveComponentDO getPrimitiveComponent(Long id) {
        PrimitiveComponentDO primitiveComponentDO = primitiveComponentMapper.selectById(id);
        if (primitiveComponentDO == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Primitive 组件不存在");
        }
        return primitiveComponentDO;
    }

    @Override
    public void updatePrimitiveComponent(Long id, PrimitiveComponentDO primitiveComponentDO) {
        PrimitiveComponentDO db = getPrimitiveComponent(id);
        BeanUtils.copyProperties(primitiveComponentDO, db);
        primitiveComponentMapper.updateById(db);
    }

    @Override
    public void updatePrimitiveComponentDefaults(Long id, PrimitiveComponentDO primitiveComponentDO) {
        PrimitiveComponentDO db = getPrimitiveComponent(id);
        db.setCollisionEnabled(primitiveComponentDO.getCollisionEnabled());
        db.setObjectType(primitiveComponentDO.getObjectType());
        db.setGenerateOverlapEvents(primitiveComponentDO.getGenerateOverlapEvents());
        db.setSimulationGeneratesHitEvents(primitiveComponentDO.getSimulationGeneratesHitEvents());
        db.setCanCharacterStepUpOn(primitiveComponentDO.getCanCharacterStepUpOn());
        db.setUseDefaultCollision(primitiveComponentDO.getUseDefaultCollision());
        db.setPhysicsMaterialJson(primitiveComponentDO.getPhysicsMaterialJson());
        db.setBoundsJson(primitiveComponentDO.getBoundsJson());
        db.setCollisionResponseJson(primitiveComponentDO.getCollisionResponseJson());
        db.setMetadataJson(primitiveComponentDO.getMetadataJson());
        primitiveComponentMapper.updateById(db);
    }

    @Override
    public void updatePrimitiveComponentSchema(Long id, PrimitiveComponentDO primitiveComponentDO) {
        PrimitiveComponentDO db = getPrimitiveComponent(id);
        db.setMetadataJson(primitiveComponentDO.getMetadataJson());
        primitiveComponentMapper.updateById(db);
    }
}
