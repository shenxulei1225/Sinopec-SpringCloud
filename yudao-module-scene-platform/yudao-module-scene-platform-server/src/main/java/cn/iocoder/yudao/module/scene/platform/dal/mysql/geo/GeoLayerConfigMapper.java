package cn.iocoder.yudao.module.scene.platform.dal.mysql.geo;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.geo.GeoLayerConfigDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GeoLayerConfigMapper extends BaseMapperX<GeoLayerConfigDO> {

    default List<GeoLayerConfigDO> selectListBySceneId(Long sceneId) {
        return selectList(new LambdaQueryWrapperX<GeoLayerConfigDO>()
                .eq(GeoLayerConfigDO::getSceneId, sceneId)
                .orderByAsc(GeoLayerConfigDO::getSortNo, GeoLayerConfigDO::getId));
    }

    default GeoLayerConfigDO selectBySceneIdAndLayerKey(Long sceneId, String layerKey) {
        return selectOne(new LambdaQueryWrapperX<GeoLayerConfigDO>()
                .eq(GeoLayerConfigDO::getSceneId, sceneId)
                .eq(GeoLayerConfigDO::getLayerKey, layerKey));
    }

    default void deleteBySceneId(Long sceneId) {
        delete(new LambdaQueryWrapperX<GeoLayerConfigDO>()
                .eq(GeoLayerConfigDO::getSceneId, sceneId));
    }
}
