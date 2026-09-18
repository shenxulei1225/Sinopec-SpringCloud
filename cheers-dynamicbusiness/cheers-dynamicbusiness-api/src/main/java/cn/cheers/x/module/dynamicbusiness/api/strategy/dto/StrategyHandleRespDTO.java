package cn.cheers.x.module.dynamicbusiness.api.strategy.dto;

import lombok.Data;

/**
 * 这一次事件有没有命中策略、做了哪条登记动作。
 */
@Data
public class StrategyHandleRespDTO {

    private boolean matched;

    /** 给人看的策略名；未命中可空 */
    private String strategyName;

    /** 给人看的动作名；未命中可空 */
    private String actionName;

    private Integer processEntryCount;

    /** 新增告警后的账本编号；未做这条动作可空 */
    private Long alarmId;

    /** 新建账后的账本编号；未做这条动作可空 */
    private Long executionRecordId;

    /** 发给设备是否成功；未做这条动作可空 */
    private Boolean dispatchSuccess;

    private String dispatchFailureReason;

    private Boolean dispatchOnline;

    private Boolean dispatchCommandSent;

    private Boolean dispatchStartupSent;

    private String dispatchCommandWireJson;

    /** 未命中时写原因，不假装已记账 */
    private String skipReason;
}
