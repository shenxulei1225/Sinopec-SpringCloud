package cn.cheers.x.module.dynamicbusiness.dal.mysql.sop;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.SopScopeRuleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SopScopeRuleMapper extends BaseMapperX<SopScopeRuleDO> {

    default List<SopScopeRuleDO> selectBySopId(Long sopId) {
        return selectList(new LambdaQueryWrapperX<SopScopeRuleDO>()
                .eq(SopScopeRuleDO::getSopId, sopId)
                .orderByAsc(SopScopeRuleDO::getSortNo)
                .orderByAsc(SopScopeRuleDO::getId));
    }

    default void deleteBySopId(Long sopId) {
        delete(new LambdaQueryWrapperX<SopScopeRuleDO>()
                .eq(SopScopeRuleDO::getSopId, sopId));
    }
}
