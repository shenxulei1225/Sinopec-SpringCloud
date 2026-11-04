package cn.cheers.x.module.dynamicbusiness.dal.mysql.strategy;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.strategy.ConditionStrategyDeviceDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 策略与设备的关联。
 * <p>按当前租户读写。不在这里决定某台设备要不要告警。
 */
@Mapper
public interface ConditionStrategyDeviceMapper extends BaseMapperX<ConditionStrategyDeviceDO> {
}
