package cn.cheers.x.twin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {
        "cn.iocoder",
        "cn.cheers.x.module.dynamicbusiness.api",
        "cn.cheers.x.scene.platform.api"
})
public class TwinServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TwinServerApplication.class, args);
    }
}
