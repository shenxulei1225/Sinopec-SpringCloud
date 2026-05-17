package cn.iocoder.yudao.module.twin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "cn.iocoder")
public class TwinServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TwinServerApplication.class, args);
    }
}
