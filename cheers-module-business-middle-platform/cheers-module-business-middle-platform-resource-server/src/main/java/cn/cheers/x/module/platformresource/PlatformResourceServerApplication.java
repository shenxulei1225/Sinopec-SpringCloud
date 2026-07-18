package cn.cheers.x.module.platformresource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"cn.cheers.x.module.platformresource"})
@EnableFeignClients(basePackages = {
        "cn.iocoder.yudao.module.system.api",
        "cn.iocoder.yudao.module.infra.api",
        "cn.cheers.x.module.dynamicbusiness.api"
})
public class PlatformResourceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatformResourceServerApplication.class, args);
    }
}
