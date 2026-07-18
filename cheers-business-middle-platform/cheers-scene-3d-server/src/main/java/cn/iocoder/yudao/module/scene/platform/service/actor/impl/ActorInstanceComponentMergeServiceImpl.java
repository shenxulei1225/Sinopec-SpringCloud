package cn.iocoder.yudao.module.scene.platform.service.actor.impl;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.actor.ActorInstanceComponentMapper;
import cn.iocoder.yudao.module.scene.platform.service.actor.ActorInstanceComponentMergeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActorInstanceComponentMergeServiceImpl implements ActorInstanceComponentMergeService {

    @Resource
    private ActorInstanceComponentMapper actorInstanceComponentMapper;

    @Override
    public List<ActorInstanceComponentDO> getMergedActorInstanceComponents(Long actorInstanceId) {
        return actorInstanceComponentMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ActorInstanceComponentDO>()
                .eq(ActorInstanceComponentDO::getActorInstanceId, actorInstanceId)
                .orderByAsc(ActorInstanceComponentDO::getSortNo)
                .orderByAsc(ActorInstanceComponentDO::getId));
    }
}
