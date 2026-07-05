package cn.cheers.x.module.platform.capability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"cn.cheers.x.module.platform.capability"})
@EnableFeignClients(basePackages = {
        "cn.iocoder.yudao.module.system.api",
        "cn.iocoder.yudao.module.infra.api"
})
public class PlatformCapabilityServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatformCapabilityServerApplication.class, args);
    }
}
