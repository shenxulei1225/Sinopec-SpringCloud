package cn.cheers.x.module.dynamicbusiness.api.strategy.dto;

import lombok.Data;

/**
 * 一条给人看的策略：当什么发生、要有什么数、做哪件事。
 */
@Data
public class StrategyItemDTO {

    private String id;

    private String name;

    private boolean enabled;

    private String eventType;

    /** 条件（人话） */
    private String conditionText;

    /** 动作登记名（人话） */
    private String actionName;
}
