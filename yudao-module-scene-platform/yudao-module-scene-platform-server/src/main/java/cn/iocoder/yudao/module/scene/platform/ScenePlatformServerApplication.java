package cn.iocoder.yudao.module.scene.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 场景平台服务启动类。
 */
@SpringBootApplication
@EnableScheduling
public class ScenePlatformServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScenePlatformServerApplication.class, args);
    }

}
