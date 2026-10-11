package cn.cheers.x.device.protocolgateway.djicloud;

import java.util.List;

/**
 * 已经完成拓扑应答的一台网关（机场或 Pilot 2 遥控器），以及当时挂着的飞机。
 * <p>这是本进程里的在线会话，不是设备台账，也不是任务。
 */
public record DjiCloudOnlineDevice(String gatewaySn, List<String> subDeviceSns) {
}
