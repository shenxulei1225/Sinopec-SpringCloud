package cn.cheers.x.scene.platform.dal.mysql.coordinate;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.scene.platform.dal.dataobject.coordinate.CoordinateReferenceDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CoordinateReferenceMapper extends BaseMapperX<CoordinateReferenceDO> {

    default CoordinateReferenceDO selectBySceneId(Long sceneId) {
        return selectOne(CoordinateReferenceDO::getSceneId, sceneId);
    }
}
