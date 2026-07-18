package cn.iocoder.yudao.module.scene.platform.service.actor.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.actor.ActorInstanceComponentMapper;
import cn.iocoder.yudao.module.scene.platform.service.actor.ActorInstanceComponentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class ActorInstanceComponentServiceImpl implements ActorInstanceComponentService {

    @Resource
    private ActorInstanceComponentMapper actorInstanceComponentMapper;

    @Override
    public List<ActorInstanceComponentDO> getActorInstanceComponentList(Long actorInstanceId) {
        return actorInstanceComponentMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ActorInstanceComponentDO>()
                .eq(ActorInstanceComponentDO::getActorInstanceId, actorInstanceId)
                .orderByAsc(ActorInstanceComponentDO::getSortNo)
                .orderByAsc(ActorInstanceComponentDO::getId));
    }

    @Override
    public ActorInstanceComponentDO getActorInstanceComponent(Long id) {
        ActorInstanceComponentDO item = actorInstanceComponentMapper.selectById(id);
        if (item == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Actor 实例组件不存在");
        }
        return item;
    }

    @Override
    public Long createActorInstanceComponent(ActorInstanceComponentDO actorInstanceComponentDO) {
        actorInstanceComponentMapper.insert(actorInstanceComponentDO);
        return actorInstanceComponentDO.getId();
    }

    @Override
    public void updateActorInstanceComponent(Long id, ActorInstanceComponentDO actorInstanceComponentDO) {
        ActorInstanceComponentDO db = getActorInstanceComponent(id);
        BeanUtils.copyProperties(actorInstanceComponentDO, db);
        actorInstanceComponentMapper.updateById(db);
    }

    @Override
    public void updateActorInstanceComponentDefaults(Long id, ActorInstanceComponentDO actorInstanceComponentDO) {
        ActorInstanceComponentDO db = getActorInstanceComponent(id);
        db.setEnabledFlag(actorInstanceComponentDO.getEnabledFlag());
        db.setSortNo(actorInstanceComponentDO.getSortNo());
        db.setOverrideJson(actorInstanceComponentDO.getOverrideJson());
        db.setMetadataJson(actorInstanceComponentDO.getMetadataJson());
        actorInstanceComponentMapper.updateById(db);
    }

    @Override
    public void updateActorInstanceComponentSchema(Long id, ActorInstanceComponentDO actorInstanceComponentDO) {
        ActorInstanceComponentDO db = getActorInstanceComponent(id);
        db.setMetadataJson(actorInstanceComponentDO.getMetadataJson());
        actorInstanceComponentMapper.updateById(db);
    }

    @Override
    public List<ActorInstanceComponentDO> listByInstanceId(Long actorInstanceId) {
        return getActorInstanceComponentList(actorInstanceId);
    }
}
