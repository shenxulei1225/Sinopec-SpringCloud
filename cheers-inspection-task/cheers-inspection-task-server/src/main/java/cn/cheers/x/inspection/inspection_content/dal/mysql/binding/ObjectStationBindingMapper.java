package cn.cheers.x.inspection.inspection_content.dal.mysql.binding;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.binding.ObjectStationBindingDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 对象↔停靠点绑定 Mapper。
 */
@Mapper
public interface ObjectStationBindingMapper extends BaseMapperX<ObjectStationBindingDO> {

    default List<ObjectStationBindingDO> selectByFacilityAndObjectIds(Long facilityId, Collection<Long> objectIds) {
        if (facilityId == null || objectIds == null || objectIds.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<ObjectStationBindingDO>()
                .eq(ObjectStationBindingDO::getFacilityId, facilityId)
                .in(ObjectStationBindingDO::getObjectId, objectIds)
                .orderByAsc(ObjectStationBindingDO::getSortNo));
    }

    default List<ObjectStationBindingDO> selectByFacilityAndObjectId(Long facilityId, Long objectId) {
        return selectList(new LambdaQueryWrapperX<ObjectStationBindingDO>()
                .eq(ObjectStationBindingDO::getFacilityId, facilityId)
                .eq(ObjectStationBindingDO::getObjectId, objectId)
                .orderByAsc(ObjectStationBindingDO::getSortNo));
    }

    default int deleteByFacilityAndObjectId(Long facilityId, Long objectId) {
        return delete(new LambdaQueryWrapperX<ObjectStationBindingDO>()
                .eq(ObjectStationBindingDO::getFacilityId, facilityId)
                .eq(ObjectStationBindingDO::getObjectId, objectId));
    }
}
