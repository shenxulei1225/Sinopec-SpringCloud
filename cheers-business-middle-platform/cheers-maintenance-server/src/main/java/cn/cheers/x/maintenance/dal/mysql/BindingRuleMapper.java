package cn.cheers.x.maintenance.dal.mysql;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.maintenance.controller.admin.vo.binding.BindingRulePageReqVO;
import cn.cheers.x.maintenance.dal.dataobject.BindingRuleDO;
import cn.cheers.x.maintenance.enums.BindingRuleStatusEnum;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BindingRuleMapper extends BaseMapperX<BindingRuleDO> {

    default List<BindingRuleDO> selectPublishedByScope(String scope) {
        return selectList(new LambdaQueryWrapperX<BindingRuleDO>()
                .eq(BindingRuleDO::getScope, scope)
                .eq(BindingRuleDO::getStatus, BindingRuleStatusEnum.PUBLISHED.getStatus()));
    }

    default PageResult<BindingRuleDO> selectPage(BindingRulePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BindingRuleDO>()
                .eqIfPresent(BindingRuleDO::getCode, reqVO.getCode())
                .eqIfPresent(BindingRuleDO::getScope, reqVO.getScope())
                .eqIfPresent(BindingRuleDO::getStatus, reqVO.getStatus())
                .orderByDesc(BindingRuleDO::getPriority)
                .orderByDesc(BindingRuleDO::getId));
    }
}
