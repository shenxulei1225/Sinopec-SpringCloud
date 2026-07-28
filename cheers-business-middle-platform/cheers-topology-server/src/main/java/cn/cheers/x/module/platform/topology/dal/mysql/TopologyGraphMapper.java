package cn.cheers.x.module.platform.topology.dal.mysql;

import cn.cheers.x.module.platform.topology.dal.dataobject.TopologyGraphDO;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TopologyGraphMapper extends BaseMapperX<TopologyGraphDO> {

    default TopologyGraphDO selectDraftByFacilityId(Long facilityId) {
        return selectOne(new LambdaQueryWrapperX<TopologyGraphDO>()
                .eq(TopologyGraphDO::getFacilityId, facilityId)
                .eq(TopologyGraphDO::getStatus, "DRAFT"));
    }

    default TopologyGraphDO selectLatestPublishedByFacilityId(Long facilityId) {
        return selectOne(new LambdaQueryWrapperX<TopologyGraphDO>()
                .eq(TopologyGraphDO::getFacilityId, facilityId)
                .eq(TopologyGraphDO::getStatus, "PUBLISHED")
                .orderByDesc(TopologyGraphDO::getVersion)
                .last("LIMIT 1"));
    }
}
