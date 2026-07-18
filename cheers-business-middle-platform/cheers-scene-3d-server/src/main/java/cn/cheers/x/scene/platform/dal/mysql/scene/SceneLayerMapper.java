package cn.cheers.x.scene.platform.dal.mysql.scene;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.scene.platform.dal.dataobject.scene.SceneLayerDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SceneLayerMapper extends BaseMapperX<SceneLayerDO> {

    default List<SceneLayerDO> selectListBySceneId(Long sceneId) {
        return selectList(new LambdaQueryWrapperX<SceneLayerDO>()
                .eq(SceneLayerDO::getSceneId, sceneId)
                .orderByAsc(SceneLayerDO::getSortNo));
    }

    default SceneLayerDO selectBySceneIdAndLayerKey(Long sceneId, String layerKey) {
        return selectOne(SceneLayerDO::getSceneId, sceneId,
                SceneLayerDO::getLayerCode, layerKey);
    }
}