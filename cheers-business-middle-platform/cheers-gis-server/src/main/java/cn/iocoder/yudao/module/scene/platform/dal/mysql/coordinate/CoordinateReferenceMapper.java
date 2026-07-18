package cn.iocoder.yudao.module.scene.platform.dal.mysql.coordinate;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.coordinate.CoordinateReferenceDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CoordinateReferenceMapper extends BaseMapperX<CoordinateReferenceDO> {

    default CoordinateReferenceDO selectBySceneId(Long sceneId) {
        return selectOne(CoordinateReferenceDO::getSceneId, sceneId);
    }
}
