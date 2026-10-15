package cn.cheers.x.device.protocolgateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 监控页启停的两台模拟器。编号和端口跟模拟器进程自己的配置一致。
 * <p>不负责：模拟器怎么组应答。
 * <p>禁止：在这里再写一套设备编号去冒充另一台。
 */
@Data
@ConfigurationProperties(prefix = "cheers.device-protocol-gateway.simulators")
public class ProtocolMonitorSimulatorProperties {

    /** 空着时从当前目录往上找 start-microservices.sh。 */
    private String script = "";

    /** 1#东古大型无人机。与模拟器 application.yaml 的 device-id 默认值相同。 */
    private String uavDeviceId = "de6e497903c847d0b2a6803aeaa1136c";

    /** 小型机器人。与模拟器 application-robot.yaml 的 device-id 默认值相同。 */
    private String robotDeviceId = "c9fb0e4891064f759509ed72c92fb71c";

    private int uavPort = 8301;

    private int robotPort = 8302;
}
