package cn.cheers.x.module.platform.topology.dal.mysql;

import cn.cheers.x.module.platform.topology.dal.dataobject.MobilityProfileDO;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MobilityProfileMapper extends BaseMapperX<MobilityProfileDO> {

    default List<MobilityProfileDO> selectAllActive() {
        return selectList(new LambdaQueryWrapperX<MobilityProfileDO>()
                .orderByAsc(MobilityProfileDO::getId));
    }
}
