package cn.cheers.x.module.dynamicbusiness.dal.dataobject.strategy;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 一条策略挂在一台设备上。
 * <p>权威是这张表。策略参数里不再用一个设备编号代表全部设备。
 * <p>不负责比较读数，也不写告警。
 */
@TableName("dynamic_condition_strategy_device")
@KeySequence("dynamic_condition_strategy_device_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionStrategyDeviceDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long strategyId;

    /** 设备实体编号。 */
    private Long deviceId;
}
