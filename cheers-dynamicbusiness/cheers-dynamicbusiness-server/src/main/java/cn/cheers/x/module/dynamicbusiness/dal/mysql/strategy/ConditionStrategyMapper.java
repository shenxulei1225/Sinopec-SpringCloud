package cn.cheers.x.module.dynamicbusiness.dal.mysql.strategy;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.tenant.core.aop.TenantIgnore;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.strategy.ConditionStrategyDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 条件策略表。
 * <p>查询启用策略时要带上平台预置行，所以忽略租户拦截后再自己按租户+平台过滤。
 */
@Mapper
public interface ConditionStrategyMapper extends BaseMapperX<ConditionStrategyDO> {

    @TenantIgnore
    default List<ConditionStrategyDO> selectVisibleEnabled(String eventType, Long tenantId) {
        LambdaQueryWrapper<ConditionStrategyDO> wrapper = new LambdaQueryWrapper<ConditionStrategyDO>()
                .eq(ConditionStrategyDO::getEnabled, true)
                .eq(ConditionStrategyDO::getEventType, eventType)
                .and(w -> w.eq(ConditionStrategyDO::getPlatform, true)
                        .or()
                        .eq(ConditionStrategyDO::getTenantId, tenantId))
                .orderByAsc(ConditionStrategyDO::getPriority)
                .orderByAsc(ConditionStrategyDO::getId);
        return selectList(wrapper);
    }

    @TenantIgnore
    default List<ConditionStrategyDO> selectVisibleAll(Long tenantId) {
        LambdaQueryWrapper<ConditionStrategyDO> wrapper = new LambdaQueryWrapper<ConditionStrategyDO>()
                .and(w -> w.eq(ConditionStrategyDO::getPlatform, true)
                        .or()
                        .eq(ConditionStrategyDO::getTenantId, tenantId))
                .orderByAsc(ConditionStrategyDO::getPriority)
                .orderByAsc(ConditionStrategyDO::getId);
        return selectList(wrapper);
    }
}
