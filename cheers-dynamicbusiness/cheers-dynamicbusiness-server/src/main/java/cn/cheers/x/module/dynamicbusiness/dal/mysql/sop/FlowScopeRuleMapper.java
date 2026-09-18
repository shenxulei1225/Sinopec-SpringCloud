package cn.cheers.x.module.dynamicbusiness.dal.mysql.sop;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.FlowScopeRuleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FlowScopeRuleMapper extends BaseMapperX<FlowScopeRuleDO> {

    default List<FlowScopeRuleDO> selectByFlowId(Long flowId) {
        return selectList(new LambdaQueryWrapperX<FlowScopeRuleDO>()
                .eq(FlowScopeRuleDO::getFlowId, flowId)
                .orderByAsc(FlowScopeRuleDO::getSortNo)
                .orderByAsc(FlowScopeRuleDO::getId));
    }

    default void deleteByFlowId(Long flowId) {
        delete(new LambdaQueryWrapperX<FlowScopeRuleDO>()
                .eq(FlowScopeRuleDO::getFlowId, flowId));
    }
}
