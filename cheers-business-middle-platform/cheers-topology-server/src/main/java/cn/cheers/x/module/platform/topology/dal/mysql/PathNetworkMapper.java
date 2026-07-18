package cn.cheers.x.module.platform.topology.dal.mysql;

import cn.cheers.x.module.platform.topology.dal.dataobject.PathNetworkDO;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PathNetworkMapper extends BaseMapperX<PathNetworkDO> {

    default PathNetworkDO selectDraftByFacilityIdAndKind(Long facilityId, String networkKind) {
        return selectOne(new LambdaQueryWrapperX<PathNetworkDO>()
                .eq(PathNetworkDO::getFacilityId, facilityId)
                .eq(PathNetworkDO::getNetworkKind, networkKind)
                .eq(PathNetworkDO::getStatus, "DRAFT"));
    }

    default PathNetworkDO selectLatestPublishedByFacilityIdAndKind(Long facilityId, String networkKind) {
        return selectOne(new LambdaQueryWrapperX<PathNetworkDO>()
                .eq(PathNetworkDO::getFacilityId, facilityId)
                .eq(PathNetworkDO::getNetworkKind, networkKind)
                .eq(PathNetworkDO::getStatus, "PUBLISHED")
                .orderByDesc(PathNetworkDO::getVersion)
                .last("LIMIT 1"));
    }
}
