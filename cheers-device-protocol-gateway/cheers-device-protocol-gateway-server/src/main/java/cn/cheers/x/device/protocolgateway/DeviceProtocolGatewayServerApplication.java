package cn.cheers.x.device.protocolgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 机器人/无人机对接协议网关启动类。
 * <p>管：通讯层会话收发、协议对接编解码、上行回调推送。
 * <p>不管：巡检任务编排、SOP、设备台账 CRUD。
 */
@SpringBootApplication
@EnableDiscoveryClient
public class DeviceProtocolGatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeviceProtocolGatewayServerApplication.class, args);
    }
}
