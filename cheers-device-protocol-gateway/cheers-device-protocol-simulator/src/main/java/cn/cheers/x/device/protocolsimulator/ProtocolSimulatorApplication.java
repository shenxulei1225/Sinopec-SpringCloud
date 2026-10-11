package cn.cheers.x.device.protocolsimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 一台设备对端。默认是无人机。加上 robot 配置后是机器人。
 * 启动后主动连协议网关的设备口，按 WebSocket 报文应答。
 * <p>不负责：协议网关收包、巡检任务、老工程的 HTTP 推送和 MySQL。
 * <p>禁止：把本进程并进网关进程；把无人机步骤结果的外层字段改成 packages。
 */
@EnableScheduling
@SpringBootApplication
public class ProtocolSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProtocolSimulatorApplication.class, args);
    }
}
