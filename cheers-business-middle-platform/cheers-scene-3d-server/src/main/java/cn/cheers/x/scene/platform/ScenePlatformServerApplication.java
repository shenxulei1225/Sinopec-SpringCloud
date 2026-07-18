package cn.cheers.x.scene.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 场景平台服务启动类。
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"cn.cheers.x.gis.api", "cn.cheers.x.scene.platform.api"})
@EnableScheduling
public class ScenePlatformServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScenePlatformServerApplication.class, args);
    }

}
