package cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo;

import lombok.Data;

/**
 * 策略页上可勾选的一台设备。
 * 编号是设备实体编号。名称只用于勾选时给人看。
 */
@Data
public class StrategyDeviceOptionVO {

    private Long id;

    private String name;
}
