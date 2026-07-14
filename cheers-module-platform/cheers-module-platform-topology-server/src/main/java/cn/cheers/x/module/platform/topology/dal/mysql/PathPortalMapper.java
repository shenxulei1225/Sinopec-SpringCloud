package cn.cheers.x.module.platform.topology.dal.mysql;

import cn.cheers.x.module.platform.topology.dal.dataobject.PathPortalDO;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
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
}
