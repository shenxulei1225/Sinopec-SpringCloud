package cn.cheers.x.module.platform.topology.dal.mysql;

import cn.cheers.x.module.platform.topology.dal.dataobject.TopologyGraphDO;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TopologyGraphMapper extends BaseMapperX<TopologyGraphDO> {

    default TopologyGraphDO selectDraftBySiteId(Long siteId) {
        return selectOne(new LambdaQueryWrapperX<TopologyGraphDO>()
                .eq(TopologyGraphDO::getSiteId, siteId)
                .eq(TopologyGraphDO::getStatus, "DRAFT"));
    }

    default TopologyGraphDO selectLatestPublishedBySiteId(Long siteId) {
        return selectOne(new LambdaQueryWrapperX<TopologyGraphDO>()
                .eq(TopologyGraphDO::getSiteId, siteId)
                .eq(TopologyGraphDO::getStatus, "PUBLISHED")
                .orderByDesc(TopologyGraphDO::getVersion)
                .last("LIMIT 1"));
    }
}
