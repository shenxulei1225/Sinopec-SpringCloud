package cn.cheers.x.module.dynamicbusiness.dal.mysql.sop;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.FlowItemPackDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FlowItemPackMapper extends BaseMapperX<FlowItemPackDO> {

    default List<FlowItemPackDO> selectByFlowId(Long flowId) {
        return selectList(new LambdaQueryWrapperX<FlowItemPackDO>()
                .eq(FlowItemPackDO::getFlowId, flowId)
                .orderByAsc(FlowItemPackDO::getSortNo)
                .orderByAsc(FlowItemPackDO::getId));
    }

    default void deleteByFlowId(Long flowId) {
        delete(new LambdaQueryWrapperX<FlowItemPackDO>()
                .eq(FlowItemPackDO::getFlowId, flowId));
    }
}
