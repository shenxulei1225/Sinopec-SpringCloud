package cn.iocoder.yudao.module.scene.platform.service.component.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.CapsuleComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.component.CapsuleComponentMapper;
import cn.iocoder.yudao.module.scene.platform.service.component.CapsuleComponentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class CapsuleComponentServiceImpl implements CapsuleComponentService {

    @Resource
    private CapsuleComponentMapper capsuleComponentMapper;

    @Override
    public List<CapsuleComponentDO> getCapsuleComponentList() {
        return capsuleComponentMapper.selectList();
    }

    @Override
    public CapsuleComponentDO getCapsuleComponent(Long id) {
        CapsuleComponentDO item = capsuleComponentMapper.selectById(id);
        if (item == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Capsule 组件不存在");
        }
        return item;
    }

    @Override
    public void updateCapsuleComponent(Long id, CapsuleComponentDO capsuleComponentDO) {
        CapsuleComponentDO db = getCapsuleComponent(id);
        BeanUtils.copyProperties(capsuleComponentDO, db);
        capsuleComponentMapper.updateById(db);
    }

    @Override
    public void updateCapsuleComponentDefaults(Long id, CapsuleComponentDO capsuleComponentDO) {
        CapsuleComponentDO db = getCapsuleComponent(id);
        db.setCapsuleRadius(capsuleComponentDO.getCapsuleRadius());
        db.setCapsuleHalfHeight(capsuleComponentDO.getCapsuleHalfHeight());
        db.setCollisionEnabled(capsuleComponentDO.getCollisionEnabled());
        db.setGenerateOverlapEvents(capsuleComponentDO.getGenerateOverlapEvents());
        db.setMetadataJson(capsuleComponentDO.getMetadataJson());
        capsuleComponentMapper.updateById(db);
    }

    @Override
    public void updateCapsuleComponentSchema(Long id, CapsuleComponentDO capsuleComponentDO) {
        CapsuleComponentDO db = getCapsuleComponent(id);
        db.setMetadataJson(capsuleComponentDO.getMetadataJson());
        capsuleComponentMapper.updateById(db);
    }
}
