package cn.iocoder.yudao.module.emergency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 应急管理服务的启动类
 *
 * @author 芋道源码
 */
@SpringBootApplication(scanBasePackages = {
        "cn.iocoder.yudao.module.emergency",
        "cn.cheers.x.framework" // 确保框架配置被扫描
})
@EnableFeignClients(basePackages = {
        "cn.cheers.x.system.api",
        "cn.cheers.x.infra.api",
        "cn.cheers.x.module.platform.runtime.api",
        "cn.cheers.x.module.platform.orchestration.api",
        "cn.cheers.x.bpm.api"
})
public class EmergencyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmergencyServerApplication.class, args);
    }

}














