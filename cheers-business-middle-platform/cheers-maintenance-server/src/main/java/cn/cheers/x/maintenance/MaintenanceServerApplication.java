package cn.cheers.x.maintenance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {
        "cn.cheers.x.maintenance.api",
        "cn.cheers.x.workorder.api"
})
public class MaintenanceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaintenanceServerApplication.class, args);
    }
}
