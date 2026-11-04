package cn.cheers.x.module.dynamicbusiness.dataprocess;

import java.time.Instant;

/**
 * 正常读数交给正在看这台设备的页面。
 * 主题用已经有的「设备状态」：device/{设备编号}/status。
 * 不发到巡检的飞机状态。异常质量不发。消息服务没连上只记日志。
 */
public interface EquipmentStatusPublisher {

    void publish(long deviceId, String pointCode, double value, Instant sampledAt);
}
