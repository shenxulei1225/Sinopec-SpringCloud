package cn.iocoder.yudao.module.scene.platform.dal.mysql.actor;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface ActorInstanceMapper extends BaseMapperX<ActorInstanceDO> {

    default List<ActorInstanceDO> selectListBySceneId(Long sceneId) {
        return selectList(new LambdaQueryWrapperX<ActorInstanceDO>()
                .eq(ActorInstanceDO::getSceneId, sceneId)
                .orderByAsc(ActorInstanceDO::getVersionNo)
                .orderByAsc(ActorInstanceDO::getInstanceCode));
    }

    default List<ActorInstanceDO> selectSimpleListBySceneIdAndKeyword(Long sceneId, String keyword) {
        LambdaQueryWrapperX<ActorInstanceDO> query = new LambdaQueryWrapperX<ActorInstanceDO>()
                .eq(ActorInstanceDO::getSceneId, sceneId);
        if (keyword != null && !keyword.isBlank()) {
            query.and(w -> w.like(ActorInstanceDO::getInstanceCode, keyword)
                    .or().like(ActorInstanceDO::getInstanceName, keyword)
                    .or().like(ActorInstanceDO::getActorCode, keyword));
        }
        query.orderByAsc(ActorInstanceDO::getInstanceName)
                .orderByAsc(ActorInstanceDO::getInstanceCode);
        return selectList(query);
    }

    default List<ActorInstanceDO> selectListByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return selectBatchIds(ids);
    }

    default ActorInstanceDO selectBySceneIdAndInstanceCode(Long sceneId, String instanceCode) {
        return selectOne(new LambdaQueryWrapperX<ActorInstanceDO>()
                .eq(ActorInstanceDO::getSceneId, sceneId)
                .eq(ActorInstanceDO::getInstanceCode, instanceCode));
    }

    default List<ActorInstanceDO> selectListByParentInstanceCode(String parentInstanceCode) {
        return selectList(new LambdaQueryWrapperX<ActorInstanceDO>()
                .eq(ActorInstanceDO::getParentInstanceCode, parentInstanceCode));
    }

    default List<ActorInstanceDO> selectListByLayer(Long sceneId, String layerKey) {
        return selectList(new LambdaQueryWrapperX<ActorInstanceDO>()
                .eq(ActorInstanceDO::getSceneId, sceneId)
                .likeIfPresent(ActorInstanceDO::getLayerKeys, layerKey));
    }
}
