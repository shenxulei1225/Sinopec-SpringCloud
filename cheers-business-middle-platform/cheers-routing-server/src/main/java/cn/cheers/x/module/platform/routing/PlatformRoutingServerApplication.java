package cn.cheers.x.module.platform.routing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"cn.cheers.x.module.platform.routing"})
@EnableFeignClients(basePackages = {
        "cn.cheers.x.module.platform.topology.api",
        "cn.cheers.x.system.api",
        "cn.cheers.x.infra.api"
})
public class PlatformRoutingServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatformRoutingServerApplication.class, args);
    }
}
