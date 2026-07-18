package cn.iocoder.yudao.module.scene.platform.service.component.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.SkeletalMeshComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.component.SkeletalMeshComponentMapper;
import cn.iocoder.yudao.module.scene.platform.service.component.SkeletalMeshComponentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class SkeletalMeshComponentServiceImpl implements SkeletalMeshComponentService {

    @Resource
    private SkeletalMeshComponentMapper skeletalMeshComponentMapper;

    @Override
    public List<SkeletalMeshComponentDO> getSkeletalMeshComponentList() {
        return skeletalMeshComponentMapper.selectList();
    }

    @Override
    public SkeletalMeshComponentDO getSkeletalMeshComponent(Long id) {
        SkeletalMeshComponentDO item = skeletalMeshComponentMapper.selectById(id);
        if (item == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Skeletal Mesh 组件不存在");
        }
        return item;
    }

    @Override
    public void updateSkeletalMeshComponent(Long id, SkeletalMeshComponentDO skeletalMeshComponentDO) {
        SkeletalMeshComponentDO db = getSkeletalMeshComponent(id);
        BeanUtils.copyProperties(skeletalMeshComponentDO, db);
        skeletalMeshComponentMapper.updateById(db);
    }

    @Override
    public void updateSkeletalMeshComponentDefaults(Long id, SkeletalMeshComponentDO skeletalMeshComponentDO) {
        SkeletalMeshComponentDO db = getSkeletalMeshComponent(id);
        db.setSkeletonCode(skeletalMeshComponentDO.getSkeletonCode());
        db.setAnimationBlueprintCode(skeletalMeshComponentDO.getAnimationBlueprintCode());
        db.setMaterialsJson(skeletalMeshComponentDO.getMaterialsJson());
        db.setCastShadow(skeletalMeshComponentDO.getCastShadow());
        db.setReceiveShadow(skeletalMeshComponentDO.getReceiveShadow());
        db.setUseAnimationBlueprint(skeletalMeshComponentDO.getUseAnimationBlueprint());
        db.setMetadataJson(skeletalMeshComponentDO.getMetadataJson());
        skeletalMeshComponentMapper.updateById(db);
    }

    @Override
    public void updateSkeletalMeshComponentSchema(Long id, SkeletalMeshComponentDO skeletalMeshComponentDO) {
        SkeletalMeshComponentDO db = getSkeletalMeshComponent(id);
        db.setMetadataJson(skeletalMeshComponentDO.getMetadataJson());
        skeletalMeshComponentMapper.updateById(db);
    }
}
