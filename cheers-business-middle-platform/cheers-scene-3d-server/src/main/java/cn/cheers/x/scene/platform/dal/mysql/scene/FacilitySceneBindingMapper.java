package cn.cheers.x.scene.platform.dal.mysql.scene;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.scene.platform.dal.dataobject.scene.FacilitySceneBindingDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FacilitySceneBindingMapper extends BaseMapperX<FacilitySceneBindingDO> {

    default FacilitySceneBindingDO selectByFacilityId(Long facilityId) {
        return selectOne(FacilitySceneBindingDO::getFacilityId, facilityId);
    }
}
