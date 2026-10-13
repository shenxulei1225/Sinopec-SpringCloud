package cn.cheers.x.device.protocolsimulator;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 模拟器连哪一台网关、冒充哪一台设备。
 * <p>不负责：协议字段说明。
 */
@Data
@ConfigurationProperties(prefix = "cheers.device-protocol-simulator")
public class SimulatorProperties {

    /**
     * 协议网关的设备口。路径最后一段是设备编号。
     */
    private String websocketUrl = "ws://127.0.0.1:8095/de6e497903c847d0b2a6803aeaa1136c";

    /**
     * 点名和上报里带的设备编号。连接路径最后一段只用这个编号。
     * 默认是已创建的 1#东古大型无人机设备编号。
     */
    private String deviceId = "de6e497903c847d0b2a6803aeaa1136c";

    /**
     * uav：无人机。robot：机器人。两边都连协议网关的设备口，编号不同。
     */
    private String role = "uav";
}
