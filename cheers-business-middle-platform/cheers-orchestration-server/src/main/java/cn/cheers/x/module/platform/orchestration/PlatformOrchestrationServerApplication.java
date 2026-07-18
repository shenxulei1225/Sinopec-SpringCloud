package cn.cheers.x.module.platform.orchestration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
        "cn.cheers.x.module.platform.orchestration",
        "cn.cheers.x.module.platform.scheduling"
})
@EnableFeignClients(basePackages = {
        "cn.cheers.x.system.api",
        "cn.cheers.x.infra.api",
        "cn.cheers.x.module.platform.runtime.api",
        "cn.cheers.x.module.platform.policy.api",
        "cn.cheers.x.module.platform.capability.api",
        "cn.cheers.x.workorder.api"
})
public class PlatformOrchestrationServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatformOrchestrationServerApplication.class, args);
    }
}
