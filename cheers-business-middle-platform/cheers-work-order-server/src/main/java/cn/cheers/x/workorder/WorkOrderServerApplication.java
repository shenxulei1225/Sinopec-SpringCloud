package cn.cheers.x.workorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "cn.cheers.x.maintenance.api")
public class WorkOrderServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkOrderServerApplication.class, args);
    }

}
