package cn.cheers.x.module.platform.topology;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"cn.cheers.x.module.platform.topology"})
@EnableFeignClients(basePackages = {
        "cn.iocoder.yudao.module.system.api",
        "cn.iocoder.yudao.module.infra.api"
})
public class PlatformTopologyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatformTopologyServerApplication.class, args);
    }
}
