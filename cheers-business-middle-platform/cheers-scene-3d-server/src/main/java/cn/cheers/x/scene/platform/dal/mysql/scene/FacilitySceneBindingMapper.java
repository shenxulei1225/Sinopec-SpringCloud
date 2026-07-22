package cn.cheers.x.scene.platform.dal.mysql.scene;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.scene.platform.dal.dataobject.scene.FacilitySceneBindingDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FacilitySceneBindingMapper extends BaseMapperX<FacilitySceneBindingDO> {

    default FacilitySceneBindingDO selectByFacilityId(Long facilityId) {
        return selectOne(FacilitySceneBindingDO::getFacilityId, facilityId);
    }

    default FacilitySceneBindingDO selectByFacilityCode(String facilityCode) {
        return selectOne(FacilitySceneBindingDO::getFacilityCode, facilityCode);
    }

    default List<FacilitySceneBindingDO> selectListBySceneCode(String sceneCode) {
        return selectList(FacilitySceneBindingDO::getSceneCode, sceneCode);
    }
}
