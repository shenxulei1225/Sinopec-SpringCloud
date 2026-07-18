package cn.cheers.x.module.platform.topology.dal.mysql;

import cn.cheers.x.module.platform.topology.dal.dataobject.PathPortalDO;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PathPortalMapper extends BaseMapperX<PathPortalDO> {

    default List<PathPortalDO> selectListByFromNetworkId(String fromNetworkId) {
        return selectList(new LambdaQueryWrapperX<PathPortalDO>()
                .eq(PathPortalDO::getFromNetworkId, fromNetworkId));
    }

    default List<PathPortalDO> selectListByToNetworkId(String toNetworkId) {
        return selectList(new LambdaQueryWrapperX<PathPortalDO>()
                .eq(PathPortalDO::getToNetworkId, toNetworkId));
    }

    default List<PathPortalDO> selectListByFacilityId(Long facilityId) {
        String prefix = facilityNetworkPrefix(facilityId);
        return selectList(new LambdaQueryWrapperX<PathPortalDO>()
                .and(wrapper -> wrapper.likeRight(PathPortalDO::getFromNetworkId, prefix)
                        .or()
                        .likeRight(PathPortalDO::getToNetworkId, prefix)));
    }

    default int deleteByFacilityId(Long facilityId) {
        String prefix = facilityNetworkPrefix(facilityId);
        return delete(new LambdaQueryWrapperX<PathPortalDO>()
                .and(wrapper -> wrapper.likeRight(PathPortalDO::getFromNetworkId, prefix)
                        .or()
                        .likeRight(PathPortalDO::getToNetworkId, prefix)));
    }

    private static String facilityNetworkPrefix(Long facilityId) {
        return "net_" + facilityId + "_";
    }
}
