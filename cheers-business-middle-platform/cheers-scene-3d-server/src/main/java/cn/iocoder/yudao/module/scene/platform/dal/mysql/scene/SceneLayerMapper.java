package cn.iocoder.yudao.module.scene.platform.dal.mysql.scene;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.scene.SceneLayerDO;
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