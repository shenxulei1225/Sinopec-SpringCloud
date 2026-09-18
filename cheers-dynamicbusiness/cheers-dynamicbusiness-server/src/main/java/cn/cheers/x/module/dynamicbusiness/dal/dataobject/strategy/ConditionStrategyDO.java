package cn.cheers.x.module.dynamicbusiness.dal.dataobject.strategy;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 一条条件策略：当什么发生、满足哪些条件、做哪件已登记的事。
 */
@TableName("dynamic_condition_strategy")
@KeySequence("dynamic_condition_strategy_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionStrategyDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 平台预置，所有租户可见 */
    @TableField("is_platform")
    private Boolean platform;

    private String name;

    private Boolean enabled;

    private String eventType;

    private String conditionJson;

    private String actionCode;

    private String actionParamsJson;

    private Integer priority;
}
