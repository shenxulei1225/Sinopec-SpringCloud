package cn.cheers.x.inspection.inspection_content.dal.mysql.profile;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.profile.InspectionObjectProfileDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 对象巡检类型台账 Mapper。
 */
@Mapper
public interface InspectionObjectProfileMapper extends BaseMapperX<InspectionObjectProfileDO> {

    default InspectionObjectProfileDO selectByFacilityAndObjectId(Long facilityId, Long objectId) {
        return selectOne(new LambdaQueryWrapperX<InspectionObjectProfileDO>()
                .eq(InspectionObjectProfileDO::getFacilityId, facilityId)
                .eq(InspectionObjectProfileDO::getObjectId, objectId));
    }

    default List<InspectionObjectProfileDO> selectByFacilityAndObjectIds(Long facilityId, Collection<Long> objectIds) {
        if (facilityId == null || objectIds == null || objectIds.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<InspectionObjectProfileDO>()
                .eq(InspectionObjectProfileDO::getFacilityId, facilityId)
                .in(InspectionObjectProfileDO::getObjectId, objectIds));
    }
}
